package com.logitrack.shipment.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import com.logitrack.shipment.config.JpaAuditingConfig;
import com.logitrack.shipment.model.Cliente;

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

    /**
     * Comprueba que las fechas se establezcan al crear un cliente
     * y que solo la fecha de modificación cambie al actualizarlo.
     */
    @Test
    void debeRegistrarCreacionYActualizacion() throws InterruptedException {
        Cliente cliente = new Cliente(
                "Juan Pérez",
                "juan@example.com",
                "3884123456"
        );

        // 1. Persistir y sincronizar con la base
        entityManager.persistAndFlush(cliente);
        UUID id = cliente.getId();

        // 2. Limpiar el contexto para forzar lectura real desde la base
        entityManager.clear();

        // 3. Releer: las fechas ahora tienen la precisión que guarda la base
        Cliente guardado = entityManager.find(Cliente.class, id);
        Instant fechaCreacion = guardado.getCreatedDate();
        Instant primeraModificacion = guardado.getLastModifiedDate();

        assertNotNull(id);
        assertNotNull(fechaCreacion);
        assertNotNull(primeraModificacion);

        // Evita que dos operaciones consecutivas queden en el mismo instante.
        Thread.sleep(20);

        guardado.setNombre("Juan Pérez Actualizado");
        entityManager.persistAndFlush(guardado);
        entityManager.clear();

        Cliente actualizado = entityManager.find(Cliente.class, id);

        assertNotNull(actualizado);
        assertEquals(fechaCreacion, actualizado.getCreatedDate());
        assertNotNull(actualizado.getLastModifiedDate());
        assertTrue(
                actualizado.getLastModifiedDate()
                        .isAfter(primeraModificacion)
        );
    }
}