package com.logitrack.shipment.dto.response;


import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa los datos de respuesta de un cliente.
 * <p>
 * Esta clase se utiliza para transferir la información de un cliente
 * hacia las capas que consumen el servicio, incluyendo su identificador,
 * nombre, correo electrónico y teléfono.
 * </p>
 *
 * <p>
 * Lombok genera automáticamente los métodos de acceso, los constructores
 * con y sin argumentos y el patrón Builder.
 * </p>
 *
 * @author NikoRKM
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de respuesta con la información de un cliente")
public class ClienteResponse{
	
	/**
     * Identificador único del cliente.
     */
	@Schema(
        description = "Identificador único del cliente",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
	private UUID id;
	
	/**
     * Nombre del cliente.
     */
	@Schema(
        description = "Nombre del cliente",
        example = "Juan Pérez"
    )
	private String nombre;
	
	/**
     * Dirección de correo electrónico del cliente.
     */
	@Schema(
        description = "Correo electrónico del cliente",
        example = "juan.perez@example.com"
    )
	private String email;
	
	/**
     * Número de teléfono del cliente.
     */
	@Schema(
        description = "Número de teléfono del cliente",
        example = "+5491123456789"
    )
	private String telefono;
	
}
