package com.logitrack.shipment.dto.response;

import java.util.UUID;

import com.logitrack.shipment.enums.EstadoRepartidor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RepartidorResponse {
	
	private UUID id;
	private String nombre;
	private String tipoVehiculo;
	private EstadoRepartidor estadoRepartidor;

}
