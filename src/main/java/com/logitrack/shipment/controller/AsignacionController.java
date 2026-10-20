package com.logitrack.shipment.controller;

import java.util.UUID;

import com.logitrack.shipment.dto.request.AsignacionRequest;
import com.logitrack.shipment.dto.response.AsignacionResponse;
import com.logitrack.shipment.service.interfaces.AsignacionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Expone la operación de asignación de repartidores (contrato 2.7 y 2.8).
 *
 * <p>{@code PUT /envios/{envioId}/repartidor} → 200 OK con el DTO de
 * asignación. Los errores se traducen vía {@code GlobalExceptionHandler}.</p>
 *
 * @author TheMiggaCool
 */
@RestController
@RequestMapping("/envios")
@RequiredArgsConstructor
@Tag(name = "Asignación", description = "Asignación de repartidores activos a envíos pendientes")
public class AsignacionController {

    private final AsignacionService asignacionService;

    @PutMapping("/{envioId}/repartidor")
    @Operation(summary = "Asigna un repartidor activo a un envío pendiente")
    public ResponseEntity<AsignacionResponse> asignarRepartidor(
            @PathVariable UUID envioId,
            @Valid @RequestBody AsignacionRequest request) {
        AsignacionResponse respuesta = asignacionService.asignarRepartidor(envioId, request);
        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }
}
