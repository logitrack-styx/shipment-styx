
package com.logitrack.shipment.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.exception.TransicionEstadoInvalidaException;

/**
 * Pruebas unitarias de la máquina de estados y las reglas de dominio de Envio.
 *
 * @author dominox45
 */
class EnvioTest {

    /**
     * Crea un envío PENDIENTE válido para usar en las pruebas.
     *
     * @return envío nuevo
     */
    private Envio nuevoEnvio() {
        UUID clienteId = UUID.randomUUID();

        Direccion direccion = Direccion.builder()
                .calle("Belgrano")
                .numero("100")
                .ciudad("Jujuy")
                .codigoPostal("4600")
                .build();

        return new Envio(clienteId, direccion, direccion, 1, 1, 1, 1);
    }

    /**
     * Lleva un envío hasta el estado indicado siguiendo el flujo válido.
     *
     * @param estado estado final que debe alcanzar el envío
     * @return envío en el estado solicitado
     */
    private Envio envioEnEstado(EstadoEnvio estado) {
        Envio envio = nuevoEnvio();

        switch (estado) {
            case EN_RUTA -> {
                envio.asignarRepartidor(UUID.randomUUID());
                envio.cambiarEstado(EstadoEnvio.EN_RUTA);
            }
            case ENTREGADO -> {
                envio.asignarRepartidor(UUID.randomUUID());
                envio.cambiarEstado(EstadoEnvio.EN_RUTA);
                envio.cambiarEstado(EstadoEnvio.ENTREGADO);
            }
            case CANCELADO -> envio.cancelar();
            default -> {
                // El envío ya está PENDIENTE.
            }
        }

        return envio;
    }

    /**
     * Verifica el estado inicial y la generación del código de seguimiento.
     */
    @Test
    void debeNacerPendienteConCodigoDeSeguimiento() {
        Envio envio = nuevoEnvio();

        assertEquals(EstadoEnvio.PENDIENTE, envio.getEstado());
        assertNotNull(envio.getCodigoSeguimiento());
        assertTrue(envio.puedeCancelarse());
    }

    /**
     * Comprueba que solo los envíos PENDIENTES pueden cancelarse.
     *
     * @param estado estado del envío
     * @param esperado resultado esperado
     */
    @ParameterizedTest
    @CsvSource({
            "PENDIENTE,true",
            "EN_RUTA,false",
            "ENTREGADO,false",
            "CANCELADO,false"
    })
    void puedeCancelarseSoloEnPendiente(
            EstadoEnvio estado,
            boolean esperado) {
        assertEquals(esperado, envioEnEstado(estado).puedeCancelarse());
    }

    /**
     * Verifica que no se puede iniciar el recorrido sin un repartidor asignado.
     */
    @Test
    void noDebePasarAEnRutaSinRepartidor() {
        Envio envio = nuevoEnvio();

        assertThrows(
                TransicionEstadoInvalidaException.class,
                () -> envio.cambiarEstado(EstadoEnvio.EN_RUTA));
    }

    /**
     * Verifica que asignar un UUID permite completar el flujo válido.
     */
    @Test
    void debeCompletarElFlujoConRepartidorAsignado() {
        Envio envio = nuevoEnvio();
        UUID repartidorId = UUID.randomUUID();

        envio.asignarRepartidor(repartidorId);

        assertEquals(EstadoEnvio.PENDIENTE, envio.getEstado());
        assertEquals(repartidorId, envio.getRepartidorId());

        envio.cambiarEstado(EstadoEnvio.EN_RUTA);
        envio.cambiarEstado(EstadoEnvio.ENTREGADO);

        assertEquals(EstadoEnvio.ENTREGADO, envio.getEstado());
    }

    /**
     * Verifica que un envío PENDIENTE puede cancelarse.
     */
    @Test
    void debeCancelarDesdePendiente() {
        Envio envio = nuevoEnvio();

        envio.cancelar();

        assertEquals(EstadoEnvio.CANCELADO, envio.getEstado());
    }

    /**
     * Verifica que no se puede cancelar un envío EN_RUTA ni ENTREGADO.
     *
     * @param estado estado desde el que se intenta cancelar
     */
    @ParameterizedTest
    @CsvSource({"EN_RUTA", "ENTREGADO"})
    void noDebeCancelarDesdeEnRutaNiEntregado(EstadoEnvio estado) {
        Envio envio = envioEnEstado(estado);

        assertThrows(
                TransicionEstadoInvalidaException.class,
                envio::cancelar);
    }

    /**
     * Verifica que los estados finales no admiten nuevas transiciones.
     *
     * @param origen estado final actual
     * @param destino estado al que se intenta pasar
     */
    @ParameterizedTest
    @CsvSource({
            "ENTREGADO,PENDIENTE",
            "ENTREGADO,EN_RUTA",
            "ENTREGADO,CANCELADO",
            "CANCELADO,PENDIENTE",
            "CANCELADO,EN_RUTA",
            "CANCELADO,ENTREGADO"
    })
    void noDebePermitirTransicionesDesdeEstadosFinales(
            EstadoEnvio origen,
            EstadoEnvio destino) {
        Envio envio = envioEnEstado(origen);

        assertThrows(
                TransicionEstadoInvalidaException.class,
                () -> envio.cambiarEstado(destino));
    }

    /**
     * Verifica que un envío no admite una segunda asignación.
     */
    @Test
    void noDebeAsignarRepartidorDosVeces() {
        Envio envio = nuevoEnvio();
        envio.asignarRepartidor(UUID.randomUUID());

        assertThrows(
                TransicionEstadoInvalidaException.class,
                () -> envio.asignarRepartidor(UUID.randomUUID()));
    }

    /**
     * Verifica que un envío cancelado no puede recibir un repartidor.
     */
    @Test
    void noDebeAsignarRepartidorFueraDePendiente() {
        Envio envio = envioEnEstado(EstadoEnvio.CANCELADO);

        assertThrows(
                TransicionEstadoInvalidaException.class,
                () -> envio.asignarRepartidor(UUID.randomUUID()));
    }
}
