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
            credentials: 'include',
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
    static getHorasDisponibles(fecha?: string): string[] {
        const horas = [];
        const hoy = this.getFechaLocal(new Date());
        const fechaSeleccionada = fecha ? this.parseFechaLocal(fecha) : null;
        const esHoy = fechaSeleccionada?.getTime() === hoy.getTime();
        const ahora = new Date();

        for (let h = 9; h <= 20; h++) {
            for (let m = 0; m < 60; m += 30) {
                const horaStr = `${String(h).padStart(2, '0')}:${String(m).padStart(2, '0')}`;
                if (!esHoy || h > ahora.getHours() || (h === ahora.getHours() && m >= ahora.getMinutes())) {
                    horas.push(horaStr);
                }
            }
        }
        return horas;
    }

    // ===== VALIDAR HORA =====
    static validarHora(fecha: string, hora: string): { valida: boolean; mensaje?: string } {
        const validacionFecha = this.validarFecha(fecha);
        if (!validacionFecha.valida) return validacionFecha;

        const hoy = this.getFechaLocal(new Date());
        const fechaSeleccionada = this.parseFechaLocal(fecha);
        const partesHora = hora.split(':').map(Number);
        if (!fechaSeleccionada || partesHora.length !== 2 || partesHora.some(Number.isNaN)) {
            return { valida: false, mensaje: 'La hora seleccionada no es válida' };
        }

        if (fechaSeleccionada.getTime() === hoy.getTime()) {
            const ahora = new Date();
            const horaSeleccionada = new Date();
            horaSeleccionada.setHours(partesHora[0], partesHora[1], 0, 0);
            if (horaSeleccionada < ahora) {
                return { valida: false, mensaje: 'No se puede reservar una hora que ya pasó' };
            }
        }

        return { valida: true };
    }

    // ===== VALIDAR FECHA =====
    static validarFecha(fecha: string): { valida: boolean; mensaje?: string } {
        const hoy = this.getFechaLocal(new Date());
        const fechaSeleccionada = this.parseFechaLocal(fecha);

        if (!fechaSeleccionada) {
            return { valida: false, mensaje: 'La fecha seleccionada no es válida' };
        }

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
        return this.formatFechaLocal(new Date());
    }

    // ===== OBTENER FECHA MÁXIMA (hoy + 30 días) =====
    static getFechaMaxima(): string {
        const hoy = new Date();
        const limite = new Date(hoy);
        limite.setDate(limite.getDate() + 30);
        return this.formatFechaLocal(limite);
    }

    private static parseFechaLocal(fecha: string): Date | null {
        const partes = fecha.split('-').map(Number);
        if (partes.length !== 3 || partes.some(Number.isNaN)) return null;

        const [anio, mes, dia] = partes;
        const resultado = new Date(anio, mes - 1, dia);
        resultado.setHours(0, 0, 0, 0);
        return resultado.getFullYear() === anio
            && resultado.getMonth() === mes - 1
            && resultado.getDate() === dia
            ? resultado
            : null;
    }

    private static getFechaLocal(fecha: Date): Date {
        return new Date(fecha.getFullYear(), fecha.getMonth(), fecha.getDate());
    }

    private static formatFechaLocal(fecha: Date): string {
        const anio = fecha.getFullYear();
        const mes = String(fecha.getMonth() + 1).padStart(2, '0');
        const dia = String(fecha.getDate()).padStart(2, '0');
        return `${anio}-${mes}-${dia}`;
    }
}