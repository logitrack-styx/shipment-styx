package com.logitrack.shipment.dto.request;

import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Datos necesarios para registrar un envío. Referencia a un cliente ya
 * existente mediante su id; no permite crear clientes implícitamente.
 *
 * @param clienteId id del cliente que solicita el envío
 * @param origen    dirección de origen
 * @param destino   dirección de destino
 * @param peso      peso del paquete
 * @param ancho     ancho del paquete
 * @param alto      alto del paquete
 * @param largo     largo del paquete
 * @author dominox45
 */
public record EnvioRequest(
        @NotNull UUID clienteId,
        @NotNull @Valid DireccionDto origen,
        @NotNull @Valid DireccionDto destino,
        @NotNull @Positive Double peso,
        @NotNull @Positive Double ancho,
        @NotNull @Positive Double alto,
        @NotNull @Positive Double largo) {
}