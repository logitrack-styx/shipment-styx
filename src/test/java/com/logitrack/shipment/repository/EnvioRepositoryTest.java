
package com.logitrack.shipment.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import com.logitrack.shipment.config.JpaAuditingConfig;
import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.model.Direccion;
import com.logitrack.shipment.model.Envio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingConfig.class)
@Testcontainers
class EnvioRepositoryTest {

    @Container
    static final MySQLContainer mysql =
            new MySQLContainer("mysql:8.4")
                    .withDatabaseName("logitrack_test")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configurarDatasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
        registry.add("spring.datasource.driver-class-name", mysql::getDriverClassName);
    }

    @Autowired
    private EnvioRepository envioRepository;

    private Envio envio;
    private UUID repartidorId;

    @BeforeEach
    void setUp() {
        Direccion origen = Direccion.builder()
                .calle("Belgrano")
                .numero("123")
                .ciudad("San Salvador de Jujuy")
                .codigoPostal("4600")
                .build();

        Direccion destino = Direccion.builder()
                .calle("Alvear")
                .numero("456")
                .ciudad("San Salvador de Jujuy")
                .codigoPostal("4600")
                .build();

        envio = new Envio(
                UUID.randomUUID(),
                origen,
                destino,
                2.5,
                20.0,
                15.0,
                30.0
        );

        envio = envioRepository.saveAndFlush(envio);
        repartidorId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Asigna repartidor cuando el envío está pendiente y no tiene repartidor")
    void asignarRepartidorSiDisponible_debeActualizarUnaFila() {
        int filasActualizadas =
                envioRepository.asignarRepartidorSiDisponible(
                        envio.getId(),
                        repartidorId,
                        EstadoEnvio.PENDIENTE
                );

        assertEquals(1, filasActualizadas);

        Envio envioActualizado = envioRepository.findById(envio.getId())
                .orElseThrow();

        assertEquals(repartidorId, envioActualizado.getRepartidorId());
        assertEquals(EstadoEnvio.PENDIENTE, envioActualizado.getEstado());
    }

    @Test
    @DisplayName("Impide reasignar un envío que ya tiene repartidor")
    void asignarRepartidorSiDisponible_noDebePermitirUnaSegundaAsignacion() {
        UUID primerRepartidorId = UUID.randomUUID();
        UUID segundoRepartidorId = UUID.randomUUID();

        int primeraActualizacion =
                envioRepository.asignarRepartidorSiDisponible(
                        envio.getId(),
                        primerRepartidorId,
                        EstadoEnvio.PENDIENTE
                );

        int segundaActualizacion =
                envioRepository.asignarRepartidorSiDisponible(
                        envio.getId(),
                        segundoRepartidorId,
                        EstadoEnvio.PENDIENTE
                );

        assertEquals(1, primeraActualizacion);
        assertEquals(0, segundaActualizacion);

        Envio envioActualizado = envioRepository.findById(envio.getId())
                .orElseThrow();

        assertEquals(primerRepartidorId, envioActualizado.getRepartidorId());
    }

    @Test
    @DisplayName("No asigna repartidor si el estado no coincide")
    void asignarRepartidorSiDisponible_noDebeActualizarEstadoDiferente() {
        int filasActualizadas =
                envioRepository.asignarRepartidorSiDisponible(
                        envio.getId(),
                        repartidorId,
                        EstadoEnvio.EN_RUTA
                );

        assertEquals(0, filasActualizadas);

        Envio envioActualizado = envioRepository.findById(envio.getId())
                .orElseThrow();

        assertEquals(EstadoEnvio.PENDIENTE, envioActualizado.getEstado());
        assertNull(envioActualizado.getRepartidorId());
    }

    @Test
    @DisplayName("No asigna repartidor a un envío inexistente")
    void asignarRepartidorSiDisponible_noDebeActualizarIdInexistente() {
        int filasActualizadas =
                envioRepository.asignarRepartidorSiDisponible(
                        UUID.randomUUID(),
                        repartidorId,
                        EstadoEnvio.PENDIENTE
                );

        assertEquals(0, filasActualizadas);
    }

    @Test
    @DisplayName("Persiste el envío con identificador y código de seguimiento")
    void guardarEnvio_debeGenerarIdentificadores() {
        assertNotNull(envio.getId());
        assertNotNull(envio.getCodigoSeguimiento());
    }
}
