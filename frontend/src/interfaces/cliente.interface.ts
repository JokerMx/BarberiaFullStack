// frontend/src/interfaces/cliente.interface.ts
import { TipoCliente } from '../enums/tipos.enum';

export interface Cliente {
  id: number;
  nombre: string;
  telefono: string;
  email: string;
  tipo: TipoCliente;
}