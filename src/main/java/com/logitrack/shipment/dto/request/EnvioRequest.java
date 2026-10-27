
package com.logitrack.shipment.dto.request;

import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Datos necesarios para registrar un envío.
 *
 * clienteId es opcional en el DTO porque:
 * - CLIENTE: se utiliza la identidad obtenida del JWT.
 * - ADMIN: debe indicar el cliente para quien registra el envío.
 */
public record EnvioRequest(
        UUID clienteId,

        @NotNull @Valid DireccionDto origen,
        @NotNull @Valid DireccionDto destino,

        @NotNull @Positive Double peso,
        @NotNull @Positive Double ancho,
        @NotNull @Positive Double alto,
        @NotNull @Positive Double largo
) {

    /**
     * Constructor compatible con el formato anterior,
     * utilizado actualmente por los tests y otros llamadores.
     */
    public EnvioRequest(
            DireccionDto origen,
            DireccionDto destino,
            Double peso,
            Double ancho,
            Double alto,
            Double largo
    ) {
        this(null, origen, destino, peso, ancho, alto, largo);
    }
}
