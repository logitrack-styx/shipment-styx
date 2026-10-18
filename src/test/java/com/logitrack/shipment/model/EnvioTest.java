package com.logitrack.shipment.model;

import com.logitrack.shipment.enums.EstadoEnvio;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias para la entidad Envios
 *
 * @author TheMiggaCool
 */
public class EnvioTest {

    /**
     * Comprueba que un envío solo con estado Pendiente se pueda cancelar
     */
    @Test
    void puedeCancelarEnvio(){
        /**
         * Constructor vacío con Estado por defecto en PENDIENTE.
         */
        Envio envio = new Envio();

        assertTrue(envio.puedeCancelarse());
    }

    /**
     * Comprueba que un envio no puede cancelarse si está en estado EN_RUTA,
     * CANCELADO o ENTREGADO.
     */
    @Test
    void noPuedecancelarEnvio(){
        /**
         * Estados no cancelables
         */
        EstadoEnvio [] estadosNoCancelables = {
                EstadoEnvio.EN_RUTA,
                EstadoEnvio.CANCELADO,
                EstadoEnvio.ENTREGADO
        };

        /**
         * Constructor vacío con Estado por defecto en PENDIENTE.
         */
        Envio envio = new Envio();

        for(EstadoEnvio estado: estadosNoCancelables){
            envio.setEstado(estado);
            assertFalse(envio.puedeCancelarse());
        }
    }
}

