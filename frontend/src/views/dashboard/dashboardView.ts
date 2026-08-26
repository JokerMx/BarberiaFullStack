// frontend/src/views/dashboard/dashboardView.ts

import { DashboardService } from '../../services/dashboardService';
import { AuthService } from '../../services/authService';
import type { UserInfo, ReservaReciente } from '../../interfaces/dashboard.interface';
import { ReservaModal } from '../reserva/reservaModal';

export class DashboardView {
    private container: HTMLElement;
    private user: UserInfo | null = null;
    private refreshInterval: number | null = null;

    constructor(container: HTMLElement) {
        this.container = container;
        if (!this.loadUser()) return;
        this.render();
        this.bindEvents();
        this.loadData();

        this.refreshInterval = window.setInterval(() => {
            this.loadData(false);
        }, 30000);
    }

    private loadUser(): boolean {
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
            return false;
        }

        if (this.user.rol === 'CLIENTE') {
            window.location.href = '/servicios.html';
            return false;
        }

        return true;
    }

    private render(): void {
        const username = this.user?.username || 'Usuario';
        const rol = this.user?.rol || '';

        this.container.innerHTML = `
            <div class="dashboard-container">
                <header class="dashboard-header">
                    <div class="header-left">
                        <h1>🪒 Barbería</h1>
                        <span class="badge-rol ${rol.toLowerCase()}">${rol}</span>
                    </div>
                    <div class="header-right">
                        <span class="user-name">👋 ${username}</span>
                        ${rol === 'ADMIN' || rol === 'BARBERO' ? '<button id="btn-nueva-reserva-header" class="btn-primary">➕ Nueva Reserva</button>' : ''}
                        <button id="logout-btn" class="btn-logout">Cerrar Sesión</button>
                    </div>
                </header>

                <section class="stats-grid">
                    ${rol === 'ADMIN' ? `
                    <div class="stat-card">
                        <div class="stat-icon">👥</div>
                        <div class="stat-info">
                            <h3 id="total-usuarios">...</h3>
                            <p>Usuarios</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">📅</div>
                        <div class="stat-info">
                            <h3 id="total-reservas">...</h3>
                            <p>Reservas Totales</p>
                        </div>
                    </div>
                    ` : ''}
                    <div class="stat-card">
                        <div class="stat-icon">📆</div>
                        <div class="stat-info">
                            <h3 id="reservas-hoy">...</h3>
                            <p>Reservas Hoy</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">✂️</div>
                        <div class="stat-info">
                            <h3 id="servicios-activos">...</h3>
                            <p>Servicios</p>
                        </div>
                    </div>
                </section>

                <section class="reservas-section">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; flex-wrap: wrap; gap: 0.5rem;">
                        <h2>📋 Reservas Recientes</h2>
                        <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                            <input type="date" id="filtro-fecha-reserva" />
                            <select id="filtro-estado-reserva">
                                <option value="">Todos los estados</option>
                                <option value="PENDIENTE">Pendiente</option>
                                <option value="CONFIRMADA">Confirmada</option>
                                <option value="COMPLETADA">Completada</option>
                                <option value="CANCELADA">Cancelada</option>
                            </select>
                            <button id="refresh-reservas-btn" class="btn-refresh">🔄 Actualizar</button>
                        </div>
                    </div>
                    <div id="reservas-list" class="reservas-list">
                        <p class="loading">Cargando reservas...</p>
                    </div>
                </section>

                ${rol === 'ADMIN' || rol === 'BARBERO' ? `
                <aside class="acciones-sidebar" aria-label="Acciones rápidas">
                    <div class="acciones-sidebar-header">
                        <span class="acciones-sidebar-title">Acciones</span>
                        <button id="acciones-toggle" class="acciones-toggle" type="button" aria-label="Contraer menú" aria-expanded="true" aria-controls="acciones-sidebar-content">☰</button>
                    </div>
                    <div id="acciones-sidebar-content" class="acciones-sidebar-content">
                    <button class="accion-card" id="btn-nueva-reserva" title="Nueva reserva" aria-label="Nueva reserva">
                        <span class="accion-icon">📅</span>
                        <span>Nueva reserva</span>
                    </button>
                    <a href="/servicios.html" class="accion-card" title="Ver servicios" aria-label="Ver servicios">
                        <span class="accion-icon">✂️</span>
                        <span>Ver servicios</span>
                    </a>
                    ${rol === 'ADMIN' ? `
                        <a href="/usuarios.html" class="accion-card" title="Gestionar usuarios" aria-label="Gestionar usuarios">
                            <span class="accion-icon">👥</span>
                            <span>Gestionar usuarios</span>
                        </a>
                    ` : ''}
                    </div>
                </aside>
                ` : ''}
            </div>
        `;
    }

    private bindEvents(): void {
        // Logout
        document.getElementById('logout-btn')?.addEventListener('click', () => {
            if (this.refreshInterval) {
                clearInterval(this.refreshInterval);
            }
            void AuthService.logout();
        });

        // Refrescar reservas
        document.getElementById('refresh-reservas-btn')?.addEventListener('click', () => {
            this.loadReservas();
        });
        document.getElementById('filtro-fecha-reserva')?.addEventListener('change', () => this.loadReservas());
        document.getElementById('filtro-estado-reserva')?.addEventListener('change', () => this.loadReservas());

        const accionesSidebar = document.querySelector('.acciones-sidebar');
        const accionesToggle = document.getElementById('acciones-toggle') as HTMLButtonElement | null;
        if (accionesSidebar && accionesToggle) {
            accionesToggle.addEventListener('click', () => {
                const contraido = accionesSidebar.classList.toggle('collapsed');
                accionesToggle.setAttribute('aria-expanded', String(!contraido));
                accionesToggle.setAttribute('aria-label', contraido ? 'Expandir menú' : 'Contraer menú');
            });
        }

        if (this.user?.rol === 'ADMIN' || this.user?.rol === 'BARBERO') {
            // ===== NUEVA RESERVA (desde el header) =====
            document.getElementById('btn-nueva-reserva-header')?.addEventListener('click', () => {
                new ReservaModal(undefined, () => {
                    this.loadData(false);
                });
            });

            // ===== NUEVA RESERVA (desde acciones) =====
            document.getElementById('btn-nueva-reserva')?.addEventListener('click', () => {
                new ReservaModal(undefined, () => {
                    this.loadData(false);
                });
            });
        }
    }

    private async loadData(showLoading: boolean = true): Promise<void> {
        try {
            const stats = await DashboardService.getStats();
            this.updateStats(stats);
            await this.loadReservas(showLoading);
        } catch (error) {
            console.error('Error al cargar datos del dashboard:', error);
        }
    }

    private updateStats(stats: any): void {
        const elements = {
            'total-usuarios': stats.totalUsuarios,
            'total-reservas': stats.totalReservas,
            'reservas-hoy': stats.reservasHoy,
            'servicios-activos': stats.serviciosActivos,
        };

        Object.entries(elements).forEach(([id, value]) => {
            const el = document.getElementById(id);
            if (el) el.textContent = String(value);
        });
    }

    private async loadReservas(showLoading: boolean = true): Promise<void> {
        const container = document.getElementById('reservas-list');
        if (!container) return;

        if (showLoading) {
            container.innerHTML = `<p class="loading">Cargando reservas...</p>`;
        }

        try {
            const fecha = (document.getElementById('filtro-fecha-reserva') as HTMLInputElement)?.value;
            const estado = (document.getElementById('filtro-estado-reserva') as HTMLSelectElement)?.value;
            let reservas: ReservaReciente[];
            if (this.user?.rol === 'CLIENTE' && this.user.id) {
                reservas = await DashboardService.getReservasPorCliente(this.user.id);
            } else if (fecha) {
                reservas = await DashboardService.getReservasPorFecha(fecha);
            } else if (estado) {
                reservas = await DashboardService.getReservasPorEstado(estado);
            } else {
                reservas = await DashboardService.getReservasRecientes(10);
            }
            this.renderReservas(reservas);
        } catch (error) {
            container.innerHTML = `<p class="error">❌ Error al cargar las reservas</p>`;
        }
    }

    private renderReservas(reservas: ReservaReciente[]): void {
        const container = document.getElementById('reservas-list');
        if (!container) return;

        if (reservas.length === 0) {
            container.innerHTML = `<p class="empty">📭 No hay reservas recientes</p>`;
            return;
        }

        container.innerHTML = `
            <div style="overflow-x: auto;">
                <table class="reservas-table">
                    <thead>
                        <tr>
                            <th>Código</th>
                            <th>Cliente</th>
                            <th>Servicio</th>
                            <th>Fecha</th>
                            <th>Hora</th>
                            <th>Estado</th>
                            ${this.user?.rol === 'ADMIN' || this.user?.rol === 'BARBERO' ? '<th>Acciones</th>' : ''}
                        </tr>
                    </thead>
                    <tbody>
                        ${reservas.map(reserva => `
                            <tr>
                                <td><strong>${reserva.codigo}</strong></td>
                                <td>${reserva.cliente}</td>
                                <td>${reserva.servicio}</td>
                                <td>${reserva.fecha}</td>
                                <td>${reserva.hora}</td>
                                <td><span class="estado-badge ${reserva.estado.toLowerCase()}">${reserva.estado}</span></td>
                                ${this.user?.rol === 'ADMIN' || this.user?.rol === 'BARBERO' ? `
                                    <td>
                                        <select class="estado-select" data-id="${reserva.id}">
                                            <option value="PENDIENTE" ${reserva.estado === 'PENDIENTE' ? 'selected' : ''}>PENDIENTE</option>
                                            <option value="CONFIRMADA" ${reserva.estado === 'CONFIRMADA' ? 'selected' : ''}>CONFIRMADA</option>
                                            <option value="COMPLETADA" ${reserva.estado === 'COMPLETADA' ? 'selected' : ''}>COMPLETADA</option>
                                            <option value="CANCELADA" ${reserva.estado === 'CANCELADA' ? 'selected' : ''}>CANCELADA</option>
                                        </select>
                                    </td>
                                ` : ''}
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;

        if (this.user?.rol === 'ADMIN' || this.user?.rol === 'BARBERO') {
            document.querySelectorAll('.estado-select').forEach((select) => {
                select.addEventListener('change', async (e) => {
                    const target = e.target as HTMLSelectElement;
                    const id = parseInt(target.dataset.id || '0');
                    const nuevoEstado = target.value;
                    
                    if (id && nuevoEstado) {
                        try {
                            await DashboardService.actualizarEstadoReserva(id, nuevoEstado);
                            await this.loadReservas(false);
                        } catch (error) {
                            alert('Error al actualizar el estado de la reserva');
                        }
                    }
                });
            });
        }

        document.querySelectorAll('.btn-cancelar-reserva').forEach((button) => {
            button.addEventListener('click', async (event) => {
                const id = Number((event.currentTarget as HTMLElement).dataset.id);
                if (!id || !confirm('¿Cancelar esta reserva?')) return;
                try {
                    await DashboardService.cancelarReserva(id);
                    await this.loadReservas(false);
                } catch {
                    alert('Error al cancelar la reserva');
                }
            });
        });
    }

    public destroy(): void {
        if (this.refreshInterval) {
            clearInterval(this.refreshInterval);
        }
    }
}