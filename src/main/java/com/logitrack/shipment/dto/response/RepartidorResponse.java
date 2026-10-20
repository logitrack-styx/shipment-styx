package com.logitrack.shipment.dto.response;

import java.util.UUID;

import com.logitrack.shipment.enums.EstadoRepartidor;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa los datos de respuesta de un repartidor.
 * <p>
 * Esta clase se utiliza para transferir la información de un repartidor
 * hacia las capas que consumen el servicio, incluyendo su identificador,
 * nombre, tipo de vehículo y estado actual.
 * </p>
 *
 * <p>
 * Lombok genera automáticamente los métodos de acceso, los constructores
 * con y sin argumentos y el patrón Builder.
 * </p>
 *
 * @author NikoRKM
 * @version 1.0
 * @see RepartidorService
 * @see EstadoRepartidor
 */
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Objeto de respuesta con la información de un repartidor")
public class RepartidorResponse {
	
	/**
     * Identificador único del repartidor.
     */
	@Schema(
        description = "Identificador único del repartidor",
        example = "550e8400-e29b-41d4-a716-446655440000"
    )
	private UUID id;
	
	/**
     * Nombre del repartidor.
     */
	@Schema(
        description = "Nombre del repartidor",
        example = "Carlos Gómez"
    )
	private String nombre;
	
	/**
     * Tipo de vehículo utilizado por el repartidor.
     */
	@Schema(
        description = "Tipo de vehículo utilizado por el repartidor",
        example = "Motocicleta"
    )
	private String tipoVehiculo;
	
	/**
     * Estado actual del repartidor dentro del sistema.
     *
     * @see EstadoRepartidor
     */
	@Schema(
        description = "Estado actual del repartidor",
        example = "ACTIVO"
    )
	private EstadoRepartidor estadoRepartidor;

}
