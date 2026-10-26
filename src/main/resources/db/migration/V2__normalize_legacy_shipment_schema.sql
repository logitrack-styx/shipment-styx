-- LogiTrack - shipment-service
-- V2: normalización del esquema heredado.
-- No elimina datos ni tablas.

-- Eliminar la FK heredada hacia clientes, si existe.
SET @sql_drop_fk_cliente = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE CONSTRAINT_SCHEMA = DATABASE()
          AND TABLE_NAME = 'envios'
          AND CONSTRAINT_NAME = 'FKc8kj7vm4uudhuwdu29ffo2fqg'
          AND CONSTRAINT_TYPE = 'FOREIGN KEY'
    ),
    'ALTER TABLE envios DROP FOREIGN KEY FKc8kj7vm4uudhuwdu29ffo2fqg',
    'SELECT 1'
);

PREPARE stmt_drop_fk_cliente FROM @sql_drop_fk_cliente;
EXECUTE stmt_drop_fk_cliente;
DEALLOCATE PREPARE stmt_drop_fk_cliente;

-- Eliminar la FK heredada hacia repartidores, si existe.
SET @sql_drop_fk_repartidor = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.TABLE_CONSTRAINTS
        WHERE CONSTRAINT_SCHEMA = DATABASE()
          AND TABLE_NAME = 'envios'
          AND CONSTRAINT_NAME = 'FKb5i2sginj39k7q7mdyw3vsp9j'
          AND CONSTRAINT_TYPE = 'FOREIGN KEY'
    ),
    'ALTER TABLE envios DROP FOREIGN KEY FKb5i2sginj39k7q7mdyw3vsp9j',
    'SELECT 1'
);

PREPARE stmt_drop_fk_repartidor FROM @sql_drop_fk_repartidor;
EXECUTE stmt_drop_fk_repartidor;
DEALLOCATE PREPARE stmt_drop_fk_repartidor;

-- Normalizar las colaciones de las tablas del servicio.
-- Se conservan las filas existentes.
ALTER TABLE envios
    CONVERT TO CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

ALTER TABLE historial_estados
    CONVERT TO CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Las direcciones son obligatorias según el modelo Envio.
ALTER TABLE envios
    MODIFY COLUMN origen_calle VARCHAR(255) NOT NULL,
    MODIFY COLUMN origen_numero VARCHAR(255) NOT NULL,
    MODIFY COLUMN origen_ciudad VARCHAR(255) NOT NULL,
    MODIFY COLUMN origen_codigo_postal VARCHAR(255) NOT NULL,
    MODIFY COLUMN destino_calle VARCHAR(255) NOT NULL,
    MODIFY COLUMN destino_numero VARCHAR(255) NOT NULL,
    MODIFY COLUMN destino_ciudad VARCHAR(255) NOT NULL,
    MODIFY COLUMN destino_codigo_postal VARCHAR(255) NOT NULL;

-- Crear el índice de estado solo si todavía no existe.
SET @sql_idx_envios_estado = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'envios'
          AND INDEX_NAME = 'idx_envios_estado'
    ),
    'SELECT 1',
    'CREATE INDEX idx_envios_estado ON envios (estado)'
);

PREPARE stmt_idx_envios_estado FROM @sql_idx_envios_estado;
EXECUTE stmt_idx_envios_estado;
DEALLOCATE PREPARE stmt_idx_envios_estado;

-- Crear el índice compuesto del historial solo si no existe.
SET @sql_idx_historial = IF(
    EXISTS (
        SELECT 1
        FROM information_schema.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'historial_estados'
          AND INDEX_NAME = 'idx_historial_envio_fecha'
    ),
    'SELECT 1',
    'CREATE INDEX idx_historial_envio_fecha
     ON historial_estados (envio, fecha_hora)'
);

PREPARE stmt_idx_historial FROM @sql_idx_historial;
EXECUTE stmt_idx_historial;
DEALLOCATE PREPARE stmt_idx_historial;
