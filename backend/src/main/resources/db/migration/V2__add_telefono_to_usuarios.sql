-- Agregar campo teléfono a la tabla usuarios
ALTER TABLE usuarios ADD COLUMN telefono VARCHAR(20);

-- Agregar índice para búsqueda por teléfono (opcional)
CREATE INDEX idx_usuarios_telefono ON usuarios(telefono);