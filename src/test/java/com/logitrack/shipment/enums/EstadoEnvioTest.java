package com.logitrack.shipment.enums;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para el enumerado EstadoEnvio.
 *
 * @author dominox45
 */
class EstadoEnvioTest {

    /**
     * Comprueba que estén definidos exactamente los estados
     * requeridos por el dominio y en el orden esperado.
     */
    @Test
    void debeContenerLosEstadosRequeridos() {
        assertArrayEquals(
                new EstadoEnvio[] {
                        EstadoEnvio.PENDIENTE,
                        EstadoEnvio.EN_RUTA,
                        EstadoEnvio.ENTREGADO,
                        EstadoEnvio.CANCELADO
                },
                EstadoEnvio.values()
        );
    }
}