package com.logitrack.shipment.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.enums.EstadoRepartidor;
import com.logitrack.shipment.exception.TransicionEstadoInvalidaException;

/**
 * Pruebas unitarias de la máquina de estados y las reglas de dominio de {@link Envio}.
 *
 * @author dominox45
 */
class EnvioTest {

    /** Crea un envío PENDIENTE válido para usar en las pruebas. */
    private Envio nuevoEnvio() {
        Cliente cliente = new Cliente("Juan Pérez", "juan@example.com", "3884123456");
        Direccion direccion = Direccion.builder().calle("Belgrano").numero("100")
                .ciudad("Jujuy").codigoPostal("4600").build();
        return new Envio(cliente, direccion, direccion, 1, 1, 1, 1);
    }

    /** Crea un repartidor activo para usar en las pruebas. */
    private Repartidor nuevoRepartidor() {
        return Repartidor.builder().nombre("Ana").tipoVehiculo("Moto")
                .estadoRepartidor(EstadoRepartidor.ACTIVO).build();
    }

    /** Lleva un envío hasta el estado indicado siguiendo el flujo válido. */
    private Envio envioEnEstado(EstadoEnvio estado) {
        Envio envio = nuevoEnvio();
        switch (estado) {
            case EN_RUTA -> {
                envio.asignarRepartidor(nuevoRepartidor());
                envio.cambiarEstado(EstadoEnvio.EN_RUTA);
            }
            case ENTREGADO -> {
                envio.asignarRepartidor(nuevoRepartidor());
                envio.cambiarEstado(EstadoEnvio.EN_RUTA);
                envio.cambiarEstado(EstadoEnvio.ENTREGADO);
            }
            case CANCELADO -> envio.cancelar();
            default -> { }
        }
        return envio;
    }

    /** Un envío nuevo nace PENDIENTE, con código de seguimiento y cancelable. */
    @Test
    void debeNacerPendienteConCodigoDeSeguimiento() {
        Envio envio = nuevoEnvio();

        assertEquals(EstadoEnvio.PENDIENTE, envio.getEstado());
        assertNotNull(envio.getCodigoSeguimiento());
        assertTrue(envio.puedeCancelarse());
    }

    /** Solo los envíos PENDIENTES pueden cancelarse. */
    @ParameterizedTest
    @CsvSource({"PENDIENTE,true", "EN_RUTA,false", "ENTREGADO,false", "CANCELADO,false"})
    void puedeCancelarseSoloEnPendiente(EstadoEnvio estado, boolean esperado) {
        assertEquals(esperado, envioEnEstado(estado).puedeCancelarse());
    }

    /** No se puede pasar a EN_RUTA sin repartidor asignado. */
    @Test
    void noDebePasarAEnRutaSinRepartidor() {
        Envio envio = nuevoEnvio();

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> envio.cambiarEstado(EstadoEnvio.EN_RUTA));
    }

    /** Asignar un repartidor no cambia el estado y habilita el flujo completo. */
    @Test
    void debeCompletarElFlujoConRepartidorAsignado() {
        Envio envio = nuevoEnvio();

        envio.asignarRepartidor(nuevoRepartidor());
        assertEquals(EstadoEnvio.PENDIENTE, envio.getEstado());

        envio.cambiarEstado(EstadoEnvio.EN_RUTA);
        envio.cambiarEstado(EstadoEnvio.ENTREGADO);

        assertEquals(EstadoEnvio.ENTREGADO, envio.getEstado());
    }

    /** Un envío PENDIENTE puede cancelarse. */
    @Test
    void debeCancelarDesdePendiente() {
        Envio envio = nuevoEnvio();

        envio.cancelar();

        assertEquals(EstadoEnvio.CANCELADO, envio.getEstado());
    }

    /** No se puede cancelar un envío EN_RUTA ni ENTREGADO. */
    @ParameterizedTest
    @CsvSource({"EN_RUTA", "ENTREGADO"})
    void noDebeCancelarDesdeEnRutaNiEntregado(EstadoEnvio estado) {
        Envio envio = envioEnEstado(estado);

        assertThrows(TransicionEstadoInvalidaException.class, envio::cancelar);
    }

    /** Los estados finales no admiten ninguna transición. */
    @ParameterizedTest
    @CsvSource({
            "ENTREGADO,PENDIENTE", "ENTREGADO,EN_RUTA", "ENTREGADO,CANCELADO",
            "CANCELADO,PENDIENTE", "CANCELADO,EN_RUTA", "CANCELADO,ENTREGADO"
    })
    void noDebePermitirTransicionesDesdeEstadosFinales(EstadoEnvio origen, EstadoEnvio destino) {
        Envio envio = envioEnEstado(origen);

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> envio.cambiarEstado(destino));
    }

    /** No se puede asignar repartidor dos veces. */
    @Test
    void noDebeAsignarRepartidorDosVeces() {
        Envio envio = nuevoEnvio();
        envio.asignarRepartidor(nuevoRepartidor());

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> envio.asignarRepartidor(nuevoRepartidor()));
    }

    /** No se puede asignar repartidor a un envío que ya no está PENDIENTE. */
    @Test
    void noDebeAsignarRepartidorFueraDePendiente() {
        Envio envio = envioEnEstado(EstadoEnvio.CANCELADO);

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> envio.asignarRepartidor(nuevoRepartidor()));
    }
}