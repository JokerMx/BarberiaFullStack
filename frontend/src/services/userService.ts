import type { RegisterRequest, RegisterResponse } from '../interfaces/user.interface';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

export class UserService {
    static async register(data: RegisterRequest): Promise<RegisterResponse> {
        const response = await fetch(`${API_BASE_URL}/usuarios/registro`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ mensaje: 'Error al registrar usuario' }));
            throw new Error(errorData.mensaje || 'Error al registrar usuario');
        }

        const text = await response.text();
        return { mensaje: text };
    }
}