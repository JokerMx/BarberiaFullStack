// frontend/src/main.ts
import { ServicioService } from './services/servicio.service';
import { ReservaService } from './services/reserva.service';
import { ServiciosView } from './views/servicios.view';
import { EstadoCarga } from './enums/estados.enum';
import { mostrarNotificacion } from './utils/notificacion';
import { validarEmail, validarTelefono, validarNombreCompleto } from './utils/validaciones';
import './style.css';

class App {
  private serviciosView: ServiciosView;

  constructor() {
    this.serviciosView = new ServiciosView();
    this.init();
  }

  private async init(): Promise<void> {
    document.addEventListener('reload-servicios', () => this.cargarServicios());
    document.addEventListener('reservar-servicio', (event: Event) => {
      const customEvent = event as CustomEvent;
      this.mostrarFormularioReserva(customEvent.detail.servicioId);
    });

    await this.cargarServicios();
  }

  private async cargarServicios(): Promise<void> {
    try {
      this.serviciosView.renderState(EstadoCarga.CARGANDO);
      const servicios = await ServicioService.getAll();
      this.serviciosView.renderState(EstadoCarga.EXITO, servicios);
    } catch (error) {
      const mensaje = error instanceof Error ? error.message : 'Error desconocido';
      this.serviciosView.renderState(EstadoCarga.ERROR, mensaje);
      console.error('Error al cargar servicios:', error);
    }
  }

  private mostrarErrorCampo(form: HTMLFormElement, fieldName: string, mensaje: string): void {
    const field = form.querySelector(`[name="${fieldName}"]`);
    if (!field) return;
    const group = field.closest('.form-group');
    if (!group) return;
    const errorSpan = group.querySelector('.field-error');
    if (errorSpan) {
      errorSpan.textContent = mensaje;
    }
    group.classList.add('error');
  }

  private limpiarErrores(form: HTMLFormElement): void {
    const groups = form.querySelectorAll('.form-group');
    groups.forEach(group => {
      group.classList.remove('error');
      const errorSpan = group.querySelector('.field-error');
      if (errorSpan) {
        errorSpan.textContent = '';
      }
    });
  }

  private limpiarErrorCampo(field: Element): void {
    const group = field.closest('.form-group');
    if (!group) return;
    group.classList.remove('error');
    const errorSpan = group.querySelector('.field-error');
    if (errorSpan) {
      errorSpan.textContent = '';
    }
  }

  private validarCampoBlur(form: HTMLFormElement, fieldName: string): boolean {
    const field = form.querySelector(`[name="${fieldName}"]`) as HTMLInputElement | HTMLSelectElement;
    if (!field) return true;

    const group = field.closest('.form-group');
    if (!group) return true;

    const value = (field as HTMLInputElement).value.trim();
    let valido = true;
    let mensaje = '';

    switch (fieldName) {
      case 'nombre_cliente':
        if (!value) {
          valido = false;
          mensaje = '❌ Este campo es obligatorio';
        } else if (!validarNombreCompleto(value)) {
          valido = false;
          mensaje = '❌ El nombre debe ser compuesto (nombre y apellido)';
        }
        break;
      case 'email':
        if (!value) {
          valido = false;
          mensaje = '❌ Este campo es obligatorio';
        } else if (!validarEmail(value)) {
          valido = false;
          mensaje = '❌ Formato de correo inválido';
        }
        break;
      case 'telefono':
        if (!value) {
          valido = false;
          mensaje = '❌ Este campo es obligatorio';
        } else if (!validarTelefono(value)) {
          valido = false;
          mensaje = '❌ Formato: +569 12345678';
        }
        break;
      case 'profesional_id':
        if (!value) {
          valido = false;
          mensaje = '❌ Seleccione un profesional';
        }
        break;
      case 'fecha': {
        if (!value) {
          valido = false;
          mensaje = '❌ Este campo es obligatorio';
        } else {
          const hoy = new Date();
          hoy.setHours(0, 0, 0, 0);
          const seleccionada = new Date(value);
          const limite = new Date(hoy.getTime() + 30 * 24 * 60 * 60 * 1000);
          if (seleccionada < hoy) {
            valido = false;
            mensaje = '❌ La fecha no puede ser anterior a la actual';
          } else if (seleccionada > limite) {
            valido = false;
            mensaje = '❌ Máximo 30 días en el futuro';
          }
        }
        break;
      }
      case 'hora':
        if (!value) {
          valido = false;
          mensaje = '❌ Seleccione una hora';
        }
        break;
    }

    if (!valido) {
      this.mostrarErrorCampo(form, fieldName, mensaje);
    } else {
      this.limpiarErrorCampo(field);
    }

    return valido;
  }

