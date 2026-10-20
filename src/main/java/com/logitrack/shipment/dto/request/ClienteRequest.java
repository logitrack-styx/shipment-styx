package com.logitrack.shipment.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
	@NotBlank(message = "El nombre del cliente es obligatorio.")
    private String nombre;

    /**
     * Correo del cliente
     */
	@NotBlank(message = "El email del cliente es obligatorio.")
	@Email(message = "Se debe ingresar un email valido")
    private String email;

    /**
     * Teléfono del cliente
     */
	@NotBlank(message = "El telefono del cliente es obligatorio.")
    private String telefono;

}
