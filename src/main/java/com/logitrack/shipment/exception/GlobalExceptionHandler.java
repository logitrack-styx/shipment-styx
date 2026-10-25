package com.logitrack.shipment.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
/**
 * Traduce las excepciones del negocio y de Spring a las respuestas HTTP
 * definidas en el contrato 2.8 del plan de trabajo.
 *
 * <p>Centraliza tanto las excepciones propias (404 / 409) como los errores
 * de Bean Validation, JSON mal formado y conversión de parámetros (400).
 * Las respuestas 500 nunca exponen detalles internos ni stack traces.</p>
 *
 * @author TheMiggaCool
 */
public class GlobalExceptionHandler {

    /**
     * Recurso no encontrado (404 Not found)
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleRecursoNoEncontrado(
            RecursoNoEncontradoException ex, HttpServletRequest request
    ) {
        ErrorResponse respuesta = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
    }

    /**
     * Recurso ya existente / Duplicado (409 Conflict)
     */
    @ExceptionHandler(RecursoYaExistenteException.class)
    public ResponseEntity<ErrorResponse> handleRecursoYaExistente(
            RecursoYaExistenteException ex, HttpServletRequest request
    ) {
        ErrorResponse respuesta = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
    }

    /**
     * Transición de Estado invalida (409 Conflict)
     */
    @ExceptionHandler(TransicionEstadoInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleTransicionEstadoInvalido(
            TransicionEstadoInvalidaException ex, HttpServletRequest request
    ) {
        ErrorResponse respuesta = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
    }

    /** Errores de Bean Validation sobre el cuerpo de la petición ({@code @Valid @RequestBody}). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleArgumentoInvalido(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, mensaje.isBlank() ? "Datos de entrada inválidos" : mensaje);
    }

    /** Errores de Bean Validation sobre parámetros sueltos ({@code @RequestParam}, {@code @PathVariable}). */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleViolacionDeRestriccion(
            ConstraintViolationException ex, HttpServletRequest request) {
        String mensaje = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining("; "));
        return build(HttpStatus.BAD_REQUEST, mensaje.isBlank() ? "Datos de entrada inválidos" : mensaje);
    }

    /** Cuerpo de la petición ilegible o con formato JSON inválido. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonInvalido(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición es ilegible o tiene un formato JSON inválido");
    }

    /** UUID o enum inválido recibido en un parámetro de ruta o de consulta. */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTipoDeArgumentoInvalido(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "El parámetro '" + ex.getName() + "' tiene un formato inválido");
    }

    /** Red de seguridad: violación de una restricción de integridad a nivel de base de datos. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleIntegridadDeDatos(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "La operación viola una restricción de integridad de datos");
    }

    /**
     * Captura general de excepciones no controladas (500)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleExceptionGlobal(
            Exception ex, HttpServletRequest request
    ) {
        ErrorResponse response = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ocurrió un error interno inesperado. Por favor intente más tarde.",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


    private ResponseEntity<ErrorResponse> build(HttpStatus status, String mensaje) {
        ErrorResponse respuesta = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .mensaje(mensaje)
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(status).body(respuesta);
    }


    /**
     * Traduce los errores de integración con courier-service al estado HTTP
     * definido por el contrato técnico.
     *
     * @param ex excepción con el estado HTTP y el mensaje controlado
     * @param request petición HTTP que originó el error
     * @return respuesta con el formato común de errores
     */
    @ExceptionHandler(CourierIntegrationException.class)
    public ResponseEntity<ErrorResponse> handleCourierIntegration(
            CourierIntegrationException ex,
            HttpServletRequest request) {

        HttpStatus status = ex.getStatus();

        ErrorResponse respuesta = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .mensaje(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(respuesta);
    }

}
