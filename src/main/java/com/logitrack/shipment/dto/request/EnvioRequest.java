package com.logitrack.shipment.dto.request;

import com.logitrack.shipment.model.Cliente;
import com.logitrack.shipment.model.Direccion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

/**
 * Datos que se piden a la hora de crear o modificar un envío
 * @author TheMiggaCool
 */
public class EnvioRequest {


    @NotNull
    @Positive
    private Float peso;

    @NotNull
    @Positive
    private Float ancho;

    @NotNull
    @Positive
    private Float alto;

    @NotNull
    @Positive
    private Float largo;

    @NotNull
    @Valid
    private Direccion origen;

    @NotNull
    @Valid
    private Direccion destino;

    @NotNull
    private ClienteRequest cliente;
}
