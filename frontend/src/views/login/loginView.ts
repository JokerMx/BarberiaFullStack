// frontend/src/views/login/loginView.ts

import { AuthService } from '../../services/authService';

export class LoginView {
    private container: HTMLElement;

    constructor(container: HTMLElement) {
        this.container = container;
        this.render();
        this.bindEvents();
    }

    private render(): void {
        this.container.innerHTML = `
            <div class="login-container">
                <h1>🪒 Barbería</h1>
                <h2>Iniciar Sesión</h2>
                <form id="login-form">
                    <div class="form-group">
                        <label for="email">Correo Electrónico</label>
                        <input type="email" id="email" placeholder="ejemplo@correo.com" required />
                    </div>
                    <div class="form-group">
                        <label for="password">Contraseña</label>
                        <input type="password" id="password" placeholder="••••••••" required />
                    </div>
                    <div id="error-message" class="error hidden"></div>
                    <button type="submit">Iniciar Sesión</button>
                </form>
                <p class="register-link">
                    ¿No tienes cuenta? <a href="/register.html">Regístrate aquí</a>
                </p>
            </div>
        `;
    }

    private bindEvents(): void {
        const form = document.getElementById('login-form') as HTMLFormElement;
        const emailInput = document.getElementById('email') as HTMLInputElement;
        const passwordInput = document.getElementById('password') as HTMLInputElement;
        const errorMessage = document.getElementById('error-message') as HTMLDivElement;

        form.addEventListener('submit', async (event) => {
            event.preventDefault();
            const email = emailInput.value.trim();
            const password = passwordInput.value.trim();

            if (!email || !password) {
                this.showError('Por favor, completa todos los campos.', errorMessage);
                return;
            }

            try {
                const response = await AuthService.login({ email, password });
                if (response.success) {
                    localStorage.setItem('user', JSON.stringify({
                        username: response.username,
                        rol: response.rol,
                    }));
                    window.location.href = '/dashboard.html';
                }
            } catch (error: any) {
                this.showError(error.message || 'Credenciales incorrectas.', errorMessage);
            }
        });
    }

    private showError(message: string, element: HTMLElement): void {
        element.textContent = message;
        element.classList.remove('hidden');
    }
}