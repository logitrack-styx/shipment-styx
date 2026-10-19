package com.logitrack.shipment.dto.request;

import com.logitrack.shipment.enums.EstadoEnvio;

import jakarta.validation.constraints.NotNull;

/**
 * Solicitud de cambio de estado de un envío.
 *
 * @param estado estado al que se quiere pasar el envío
 * @author dominox45
 */
public record CambioEstadoRequest(@NotNull EstadoEnvio estado) {
}