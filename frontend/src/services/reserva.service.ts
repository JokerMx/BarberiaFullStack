// frontend/src/services/reservaService.ts

import type { Servicio, ReservaRequest, ReservaResponse } from '../interfaces/reserva.interface';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

export class ReservaService {
    // ===== OBTENER SERVICIOS ACTIVOS =====
    static async getServicios(): Promise<Servicio[]> {
        const response = await fetch(`${API_BASE_URL}/servicios`);
        if (!response.ok) {
            throw new Error('Error al cargar los servicios');
        }
        return response.json();
    }

    // ===== CREAR RESERVA =====
    static async crearReserva(data: ReservaRequest): Promise<ReservaResponse> {
        const response = await fetch(`${API_BASE_URL}/reservas`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        });

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.mensaje || 'Error al crear la reserva');
        }

        return response.json();
    }

    // ===== OBTENER HORAS DISPONIBLES (simulación) =====
    static getHorasDisponibles(): string[] {
        const horas = [];
        for (let h = 9; h <= 20; h++) {
            for (let m = 0; m < 60; m += 30) {
                const horaStr = `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`;
                horas.push(horaStr);
            }
        }
        return horas;
    }

    // ===== VALIDAR FECHA =====
    static validarFecha(fecha: string): { valida: boolean; mensaje?: string } {
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);

        const fechaSeleccionada = new Date(fecha);
        fechaSeleccionada.setHours(0, 0, 0, 0);

        // 1. No puede ser anterior a hoy
        if (fechaSeleccionada < hoy) {
            return { valida: false, mensaje: 'No se puede seleccionar una fecha anterior a hoy' };
        }

        // 2. No puede ser más de 30 días en el futuro
        const limite = new Date(hoy);
        limite.setDate(limite.getDate() + 30);
        if (fechaSeleccionada > limite) {
            return { valida: false, mensaje: 'No se puede seleccionar una fecha con más de 30 días de anticipación' };
        }

        return { valida: true };
    }

    // ===== OBTENER FECHA MÍNIMA (hoy) =====
    static getFechaMinima(): string {
        const hoy = new Date();
        return hoy.toISOString().split('T')[0];
    }

    // ===== OBTENER FECHA MÁXIMA (hoy + 30 días) =====
    static getFechaMaxima(): string {
        const hoy = new Date();
        const limite = new Date(hoy);
        limite.setDate(limite.getDate() + 30);
        return limite.toISOString().split('T')[0];
    }
}