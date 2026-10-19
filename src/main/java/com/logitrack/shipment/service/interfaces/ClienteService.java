package com.logitrack.shipment.service.interfaces;

import java.util.UUID;

import com.logitrack.shipment.dto.request.ClienteRequest;
import com.logitrack.shipment.dto.response.ClienteResponse;

public interface ClienteService {

	public ClienteResponse createCliente(ClienteRequest clienteRequest);
	
	public ClienteResponse findClienteById(UUID id);
	
}
