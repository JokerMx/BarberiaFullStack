// frontend/src/views/servicios/serviciosView.ts

import { ServicioService } from '../../services/servicio.service';
import { AuthService } from '../../services/authService';
import type { Servicio, ServicioRequest } from '../../interfaces/servicio.interface';
import { ReservaModal } from '../reserva/reservaModal';

export class ServiciosView {
    private container: HTMLElement;
    private user: any = null;

    constructor(container: HTMLElement) {
        this.container = container;
        this.loadUser();
        this.render();
        this.loadServicios();
    }

    private loadUser(): void {
        const userData = localStorage.getItem('user');
        if (userData) {
            try {
                this.user = JSON.parse(userData);
            } catch {
                this.user = null;
            }
        }

        if (!this.user) {
            window.location.href = '/login.html';
        }
    }

    private render(): void {
        this.container.innerHTML = `
            <div class="dashboard-container">
                <header class="dashboard-header">
                    <div class="header-left">
                        <h1>🪒 Barbería</h1>
                        <span class="badge-rol ${this.user?.rol?.toLowerCase() || ''}">${this.user?.rol || ''}</span>
                    </div>
                    <div class="header-right">
                        <span class="user-name">👋 ${this.user?.username || 'Usuario'}</span>
                        <a href="/dashboard.html" class="btn-back">← Volver</a>
                        <button id="logout-btn" class="btn-logout">Cerrar Sesión</button>
                    </div>
                </header>

                <section class="servicios-section">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 0.5rem;">
                        <h2>✂️ Servicios de la Barbería</h2>
                        <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                            <button id="refresh-servicios-btn" class="btn-refresh">🔄 Actualizar</button>
                            <button id="nueva-reserva-btn" class="btn-primary">➕ Nueva Reserva</button>
                        </div>
                    </div>
                    ${this.user?.rol === 'ADMIN' ? `
                        <form id="servicio-form" class="servicio-admin-form">
                            <input type="hidden" id="servicio-id" />
                            <input type="text" id="servicio-nombre" placeholder="Nombre del servicio" required maxlength="100" />
                            <input type="number" id="servicio-precio" placeholder="Precio" min="0" step="0.01" required />
                            <input type="number" id="servicio-duracion" placeholder="Duración (minutos)" min="1" required />
                            <input type="text" id="servicio-descripcion" placeholder="Descripción (opcional)" />
                            <button type="submit" class="btn-primary">Guardar servicio</button>
                            <button type="button" id="cancelar-edicion-servicio" class="btn-back" hidden>Cancelar</button>
                        </form>
                    ` : ''}
                    <div id="servicios-list" class="servicios-grid">
                        <p class="loading">Cargando servicios...</p>
                    </div>
                </section>
            </div>
        `;

        // Eventos
        document.getElementById('logout-btn')?.addEventListener('click', () => {
            void AuthService.logout();
        });

        document.getElementById('refresh-servicios-btn')?.addEventListener('click', () => {
            this.loadServicios();
        });

        document.getElementById('nueva-reserva-btn')?.addEventListener('click', () => {
            new ReservaModal(undefined, () => {
                // Recargar servicios o mostrar confirmación
                console.log('Reserva creada exitosamente');
            });
        });

        if (this.user?.rol === 'ADMIN') {
            document.getElementById('servicio-form')?.addEventListener('submit', (event) => {
                event.preventDefault();
                this.guardarServicio();
            });
            document.getElementById('cancelar-edicion-servicio')?.addEventListener('click', () => this.limpiarFormulario());
        }
    }

