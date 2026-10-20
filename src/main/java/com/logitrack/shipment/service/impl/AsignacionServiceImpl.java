package com.logitrack.shipment.service.impl;

import java.util.UUID;

import com.logitrack.shipment.dto.request.AsignacionRequest;
import com.logitrack.shipment.dto.response.AsignacionResponse;
import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.enums.EstadoRepartidor;
import com.logitrack.shipment.exception.RecursoNoEncontradoException;
import com.logitrack.shipment.exception.TransicionEstadoInvalidaException;
import com.logitrack.shipment.model.Envio;
import com.logitrack.shipment.model.Repartidor;
import com.logitrack.shipment.repository.EnvioRepository;
import com.logitrack.shipment.repository.RepartidorRepository;
import com.logitrack.shipment.service.interfaces.AsignacionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del caso de uso de asignación (contrato 2.7 del plan de trabajo).
 *
 * <p>Reglas aplicadas, en orden:</p>
 * <ol>
 *   <li>El envío debe existir → si no, 404 ({@link RecursoNoEncontradoException}).</li>
 *   <li>El repartidor debe existir → si no, 404.</li>
 *   <li>El repartidor debe estar ACTIVO → si no, 409 ({@link TransicionEstadoInvalidaException}).</li>
 *   <li>El envío debe estar PENDIENTE → si no, 409.</li>
 *   <li>El envío no debe tener ya un repartidor asignado → si lo tiene, 409
 *       (una segunda asignación nunca sobrescribe la primera silenciosamente).</li>
 * </ol>
 *
 * <p>La asociación se persiste en la misma transacción y NO cambia el estado
 * del envío: la transición a EN_RUTA se hace, aparte, mediante
 * {@code EnvioService.cambiarEstado}, que a su vez valida en el dominio que
 * exista un repartidor asignado.</p>
 *
 * @author TheMiggaCool
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsignacionServiceImpl implements AsignacionService {

    private final EnvioRepository envioRepository;
    private final RepartidorRepository repartidorRepository;

    @Override
    @Transactional
    public AsignacionResponse asignarRepartidor(UUID envioId, AsignacionRequest request) {
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un envío con id " + envioId));

        Repartidor repartidor = repartidorRepository.findById(request.getRepartidorId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe un repartidor con id " + request.getRepartidorId()));

        if (repartidor.getEstadoRepartidor() != EstadoRepartidor.ACTIVO) {
            throw new TransicionEstadoInvalidaException(
                    "El repartidor " + repartidor.getId() + " no está ACTIVO");
        }

        if (envio.getEstado() != EstadoEnvio.PENDIENTE) {
            throw new TransicionEstadoInvalidaException(
                    "Solo se puede asignar un repartidor a un envío en estado PENDIENTE");
        }

        if (envio.getRepartidor() != null) {
            throw new TransicionEstadoInvalidaException(
                    "El envío " + envio.getId() + " ya tiene un repartidor asignado");
        }

        envio.asignarRepartidor(repartidor);
        Envio envioActualizado = envioRepository.save(envio);

        log.info("Repartidor {} asignado al envío {}", repartidor.getId(), envioActualizado.getId());

        return AsignacionResponse.builder()
                .envioId(envioActualizado.getId())
                .codigoSeguimiento(envioActualizado.getCodigoSeguimiento())
                .repartidorId(repartidor.getId())
                .repartidorNombre(repartidor.getNombre())
                .build();
    }
}
