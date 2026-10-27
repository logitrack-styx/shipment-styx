package com.logitrack.shipment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.logitrack.shipment.client.CourierClient;
import com.logitrack.shipment.dto.request.AsignacionRequest;
import com.logitrack.shipment.dto.response.AsignacionResponse;
import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.exception.RecursoNoEncontradoException;
import com.logitrack.shipment.exception.TransicionEstadoInvalidaException;
import com.logitrack.shipment.model.Direccion;
import com.logitrack.shipment.model.Envio;
import com.logitrack.shipment.repository.EnvioRepository;

/**
 * Pruebas unitarias del servicio de asignación de repartidores.
 *
 * @author dominox45
 */
@ExtendWith(MockitoExtension.class)
class AsignacionServiceImplTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private CourierClient courierClient;

    @InjectMocks
    private AsignacionServiceImpl asignacionService;

    private UUID envioId;
    private UUID repartidorId;
    private Envio envio;
    private AsignacionRequest request;

    private static final String AUTHORIZATION_HEADER =
            "Bearer test-admin-token";

    @BeforeEach
    void setUp() {
        envioId = UUID.randomUUID();
        repartidorId = UUID.randomUUID();

        Direccion direccion = new Direccion(
                "Calle",
                "123",
                "San Salvador de Jujuy",
                "4600");

        envio = new Envio(
                UUID.randomUUID(),
                direccion,
                direccion,
                1.0,
                1.0,
                1.0,
                1.0);

        request = AsignacionRequest.builder()
                .repartidorId(repartidorId)
                .build();
    }

    @Test
    void debeLanzar404SiElEnvioNoExiste() {
        when(envioRepository.findById(envioId))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> asignacionService.asignarRepartidor(
                        envioId, request, AUTHORIZATION_HEADER));

        verifyNoInteractions(courierClient);
        verify(envioRepository, never()).save(any(Envio.class));
    }

    @Test
    void debeLanzar409SiElRepartidorNoEstaDisponible() {
        when(envioRepository.findById(envioId))
                .thenReturn(Optional.of(envio));

        when(courierClient.estaDisponible(
                repartidorId, AUTHORIZATION_HEADER))
                .thenReturn(false);

        assertThrows(
                TransicionEstadoInvalidaException.class,
                () -> asignacionService.asignarRepartidor(
                        envioId, request, AUTHORIZATION_HEADER));

        verify(courierClient).estaDisponible(
                repartidorId, AUTHORIZATION_HEADER);

        verify(envioRepository, never()).save(any(Envio.class));
        verify(envioRepository, never()).asignarRepartidorSiDisponible(
                envioId, repartidorId, EstadoEnvio.PENDIENTE);
    }

    @Test
    void debeLanzar409SiElEnvioNoEstaPendiente() {
        envio.asignarRepartidor(UUID.randomUUID());
        envio.cambiarEstado(EstadoEnvio.EN_RUTA);

        when(envioRepository.findById(envioId))
                .thenReturn(Optional.of(envio));

        assertThrows(
                TransicionEstadoInvalidaException.class,
                () -> asignacionService.asignarRepartidor(
                        envioId, request, AUTHORIZATION_HEADER));

        verifyNoInteractions(courierClient);
        verify(envioRepository, never()).asignarRepartidorSiDisponible(
                envioId, repartidorId, EstadoEnvio.PENDIENTE);
    }

    @Test
    void debeLanzar409SiElEnvioYaTieneRepartidor() {
        envio.asignarRepartidor(UUID.randomUUID());

        when(envioRepository.findById(envioId))
                .thenReturn(Optional.of(envio));

        assertThrows(
                TransicionEstadoInvalidaException.class,
                () -> asignacionService.asignarRepartidor(
                        envioId, request, AUTHORIZATION_HEADER));

        verifyNoInteractions(courierClient);
        verify(envioRepository, never()).asignarRepartidorSiDisponible(
                envioId, repartidorId, EstadoEnvio.PENDIENTE);
    }

    @Test
    void debeAsignarRepartidorMedianteActualizacionAtomica() {
        when(envioRepository.findById(envioId))
                .thenReturn(Optional.of(envio));

        when(courierClient.estaDisponible(
                repartidorId, AUTHORIZATION_HEADER))
                .thenReturn(true);

        when(envioRepository.asignarRepartidorSiDisponible(
                envioId, repartidorId, EstadoEnvio.PENDIENTE))
                .thenReturn(1);

        AsignacionResponse respuesta =
                asignacionService.asignarRepartidor(
                        envioId, request, AUTHORIZATION_HEADER);

        assertEquals(repartidorId, respuesta.getRepartidorId());

        verify(courierClient).estaDisponible(
                repartidorId, AUTHORIZATION_HEADER);

        verify(envioRepository).asignarRepartidorSiDisponible(
                envioId, repartidorId, EstadoEnvio.PENDIENTE);

        verify(envioRepository, never()).save(any(Envio.class));

        // La actualización masiva no modifica la instancia cargada en memoria.
        assertEquals(EstadoEnvio.PENDIENTE, envio.getEstado());
    }

    @Test
    void debeLanzar409SiLaActualizacionAtomicaNoModificaFilas() {
        when(envioRepository.findById(envioId))
                .thenReturn(Optional.of(envio));

        when(courierClient.estaDisponible(
                repartidorId, AUTHORIZATION_HEADER))
                .thenReturn(true);

        when(envioRepository.asignarRepartidorSiDisponible(
                envioId, repartidorId, EstadoEnvio.PENDIENTE))
                .thenReturn(0);

        assertThrows(
                TransicionEstadoInvalidaException.class,
                () -> asignacionService.asignarRepartidor(
                        envioId, request, AUTHORIZATION_HEADER));

        verify(envioRepository).asignarRepartidorSiDisponible(
                envioId, repartidorId, EstadoEnvio.PENDIENTE);
    }
}