package com.logitrack.shipment.dto.response;

import java.util.UUID;

import com.logitrack.shipment.dto.request.DireccionDto;
import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.model.Envio;

/**
 * Representación de un envío expuesta por la API. No contiene entidades
 * ni colecciones JPA.
 *
 * @param id                identificador del envío
 * @param codigoSeguimiento código de seguimiento público
 * @param estado            estado actual
 * @param clienteId         id del cliente
 * @param clienteNombre     nombre del cliente
 * @param origen            dirección de origen
 * @param destino           dirección de destino
 * @param peso              peso del paquete
 * @param ancho             ancho del paquete
 * @param alto              alto del paquete
 * @param largo             largo del paquete
 * @author dominox45
 */
public record EnvioResponse(
        UUID id,
        UUID codigoSeguimiento,
        EstadoEnvio estado,
        UUID clienteId,
        String clienteNombre,
        DireccionDto origen,
        DireccionDto destino,
        double peso,
        double ancho,
        double alto,
        double largo) {

    /**
     * Construye la respuesta a partir de la entidad. Debe invocarse dentro
     * de la transacción, porque accede al cliente (carga perezosa).
     *
     * @param envio envío persistido
     * @return DTO de respuesta
     */
    public static EnvioResponse from(Envio envio) {
        return new EnvioResponse(envio.getId(), envio.getCodigoSeguimiento(), envio.getEstado(),
                envio.getCliente().getId(), envio.getCliente().getNombre(),
                DireccionDto.from(envio.getOrigen()), DireccionDto.from(envio.getDestino()),
                envio.getPeso(), envio.getAncho(), envio.getAlto(), envio.getLargo());
    }
}