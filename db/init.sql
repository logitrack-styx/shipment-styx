-- =====================================================================
-- LogiTrack - shipment-service
-- Script inicial de base de datos (MySQL 8+)
-- Los UUID se guardan como BINARY(16), el mapeo por defecto de
-- Hibernate 6 en MySQL. Las fechas de auditoría son DATETIME(6).
-- =====================================================================

CREATE DATABASE IF NOT EXISTS logitrack
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE logitrack;

-- ---------------------------------------------------------------------
-- clientes
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clientes (
                                        id                 BINARY(16)   NOT NULL,
    created_date       DATETIME(6)  NOT NULL,
    last_modified_date DATETIME(6)  NOT NULL,
    nombre             VARCHAR(100) NOT NULL,
    email              VARCHAR(254) NOT NULL,
    telefono           VARCHAR(25)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_clientes_email UNIQUE (email)
    ) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- repartidores
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS repartidores (
                                            id                 BINARY(16)   NOT NULL,
    created_date       DATETIME(6)  NOT NULL,
    last_modified_date DATETIME(6)  NOT NULL,
    nombre             VARCHAR(100) NOT NULL,
    tipo_vehiculo      VARCHAR(30)  NOT NULL,
    estado_repartidor  VARCHAR(20)  NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT ck_repartidores_estado
    CHECK (estado_repartidor IN ('ACTIVO', 'INACTIVO'))
    ) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- envios
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS envios (
                                      id                    BINARY(16)   NOT NULL,
    created_date          DATETIME(6)  NOT NULL,
    last_modified_date    DATETIME(6)  NOT NULL,
    codigo_seguimiento    BINARY(16)   NOT NULL,
    peso                  DOUBLE       NOT NULL,
    ancho                 DOUBLE       NOT NULL,
    alto                  DOUBLE       NOT NULL,
    largo                 DOUBLE       NOT NULL,
    estado                VARCHAR(20)  NOT NULL,
    origen_calle          VARCHAR(255) NOT NULL,
    origen_numero         VARCHAR(255) NOT NULL,
    origen_ciudad         VARCHAR(255) NOT NULL,
    origen_codigo_postal  VARCHAR(255) NOT NULL,
    destino_calle         VARCHAR(255) NOT NULL,
    destino_numero        VARCHAR(255) NOT NULL,
    destino_ciudad        VARCHAR(255) NOT NULL,
    destino_codigo_postal VARCHAR(255) NOT NULL,
    cliente_id            BINARY(16)   NOT NULL,
    repartidor_id         BINARY(16)   NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_envios_codigo_seguimiento UNIQUE (codigo_seguimiento),
    CONSTRAINT ck_envios_estado
    CHECK (estado IN ('PENDIENTE', 'EN_RUTA', 'ENTREGADO', 'CANCELADO')),
    CONSTRAINT fk_envios_cliente
    FOREIGN KEY (cliente_id) REFERENCES clientes (id),
    CONSTRAINT fk_envios_repartidor
    FOREIGN KEY (repartidor_id) REFERENCES repartidores (id)
    ) ENGINE = InnoDB;

CREATE INDEX idx_envios_estado ON envios (estado);

-- ---------------------------------------------------------------------
-- historial_estados (auditoría de cambios de estado)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS historial_estados (
                                                 id              BINARY(16)  NOT NULL,
    envio           BINARY(16)  NOT NULL,
    estado_anterior VARCHAR(20) NULL,
    estado_nuevo    VARCHAR(20) NOT NULL,
    fecha_hora      DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_historial_envio
    FOREIGN KEY (envio) REFERENCES envios (id),
    CONSTRAINT ck_historial_estado_anterior
    CHECK (estado_anterior IS NULL OR estado_anterior IN
('PENDIENTE', 'EN_RUTA', 'ENTREGADO', 'CANCELADO')),
    CONSTRAINT ck_historial_estado_nuevo
    CHECK (estado_nuevo IN ('PENDIENTE', 'EN_RUTA', 'ENTREGADO', 'CANCELADO'))
    ) ENGINE = InnoDB;

CREATE INDEX idx_historial_envio_fecha ON historial_estados (envio, fecha_hora);