package com.logitrack.shipment.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.event.EnvioEstadoCambiadoEvent;
import com.logitrack.shipment.model.Cliente;
import com.logitrack.shipment.model.Direccion;
import com.logitrack.shipment.model.Envio;
import com.logitrack.shipment.model.HistorialEstado;
import com.logitrack.shipment.repository.ClienteRepository;
import com.logitrack.shipment.repository.EnvioRepository;
import com.logitrack.shipment.repository.HistorialEstadoRepository;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Pruebas de integración de {@link HistorialEstadoListener} (Issue 01).
 *
 * <p>Se usa {@link TransactionTemplate} en vez de {@code @Transactional} en
 * los métodos de test para poder controlar explícitamente cuándo existe (o
 * no) una transacción activa, que es justamente lo que exige la propagación
 * {@code MANDATORY} del listener.</p>
 *
 * @author TheMiggaCool
 */
@SpringBootTest
@ActiveProfiles("test")
class HistorialEstadoListenerIntegrationTest {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private HistorialEstadoRepository historialEstadoRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private EntityManager entityManager;

    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Persiste un cliente con email único y un envío PENDIENTE asociado.
     *
     * @return el envío persistido
     */
    private Envio nuevoEnvioPersistido() {
        Cliente cliente = clienteRepository.save(new Cliente(
                "Juan Pérez",
                "juan+" + UUID.randomUUID() + "@example.com",
                "3884123456"));

        Direccion direccion = new Direccion("Calle", "123", "San Salvador de Jujuy", "4600");
        Envio envio = new Envio(cliente, direccion, direccion, 1.0, 1.0, 1.0, 1.0);

        return envioRepository.save(envio);
    }

    @Test
    void debeRegistrarHistorialConEstadoAnteriorNuloEnElAltaInicial() {
        transactionTemplate.executeWithoutResult(status -> {
            Envio envio = nuevoEnvioPersistido();

            eventPublisher.publishEvent(new EnvioEstadoCambiadoEvent(envio, null, EstadoEnvio.PENDIENTE));
            entityManager.flush();

            List<HistorialEstado> historial =
                    historialEstadoRepository.findByEnvioIdOrderByFechaHoraAsc(envio.getId());

            assertEquals(1, historial.size());
            assertNull(historial.get(0).getEstadoAnterior());
            assertEquals(EstadoEnvio.PENDIENTE, historial.get(0).getEstadoNuevo());
        });
    }

    @Test
    void debeRegistrarHistorialConEstadosAnteriorYNuevoEnUnCambioDeEstado() {
        transactionTemplate.executeWithoutResult(status -> {
            Envio envio = nuevoEnvioPersistido();

            eventPublisher.publishEvent(
                    new EnvioEstadoCambiadoEvent(envio, EstadoEnvio.PENDIENTE, EstadoEnvio.EN_RUTA));
            entityManager.flush();

            List<HistorialEstado> historial =
                    historialEstadoRepository.findByEnvioIdOrderByFechaHoraAsc(envio.getId());

            assertEquals(1, historial.size());
            assertEquals(EstadoEnvio.PENDIENTE, historial.get(0).getEstadoAnterior());
            assertEquals(EstadoEnvio.EN_RUTA, historial.get(0).getEstadoNuevo());
        });
    }

    @Test
    void debeFallarSiSePublicaElEventoSinUnaTransaccionActiva() {
        Envio envio = transactionTemplate.execute(status -> nuevoEnvioPersistido());

        // Fuera de transactionTemplate: no hay transacción activa en este punto.
        assertThrows(IllegalTransactionStateException.class, () ->
                eventPublisher.publishEvent(
                        new EnvioEstadoCambiadoEvent(envio, EstadoEnvio.PENDIENTE, EstadoEnvio.EN_RUTA)));

        List<HistorialEstado> historial =
                historialEstadoRepository.findByEnvioIdOrderByFechaHoraAsc(envio.getId());
        assertTrue(historial.isEmpty());
    }

    @Test
    void debeRevertirTodaLaOperacionSiFallaLaPersistenciaDelHistorial() {
        AtomicReference<UUID> envioIdRef = new AtomicReference<>();

        assertThrows(RuntimeException.class, () ->
                transactionTemplate.executeWithoutResult(status -> {
                    Envio envio = nuevoEnvioPersistido();
                    envioIdRef.set(envio.getId());

                    // estadoNuevo nulo viola @NotNull: debe abortar toda la
                    // transacción, incluido el alta del propio envío.
                    eventPublisher.publishEvent(new EnvioEstadoCambiadoEvent(envio, null, null));
                    entityManager.flush();
                }));

        assertTrue(envioRepository.findById(envioIdRef.get()).isEmpty());
        assertTrue(historialEstadoRepository.findByEnvioIdOrderByFechaHoraAsc(envioIdRef.get()).isEmpty());
    }
}