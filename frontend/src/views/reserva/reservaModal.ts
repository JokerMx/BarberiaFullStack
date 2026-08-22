// frontend/src/views/reserva/reservaModal.ts

import { ReservaService } from '../../services/reserva.service';
import type { Servicio } from '../../interfaces/reserva.interface';

export class ReservaModal {
    private overlay: HTMLDivElement;
    private modal: HTMLDivElement;
    private servicio: Servicio | null = null;
    private onSuccess?: () => void;

    constructor(servicio?: Servicio, onSuccess?: () => void) {
        this.servicio = servicio || null;
        this.onSuccess = onSuccess;
        this.overlay = document.createElement('div');
        this.modal = document.createElement('div');
        this.render();
        this.bindEvents();
    }

    private render(): void {
        // Overlay
        this.overlay.className = 'modal-overlay';
        this.overlay.id = 'reserva-modal-overlay';

        // Modal
        this.modal.className = 'modal-content';
        this.modal.id = 'reserva-modal';

        const fechaMin = ReservaService.getFechaMinima();
        const fechaMax = ReservaService.getFechaMaxima();
        const horas = ReservaService.getHorasDisponibles();

        const nombreServicio = this.servicio?.nombre || '';
        const precioServicio = this.servicio?.precio ? `$${this.servicio.precio.toLocaleString()}` : '';

        this.modal.innerHTML = `
            <div class="modal-header">
                <h2>📅 Nueva Reserva</h2>
                <button class="modal-close" id="modal-close-btn">✕</button>
            </div>
            <div class="modal-body">
                ${this.servicio ? `
                    <div class="servicio-info-modal">
                        <h3>${nombreServicio}</h3>
                        <p class="servicio-precio-modal">💰 ${precioServicio}</p>
                        <p class="servicio-duracion-modal">⏱️ ${this.servicio.duracionMinutos} minutos</p>
                    </div>
                ` : `
                    <div class="form-group">
                        <label for="servicio-select">Seleccionar Servicio</label>
                        <select id="servicio-select" required>
                            <option value="">-- Seleccionar --</option>
                        </select>
                    </div>
                `}

                <div class="form-group">
                    <label for="fecha-reserva">Fecha</label>
                    <input type="date" id="fecha-reserva" 
                           min="${fechaMin}" max="${fechaMax}" 
                           value="${fechaMin}" required />
                    <small class="fecha-helper">📌 Puedes reservar desde hoy hasta 30 días después</small>
                    <div id="fecha-error" class="error hidden"></div>
                </div>

                <div class="form-group">
                    <label for="hora-reserva">Hora</label>
                    <select id="hora-reserva" required>
                        ${horas.map(h => `<option value="${h}">${h}</option>`).join('')}
                    </select>
                </div>

                <div class="form-group">
                    <label for="notas-reserva">Notas (opcional)</label>
                    <textarea id="notas-reserva" rows="2" placeholder="Preferencias o comentarios..."></textarea>
                </div>

                <div id="reserva-error" class="error hidden"></div>
                <div id="reserva-success" class="success hidden"></div>

                <button id="confirmar-reserva-btn" class="btn-confirmar">✅ Confirmar Reserva</button>
            </div>
        `;

        this.overlay.appendChild(this.modal);
        document.body.appendChild(this.overlay);

        if (!this.servicio) {
            this.cargarServicios();
        }
    }

    private async cargarServicios(): Promise<void> {
        const select = document.getElementById('servicio-select') as HTMLSelectElement;
        if (!select) return;

        try {
            const servicios = await ReservaService.getServicios();
            servicios.forEach(serv => {
                const option = document.createElement('option');
                option.value = String(serv.id);
                option.textContent = `${serv.nombre} - $${serv.precio.toLocaleString()}`;
                select.appendChild(option);
            });
        } catch (error) {
            console.error('Error al cargar servicios:', error);
        }
    }

    private bindEvents(): void {
        const closeBtn = document.getElementById('modal-close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => this.close());
        }

        this.overlay.addEventListener('click', (e) => {
            if (e.target === this.overlay) {
                this.close();
            }
        });

        const fechaInput = document.getElementById('fecha-reserva') as HTMLInputElement;
        const fechaError = document.getElementById('fecha-error') as HTMLDivElement;
        if (fechaInput && fechaError) {
            fechaInput.addEventListener('change', () => {
                const validacion = ReservaService.validarFecha(fechaInput.value);
                if (!validacion.valida) {
                    fechaError.textContent = validacion.mensaje || '';
                    fechaError.classList.remove('hidden');
                    fechaInput.classList.add('input-error');
                } else {
                    fechaError.classList.add('hidden');
                    fechaInput.classList.remove('input-error');
                }
            });
        }

        const confirmBtn = document.getElementById('confirmar-reserva-btn');
        if (confirmBtn) {
            confirmBtn.addEventListener('click', () => this.confirmarReserva());
        }
    }

    private async confirmarReserva(): Promise<void> {
        const errorElement = document.getElementById('reserva-error') as HTMLDivElement;
        const successElement = document.getElementById('reserva-success') as HTMLDivElement;
        const fechaInput = document.getElementById('fecha-reserva') as HTMLInputElement;
        const horaSelect = document.getElementById('hora-reserva') as HTMLSelectElement;
        const notasTextarea = document.getElementById('notas-reserva') as HTMLTextAreaElement;

        errorElement.classList.add('hidden');
        successElement.classList.add('hidden');

        // Obtener ID del usuario desde localStorage
        let clienteId: number | null = null;
        try {
            const userData = localStorage.getItem('user');
            if (userData) {
                const usuario = JSON.parse(userData);
                clienteId = usuario.id || null;
            }
        } catch {
            clienteId = null;
        }

        if (!clienteId) {
            errorElement.textContent = 'Debes iniciar sesión para reservar';
            errorElement.classList.remove('hidden');
            return;
        }

        // Obtener servicioId
        let servicioId: number;
        if (this.servicio) {
            servicioId = this.servicio.id;
        } else {
            const select = document.getElementById('servicio-select') as HTMLSelectElement;
            servicioId = parseInt(select.value);
            if (!servicioId) {
                errorElement.textContent = 'Por favor, selecciona un servicio';
                errorElement.classList.remove('hidden');
                return;
            }
        }

        const fecha = fechaInput.value;
        const hora = horaSelect.value;
        const notas = notasTextarea.value.trim();

        // Validar fecha
        const validacion = ReservaService.validarFecha(fecha);
        if (!validacion.valida) {
            errorElement.textContent = validacion.mensaje || '';
            errorElement.classList.remove('hidden');
            return;
        }

        try {
            const reserva = await ReservaService.crearReserva({
                clienteId,
                servicioId,
                fecha,
                hora,
                notas: notas || undefined,
            });

            successElement.textContent = `✅ Reserva creada exitosamente! Código: ${reserva.codigo}`;
            successElement.classList.remove('hidden');

            setTimeout(() => {
                this.close();
                if (this.onSuccess) {
                    this.onSuccess();
                }
            }, 2000);

        } catch (error: any) {
            errorElement.textContent = error.message || 'Error al crear la reserva';
            errorElement.classList.remove('hidden');
        }
    }

    public close(): void {
        if (this.overlay.parentNode) {
            this.overlay.parentNode.removeChild(this.overlay);
        }
    }
}