// frontend/src/enums/estados.enum.ts
export enum EstadoReserva {
  PENDIENTE = 'PENDIENTE',
  CONFIRMADA = 'CONFIRMADA',
  EN_PROGRESO = 'EN_PROGRESO',
  COMPLETADA = 'COMPLETADA',
  CANCELADA = 'CANCELADA'
}

export enum EstadoCarga {
  INICIAL = 'INICIAL',
  CARGANDO = 'CARGANDO',
  EXITO = 'EXITO',
  ERROR = 'ERROR',
  VACIO = 'VACIO'
}