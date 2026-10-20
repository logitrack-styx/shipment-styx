package com.logitrack.shipment.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos necesarios para asignar un repartidor a un envío (contrato 2.7).
 *
 * @author TheMiggaCool
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionRequest {

    /** Id del repartidor a asignar. Debe existir y estar ACTIVO. */
    @NotNull
    private UUID repartidorId;
}
