package com.logitrack.shipment.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.logitrack.shipment.dto.request.ClienteRequest;
import com.logitrack.shipment.dto.response.ClienteResponse;
import com.logitrack.shipment.exceptions.RecursoNoEncontradoException;
import com.logitrack.shipment.exceptions.RecursoYaExistenteException;
import com.logitrack.shipment.model.Cliente;
import com.logitrack.shipment.repository.ClienteRepository;
import com.logitrack.shipment.service.interfaces.ClienteService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClienteServiceImp implements ClienteService {

	private final ClienteRepository clienteRepository;
	
	@Override
	@Transactional
	public ClienteResponse createCliente(ClienteRequest clienteRequest) {
		verifyUnique(clienteRequest);
		
		Cliente cliente = mapToEntity(clienteRequest);
		Cliente savedCliente = clienteRepository.save(cliente);
		log.info("Se ha creado el Cliente: " + savedCliente.getId());
		
		return mapToResponseDto(savedCliente);
	}

	@Override
	@Transactional(readOnly = true)
	public ClienteResponse findClienteById(UUID id) {
		Cliente cliente = clienteRepository.findById(id)
				.orElseThrow(() -> {
					log.info("NO se ha encontro el Cliente: " + id);
					return new RecursoNoEncontradoException("Cliente no encontrado: " + id);
				});
		log.info("Se ha encontro el Cliente: " + id);
		return mapToResponseDto(cliente);
	}
	
	private ClienteResponse mapToResponseDto(Cliente cliente) {
		return ClienteResponse.builder()
				.id(cliente.getId())
				.nombre(cliente.getNombre())
				.email(cliente.getEmail())
				.telefono(cliente.getTelefono())
				.build();
	}
	
	private Cliente mapToEntity(ClienteRequest clienteRequest) {
		Cliente cliente = new Cliente(clienteRequest.getNombre(), clienteRequest.getEmail(), clienteRequest.getTelefono());
		return cliente;
	}
	
	private void verifyUnique(ClienteRequest clienteRequest) {
		if(clienteRepository.existsByEmail(clienteRequest.getEmail())) {
			throw new RecursoYaExistenteException("Cliente ya existente: " + clienteRequest.getEmail());
		}
	}

}
