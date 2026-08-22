// frontend/src/router.ts

import { LoginView } from './views/login/loginView';
import { RegisterView } from './views/register/registerView';
import { DashboardView } from './views/dashboard/dashboardView';
import { ServiciosView } from './views/servicios/serviciosView';
import { UsuariosView } from './views/usuarios/usuariosView';

export class Router {
    private app: HTMLElement;
    private routes: Record<string, any> = {
        '/login.html': LoginView,
        '/register.html': RegisterView,
        '/dashboard.html': DashboardView,
        '/servicios.html': ServiciosView,
        '/usuarios.html': UsuariosView,
        '/': LoginView,
    };
    constructor(app: HTMLElement) {
        this.app = app;
    }

    init(): void {
        window.addEventListener('popstate', () => this.handleRoute());
        document.addEventListener('click', (e) => {
            const target = e.target as HTMLAnchorElement;
            if (target.tagName === 'A' && target.href.startsWith(window.location.origin)) {
                e.preventDefault();
                const path = target.pathname;
                window.history.pushState({}, '', path);
                this.handleRoute();
            }
        });
        this.handleRoute();
    }

    private handleRoute(): void {
        const path = window.location.pathname || '/';
        const ViewClass = this.routes[path] || this.routes['/'];

        if (!ViewClass) {
            console.warn(`⚠️ Ruta no encontrada: ${path}, redirigiendo a /`);
            window.location.href = '/';
            return;
        }

        this.app.innerHTML = '';
        new ViewClass(this.app);
    }
}