    private async loadServicios(): Promise<void> {
        const container = document.getElementById('servicios-list');
        if (!container) return;

        container.innerHTML = `<p class="loading">Cargando servicios...</p>`;

        try {
            const servicios = await ServicioService.getAll();

            if (servicios.length === 0) {
                container.innerHTML = `<p class="empty">📭 No hay servicios disponibles</p>`;
                return;
            }

            container.innerHTML = `
                <div class="servicios-grid-inner">
                    ${servicios.map((servicio: Servicio) => `
                        <div class="servicio-card" data-id="${servicio.id}">
                            <h3>${servicio.nombre}</h3>
                            <p class="servicio-precio">💰 $${servicio.precio.toLocaleString()}</p>
                            <p class="servicio-duracion">⏱️ ${servicio.duracionMinutos} minutos</p>
                            ${servicio.descripcion ? `<p class="servicio-descripcion">${servicio.descripcion}</p>` : ''}
                            ${servicio.activo !== false ? '<span class="badge-activo">✅ Activo</span>' : '<span class="badge-inactivo">❌ Inactivo</span>'}
                            <button class="btn-reservar" data-id="${servicio.id}">📅 Reservar</button>
                            ${this.user?.rol === 'ADMIN' ? `
                                <button class="btn-editar-servicio" data-id="${servicio.id}">✏️ Editar</button>
                                <button class="btn-desactivar-servicio" data-id="${servicio.id}">🗑️ Desactivar</button>
                            ` : ''}
                        </div>
                    `).join('')}
                </div>
            `;

            // Evento para cada tarjeta
            document.querySelectorAll('.servicio-card').forEach((card) => {
                card.addEventListener('click', (e) => {
                    // Si el clic fue en el botón "Reservar", no abrir el modal dos veces
                    if ((e.target as HTMLElement).classList.contains('btn-reservar')) {
                        return;
                    }
                    const id = parseInt(card.getAttribute('data-id') || '0');
                    const servicio = servicios.find((s: any) => s.id === id);
                    if (servicio) {
                        new ReservaModal(servicio, () => {
                            console.log('Reserva creada exitosamente');
                        });
                    }
                });
            });

            // Evento para botones "Reservar"
            document.querySelectorAll('.btn-reservar').forEach((btn) => {
                btn.addEventListener('click', (e) => {
                    e.stopPropagation(); // Evita que el evento se propague a la tarjeta
                    const id = parseInt(btn.getAttribute('data-id') || '0');
                    const servicio = servicios.find((s: any) => s.id === id);
                    if (servicio) {
                        new ReservaModal(servicio, () => {
                            console.log('Reserva creada exitosamente');
                        });
                    }
                });
            });

            document.querySelectorAll('.btn-editar-servicio').forEach((btn) => {
                btn.addEventListener('click', (event) => {
                    event.stopPropagation();
                    const id = Number((event.currentTarget as HTMLElement).dataset.id);
                    const servicio = servicios.find(item => item.id === id);
                    if (servicio) this.cargarFormulario(servicio);
                });
            });

            document.querySelectorAll('.btn-desactivar-servicio').forEach((btn) => {
                btn.addEventListener('click', async (event) => {
                    event.stopPropagation();
                    const id = Number((event.currentTarget as HTMLElement).dataset.id);
                    if (!id || !confirm('¿Desactivar este servicio?')) return;
                    try {
                        await ServicioService.deactivate(id);
                        await this.loadServicios();
                    } catch (error) {
                        alert(error instanceof Error ? error.message : 'Error al desactivar el servicio');
                    }
                });
            });

        } catch (error) {
            container.innerHTML = `<p class="error">❌ Error al cargar los servicios</p>`;
        }
    }

    private cargarFormulario(servicio: Servicio): void {
        (document.getElementById('servicio-id') as HTMLInputElement).value = String(servicio.id);
        (document.getElementById('servicio-nombre') as HTMLInputElement).value = servicio.nombre;
        (document.getElementById('servicio-precio') as HTMLInputElement).value = String(servicio.precio);
        (document.getElementById('servicio-duracion') as HTMLInputElement).value = String(servicio.duracionMinutos);
        (document.getElementById('servicio-descripcion') as HTMLInputElement).value = servicio.descripcion || '';
        (document.getElementById('cancelar-edicion-servicio') as HTMLButtonElement).hidden = false;
    }

    private limpiarFormulario(): void {
        (document.getElementById('servicio-form') as HTMLFormElement)?.reset();
        (document.getElementById('servicio-id') as HTMLInputElement).value = '';
        (document.getElementById('cancelar-edicion-servicio') as HTMLButtonElement).hidden = true;
    }

    private async guardarServicio(): Promise<void> {
        const id = Number((document.getElementById('servicio-id') as HTMLInputElement).value);
        const data: ServicioRequest = {
            nombre: (document.getElementById('servicio-nombre') as HTMLInputElement).value.trim(),
            precio: Number((document.getElementById('servicio-precio') as HTMLInputElement).value),
            duracionMinutos: Number((document.getElementById('servicio-duracion') as HTMLInputElement).value),
            descripcion: (document.getElementById('servicio-descripcion') as HTMLInputElement).value.trim(),
        };

        try {
            if (id) await ServicioService.update(id, data);
            else await ServicioService.create(data);
            this.limpiarFormulario();
            await this.loadServicios();
        } catch (error) {
            alert(error instanceof Error ? error.message : 'Error al guardar el servicio');
        }
    }
}