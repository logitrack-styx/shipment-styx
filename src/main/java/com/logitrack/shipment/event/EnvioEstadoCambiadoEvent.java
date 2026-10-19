package com.logitrack.shipment.event;

import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.model.Envio;

/** Contrato entre el servicio de envíos y el listener de auditoría. */
public record EnvioEstadoCambiadoEvent(
        Envio envio, EstadoEnvio estadoAnterior, EstadoEnvio estadoNuevo) {

}