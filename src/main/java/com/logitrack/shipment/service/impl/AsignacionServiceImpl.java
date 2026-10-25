
package com.logitrack.shipment.service.impl;

import java.util.UUID;

import com.logitrack.shipment.client.CourierClient;
import com.logitrack.shipment.dto.request.AsignacionRequest;
import com.logitrack.shipment.dto.response.AsignacionResponse;
import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.exception.RecursoNoEncontradoException;
import com.logitrack.shipment.exception.TransicionEstadoInvalidaException;
import com.logitrack.shipment.model.Envio;
import com.logitrack.shipment.repository.EnvioRepository;
import com.logitrack.shipment.service.interfaces.AsignacionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementa la asignación de repartidores a envíos.
 *
 * <p>Consulta la disponibilidad en courier-service y almacena únicamente
 * el UUID del repartidor, sin establecer relaciones JPA entre servicios.</p>
 *
 * @author dominox45
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsignacionServiceImpl implements AsignacionService {

    private final EnvioRepository envioRepository;
    private final CourierClient courierClient;

    /**
     * Asigna un repartidor disponible a un envío pendiente.
     *
     * @param envioId identificador UUID del envío
     * @param request datos de la asignación
     * @param authorizationHeader cabecera Authorization del administrador
     * @return datos de la asignación realizada
     * @throws RecursoNoEncontradoException si el envío no existe o el
     *         repartidor no existe en courier-service
     * @throws TransicionEstadoInvalidaException si el envío no admite
     *         asignación o el repartidor no está disponible
     */
    @Override
    @Transactional
    public AsignacionResponse asignarRepartidor(
            UUID envioId,
            AsignacionRequest request,
            String authorizationHeader) {

        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un envío con id " + envioId));

        if (envio.getEstado() != EstadoEnvio.PENDIENTE) {
            throw new TransicionEstadoInvalidaException(
                    "Solo se puede asignar un repartidor a un envío PENDIENTE");
        }

        if (envio.getRepartidorId() != null) {
            throw new TransicionEstadoInvalidaException(
                    "El envío " + envioId + " ya tiene un repartidor asignado");
        }

        UUID repartidorId = request.getRepartidorId();

        boolean disponible = courierClient.estaDisponible(
                repartidorId,
                authorizationHeader);

        if (!disponible) {
            throw new TransicionEstadoInvalidaException(
                    "El repartidor " + repartidorId + " no está disponible");
        }

        envio.asignarRepartidor(repartidorId);
        Envio envioActualizado = envioRepository.save(envio);

        log.info(
                "Repartidor {} asignado al envío {}",
                repartidorId,
                envioActualizado.getId());

        return AsignacionResponse.builder()
                .envioId(envioActualizado.getId())
                .codigoSeguimiento(envioActualizado.getCodigoSeguimiento())
                .repartidorId(repartidorId)
                .build();
    }
}
