package com.logitrack.shipment.exception;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Representa la respuesta HTTP generada ante un error de la API.
 *
 * @author TheMiggaCool
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    /** Código numérico HTTP. */
    private int status;

    /** Descripción breve del error. */
    private String error;

    /** Detalle del error que puede exponerse al cliente. */
    private String mensaje;

    /** Fecha y hora en que se generó la respuesta. */
    private OffsetDateTime timestamp;
}