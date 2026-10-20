package com.logitrack.shipment.dto.request;

import jakarta.validation.constraints.NotBlank;
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
	@NotBlank(message = "El nombre del repartidor es obligatorio.")
    private String nombre;

    /**
     * Vehículo del repartidor
     */
	@NotBlank(message = "El tipo de vehiculo del repartidor es obligatorio.")
    private String tipoVehiculo;
}
