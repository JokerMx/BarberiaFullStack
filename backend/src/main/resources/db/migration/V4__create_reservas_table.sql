-- Tabla de reservas
CREATE TABLE IF NOT EXISTS reservas (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    cliente_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    servicio_id BIGINT NOT NULL REFERENCES servicios(id) ON DELETE CASCADE,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    notas TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para búsquedas frecuentes
CREATE INDEX idx_reservas_cliente_id ON reservas(cliente_id);
CREATE INDEX idx_reservas_servicio_id ON reservas(servicio_id);
CREATE INDEX idx_reservas_fecha ON reservas(fecha);
CREATE INDEX idx_reservas_estado ON reservas(estado);