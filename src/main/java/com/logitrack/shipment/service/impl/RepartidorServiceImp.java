package com.logitrack.shipment.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logitrack.shipment.dto.request.RepartidorRequest;
import com.logitrack.shipment.dto.response.RepartidorResponse;
import com.logitrack.shipment.enums.EstadoRepartidor;
import com.logitrack.shipment.exceptions.RecursoNoEncontradoException;
import com.logitrack.shipment.model.Repartidor;
import com.logitrack.shipment.repository.RepartidorRepository;
import com.logitrack.shipment.service.interfaces.RepartidorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepartidorServiceImp implements RepartidorService {
	
	private final RepartidorRepository repartidorRepository;

	@Override
	@Transactional
	public RepartidorResponse createRepartidor(RepartidorRequest repartidorRequest) {
		Repartidor repartidor = mapToEntity(repartidorRequest);
		Repartidor savedRepartidor = repartidorRepository.save(repartidor);
		log.info("Se ha creado el Repartidor: " + savedRepartidor.getId());
		
		return mapToResponseDto(savedRepartidor);
	}

	@Override
	@Transactional(readOnly = true)
	public RepartidorResponse findRepartidorById(UUID id) {
		Repartidor repartidor = repartidorRepository.findById(id)
				.orElseThrow(() -> {
					log.info("NO se ha encontro el Repartidor: " + id);
					return new RecursoNoEncontradoException("Repartidor no encontrado: " + id);
				});
		log.info("Se ha encontro el Repartidor: " + id);
		return mapToResponseDto(repartidor);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RepartidorResponse> findRepartidorByEstadoRepartidor(EstadoRepartidor estadoRepartidor) {
		List<Repartidor> repartidores = repartidorRepository.findByEstadoRepartidor(estadoRepartidor);
		log.info("Se han encontraron " + repartidores.size() + " Repartidores: " + estadoRepartidor);
		return repartidores
				.stream()
				.map(this::mapToResponseDto)
				.toList();
	}
	
	private RepartidorResponse mapToResponseDto(Repartidor repartidor) {
		return RepartidorResponse.builder()
				.id(repartidor.getId())
				.nombre(repartidor.getNombre())
				.tipoVehiculo(repartidor.getTipoVehiculo())
				.estadoRepartidor(repartidor.getEstadoRepartidor())
				.build();
	}
	
	private Repartidor mapToEntity(RepartidorRequest repartidorRequest) {
		Repartidor repartidor = Repartidor.builder()
				.nombre(repartidorRequest.getNombre())
				.tipoVehiculo(repartidorRequest.getTipoVehiculo())
				.estadoRepartidor(EstadoRepartidor.ACTIVO)
				.build();
		return repartidor;
	}

}
