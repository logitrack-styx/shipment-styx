-- LogiTrack - shipment-service
-- La estructura de las tablas se administra exclusivamente mediante Flyway.
-- Migracion inicial: src/main/resources/db/migration/V1__create_shipment_schema.sql

CREATE DATABASE IF NOT EXISTS logitrack
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
