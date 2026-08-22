// frontend/src/services/userService.ts

import type { RegisterRequest, RegisterResponse } from '../interfaces/user.interface';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

export interface User {
    id: number;
    username: string;
    email: string;
    nombreCompleto: string;
    rol: string;
    activo: boolean;
    fechaCreacion: string;
    fechaActualizacion: string;
    intentosFallidos: number;
    bloqueadoHasta: string | null;
}

export class UserService {
    // ===== REGISTRO =====
    static async register(data: RegisterRequest): Promise<RegisterResponse> {
        const response = await fetch(`${API_BASE_URL}/usuarios/registro`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({ mensaje: 'Error al registrar usuario' }));
            throw new Error(errorData.mensaje || 'Error al registrar usuario');
        }

        const text = await response.text();
        return { mensaje: text };
    }

    // ===== LISTAR TODOS LOS USUARIOS =====
    static async listarUsuarios(): Promise<User[]> {
        const response = await fetch(`${API_BASE_URL}/usuarios`);
        if (!response.ok) {
            throw new Error('Error al cargar usuarios');
        }
        return response.json();
    }

    // ===== BUSCAR POR USERNAME =====
    static async buscarPorUsername(username: string): Promise<User> {
        const response = await fetch(`${API_BASE_URL}/usuarios/username/${encodeURIComponent(username)}`);
        if (!response.ok) {
            throw new Error('Usuario no encontrado');
        }
        return response.json();
    }

    // ===== BUSCAR POR EMAIL =====
    static async buscarPorEmail(email: string): Promise<User> {
        const response = await fetch(`${API_BASE_URL}/usuarios/email/${encodeURIComponent(email)}`);
        if (!response.ok) {
            throw new Error('Usuario no encontrado');
        }
        return response.json();
    }

    // ===== ACTUALIZAR USUARIO =====
    static async actualizarUsuario(id: number, data: Partial<User>): Promise<User> {
        const response = await fetch(`${API_BASE_URL}/usuarios/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data),
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Error al actualizar usuario');
        }

        return response.json();
    }

    // ===== ELIMINAR USUARIO =====
    static async eliminarUsuario(id: number): Promise<void> {
        const response = await fetch(`${API_BASE_URL}/usuarios/${id}`, {
            method: 'DELETE',
        });

        if (!response.ok) {
            throw new Error('Error al eliminar usuario');
        }
    }

    // ===== CAMBIAR ESTADO (Activar/Desactivar) =====
    static async cambiarEstado(id: number, activo: boolean): Promise<User> {
        return this.actualizarUsuario(id, { activo });
    }

    // ===== CAMBIAR ROL =====
    static async cambiarRol(id: number, rol: string): Promise<User> {
        return this.actualizarUsuario(id, { rol });
    }
}