package com.logitrack.shipment.dto.request;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

/**
 * Datos necesarios para la creación/modificación de un cliente
 * @author TheMiggaCool
 */
public class ClienteRequest {

    /**
     * Nombre del cliente
     */
    private String nombre;

    /**
     * Correo del cliente
     */
    private String email;

    /**
     * Teléfono del cliente
     */
    private String telefono;

}
