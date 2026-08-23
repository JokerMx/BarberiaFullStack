// frontend/src/views/login/loginView.ts

import { AuthService } from '../../services/authService';
import { UserService } from '../../services/userService';

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
                        <label for="email">Usuario o correo electrónico</label>
                        <input type="text" id="email" placeholder="usuario o ejemplo@correo.com" required />
                    </div>
                    <div class="form-group">
                        <label for="password">Contraseña</label>
                        <input type="password" id="password" placeholder="••••••••" required />
                    </div>
                    <div id="error-message" class="error hidden"></div>
                    <div id="success-message" class="success hidden"></div>
                    <button type="submit" id="login-btn">Iniciar Sesión</button>
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
        const successMessage = document.getElementById('success-message') as HTMLDivElement;
        const submitBtn = document.getElementById('login-btn') as HTMLButtonElement;

        form.addEventListener('submit', async (event) => {
            event.preventDefault();

            const email = emailInput.value.trim();
            const password = passwordInput.value.trim();

            // Ocultar mensajes anteriores
            errorMessage.classList.add('hidden');
            errorMessage.textContent = '';
            successMessage.classList.add('hidden');
            successMessage.textContent = '';

            // Validaciones básicas
            if (!email || !password) {
                this.showError('Por favor, completa todos los campos.', errorMessage);
                return;
            }

            // Deshabilitar botón mientras se procesa
            submitBtn.disabled = true;
            submitBtn.textContent = '⏳ Iniciando sesión...';

            try {
                console.log('🔍 Intentando login con:', { email, password: '***' });

                // ===== PETICIÓN POST =====
                const response = await AuthService.login({ email, password });

                console.log('✅ Respuesta del servidor:', response);

                // Verificar que el login fue exitoso
                if (response.success) {
                    const usuario = await UserService.buscarPorUsername(response.username);

                    // Guardar datos del usuario en localStorage
                    const userData = {
                        id: usuario.id,
                        username: response.username,
                        rol: response.rol,
                        email: email,
                        nombreCompleto: usuario.nombreCompleto,
                    };
                    localStorage.setItem('user', JSON.stringify(userData));

                    console.log('📦 Usuario guardado en localStorage:', userData);

                    // Mostrar mensaje de éxito
                    successMessage.textContent = '✅ ¡Login exitoso! Redirigiendo...';
                    successMessage.classList.remove('hidden');

                    // Redirigir al dashboard después de 1 segundo
                    setTimeout(() => {
                        window.location.href = '/dashboard.html';
                    }, 1000);
                } else {
                    // Si success es false, mostrar mensaje de error
                    this.showError(response.mensaje || 'Credenciales incorrectas.', errorMessage);
                }
            } catch (error: any) {
                console.error('❌ Error en login:', error);
                this.showError(error.message || 'Error al iniciar sesión. Inténtalo de nuevo.', errorMessage);
            } finally {
                // Restaurar botón
                submitBtn.disabled = false;
                submitBtn.textContent = 'Iniciar Sesión';
            }
        });
    }

    private showError(message: string, element: HTMLElement): void {
        element.textContent = message;
        element.classList.remove('hidden');
        // Scroll al mensaje de error
        element.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

}