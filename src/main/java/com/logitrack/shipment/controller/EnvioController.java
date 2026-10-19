package com.logitrack.shipment.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.logitrack.shipment.dto.request.CambioEstadoRequest;
import com.logitrack.shipment.dto.request.EnvioRequest;
import com.logitrack.shipment.dto.response.EnvioResponse;
import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.service.interfaces.EnvioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Endpoints REST para el registro, consulta y ciclo de vida de los envíos.
 * Solo valida la entrada y delega en {@link EnvioService}; los errores los
 * traduce el manejador global de excepciones.
 *
 * @author dominox45
 */
@RestController
@RequestMapping("/envios")
@RequiredArgsConstructor
@Tag(name = "Envíos", description = "Registro, consulta y ciclo de vida de envíos")
public class EnvioController {

    private final EnvioService envioService;

    /**
     * Registra un nuevo envío.
     *
     * @param request datos del envío
     * @return el envío creado con código 201
     */
    @PostMapping
    @Operation(summary = "Registrar un envío")
    @ApiResponse(responseCode = "201", description = "Envío creado")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "404", description = "Cliente inexistente")
    public ResponseEntity<EnvioResponse> registrar(@Valid @RequestBody EnvioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(envioService.registrar(request));
    }

    /**
     * Lista envíos, con filtro opcional por estado.
     *
     * @param estado estado a filtrar; si se omite, devuelve todos
     * @return lista de envíos
     */
    @GetMapping
    @Operation(summary = "Listar envíos, con filtro opcional por estado")
    @ApiResponse(responseCode = "400", description = "Estado inválido")
    public List<EnvioResponse> listar(@RequestParam(required = false) EstadoEnvio estado) {
        return envioService.listar(estado);
    }

    /**
     * Consulta un envío por su identificador.
     *
     * @param id identificador del envío
     * @return el envío encontrado
     */
    @GetMapping("/{id}")
    @Operation(summary = "Consultar un envío por id")
    @ApiResponse(responseCode = "404", description = "Envío inexistente")
    public EnvioResponse obtener(@PathVariable UUID id) {
        return envioService.obtenerPorId(id);
    }

    /**
     * Consulta un envío por su código de seguimiento.
     *
     * @param codigoSeguimiento código de seguimiento
     * @return el envío encontrado
     */
    @GetMapping("/seguimiento/{codigoSeguimiento}")
    @Operation(summary = "Consultar un envío por código de seguimiento")
    @ApiResponse(responseCode = "404", description = "Envío inexistente")
    public EnvioResponse obtenerPorSeguimiento(@PathVariable UUID codigoSeguimiento) {
        return envioService.obtenerPorCodigoSeguimiento(codigoSeguimiento);
    }

    /**
     * Cambia el estado de un envío.
     *
     * @param id      identificador del envío
     * @param request estado de destino
     * @return el envío actualizado
     */
    @PatchMapping("/{id}/estado")
    @Operation(summary = "Cambiar el estado de un envío")
    @ApiResponse(responseCode = "404", description = "Envío inexistente")
    @ApiResponse(responseCode = "409", description = "Transición no permitida")
    public EnvioResponse cambiarEstado(@PathVariable UUID id,
                                       @Valid @RequestBody CambioEstadoRequest request) {
        return envioService.cambiarEstado(id, request.estado());
    }

    /**
     * Cancela un envío PENDIENTE.
     *
     * @param id identificador del envío
     * @return respuesta 204 sin contenido
     */
    @PatchMapping("/{id}/cancelacion")
    @Operation(summary = "Cancelar un envío PENDIENTE")
    @ApiResponse(responseCode = "204", description = "Envío cancelado")
    @ApiResponse(responseCode = "404", description = "Envío inexistente")
    @ApiResponse(responseCode = "409", description = "El envío no está PENDIENTE")
    public ResponseEntity<Void> cancelar(@PathVariable UUID id) {
        envioService.cancelarEnvio(id);
        return ResponseEntity.noContent().build();
    }
}