// frontend/src/interfaces/reserva.interface.ts

export interface Servicio {
    id: number;
    nombre: string;
    descripcion?: string;
    precio: number;
    duracionMinutos: number;
    activo: boolean;
}

export interface ReservaRequest {
    clienteId: number;
    servicioId: number;
    fecha: string;       // formato: YYYY-MM-DD
    hora: string;        // formato: HH:mm
    notas?: string;
}

export interface ReservaResponse {
    id: number;
    codigo: string;
    clienteId: number;
    servicioId: number;
    fecha: string;
    hora: string;
    estado: string;
    notas?: string;
}