  private async mostrarFormularioReserva(servicioId: number): Promise<void> {
    const modal = document.getElementById('modal-reserva');
    if (!modal) return;

    const form = modal.querySelector('form');
    if (!form) return;

    const formElement = form as HTMLFormElement;

    const inputServicio = form.querySelector('input[name="servicio_id"]') as HTMLInputElement;
    if (inputServicio) {
      inputServicio.value = servicioId.toString();
    }

    const inputFecha = form.querySelector('#fecha') as HTMLInputElement;
    if (inputFecha) {
      const hoy = new Date();
      const fechaHoy = hoy.toISOString().split('T')[0];
      const fechaMax = new Date(hoy.getTime() + 30 * 24 * 60 * 60 * 1000);
      const fechaMaxStr = fechaMax.toISOString().split('T')[0];
      inputFecha.min = fechaHoy;
      inputFecha.max = fechaMaxStr;
      inputFecha.value = '';
    }

    this.limpiarErrores(formElement);
    modal.classList.remove('hidden');

    const camposValidar: string[] = ['nombre_cliente', 'email', 'telefono', 'profesional_id', 'fecha', 'hora'];
    camposValidar.forEach(campo => {
      const field = form.querySelector(`[name="${campo}"]`);
      if (field) {
        field.addEventListener('blur', () => {
          this.validarCampoBlur(formElement, campo);
        });
      }
    });

    form.addEventListener('submit', async (event: Event) => {
      event.preventDefault();
      this.limpiarErrores(formElement);

      const formData = new FormData(form);
      const nombreCliente = formData.get('nombre_cliente') as string;
      const email = formData.get('email') as string;
      const telefono = formData.get('telefono') as string;
      const profesionalId = formData.get('profesional_id') as string;
      const fechaStr = formData.get('fecha') as string;
      const hora = formData.get('hora') as string;

      let hasFieldError = false;

      for (const campo of camposValidar) {
        if (!this.validarCampoBlur(formElement, campo)) {
          hasFieldError = true;
        }
      }

      if (hasFieldError) return;

      const reservaData = {
        nombre_cliente: nombreCliente,
        email: email,
        telefono: telefono,
        servicio_id: parseInt(formData.get('servicio_id') as string),
        profesional_id: parseInt(profesionalId),
        fecha: fechaStr,
        hora: hora
      };

      if (!reservaData.servicio_id) {
        this.mostrarErrorCampo(formElement, 'profesional_id', 'Servicio inválido');
        return;
      }

      try {
        await ReservaService.create(reservaData);
        mostrarNotificacion('Reserva creada exitosamente', 'exitosa');
        await this.serviciosView.cargarReservas();
        modal.classList.add('hidden');
        form.reset();
        this.limpiarErrores(formElement);
      } catch (error) {
        const mensaje = error instanceof Error ? error.message : 'Error al crear la reserva';
        mostrarNotificacion(mensaje, 'error');
        console.error('Error al crear reserva:', error);
      }
    });

    const closeBtn = modal.querySelector('.close-modal');
    if (closeBtn) {
      closeBtn.addEventListener('click', () => {
        modal.classList.add('hidden');
        form.reset();
      });
    }
  }
}

document.addEventListener('DOMContentLoaded', () => {
  new App();
});
