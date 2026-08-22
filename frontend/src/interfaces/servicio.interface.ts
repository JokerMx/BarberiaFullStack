// frontend/src/interfaces/servicio.interface.ts
import { CategoriaServicio } from '../enums/categorias.enum';

export interface Servicio {
  id: number;
  nombre: string;
  descripcion: string;
  precio: number;
  duracion: number;
  categoria: CategoriaServicio;
  activo: boolean;
  created_at: Date;
  updated_at: Date;
}