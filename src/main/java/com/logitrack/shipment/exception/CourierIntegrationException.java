package com.logitrack.shipment.exception;

import org.springframework.http.HttpStatus;

/**
 * Representa un error producido durante la comunicación con courier-service.
 *
 * <p>Conserva el estado HTTP que shipment-service debe devolver al cliente
 * según el contrato de integración entre los microservicios.</p>
 *
 * @author dominox45
 */
public class CourierIntegrationException extends RuntimeException {

    private final HttpStatus status;

    /**
     * Construye una excepción de integración con su estado HTTP asociado.
     *
     * @param status estado HTTP que debe devolver shipment-service
     * @param message descripción controlada del error
     */
    public CourierIntegrationException(
            HttpStatus status,
            String message) {
        super(message);
        this.status = status;
    }

    /**
     * Obtiene el estado HTTP asociado a la excepción.
     *
     * @return estado HTTP que debe utilizar el manejador global
     */
    public HttpStatus getStatus() {
        return status;
    }
}