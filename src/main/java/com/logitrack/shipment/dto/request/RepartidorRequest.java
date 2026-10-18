package com.logitrack.shipment.dto.request;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

/**
 * Datos requeridos para la creación/modificación de repartidores
 * @author TheMiggaCool
 */
public class RepartidorRequest {

    /**
     * Nombre del repartidor
     */
    private String nombre;

    /**
     * Vehículo del repartidor
     */
    private String tipoVehiculo;
}
