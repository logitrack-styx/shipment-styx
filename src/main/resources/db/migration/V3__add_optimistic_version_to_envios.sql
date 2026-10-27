-- Versión de entidad para detectar concurrencia.
-- Los registros existentes parten de 0.
ALTER TABLE envios
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;