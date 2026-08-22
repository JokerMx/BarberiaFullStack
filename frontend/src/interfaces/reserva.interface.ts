// frontend/src/interfaces/reserva.interface.ts
import { EstadoReserva } from '../enums/estados.enum';

export interface Reserva {
  id: number;
  cliente_id: number;
  servicio_id: number;
  profesional_id: number;
  fecha: Date;
  hora: string;
  estado: EstadoReserva;
  created_at: Date;
  updated_at: Date;
  cliente_nombre?: string;
  servicio_nombre?: string;
  profesional_nombre?: string;
}

export interface CrearReservaDTO {
  nombre_cliente: string;
  email: string;
  telefono: string;
  servicio_id: number;
  profesional_id: number;
  fecha: string;
  hora: string;
}
