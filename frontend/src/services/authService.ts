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

export interface UserSession {
    id?: number;
    username: string;
    rol: string;
    email?: string;
    nombreCompleto?: string;
}

export class AuthService {
    // ===== LOGIN =====
    static async login(credentials: LoginRequest): Promise<LoginResponse> {
        try {
            console.log('🔍 Enviando petición POST a:', `${API_BASE_URL}/auth/login`);
            console.log('📦 Datos:', { email: credentials.email, password: '***' });

            const response = await fetch(`${API_BASE_URL}/auth/login`, {
                method: 'POST',  // ← MÉTODO POST CORRECTO
                credentials: 'include',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    email: credentials.email,
                    password: credentials.password
                }),
            });

            console.log('📡 Status de respuesta:', response.status);

            // Intentar parsear la respuesta como JSON
            let data: any;
            const textResponse = await response.text();
            console.log('📄 Respuesta cruda:', textResponse);

            try {
                data = JSON.parse(textResponse);
            } catch (parseError) {
                console.error('❌ Error al parsear JSON:', parseError);
                throw new Error('Error en el formato de respuesta del servidor');
            }

            // Si la respuesta no es exitosa (HTTP error)
            if (!response.ok) {
                const mensaje = data.mensaje || data.message || 'Error al iniciar sesión';
                console.error('❌ Error HTTP:', response.status, mensaje);
                throw new Error(mensaje);
            }

            // Verificar que la respuesta tenga el formato esperado
            if (data.success === undefined) {
                console.warn('⚠️ Respuesta sin campo success, asumiendo éxito');
                return {
                    success: true,
                    mensaje: data.mensaje || 'Login exitoso',
                    username: data.username || '',
                    rol: data.rol || 'CLIENTE',
                    bloqueado: data.bloqueado || false,
                };
            }

            console.log('✅ Login exitoso:', data);
            return data;

        } catch (error: any) {
            console.error('❌ Error en login:', error);
            // Si el error es de red
            if (error.message === 'Failed to fetch' || error.message.includes('NetworkError')) {
                throw new Error('No se pudo conectar con el servidor. Verifica que el backend esté corriendo.');
            }
            throw error;
        }
    }

    // ===== HEALTH CHECK =====
    static async healthCheck(): Promise<boolean> {
        try {
            const response = await fetch(`${API_BASE_URL}/auth/health`);
            return response.ok;
        } catch {
            return false;
        }
    }

    // ===== OBTENER SESIÓN =====
    static getSession(): UserSession | null {
        try {
            const userData = localStorage.getItem('user');
            if (userData) {
                return JSON.parse(userData);
            }
        } catch (error) {
            console.error('❌ Error al leer sesión:', error);
            return null;
        }
        return null;
    }

    // ===== VERIFICAR AUTENTICACIÓN =====
    static isAuthenticated(): boolean {
        const session = this.getSession();
        return session !== null && session.username !== undefined && session.username !== '';
    }

    // ===== OBTENER ID DEL USUARIO =====
    static getUserId(): number | null {
        const session = this.getSession();
        return session?.id || null;
    }

    // ===== CERRAR SESIÓN =====
    static async logout(): Promise<void> {
        await fetch(`${API_BASE_URL}/auth/logout`, {
            method: 'POST',
            credentials: 'include',
        }).catch(() => undefined);
        localStorage.removeItem('user');
        console.log('👋 Sesión cerrada');
        window.location.href = '/login.html';
    }
}