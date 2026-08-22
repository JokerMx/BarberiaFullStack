import { UserService } from '../../services/userService';
import type { RegisterRequest } from '../../interfaces/user.interface';
import { Rol } from '../../enums/roles.enum';

export class RegisterView {
    private container: HTMLElement;
    private form: HTMLFormElement | null = null;

    constructor(container: HTMLElement) {
        this.container = container;
        this.render();
        this.bindEvents();
    }

    private render(): void {
        this.container.innerHTML = `
            <div class="register-container">
                <h1>🪒 Barbería</h1>
                <h2>Crear Cuenta</h2>
                <form id="register-form">
                    <div class="form-group">
                        <label for="nombreCompleto">Nombre Completo</label>
                        <input type="text" id="nombreCompleto" placeholder="Juan Pérez" required />
                    </div>
                    <div class="form-group">
                        <label for="username">Nombre de Usuario</label>
                        <input type="text" id="username" placeholder="juanperez" required />
                    </div>
                    <div class="form-group">
                        <label for="email">Correo Electrónico</label>
                        <input type="email" id="email" placeholder="juan@correo.com" required />
                    </div>
                    <div class="form-group">
                        <label for="password">Contraseña</label>
                        <input type="password" id="password" placeholder="••••••••" minlength="6" required />
                    </div>
                    <div class="form-group">
                        <label for="rol">Rol</label>
                        <select id="rol" required>
                            <option value="${Rol.CLIENTE}">Cliente</option>
                            <option value="${Rol.BARBERO}">Barbero</option>
                            <option value="${Rol.ADMIN}">Administrador</option>
                        </select>
                    </div>
                    <div id="register-error" class="error hidden"></div>
                    <div id="register-success" class="success hidden"></div>
                    <button type="submit">Registrarse</button>
                </form>
                <p class="login-link">
                    ¿Ya tienes cuenta? <a href="/login.html">Inicia sesión aquí</a>
                </p>
            </div>
        `;
    }

    private bindEvents(): void {
        this.form = document.getElementById('register-form') as HTMLFormElement;
        if (!this.form) return;

        this.form.addEventListener('submit', async (event) => {
            event.preventDefault();
            await this.handleSubmit();
        });
    }

    private async handleSubmit(): Promise<void> {
        const nombreCompleto = (document.getElementById('nombreCompleto') as HTMLInputElement).value.trim();
        const username = (document.getElementById('username') as HTMLInputElement).value.trim();
        const email = (document.getElementById('email') as HTMLInputElement).value.trim();
        const password = (document.getElementById('password') as HTMLInputElement).value;
        const rol = (document.getElementById('rol') as HTMLSelectElement).value;

        const errorElement = document.getElementById('register-error') as HTMLDivElement;
        const successElement = document.getElementById('register-success') as HTMLDivElement;

        // Ocultar mensajes anteriores
        errorElement.classList.add('hidden');
        successElement.classList.add('hidden');

        // Validaciones básicas
        if (!nombreCompleto || !username || !email || !password) {
            this.showError('Por favor, completa todos los campos.', errorElement);
            return;
        }

        if (password.length < 6) {
            this.showError('La contraseña debe tener al menos 6 caracteres.', errorElement);
            return;
        }

        if (!this.isValidEmail(email)) {
            this.showError('Por favor, ingresa un correo electrónico válido.', errorElement);
            return;
        }

        const data: RegisterRequest = {
            username,
            email,
            nombreCompleto,
            password,
            rol,
        };

        try {
            const response = await UserService.register(data);
            successElement.textContent = response.mensaje || 'Usuario registrado exitosamente.';
            successElement.classList.remove('hidden');
            this.form?.reset();

            // Redirigir al login después de 2 segundos
            setTimeout(() => {
                window.location.href = '/login.html';
            }, 2000);

        } catch (error: any) {
            this.showError(error.message || 'Error al registrar usuario.', errorElement);
        }
    }

    private showError(message: string, element: HTMLElement): void {
        element.textContent = message;
        element.classList.remove('hidden');
    }

    private isValidEmail(email: string): boolean {
        const emailRegex = /^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
        return emailRegex.test(email);
    }
}