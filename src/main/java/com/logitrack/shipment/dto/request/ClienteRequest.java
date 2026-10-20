package com.logitrack.shipment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Schema(description = "Datos requeridos para registrar un cliente")

/**
 * Datos necesarios para la creación/modificación de un cliente
 * @author TheMiggaCool
 */
public class ClienteRequest {

    /**
     * Nombre del cliente
     */
	@NotBlank(message = "El nombre del cliente es obligatorio.")
	@Schema(
        description = "Nombre completo del cliente",
        example = "Juan Pérez",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nombre;

    /**
     * Correo del cliente
     */
	@NotBlank(message = "El email del cliente es obligatorio.")
	@Email(message = "Se debe ingresar un email valido")
	@Schema(
        description = "Correo electrónico válido del cliente",
        example = "juan.perez@example.com",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    /**
     * Teléfono del cliente
     */
	@NotBlank(message = "El telefono del cliente es obligatorio.")
	@Schema(
        description = "Número de teléfono del cliente",
        example = "+5491123456789",
        requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String telefono;

}
