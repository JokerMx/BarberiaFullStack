// frontend/src/interfaces/servicio.interface.ts
export interface Servicio {
  id: number;
  nombre: string;
  descripcion?: string;
  precio: number;
  duracionMinutos: number;
  activo: boolean;
  fechaCreacion?: string;
  fechaActualizacion?: string;
}

export type ServicioRequest = Pick<Servicio, 'nombre' | 'descripcion' | 'precio' | 'duracionMinutos'> & {
  activo?: boolean;
};