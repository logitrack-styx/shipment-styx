
package com.logitrack.shipment.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * Representa el resultado de asignar un repartidor a un envío.
 *
 * @author TheMiggaCool
 */
@Getter
@Builder
public class AsignacionResponse {

    /** Identificador del envío. */
    private UUID envioId;

    /** Código de seguimiento del envío. */
    private UUID codigoSeguimiento;

    /** Identificador del repartidor asignado. */
    private UUID repartidorId;
}
