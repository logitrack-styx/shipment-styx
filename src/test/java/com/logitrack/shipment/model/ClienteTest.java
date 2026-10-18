package com.logitrack.shipment.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Pruebas unitarias para las validaciones de la entidad Cliente.
 *
 * @author dominox45
 */
class ClienteTest {

    private static final String NOMBRE_VALIDO = "Juan Pérez";
    private static final String EMAIL_VALIDO = "juan@example.com";
    private static final String TELEFONO_VALIDO = "3884123456";

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void iniciarValidador() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void cerrarValidador() {
        factory.close();
    }

    /** Comprueba que el campo indicado tenga errores de validación. */
    private static boolean tieneErrorEn(Cliente cliente, String campo) {
        Set<ConstraintViolation<Cliente>> errores = validator.validate(cliente);
        return errores.stream()
                .anyMatch(e -> e.getPropertyPath().toString().equals(campo));
    }

    // ---------- Cliente válido ----------

    @Test
    void debeAceptarUnClienteValido() {
        Cliente cliente = new Cliente(NOMBRE_VALIDO, EMAIL_VALIDO, TELEFONO_VALIDO);

        assertTrue(validator.validate(cliente).isEmpty());
    }

    @Test
    void debeAceptarLosLimitesExactosDeLongitud() {
        Cliente cliente = new Cliente(
                "a".repeat(100),
                "a".repeat(242) + "@test.com",   // 242 + 9 = 251, dentro de los 254
                "1".repeat(25)
        );

        assertTrue(validator.validate(cliente).isEmpty());
    }

    // ---------- Nombre ----------

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void debeRechazarNombreNuloOEnBlanco(String nombre) {
        Cliente cliente = new Cliente(nombre, EMAIL_VALIDO, TELEFONO_VALIDO);

        assertTrue(tieneErrorEn(cliente, "nombre"));
    }

    @Test
    void debeRechazarNombreDeUnCaracter() {
        Cliente cliente = new Cliente("A", EMAIL_VALIDO, TELEFONO_VALIDO);

        assertTrue(tieneErrorEn(cliente, "nombre"));
    }

    @Test
    void debeRechazarNombreDeMasDe100Caracteres() {
        Cliente cliente = new Cliente("a".repeat(101), EMAIL_VALIDO, TELEFONO_VALIDO);

        assertTrue(tieneErrorEn(cliente, "nombre"));
    }

    // ---------- Teléfono ----------

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void debeRechazarTelefonoNuloOVacio(String telefono) {
        Cliente cliente = new Cliente(NOMBRE_VALIDO, EMAIL_VALIDO, telefono);

        assertTrue(tieneErrorEn(cliente, "telefono"));
    }

    @Test
    void debeRechazarTelefonoDeMasDe25Caracteres() {
        Cliente cliente = new Cliente(NOMBRE_VALIDO, EMAIL_VALIDO, "1".repeat(26));

        assertTrue(tieneErrorEn(cliente, "telefono"));
    }

    // ---------- Email ----------

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    void debeRechazarEmailNuloOVacio(String email) {
        Cliente cliente = new Cliente(NOMBRE_VALIDO, email, TELEFONO_VALIDO);

        assertTrue(tieneErrorEn(cliente, "email"));
    }

    @Test
    void debeRechazarEmailDeMasDe254Caracteres() {
        // Formato válido pero demasiado largo: 250 + "@a.com" = 256
        String emailLargo = "a".repeat(250) + "@a.com";
        Cliente cliente = new Cliente(NOMBRE_VALIDO, emailLargo, TELEFONO_VALIDO);

        assertTrue(tieneErrorEn(cliente, "email"));
    }

    @Test
    void debeRechazarUnEmailInvalido() {
        Cliente cliente = new Cliente(NOMBRE_VALIDO, "correo-invalido", TELEFONO_VALIDO);

        assertTrue(tieneErrorEn(cliente, "email"));
    }

    @Test
    void noDebeMarcarErrorEnEmailValido() {
        Cliente cliente = new Cliente(NOMBRE_VALIDO, EMAIL_VALIDO, "");

        assertFalse(tieneErrorEn(cliente, "email"));
    }
}