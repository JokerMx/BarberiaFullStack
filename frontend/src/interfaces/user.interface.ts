export interface User {
    id: number;
    username: string;
    email: string;
    nombreCompleto: string;
    rol: string;
    activo: boolean;
    fechaCreacion: string;
    fechaActualizacion: string;
}

export interface RegisterRequest {
    username: string;
    email: string;
    nombreCompleto: string;
    password: string;
    rol: string;
}

export interface RegisterResponse {
    mensaje: string;
    usuario?: User;
}