// frontend/src/views/usuarios/usuariosView.ts

import { UserService, type User } from '../../services/userService';

export class UsuariosView {
    private container: HTMLElement;
    private user: any = null;
    private usuarios: User[] = [];

    constructor(container: HTMLElement) {
        this.container = container;
        this.loadUser();
        this.render();
        this.cargarUsuarios();
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

        // Solo ADMIN puede gestionar usuarios
        if (this.user?.rol !== 'ADMIN') {
            this.container.innerHTML = `
                <div class="dashboard-container">
                    <div class="error-container">
                        <h2>⛔ Acceso Denegado</h2>
                        <p>No tienes permisos para gestionar usuarios.</p>
                        <a href="/dashboard.html" class="btn-back">← Volver al Dashboard</a>
                    </div>
                </div>
            `;
            return;
        }
    }

    private render(): void {
        this.container.innerHTML = `
            <div class="dashboard-container">
                <header class="dashboard-header">
                    <div class="header-left">
                        <h1>👥 Gestionar Usuarios</h1>
                        <span class="badge-rol admin">ADMIN</span>
                    </div>
                    <div class="header-right">
                        <span class="user-name">👋 ${this.user?.username || 'Usuario'}</span>
                        <a href="/dashboard.html" class="btn-back">← Volver</a>
                        <button id="logout-btn" class="btn-logout">Cerrar Sesión</button>
                    </div>
                </header>

                <section class="usuarios-section">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 1.5rem; flex-wrap: wrap; gap: 0.5rem;">
                        <h2>📋 Lista de Usuarios</h2>
                        <div style="display: flex; gap: 0.5rem; flex-wrap: wrap;">
                            <button id="refresh-usuarios-btn" class="btn-refresh">🔄 Actualizar</button>
                            <button id="btn-nuevo-usuario" class="btn-primary">➕ Nuevo Usuario</button>
                        </div>
                    </div>

                    <div class="busqueda-container">
                        <input type="text" id="buscar-usuario" placeholder="🔍 Buscar por username o email..." />
                        <button id="btn-buscar" class="btn-buscar">Buscar</button>
                    </div>

                    <div id="usuarios-list" class="usuarios-list">
                        <p class="loading">Cargando usuarios...</p>
                    </div>
                </section>
            </div>
        `;

        // Eventos
        document.getElementById('logout-btn')?.addEventListener('click', () => {
            localStorage.removeItem('user');
            window.location.href = '/login.html';
        });

        document.getElementById('refresh-usuarios-btn')?.addEventListener('click', () => {
            this.cargarUsuarios();
        });

        document.getElementById('btn-nuevo-usuario')?.addEventListener('click', () => {
            window.location.href = '/register.html';
        });

        document.getElementById('btn-buscar')?.addEventListener('click', () => {
            this.buscarUsuario();
        });

        document.getElementById('buscar-usuario')?.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                this.buscarUsuario();
            }
        });
    }

    private async cargarUsuarios(): Promise<void> {
        const container = document.getElementById('usuarios-list');
        if (!container) return;

        container.innerHTML = `<p class="loading">Cargando usuarios...</p>`;

        try {
            this.usuarios = await UserService.listarUsuarios();
            this.renderUsuarios(this.usuarios);
        } catch (error) {
            container.innerHTML = `<p class="error">❌ Error al cargar los usuarios</p>`;
        }
    }

    private renderUsuarios(usuarios: User[]): void {
        const container = document.getElementById('usuarios-list');
        if (!container) return;

        if (usuarios.length === 0) {
            container.innerHTML = `<p class="empty">📭 No hay usuarios registrados</p>`;
            return;
        }

        container.innerHTML = `
            <div style="overflow-x: auto;">
                <table class="usuarios-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Usuario</th>
                            <th>Email</th>
                            <th>Nombre</th>
                            <th>Rol</th>
                            <th>Estado</th>
                            <th>Intentos</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${usuarios.map(usuario => `
                            <tr>
                                <td>${usuario.id}</td>
                                <td><strong>${usuario.username}</strong></td>
                                <td>${usuario.email}</td>
                                <td>${usuario.nombreCompleto}</td>
                                <td>
                                    <select class="rol-select" data-id="${usuario.id}">
                                        <option value="ADMIN" ${usuario.rol === 'ADMIN' ? 'selected' : ''}>ADMIN</option>
                                        <option value="BARBERO" ${usuario.rol === 'BARBERO' ? 'selected' : ''}>BARBERO</option>
                                        <option value="CLIENTE" ${usuario.rol === 'CLIENTE' ? 'selected' : ''}>CLIENTE</option>
                                    </select>
                                </td>
                                <td>
                                    <button class="estado-btn ${usuario.activo ? 'activo' : 'inactivo'}" data-id="${usuario.id}">
                                        ${usuario.activo ? '✅ Activo' : '❌ Inactivo'}
                                    </button>
                                </td>
                                <td>
                                    <span class="intentos-badge ${usuario.intentosFallidos >= 5 ? 'bloqueado' : ''}">
                                        ${usuario.intentosFallidos}/5
                                    </span>
                                    ${usuario.bloqueadoHasta ? `<span class="bloqueado-hasta">🔒 hasta ${new Date(usuario.bloqueadoHasta).toLocaleDateString()}</span>` : ''}
                                </td>
                                <td>
                                    <button class="btn-eliminar" data-id="${usuario.id}" ${usuario.id === 1 ? 'disabled' : ''}>
                                        🗑️
                                    </button>
                                </td>
                            </tr>
                        `).join('')}
                    </tbody>
                </table>
            </div>
        `;

        // Eventos para cambiar rol
        document.querySelectorAll('.rol-select').forEach((select) => {
            select.addEventListener('change', async (e) => {
                const target = e.target as HTMLSelectElement;
                const id = parseInt(target.dataset.id || '0');
                const nuevoRol = target.value;

                if (id && nuevoRol) {
                    try {
                        await UserService.cambiarRol(id, nuevoRol);
                        await this.cargarUsuarios();
                    } catch (error) {
                        alert('Error al cambiar el rol del usuario');
                        target.value = target.dataset.rolAnterior || 'CLIENTE';
                    }
                }
            });
        });

        // Eventos para cambiar estado (Activar/Desactivar)
        document.querySelectorAll('.estado-btn').forEach((btn) => {
            btn.addEventListener('click', async (e) => {
                const target = e.target as HTMLButtonElement;
                const id = parseInt(target.dataset.id || '0');
                const activo = target.classList.contains('activo');

                if (id) {
                    try {
                        await UserService.cambiarEstado(id, !activo);
                        await this.cargarUsuarios();
                    } catch (error) {
                        alert('Error al cambiar el estado del usuario');
                    }
                }
            });
        });

        // Eventos para eliminar usuario
        document.querySelectorAll('.btn-eliminar').forEach((btn) => {
            btn.addEventListener('click', async (e) => {
                const target = e.target as HTMLButtonElement;
                const id = parseInt(target.dataset.id || '0');

                if (id && confirm('¿Estás seguro de eliminar este usuario?')) {
                    try {
                        await UserService.eliminarUsuario(id);
                        await this.cargarUsuarios();
                    } catch (error) {
                        alert('Error al eliminar el usuario');
                    }
                }
            });
        });
    }

    private async buscarUsuario(): Promise<void> {
        const input = document.getElementById('buscar-usuario') as HTMLInputElement;
        const termino = input.value.trim();

        if (!termino) {
            await this.cargarUsuarios();
            return;
        }

        const container = document.getElementById('usuarios-list');
        if (!container) return;

        container.innerHTML = `<p class="loading">Buscando...</p>`;

        try {
            // Buscar por username o email
            let usuario: User | null = null;
            try {
                usuario = await UserService.buscarPorUsername(termino);
            } catch {
                try {
                    usuario = await UserService.buscarPorEmail(termino);
                } catch {
                    usuario = null;
                }
            }

            if (usuario) {
                this.renderUsuarios([usuario]);
            } else {
                container.innerHTML = `<p class="empty">🔍 No se encontró ningún usuario con "${termino}"</p>`;
            }
        } catch (error) {
            container.innerHTML = `<p class="error">❌ Error al buscar el usuario</p>`;
        }
    }
}