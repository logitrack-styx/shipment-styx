-- LogiTrack - shipment-service
-- V1: esquema inicial de envios e historial de estados.

CREATE TABLE envios (
    id                    BINARY(16)  NOT NULL,
    created_date          DATETIME(6) NOT NULL,
    last_modified_date    DATETIME(6) NOT NULL,
    codigo_seguimiento    BINARY(16)  NOT NULL,
    peso                  DOUBLE      NOT NULL,
    ancho                 DOUBLE      NOT NULL,
    alto                  DOUBLE      NOT NULL,
    largo                 DOUBLE      NOT NULL,
    estado ENUM(
        'CANCELADO', 'ENTREGADO', 'EN_RUTA', 'PENDIENTE'
    ) NOT NULL,
    origen_calle          VARCHAR(255) NOT NULL,
    origen_numero         VARCHAR(255) NOT NULL,
    origen_ciudad         VARCHAR(255) NOT NULL,
    origen_codigo_postal  VARCHAR(255) NOT NULL,
    destino_calle         VARCHAR(255) NOT NULL,
    destino_numero        VARCHAR(255) NOT NULL,
    destino_ciudad        VARCHAR(255) NOT NULL,
    destino_codigo_postal VARCHAR(255) NOT NULL,
    cliente_id            BINARY(16) NOT NULL,
    repartidor_id         BINARY(16) NULL,
    CONSTRAINT pk_envios PRIMARY KEY (id),
    CONSTRAINT uk_envios_codigo_seguimiento
        UNIQUE (codigo_seguimiento)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_envios_estado ON envios (estado);

CREATE TABLE historial_estados (
    id              BINARY(16)  NOT NULL,
    envio           BINARY(16)  NOT NULL,
    estado_anterior ENUM(
        'CANCELADO', 'ENTREGADO', 'EN_RUTA', 'PENDIENTE'
    ) NULL,
    estado_nuevo ENUM(
        'CANCELADO', 'ENTREGADO', 'EN_RUTA', 'PENDIENTE'
    ) NOT NULL,
    fecha_hora      DATETIME(6) NOT NULL,
    CONSTRAINT pk_historial_estados PRIMARY KEY (id),
    CONSTRAINT fk_historial_envio
        FOREIGN KEY (envio) REFERENCES envios (id)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE INDEX idx_historial_envio_fecha
    ON historial_estados (envio, fecha_hora);
