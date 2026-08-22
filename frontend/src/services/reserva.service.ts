// frontend/src/services/reserva.service.ts
import { ApiService } from './api.service';
import type { Reserva, CrearReservaDTO } from '../interfaces/reserva.interface';

export class ReservaService {
  static async getAll(): Promise<Reserva[]> {
    const response = await ApiService.fetch<Reserva[]>('/reservas');
    return response.data;
  }

  static async create(data: CrearReservaDTO): Promise<Reserva> {
    const response = await ApiService.fetch<Reserva>('/reservas', {
      method: 'POST',
      body: JSON.stringify(data)
    });
    return response.data;
  }

  static async updateEstado(id: number, estado: string): Promise<void> {
    await ApiService.fetch(`/reservas/${id}/estado`, {
      method: 'PATCH',
      body: JSON.stringify({ estado })
    });
  }
}