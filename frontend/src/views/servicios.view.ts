// frontend/src/views/servicios.view.ts
import type { Servicio } from '../interfaces/servicio.interface';
import { EstadoCarga } from '../enums/estados.enum';
import { obtenerIconoCategoria } from '../utils/icon-categories';
import { ReservaService } from '../services/reserva.service';
import type { Reserva } from '../interfaces/reserva.interface';

export class ServiciosView {
  private container: HTMLElement;
  private reservasContainer: HTMLElement;

  constructor() {
    const container = document.getElementById('servicios-container');
    if (!container) {
      throw new Error('Contenedor de servicios no encontrado');
    }
    this.container = container;

    const reservasContainer = document.getElementById('reservas-container');
    if (!reservasContainer) {
      throw new Error('Contenedor de reservas no encontrado');
    }
    this.reservasContainer = reservasContainer;

    this.cargarReservas();
    this.iniciarAutoRefresh();
  }

  private iniciarAutoRefresh(): void {
    setInterval(() => {
      this.cargarReservas();
    }, 30000);
  }

  public async cargarReservas(): Promise<void> {
    try {
      const reservas = await ReservaService.getAll();
      this.renderReservas(reservas);
    } catch (error) {
      const mensaje = error instanceof Error ? error.message : 'Error desconocido';
      this.reservasContainer.innerHTML = `
        <div class="error-container">
          <span class="error-icon">⚠️</span>
          <p class="error-message">${mensaje}</p>
        </div>
      `;
    }
  }

  renderState(state: EstadoCarga, data?: Servicio[] | string): void {
    this.clearContainer();

    switch (state) {
      case EstadoCarga.CARGANDO:
        this.renderLoading();
        break;
      case EstadoCarga.EXITO:
        if (Array.isArray(data) && data.length > 0) {
          this.renderServicios(data);
        } else {
          this.renderEmpty();
        }
        break;
      case EstadoCarga.ERROR:
        this.renderError(data as string || 'Error al cargar los servicios');
        break;
      case EstadoCarga.VACIO:
        this.renderEmpty();
        break;
      default:
        this.renderInitial();
        break;
    }
  }

  private clearContainer(): void {
    this.container.innerHTML = '';
  }

  private renderLoading(): void {
    this.container.innerHTML = `
      <div class="loading-container">
        <div class="spinner"></div>
        <p>Cargando servicios...</p>
      </div>
    `;
  }

  private renderServicios(servicios: Servicio[]): void {
    this.container.innerHTML = `
      <div class="servicios-grid">
        ${servicios.map(servicio => `
          <div class="servicio-card" data-id="${servicio.id}">
            <span class="categoria-badge">${obtenerIconoCategoria(servicio.categoria)}</span>
            <h3>${servicio.nombre}</h3>
            <p class="descripcion">${servicio.descripcion || 'Disfruta de una experiencia única en nuestro salón.'}</p>
            <div class="info">
              <span class="duracion">${servicio.duracion} min</span>
              <span class="precio">$${servicio.precio.toLocaleString('es-CO')}</span>
            </div>
            <button class="btn-reservar" data-id="${servicio.id}">Reservar</button>
          </div>
        `).join('')}
      </div>
    `;
    this.addEventListeners();
  }

  private renderError(message: string): void {
    this.container.innerHTML = `
      <div class="error-container">
        <span class="error-icon">⚠️</span>
        <p class="error-message">${message}</p>
        <button class="btn-reintentar">Reintentar</button>
      </div>
    `;
    const retryButton = this.container.querySelector('.btn-reintentar');
    if (retryButton) {
      retryButton.addEventListener('click', () => {
        const reloadEvent = new CustomEvent('reload-servicios');
        document.dispatchEvent(reloadEvent);
      });
    }
  }

  private renderEmpty(): void {
    this.container.innerHTML = `
      <div class="empty-container">
        <p>No hay servicios disponibles</p>
      </div>
    `;
  }

  private renderInitial(): void {
    this.container.innerHTML = `
      <div class="initial-container">
        <p>Cargando servicios disponibles...</p>
      </div>
    `;
  }

  private renderReservas(reservas: Reserva[]): void {
    if (!reservas || reservas.length === 0) {
      this.reservasContainer.innerHTML = `
        <div class="empty-container">
          <p>No hay reservas registradas aún</p>
        </div>
      `;
      return;
    }

    this.reservasContainer.innerHTML = `
      <ul>
        ${reservas.map(r => `
          <li>
            <div class="reserva-info">
              <span class="nombre">${r.servicio_nombre || 'Servicio'}</span>
              <span class="detalle">${r.cliente_nombre || 'Cliente'} • ${r.profesional_nombre || 'Profesional'}</span>
              <span class="fecha-hora">${new Date(r.fecha).toLocaleDateString('es-CO')} • ${r.hora}</span>
            </div>
            <span class="estado ${r.estado.toLowerCase()}">${r.estado}</span>
          </li>
        `).join('')}
      </ul>
    `;
  }

  private addEventListeners(): void {
    const buttons = this.container.querySelectorAll('.btn-reservar');
    buttons.forEach(button => {
      button.addEventListener('click', (event: Event) => {
        const target = event.currentTarget as HTMLButtonElement;
        const servicioId = target.dataset.id;
        if (servicioId) {
          const customEvent = new CustomEvent('reservar-servicio', {
            detail: { servicioId: parseInt(servicioId) }
          });
          document.dispatchEvent(customEvent);
        }
      });
    });
  }
}