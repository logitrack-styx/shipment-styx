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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "Operaciones para la gestión de clientes")
public class ClienteController {
	
	private final ClienteService clienteService;
	
	@PostMapping
	@Operation(
        summary = "Registrar un cliente",
        description = "Registra un nuevo cliente a partir de los datos proporcionados."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
        //@ApiResponse(responseCode = "400", description = "Los datos del cliente son inválidos")
    })
	public ResponseEntity<ClienteResponse> create(@Valid @RequestBody ClienteRequest clienteRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.create(clienteRequest));
	}

	@GetMapping
	@Operation(
        summary = "Listar clientes",
        description = "Obtiene la lista de todos los clientes registrados."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de clientes obtenida exitosamente")
    })
	public ResponseEntity<List<ClienteResponse>> findAll() {
		return ResponseEntity.ok(clienteService.findAll());
	}
	
	@GetMapping("/{id}")
	@Operation(
        summary = "Buscar un cliente por ID",
        description = "Obtiene los datos de un cliente a partir de su identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cliente encontrado exitosamente"),
        //@ApiResponse(responseCode = "400", description = "El identificador proporcionado no es válido"),
        @ApiResponse(responseCode = "404", description = "No se encontró el cliente solicitado")
    })
	public ResponseEntity<ClienteResponse> findById(
			@Parameter(
                description = "Identificador único del cliente (UUID)",
                example = "550e8400-e29b-41d4-a716-446655440000",
                required = true
            )
			@PathVariable UUID id) {
		return ResponseEntity.ok(clienteService.findById(id));
	}
	
}
