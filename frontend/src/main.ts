// frontend/src/main.ts

import './styles.css';
import { Router } from './router';

document.addEventListener('DOMContentLoaded', () => {
    const app = document.getElementById('app');
    if (!app) {
        console.error('❌ Elemento #app no encontrado');
        return;
    }

    // Inicializar el enrutador
    const router = new Router(app);
    router.init();
});