// frontend/src/services/dashboardService.ts

import type { DashboardStats, ReservaReciente } from '../interfaces/dashboard.interface';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

export class DashboardService {
    // ===== OBTENER ESTADÍSTICAS REALES =====
    static async getStats(): Promise<DashboardStats> {
        try {
            // Obtener usuarios
            const usuariosRes = await fetch(`${API_BASE_URL}/usuarios`, { credentials: 'include' });
            const usuarios = usuariosRes.ok ? await usuariosRes.json() : [];

            // Obtener reservas (cuando exista el endpoint)
            let reservas: any[] = [];
            try {
                const reservasRes = await fetch(`${API_BASE_URL}/reservas`, { credentials: 'include' });
                if (reservasRes.ok) {
                    reservas = await reservasRes.json();
                }
            } catch {
                // Si no existe el endpoint, usar datos vacíos
            }

            // Obtener servicios
            let servicios: any[] = [];
            try {
                const serviciosRes = await fetch(`${API_BASE_URL}/servicios`);
                if (serviciosRes.ok) {
                    servicios = await serviciosRes.json();
                }
            } catch {
                // Si no existe el endpoint, usar datos vacíos
            }

            // Calcular reservas de hoy
            const fechaActual = new Date();
            const hoy = `${fechaActual.getFullYear()}-${String(fechaActual.getMonth() + 1).padStart(2, '0')}-${String(fechaActual.getDate()).padStart(2, '0')}`;
            const reservasHoy = reservas.filter(r => r.fecha === hoy);

            return {
                totalUsuarios: usuarios.length || 0,
                totalReservas: reservas.length || 0,
                reservasHoy: reservasHoy.length || 0,
                serviciosActivos: servicios.filter(s => s.activo !== false).length || 0,
            };
        } catch (error) {
            console.error('Error al obtener estadísticas:', error);
            // Retornar datos vacíos en caso de error
            return {
                totalUsuarios: 0,
                totalReservas: 0,
                reservasHoy: 0,
                serviciosActivos: 0,
            };
        }
    }

    // ===== OBTENER RESERVAS RECIENTES (REALES) =====
    static async getReservasRecientes(limite: number = 5): Promise<ReservaReciente[]> {
        try {
            const response = await fetch(`${API_BASE_URL}/reservas`, { credentials: 'include' });
            if (!response.ok) {
                throw new Error('Error al cargar reservas');
            }
            const data = await response.json();
            return data.slice(0, limite).map((reserva: any) => ({
                id: reserva.id,
                codigo: reserva.codigo || `RES-${String(reserva.id).padStart(6, '0')}`,
                cliente: reserva.clienteNombre || `Cliente #${reserva.clienteId}`,
                servicio: reserva.servicioNombre || `Servicio #${reserva.servicioId}`,
                fecha: reserva.fecha,
                hora: reserva.hora,
                estado: reserva.estado || 'PENDIENTE',
            }));
        } catch (error) {
            console.warn('No se pudieron cargar reservas reales:', error);
            return [];
        }
    }

    static async getReservasPorCliente(clienteId: number): Promise<ReservaReciente[]> {
        return this.mapReservas(`/reservas/cliente/${clienteId}`);
    }

    static async getReservasPorFecha(fecha: string): Promise<ReservaReciente[]> {
        return this.mapReservas(`/reservas/fecha/${encodeURIComponent(fecha)}`);
    }

    static async getReservasPorEstado(estado: string): Promise<ReservaReciente[]> {
        return this.mapReservas(`/reservas/estado/${encodeURIComponent(estado)}`);
    }

    private static async mapReservas(endpoint: string): Promise<ReservaReciente[]> {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, { credentials: 'include' });
        if (!response.ok) throw new Error('Error al cargar reservas');
        const data = await response.json();
        return data.map((reserva: any) => ({
            id: reserva.id,
            codigo: reserva.codigo || `RES-${String(reserva.id).padStart(6, '0')}`,
            cliente: reserva.clienteNombre || `Cliente #${reserva.clienteId}`,
            servicio: reserva.servicioNombre || `Servicio #${reserva.servicioId}`,
            fecha: reserva.fecha,
            hora: reserva.hora,
            estado: reserva.estado || 'PENDIENTE',
        }));
    }

    static async cancelarReserva(id: number): Promise<void> {
        const response = await fetch(`${API_BASE_URL}/reservas/${id}`, { method: 'DELETE' });
        if (!response.ok) throw new Error('Error al cancelar la reserva');
    }

    // ===== OBTENER SERVICIOS ACTIVOS =====
    static async getServiciosActivos(): Promise<any[]> {
        try {
            const response = await fetch(`${API_BASE_URL}/servicios`);
            if (!response.ok) {
                throw new Error('Error al cargar servicios');
            }
            const data = await response.json();
            return data.filter((s: any) => s.activo !== false);
        } catch (error) {
            console.warn('No se pudieron cargar servicios reales:', error);
            return [];
        }
    }

    // ===== CREAR RESERVA =====
    static async crearReserva(data: any): Promise<any> {
        const response = await fetch(`${API_BASE_URL}/reservas`, {
            method: 'POST',
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || 'Error al crear la reserva');
        }

        return response.json();
    }

    // ===== ACTUALIZAR ESTADO DE RESERVA =====
    static async actualizarEstadoReserva(id: number, estado: string): Promise<any> {
        const response = await fetch(`${API_BASE_URL}/reservas/${id}/estado?estado=${estado}`, {
            method: 'PUT',
            credentials: 'include',
        });

        if (!response.ok) {
            if (response.status === 403) {
                throw new Error('No tienes permisos para actualizar el estado de una reserva');
            }
            throw new Error('Error al actualizar el estado de la reserva');
        }

        return response.json();
    }
}