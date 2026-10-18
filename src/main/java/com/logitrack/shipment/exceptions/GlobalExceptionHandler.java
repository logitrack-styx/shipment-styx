package com.logitrack.shipment.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
/**
 * Maneja las excepciones del negocio
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
    public ResponseEntity<ErrorResponse> handleRecursoYaExistnte(
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
     * Transición de Estado invalida (406 Not acceptable)
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
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(respuesta);
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
}
