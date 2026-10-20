package com.logitrack.shipment.dto.response;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * Respuesta de la operación de asignación: identifica el envío y el
 * repartidor que quedó asignado (contrato 2.7).
 *
 * @author TheMiggaCool
 */
@Getter
@Builder
public class AsignacionResponse {

    private UUID envioId;
    private UUID codigoSeguimiento;
    private UUID repartidorId;
    private String repartidorNombre;
}