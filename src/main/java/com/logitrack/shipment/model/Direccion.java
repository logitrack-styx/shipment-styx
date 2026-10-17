package com.logitrack.shipment.model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Direccion {

	private String calle;
	private String numero;
	private String ciudad;
	private String codigoPostal;
	
}
