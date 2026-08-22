// frontend/src/views/servicios/serviciosView.ts

import { DashboardService } from '../../services/dashboardService';
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
                    <div id="servicios-list" class="servicios-grid">
                        <p class="loading">Cargando servicios...</p>
                    </div>
                </section>
            </div>
        `;

        // Eventos
        document.getElementById('logout-btn')?.addEventListener('click', () => {
            localStorage.removeItem('user');
            window.location.href = '/login.html';
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
    }

    private async loadServicios(): Promise<void> {
        const container = document.getElementById('servicios-list');
        if (!container) return;

        container.innerHTML = `<p class="loading">Cargando servicios...</p>`;

        try {
            const servicios = await DashboardService.getServiciosActivos();

            if (servicios.length === 0) {
                container.innerHTML = `<p class="empty">📭 No hay servicios disponibles</p>`;
                return;
            }

            container.innerHTML = `
                <div class="servicios-grid-inner">
                    ${servicios.map((servicio: any) => `
                        <div class="servicio-card" data-id="${servicio.id}">
                            <h3>${servicio.nombre}</h3>
                            <p class="servicio-precio">💰 $${servicio.precio.toLocaleString()}</p>
                            <p class="servicio-duracion">⏱️ ${servicio.duracionMinutos} minutos</p>
                            ${servicio.descripcion ? `<p class="servicio-descripcion">${servicio.descripcion}</p>` : ''}
                            ${servicio.activo !== false ? '<span class="badge-activo">✅ Activo</span>' : '<span class="badge-inactivo">❌ Inactivo</span>'}
                            <button class="btn-reservar" data-id="${servicio.id}">📅 Reservar</button>
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

        } catch (error) {
            container.innerHTML = `<p class="error">❌ Error al cargar los servicios</p>`;
        }
    }
}