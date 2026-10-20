package com.logitrack.shipment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logitrack.shipment.dto.request.EnvioRequest;
import com.logitrack.shipment.dto.response.EnvioResponse;
import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.event.EnvioEstadoCambiadoEvent;
import com.logitrack.shipment.exception.RecursoNoEncontradoException;
import com.logitrack.shipment.model.Cliente;
import com.logitrack.shipment.model.Envio;
import com.logitrack.shipment.repository.ClienteRepository;
import com.logitrack.shipment.repository.EnvioRepository;
import com.logitrack.shipment.service.interfaces.EnvioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de los casos de uso de envíos.
 *
 * <p>Orquesta la búsqueda, la persistencia y la publicación de eventos; las
 * reglas de transición viven en {@link Envio}. Los eventos se publican dentro
 * de la transacción para que el historial se guarde o se revierta junto con
 * la operación.</p>
 *
 * @author dominox45
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository envioRepository;
    private final ClienteRepository clienteRepository;
    private final ApplicationEventPublisher eventPublisher;

    /** {@inheritDoc} */
    @Override
    public EnvioResponse registrar(EnvioRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado: " + request.clienteId()));

        Envio envio = envioRepository.save(new Envio(cliente,
                request.origen().aEntidad(), request.destino().aEntidad(),
                request.peso(), request.ancho(), request.alto(), request.largo()));

        eventPublisher.publishEvent(new EnvioEstadoCambiadoEvent(envio, null, envio.getEstado()));
        log.info("Envío registrado con código de seguimiento {}", envio.getCodigoSeguimiento());
        return EnvioResponse.from(envio);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public EnvioResponse obtenerPorId(UUID id) {
        return EnvioResponse.from(buscarPorId(id));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public EnvioResponse obtenerPorCodigoSeguimiento(UUID codigoSeguimiento) {
        return EnvioResponse.from(envioRepository.findByCodigoSeguimiento(codigoSeguimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Envío no encontrado con código de seguimiento: " + codigoSeguimiento)));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponse> listar(EstadoEnvio estado) {
        List<Envio> envios = (estado == null)
                ? envioRepository.findAll()
                : envioRepository.findByEstado(estado);
        return envios.stream().map(EnvioResponse::from).toList();
    }

    /** {@inheritDoc} */
    @Override
    public EnvioResponse cambiarEstado(UUID id, EstadoEnvio nuevoEstado) {
        Envio envio = buscarPorId(id);
        EstadoEnvio anterior = envio.getEstado();

        // El dominio valida; si lanza excepción, no se publica ningún evento.
        envio.cambiarEstado(nuevoEstado);
        envioRepository.save(envio);

        eventPublisher.publishEvent(new EnvioEstadoCambiadoEvent(envio, anterior, nuevoEstado));
        return EnvioResponse.from(envio);
    }

    /** {@inheritDoc} */
    @Override
    public void cancelarEnvio(UUID id) {
        Envio envio = buscarPorId(id);
        EstadoEnvio anterior = envio.getEstado();

        envio.cancelar();
        envioRepository.save(envio);

        eventPublisher.publishEvent(
                new EnvioEstadoCambiadoEvent(envio, anterior, EstadoEnvio.CANCELADO));
    }

    /**
     * Busca un envío por id o lanza la excepción de recurso inexistente.
     *
     * @param id identificador del envío
     * @return el envío encontrado
     */
    private Envio buscarPorId(UUID id) {
        return envioRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException("Envío no encontrado: " + id));
    }
}