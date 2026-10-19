package com.logitrack.shipment.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.logitrack.shipment.dto.request.RepartidorRequest;
import com.logitrack.shipment.dto.response.RepartidorResponse;
import com.logitrack.shipment.enums.EstadoRepartidor;

public interface RepartidorService {

	public RepartidorResponse createRepartidor(RepartidorRequest repartidorRequest);
	
	public RepartidorResponse findRepartidorById(UUID id);
	
	public List<RepartidorResponse> findRepartidorByEstadoRepartidor(EstadoRepartidor estadoRepartidor);
	
}
