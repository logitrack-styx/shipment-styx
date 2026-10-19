package com.logitrack.shipment.service.interfaces;

import com.logitrack.shipment.dto.request.EnvioRequest;
import com.logitrack.shipment.dto.request.RepartidorRequest;
import com.logitrack.shipment.dto.response.EnvioResponse;

/**
 * Interface con la firma de los métodos para Envio
 * @author TheMiggaCool
 */
public interface EnvioService {

    /**
     * Crear envio
     */
    EnvioResponse crearEnvio(EnvioRequest envio);

    /**
     * Cambiar el estado del envío
     */
    EnvioResponse actualizarEstado(EnvioRequest envio);

    /**
     * Cancelar envío
     */
    EnvioResponse cancelarEnvio(EnvioRequest envio);

    /**
     * Asignar repartidor a envío
     */
    EnvioResponse asignarRepartidor(EnvioRequest envio, RepartidorRequest repartidor);

}
