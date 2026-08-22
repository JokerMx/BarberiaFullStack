-- Tabla de servicios de la barbería
CREATE TABLE IF NOT EXISTS servicios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL,
    duracion_minutos INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insertar servicios iniciales
INSERT INTO servicios (nombre, descripcion, precio, duracion_minutos) VALUES
    ('Corte de Cabello', 'Corte clásico o moderno según preferencia', 15000, 30),
    ('Barba', 'Perfilado y arreglo de barba', 8000, 20),
    ('Corte + Barba', 'Combo completo de corte y barba', 20000, 50),
    ('Tinte', 'Aplicación de tinte profesional', 25000, 60),
    ('Lavado de Cabello', 'Lavado con masaje capilar', 5000, 15);