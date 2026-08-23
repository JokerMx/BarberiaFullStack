// frontend/src/services/servicio.service.ts
import { ApiService } from './api.service';
import type { Servicio, ServicioRequest } from '../interfaces/servicio.interface';

export class ServicioService {
  static async getAll(): Promise<Servicio[]> {
    return ApiService.fetch<Servicio[]>('/servicios');
  }

  static async getById(id: number): Promise<Servicio> {
    return ApiService.fetch<Servicio>(`/servicios/${id}`);
  }

  static async create(data: ServicioRequest): Promise<Servicio> {
    return ApiService.fetch<Servicio>('/servicios', {
      method: 'POST',
      body: JSON.stringify({ ...data, activo: data.activo ?? true }),
    });
  }

  static async update(id: number, data: ServicioRequest): Promise<Servicio> {
    return ApiService.fetch<Servicio>(`/servicios/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    });
  }

  static async deactivate(id: number): Promise<void> {
    await ApiService.fetch<void>(`/servicios/${id}`, { method: 'DELETE' });
  }
}