package com.logitrack.shipment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Datos requeridos para registrar un repartidor")

/**
 * Datos requeridos para la creación/modificación de repartidores
 * @author TheMiggaCool
 */
public class RepartidorRequest {

    /**
     * Nombre del repartidor
     */
	@NotBlank(message = "El nombre del repartidor es obligatorio.")
	@Schema(
        description = "Nombre completo del repartidor",
        example = "Carlos Gómez",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombre;

    /**
     * Vehículo del repartidor
     */
	@NotBlank(message = "El tipo de vehiculo del repartidor es obligatorio.")
	@Schema(
        description = "Tipo de vehículo utilizado por el repartidor",
        example = "Motocicleta",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String tipoVehiculo;
}
