package com.logitrack.shipment.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import com.logitrack.shipment.config.JpaAuditingConfig;
import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.model.Direccion;
import com.logitrack.shipment.model.Envio;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

/**
 * Pruebas de integración para la auditoría automática de entidades.
 *
 * @author dominox45
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class AuditingIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private com.logitrack.shipment.repository.EnvioRepository envioRepository;

    /**
     * Comprueba que las fechas se establezcan al crear un envío
     * y que solo la fecha de modificación cambie al actualizarlo.
     */
    @Test
    void debeRegistrarCreacionYActualizacion() throws InterruptedException {
        Envio envio = new Envio(
                UUID.randomUUID(),
                new Direccion(
                        "Belgrano",
                        "123",
                        "San Salvador de Jujuy",
                        "4600"
                ),
                new Direccion(
                        "Alvear",
                        "456",
                        "San Salvador de Jujuy",
                        "4600"
                ),
                2.5,
                20.0,
                15.0,
                30.0
        );

        // 1. Persistir y sincronizar con la base.
        entityManager.persistAndFlush(envio);
        UUID id = envio.getId();

        // 2. Limpiar el contexto para forzar una lectura desde la base.
        entityManager.clear();

        // 3. Releer y capturar las fechas de auditoría iniciales.
        Envio guardado = entityManager.find(Envio.class, id);
        Instant fechaCreacion = guardado.getCreatedDate();
        Instant primeraModificacion = guardado.getLastModifiedDate();

        assertNotNull(id);
        assertNotNull(guardado);
        assertNotNull(fechaCreacion);
        assertNotNull(primeraModificacion);
        assertEquals(EstadoEnvio.PENDIENTE, guardado.getEstado());

        // Evitar que ambas operaciones queden en el mismo instante.
        Thread.sleep(20);

        // 4. Modificar el envío mediante una transición válida del dominio.
        guardado.cancelar();
        entityManager.persistAndFlush(guardado);
        entityManager.clear();

        // 5. Releer y comprobar las fechas después de la modificación.
        Envio actualizado = entityManager.find(Envio.class, id);

        assertNotNull(actualizado);
        assertEquals(EstadoEnvio.CANCELADO, actualizado.getEstado());
        assertEquals(fechaCreacion, actualizado.getCreatedDate());
        assertNotNull(actualizado.getLastModifiedDate());
        assertTrue(
                actualizado.getLastModifiedDate()
                        .isAfter(primeraModificacion)
        );
    }
    /** Comprueba que la asignación masiva actualiza la auditoría sin callbacks JPA. */
    @Test
    void asignacionMasivaActualizaAuditoriaYVersion() {
        Direccion direccion = new Direccion("Belgrano", "123", "Jujuy", "4600");
        Envio envio = entityManager.persistAndFlush(new Envio(UUID.randomUUID(),
                direccion, direccion, 1, 1, 1, 1));
        UUID id = envio.getId();
        entityManager.clear();
        Envio inicial = entityManager.find(Envio.class, id);
        Instant creacion = inicial.getCreatedDate();
        Instant modificacion = creacion.plusSeconds(10)
                .truncatedTo(java.time.temporal.ChronoUnit.MICROS);
        long version = inicial.getVersion();
        UUID repartidor = UUID.randomUUID();

        assertEquals(1, envioRepository.asignarRepartidorSiDisponible(
                id, repartidor, EstadoEnvio.PENDIENTE, modificacion));

        Envio actualizado = entityManager.find(Envio.class, id);
        assertEquals(creacion, actualizado.getCreatedDate());
        assertEquals(modificacion, actualizado.getLastModifiedDate());
        assertEquals(version + 1, actualizado.getVersion());
        assertEquals(repartidor, actualizado.getRepartidorId());
        assertEquals(EstadoEnvio.PENDIENTE, actualizado.getEstado());
    }

}
