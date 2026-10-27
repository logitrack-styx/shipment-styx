package com.logitrack.shipment.service.interfaces;

import java.util.UUID;

import com.logitrack.shipment.dto.request.AsignacionRequest;
import com.logitrack.shipment.dto.response.AsignacionResponse;

/**
 * Define el caso de uso de asignación de repartidores a envíos.
 *
 * @author TheMiggaCool
 */
public interface AsignacionService {

    /**
     * Asigna un repartidor disponible a un envío pendiente.
     *
     * @param envioId identificador UUID del envío
     * @param request datos de la asignación
     * @param authorizationHeader cabecera Authorization del administrador
     * @return información del envío y del repartidor asignado
     */
    AsignacionResponse asignarRepartidor(
            UUID envioId,
            AsignacionRequest request,
            String authorizationHeader);
}