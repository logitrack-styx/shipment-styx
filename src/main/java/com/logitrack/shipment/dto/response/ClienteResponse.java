package com.logitrack.shipment.dto.response;


import java.util.UUID;

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
public class ClienteResponse {
	
	private UUID id;
	private String nombre;
	private String email;
	private String telefono;
	
}
