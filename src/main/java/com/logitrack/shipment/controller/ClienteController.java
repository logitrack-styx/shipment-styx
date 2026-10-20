package com.logitrack.shipment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.logitrack.shipment.dto.request.ClienteRequest;
import com.logitrack.shipment.dto.response.ClienteResponse;
import com.logitrack.shipment.service.interfaces.ClienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {
	
	private final ClienteService clienteService;
	
	@PostMapping
	public ResponseEntity<ClienteResponse> create(@Valid @RequestBody ClienteRequest clienteRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.create(clienteRequest));
	}

	@GetMapping
	public ResponseEntity<List<ClienteResponse>> findAll() {
		return ResponseEntity.ok(clienteService.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ClienteResponse> findById(@PathVariable UUID id) {
		return ResponseEntity.ok(clienteService.findById(id));
	}
	
}
