package com.logitrack.shipment.client;

import java.util.UUID;

/**
 * Define las operaciones que shipment-service necesita de
 * {@code courier-service}.
 *
 * <p>Esta abstracción evita acoplar la lógica de negocio al cliente HTTP
 * concreto y facilita simular la dependencia en pruebas unitarias.</p>
 */
public interface CourierClient {

    /**
     * Consulta si un repartidor está disponible para recibir una asignación.
     *
     * @param repartidorId identificador UUID del repartidor
     * @param authorizationHeader token de autorización que se reenvía
     *                             al servicio remoto
     * @return {@code true} si el servicio remoto confirma que está disponible;
     *         {@code false} si existe y no está disponible
     * @throws com.logitrack.shipment.exception.CourierIntegrationException
     *         si la respuesta remota no se puede procesar o se produce
     *         un error de integración
     */
    boolean estaDisponible(UUID repartidorId, String authorizationHeader);
}