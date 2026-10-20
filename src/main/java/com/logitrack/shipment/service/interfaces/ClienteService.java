package com.logitrack.shipment.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.logitrack.shipment.dto.request.ClienteRequest;
import com.logitrack.shipment.dto.response.ClienteResponse;

public interface ClienteService {

	public ClienteResponse create(ClienteRequest clienteRequest);
	
	public List<ClienteResponse> findAll();
	
	public ClienteResponse findById(UUID id);
	
}
