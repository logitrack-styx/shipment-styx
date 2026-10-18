package com.logitrack.shipment.audit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import com.logitrack.shipment.model.Cliente;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

/**
 * Pruebas de integración para la auditoría automática de entidades.
 *
 * @author dominox45
 */
@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
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

        Cliente guardado = entityManager.persistFlushFind(cliente);

        UUID id = guardado.getId();
        Instant fechaCreacion = guardado.getCreatedDate();
        Instant primeraModificacion = guardado.getLastModifiedDate();

        assertNotNull(id);
        assertNotNull(fechaCreacion);
        assertNotNull(primeraModificacion);

        // Evita que dos operaciones consecutivas queden en el mismo instante.
        Thread.sleep(10);

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