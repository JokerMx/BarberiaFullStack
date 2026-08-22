// frontend/src/interfaces/dashboard.interface.ts

export interface UserInfo {
    username: string;
    rol: string;
    email?: string;
    nombreCompleto?: string;
}

export interface DashboardStats {
    totalUsuarios: number;
    totalReservas: number;
    reservasHoy: number;
    serviciosActivos: number;
}

export interface ReservaReciente {
    id: number;
    codigo: string;
    cliente: string;
    servicio: string;
    fecha: string;
    hora: string;
    estado: 'PENDIENTE' | 'CONFIRMADA' | 'COMPLETADA' | 'CANCELADA';
}