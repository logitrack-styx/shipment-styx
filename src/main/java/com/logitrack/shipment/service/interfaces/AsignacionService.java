package com.logitrack.shipment.service.interfaces;

import java.util.UUID;

import com.logitrack.shipment.dto.request.AsignacionRequest;
import com.logitrack.shipment.dto.response.AsignacionResponse;

/**
 * Caso de uso de asignación de repartidores a envíos (contrato 2.7).
 *
 * <p>No forma parte de {@link EnvioService}: la asignación no cambia el
 * estado del envío, solo persiste la relación envío–repartidor.</p>
 *
 * @author TheMiggaCool
 */
public interface AsignacionService {

    /**
     * Asigna un repartidor ACTIVO a un envío PENDIENTE.
     *
     * @param envioId id del envío
     * @param request datos de la asignación (id del repartidor)
     * @return datos de la asignación resultante
     */
    AsignacionResponse asignarRepartidor(UUID envioId, AsignacionRequest request);
}
