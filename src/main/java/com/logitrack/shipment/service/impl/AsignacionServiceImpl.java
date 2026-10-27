
package com.logitrack.shipment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

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

/**
 * Implementa la asignación de repartidores a envíos.
 *
 * Consulta la disponibilidad en courier-service y almacena únicamente
 * el UUID del repartidor, sin establecer relaciones JPA entre servicios.
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
     * La asignación definitiva se realiza mediante una actualización
     * condicional atómica para evitar asignaciones concurrentes.
     *
     * @param envioId identificador UUID del envío
     * @param request datos de la asignación
     * @param authorizationHeader cabecera Authorization reenviada a courier-service
     * @return datos de la asignación realizada
     */
    @Override
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

        int actualizadas = envioRepository.asignarRepartidorSiDisponible(
                envioId,
                repartidorId,
                EstadoEnvio.PENDIENTE);

        if (actualizadas != 1) {
            throw new TransicionEstadoInvalidaException(
                    "El envío cambió de estado o fue asignado por otra "
                            + "solicitud; vuelva a consultar");
        }

        Envio envioActualizado = envioRepository.findById(envioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un envío con id " + envioId));

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
