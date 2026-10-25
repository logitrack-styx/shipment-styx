
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
import com.logitrack.shipment.model.Envio;
import com.logitrack.shipment.repository.EnvioRepository;
import com.logitrack.shipment.service.interfaces.EnvioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementación de los casos de uso relacionados con la gestión de envíos.
 *
 * <p>Coordina la persistencia de envíos, las consultas y la publicación de
 * eventos para mantener el historial de estados. La lógica de transición
 * permanece encapsulada en {@link Envio}.</p>
 *
 * <p>Los clientes se identifican mediante UUID; este servicio no consulta
 * una entidad {@code Cliente} local.</p>
 *
 * @author dominox45
 * @version 2.0
 * @see Envio
 * @see EnvioService
 * @see EnvioRepository
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EnvioServiceImpl implements EnvioService{

    private final EnvioRepository envioRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Registra un envío nuevo en estado {@code PENDIENTE} y publica el evento
     * inicial para registrar su historial.
     *
     * @param request datos necesarios para registrar el envío
     * @return DTO con los datos del envío persistido
     */
    @Override
    public EnvioResponse registrar(EnvioRequest request) {
        Envio envio = new Envio(
                request.clienteId(),
                request.origen().aEntidad(),
                request.destino().aEntidad(),
                request.peso(),
                request.ancho(),
                request.alto(),
                request.largo());

        envio = envioRepository.save(envio);

        eventPublisher.publishEvent(
                new EnvioEstadoCambiadoEvent(envio, null, envio.getEstado()));

        log.info("Envío registrado con código de seguimiento {}",
                envio.getCodigoSeguimiento());

        return EnvioResponse.from(envio);
    }

    /**
     * Busca un envío por su identificador UUID.
     *
     * @param id identificador del envío
     * @return DTO con la información del envío
     * @throws RecursoNoEncontradoException si no existe el envío
     */
    @Override
    @Transactional(readOnly = true)
    public EnvioResponse obtenerPorId(UUID id) {
        return EnvioResponse.from(buscarPorId(id));
    }

    /**
     * Busca un envío por su código de seguimiento.
     *
     * @param codigoSeguimiento código de seguimiento del envío
     * @return DTO con la información del envío
     * @throws RecursoNoEncontradoException si no existe el envío
     */
    @Override
    @Transactional(readOnly = true)
    public EnvioResponse obtenerPorCodigoSeguimiento(UUID codigoSeguimiento) {
        Envio envio = envioRepository.findByCodigoSeguimiento(codigoSeguimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Envío no encontrado con código de seguimiento: "
                                + codigoSeguimiento));

        return EnvioResponse.from(envio);
    }

    /**
     * Obtiene los envíos registrados, opcionalmente filtrados por estado.
     *
     * @param estado estado por el cual filtrar; {@code null} indica que no
     *               se aplica filtro
     * @return lista de envíos que cumplen el criterio
     */
    @Override
    @Transactional(readOnly = true)
    public List<EnvioResponse> listar(EstadoEnvio estado) {
        List<Envio> envios = estado == null
                ? envioRepository.findAll()
                : envioRepository.findByEstado(estado);

        return envios.stream()
                .map(EnvioResponse::from)
                .toList();
    }

    /**
     * Cambia el estado de un envío y publica un evento para registrar
     * la transición en el historial.
     *
     * @param id identificador del envío
     * @param nuevoEstado estado de destino solicitado
     * @return DTO actualizado
     * @throws RecursoNoEncontradoException si el envío no existe
     */
    @Override
    public EnvioResponse cambiarEstado(UUID id, EstadoEnvio nuevoEstado) {
        Envio envio = buscarPorId(id);
        EstadoEnvio estadoAnterior = envio.getEstado();

        envio.cambiarEstado(nuevoEstado);
        envio = envioRepository.save(envio);

        eventPublisher.publishEvent(
                new EnvioEstadoCambiadoEvent(envio, estadoAnterior, envio.getEstado()));

        return EnvioResponse.from(envio);
    }

    /**
     * Cancela un envío únicamente si su estado permite la cancelación.
     *
     * @param id identificador del envío
     * @throws RecursoNoEncontradoException si el envío no existe
     */
    @Override
    public void cancelarEnvio(UUID id) {
        Envio envio = buscarPorId(id);
        EstadoEnvio estadoAnterior = envio.getEstado();

        envio.cancelar();
        envioRepository.save(envio);

        eventPublisher.publishEvent(
                new EnvioEstadoCambiadoEvent(envio, estadoAnterior, envio.getEstado()));
    }

    /**
     * Recupera la entidad de un envío por su identificador.
     *
     * @param id identificador del envío
     * @return entidad encontrada
     * @throws RecursoNoEncontradoException si no existe el envío
     */
    private Envio buscarPorId(UUID id) {
        return envioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Envío no encontrado: " + id));
    }
}
