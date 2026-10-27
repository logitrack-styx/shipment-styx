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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;

/**
 * Expone la operación de asignación de repartidores.
 *
 * @author TheMiggaCool
 */
@RestController
@RequestMapping("/envios")
@RequiredArgsConstructor
@Tag(
        name = "Asignación",
        description = "Asignación de repartidores a envíos pendientes")
public class AsignacionController {

    private final AsignacionService asignacionService;

    /**
     * Asigna un repartidor disponible a un envío pendiente.
     *
     * @param envioId identificador UUID del envío
     * @param request datos del repartidor que se desea asignar
     * @param authorizationHeader cabecera Authorization recibida
     * @return respuesta HTTP 200 con los datos de la asignación
     */
    @PutMapping("/{envioId}/repartidor")
    @Operation(summary = "Asigna un repartidor disponible a un envío pendiente")
    public ResponseEntity<AsignacionResponse> asignarRepartidor(
            @PathVariable UUID envioId,
            @Valid @RequestBody AsignacionRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        AsignacionResponse respuesta = asignacionService.asignarRepartidor(
                envioId,
                request,
                authorizationHeader);

        return ResponseEntity.status(HttpStatus.OK).body(respuesta);
    }
}