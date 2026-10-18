package com.logitrack.shipment.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.logitrack.shipment.config.JpaAuditingConfig;

/**
 * Pruebas de integración de las restricciones de base de datos de Cliente.
 *
 * @author dominox45
 */
@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
    class ClienteConstraintsIntegrationTest{

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void debeRechazarDosClientesConElMismoEmail() {
        Cliente primero = new Cliente("Juan Pérez", "mismo@example.com", "3884123456");
        Cliente segundo = new Cliente("María López", "mismo@example.com", "3884999999");

        entityManager.persistAndFlush(primero);

        assertThrows(
                ConstraintViolationException.class,
                () -> entityManager.persistAndFlush(segundo)
        );
    }

    @Test
    void debeRechazarEmailsQueSoloDifierenEnMayusculas() {
        entityManager.persistAndFlush(
                new Cliente("Juan Pérez", "mismo@example.com", "3884123456"));

        Cliente duplicado = new Cliente("María López", "MISMO@Example.com", "3884999999");

        assertThrows(
                ConstraintViolationException.class,
                () -> entityManager.persistAndFlush(duplicado)
        );
    }
}