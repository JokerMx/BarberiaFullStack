// frontend/src/services/servicio.service.ts
import { ApiService } from './api.service';
import type { Servicio } from '../interfaces/servicio.interface';

export class ServicioService {
  static async getAll(): Promise<Servicio[]> {
    const response = await ApiService.fetch<Servicio[]>('/servicios');
    return response.data;
  }

  static async getById(id: number): Promise<Servicio> {
    const response = await ApiService.fetch<Servicio>(`/servicios/${id}`);
    return response.data;
  }
}