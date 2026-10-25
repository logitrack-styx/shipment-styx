package com.logitrack.shipment.dto.response;

import java.util.UUID;

import com.logitrack.shipment.dto.request.DireccionDto;
import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.model.Envio;

/**
 * DTO que representa la información de un envío expuesta por la API REST.
 *
 * <p>Desacopla la respuesta HTTP de la entidad JPA y de los modelos
 * pertenecientes a otros microservicios. Los clientes y repartidores se
 * identifican mediante UUID, sin exponer entidades relacionadas.</p>
 *
 * @param id identificador único del envío
 * @param codigoSeguimiento código público de seguimiento
 * @param estado estado actual del envío
 * @param clienteId identificador del cliente propietario
 * @param repartidorId identificador del repartidor asignado, o {@code null}
 * @param origen dirección de origen
 * @param destino dirección de destino
 * @param peso peso del paquete
 * @param ancho ancho del paquete
 * @param alto alto del paquete
 * @param largo largo del paquete
 * @author dominox45
 */
public record EnvioResponse(
        UUID id,
        UUID codigoSeguimiento,
        EstadoEnvio estado,
        UUID clienteId,
        UUID repartidorId,
        DireccionDto origen,
        DireccionDto destino,
        double peso,
        double ancho,
        double alto,
        double largo) {

    /**
     * Construye el DTO a partir de una entidad de envío.
     *
     * @param envio entidad que contiene los datos del envío
     * @return representación del envío para la API
     * @throws NullPointerException si {@code envio} es {@code null}
     */
    public static EnvioResponse from(Envio envio) {
        return new EnvioResponse(
                envio.getId(),
                envio.getCodigoSeguimiento(),
                envio.getEstado(),
                envio.getClienteId(),
                envio.getRepartidorId(),
                DireccionDto.from(envio.getOrigen()),
                DireccionDto.from(envio.getDestino()),
                envio.getPeso(),
                envio.getAncho(),
                envio.getAlto(),
                envio.getLargo());
    }
}