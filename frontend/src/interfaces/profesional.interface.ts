// frontend/src/interfaces/profesional.interface.ts
import { Especialidad } from '../enums/tipos.enum';

export interface Profesional {
  id: number;
  nombre: string;
  especialidad: Especialidad;
  horario: Record<string, string>;
  activo: boolean;
}