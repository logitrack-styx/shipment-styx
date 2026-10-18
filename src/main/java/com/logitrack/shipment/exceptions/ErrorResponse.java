package com.logitrack.shipment.exceptions;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
/**
 * Respuesta del sistema a excepciones personalizadas
 * @author TheMiggaCool
 */
public class ErrorResponse {
    private int status;
    private String error;
    private String mensaje;
    private LocalDateTime timestamp;
}
