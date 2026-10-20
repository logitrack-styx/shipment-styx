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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logitrack.shipment.dto.request.RepartidorRequest;
import com.logitrack.shipment.dto.response.RepartidorResponse;
import com.logitrack.shipment.enums.EstadoRepartidor;
import com.logitrack.shipment.service.interfaces.RepartidorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/repartidores")
@RequiredArgsConstructor
public class RepartidorController {

	private final RepartidorService repartidorService;
	
	@PostMapping
	public ResponseEntity<RepartidorResponse> create(@Valid @RequestBody RepartidorRequest repartidorRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(repartidorService.create(repartidorRequest));
	}
	
	@GetMapping
	public ResponseEntity<List<RepartidorResponse>> findAll(@RequestParam(required = false) EstadoRepartidor estado) {
		return ResponseEntity.ok(repartidorService.findAll(estado));
	}

	@GetMapping("/{id}")
	public ResponseEntity<RepartidorResponse> findById(@PathVariable UUID id) {
		return ResponseEntity.ok(repartidorService.findById(id));
	}
	
}
