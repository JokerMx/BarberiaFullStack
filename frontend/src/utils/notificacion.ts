// frontend/src/utils/notificacion.ts
export type TipoNotificacion = 'exitosa' | 'error' | 'advertencia';

export function mostrarNotificacion(
  mensaje: string,
  tipo: TipoNotificacion = 'exitosa'
): void {
  const existente = document.querySelector('.notificacion-flotante');
  if (existente) {
    existente.remove();
  }

  const iconos: Record<TipoNotificacion, string> = {
    exitosa: '✅',
    error: '❌',
    advertencia: '⚠️'
  };

  const colores: Record<TipoNotificacion, string> = {
    exitosa: 'linear-gradient(135deg, #2ecc71, #27ae60)',
    error: 'linear-gradient(135deg, #e74c3c, #c0392b)',
    advertencia: 'linear-gradient(135deg, #f39c12, #e67e22)'
  };

  const notificacion = document.createElement('div');
  notificacion.className = 'notificacion-flotante';
  notificacion.style.cssText = `
    position: fixed;
    top: 80px;
    right: 20px;
    min-width: 340px;
    padding: 1rem 1.5rem;
    background: ${colores[tipo]};
    color: white;
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 255, 255, 0.2);
    z-index: 2000;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    font-weight: 500;
    font-size: 0.95rem;
    opacity: 0;
    transform: translateX(120%);
    transition: all 0.4s cubic-bezier(0.25, 0.8, 0.25, 1);
  `;

  notificacion.innerHTML = `
    <span style="font-size: 1.4rem; flex-shrink: 0;">${iconos[tipo]}</span>
    <span style="flex: 1;">${mensaje}</span>
    <button style="
      background: rgba(255, 255, 255, 0.2);
      border: none;
      color: white;
      font-size: 1.1rem;
      cursor: pointer;
      padding: 0.2rem 0.5rem;
      border-radius: 6px;
      transition: background 0.2s;
    " onmouseover="this.style.background='rgba(255,255,255,0.3)'" onmouseout="this.style.background='rgba(255,255,255,0.2)'">&times;</button>
  `;

  const closeBtn = notificacion.querySelector('button');
  closeBtn?.addEventListener('click', () => {
    notificacion.style.opacity = '0';
    notificacion.style.transform = 'translateX(120%)';
    setTimeout(() => notificacion.remove(), 400);
  });

  document.body.appendChild(notificacion);

  setTimeout(() => {
    notificacion.style.opacity = '1';
    notificacion.style.transform = 'translateX(0)';
  }, 10);

  const duracion = parseInt(import.meta.env.VITE_NOTIFICATION_DURATION || '5000', 10);

  setTimeout(() => {
    if (notificacion.parentNode) {
      notificacion.style.opacity = '0';
      notificacion.style.transform = 'translateX(120%)';
      setTimeout(() => notificacion.remove(), 400);
    }
  }, duracion);
}
