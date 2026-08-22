// frontend/src/services/authService.ts

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

export interface LoginRequest {
    email: string;
    password: string;
}

export interface LoginResponse {
    success: boolean;
    mensaje: string;
    username: string;
    rol: string;
    bloqueado: boolean;
}

export class AuthService {
    static async login(credentials: LoginRequest): Promise<LoginResponse> {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(credentials),
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.mensaje || 'Error al iniciar sesión');
        }

        return response.json();
    }

    static async healthCheck(): Promise<boolean> {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/health`);
            return response.ok;
        } catch {
            return false;
        }
    }
}