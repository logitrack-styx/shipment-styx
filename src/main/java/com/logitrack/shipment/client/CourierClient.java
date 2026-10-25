
package com.logitrack.shipment.client;

import java.util.UUID;

/**
 * Define la comunicación entre shipment-service y courier-service.
 *
 * <p>Permite consultar la disponibilidad de un repartidor sin acceder
 * directamente a las entidades ni a la base de datos del otro microservicio.</p>
 *
 * <p>El token de autorización corresponde al administrador que solicita
 * la asignación. La implementación concreta deberá propagarlo mediante
 * la cabecera HTTP {@code Authorization}.</p>
 *
 * @author dominox45
 * @version 1.0
 * @see java.util.UUID
 */
public interface CourierClient {

    /**
     * Consulta si un repartidor está disponible para recibir una asignación.
     *
     * @param repartidorId identificador UUID del repartidor en courier-service
     * @param authorizationHeader valor de la cabecera de autorización Bearer
     * @return {@code true} si el repartidor está disponible; {@code false}
     *         si existe, pero no está disponible
     * @throws RuntimeException si courier-service no puede responder
     *         correctamente; la implementación deberá traducir los errores
     *         HTTP y de conectividad según el contrato de Sprint 2
     */
    boolean estaDisponible(UUID repartidorId, String authorizationHeader);
}
