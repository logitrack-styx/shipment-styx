package com.logitrack.shipment.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la entidad Cliente.
 *
 * @author dominox45
 */
class ClienteTest {

    /**
     * Comprueba que un cliente con datos válidos no genere errores
     * de validación.
     */
    @Test
    void debeAceptarUnClienteValido() {
        try (ValidatorFactory factory =
                     Validation.buildDefaultValidatorFactory()) {

            Validator validator = factory.getValidator();

            Cliente cliente = new Cliente(
                    "Juan Pérez",
                    "juan@example.com",
                    "3884123456"
            );

            Set<ConstraintViolation<Cliente>> errores =
                    validator.validate(cliente);

            assertTrue(errores.isEmpty());
        }
    }

    /**
     * Comprueba que un correo electrónico inválido genere errores
     * de validación.
     */
    @Test
    void debeRechazarUnEmailInvalido() {
        try (ValidatorFactory factory =
                     Validation.buildDefaultValidatorFactory()) {

            Validator validator = factory.getValidator();

            Cliente cliente = new Cliente(
                    "Juan Pérez",
                    "correo-invalido",
                    "3884123456"
            );

            Set<ConstraintViolation<Cliente>> errores =
                    validator.validate(cliente);

            assertFalse(errores.isEmpty());
        }
    }
}