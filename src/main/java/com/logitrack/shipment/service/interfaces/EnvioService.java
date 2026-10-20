package com.logitrack.shipment.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.logitrack.shipment.dto.request.EnvioRequest;
import com.logitrack.shipment.dto.response.EnvioResponse;
import com.logitrack.shipment.enums.EstadoEnvio;

/**
 * Casos de uso del ciclo de vida de los envíos. La asignación de repartidores
 * pertenece a {@code AsignacionService} y no se incluye aquí.
 *
 * @author dominox45
 */
public interface EnvioService {

    /**
     * Registra un envío en estado PENDIENTE para un cliente existente.
     *
     * @param request datos del envío
     * @return el envío registrado
     */
    EnvioResponse registrar(EnvioRequest request);

    /**
     * Consulta un envío por su identificador.
     *
     * @param id identificador del envío
     * @return el envío encontrado
     */
    EnvioResponse obtenerPorId(UUID id);

    /**
     * Consulta un envío por su código de seguimiento.
     *
     * @param codigoSeguimiento código de seguimiento
     * @return el envío encontrado
     */
    EnvioResponse obtenerPorCodigoSeguimiento(UUID codigoSeguimiento);

    /**
     * Lista envíos, opcionalmente filtrados por estado.
     *
     * @param estado estado a filtrar; {@code null} devuelve todos
     * @return lista de envíos
     */
    List<EnvioResponse> listar(EstadoEnvio estado);

    /**
     * Cambia el estado de un envío respetando la máquina de estados del dominio.
     *
     * @param id          identificador del envío
     * @param nuevoEstado estado de destino
     * @return el envío actualizado
     */
    EnvioResponse cambiarEstado(UUID id, EstadoEnvio nuevoEstado);

    /**
     * Cancela un envío; solo es válido si está PENDIENTE.
     *
     * @param id identificador del envío
     */
    void cancelarEnvio(UUID id);
}