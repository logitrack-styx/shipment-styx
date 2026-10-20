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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/repartidores")
@RequiredArgsConstructor
@Tag(name = "Repartidores", description = "Operaciones para la gestión de repartidores")
public class RepartidorController {

	private final RepartidorService repartidorService;
	
	@PostMapping
	@Operation(
        summary = "Registrar un repartidor",
        description = "Registra un nuevo repartidor a partir de los datos proporcionados."
    )
    @ApiResponses(value = {
    	@ApiResponse(responseCode = "201", description = "Repartidor creado exitosamente"),
        //@ApiResponse(responseCode = "400", description = "Los datos del repartidor son inválidos")
    })
	public ResponseEntity<RepartidorResponse> create(@Valid @RequestBody RepartidorRequest repartidorRequest) {
		return ResponseEntity.status(HttpStatus.CREATED).body(repartidorService.create(repartidorRequest));
	}
	
	@GetMapping
	@Operation(
        summary = "Listar repartidores",
        description = "Obtiene la lista de repartidores. Permite filtrar por estado; si no se especifica, devuelve todos los repartidores."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de repartidores obtenida exitosamente"),
        //@ApiResponse(responseCode = "400", description = "El estado proporcionado no es válido")
    })
	public ResponseEntity<List<RepartidorResponse>> findAll(@RequestParam(required = false) EstadoRepartidor estado) {
		return ResponseEntity.ok(repartidorService.findAll(estado));
	}

	@GetMapping("/{id}")
	@Operation(
        summary = "Buscar un repartidor por ID",
        description = "Obtiene los datos de un repartidor a partir de su identificador único."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Repartidor encontrado exitosamente"),
        //@ApiResponse(responseCode = "400", description = "El identificador proporcionado no es válido"),
        @ApiResponse(responseCode = "404", description = "No se encontró el repartidor solicitado")
    })
	public ResponseEntity<RepartidorResponse> findById(
			@Parameter(
                description = "Identificador único del repartidor (UUID)",
                example = "550e8400-e29b-41d4-a716-446655440000",
                required = true
            )
			@PathVariable UUID id) {
		return ResponseEntity.ok(repartidorService.findById(id));
	}
	
}
