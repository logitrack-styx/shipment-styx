
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
 * Pruebas unitarias de la asignación de repartidores mediante CourierClient.
 *
 * @author dominox45
 */
@ExtendWith(MockitoExtension.class)
class AsignacionServiceImplTest {

    /** Repositorio simulado de envíos. */
    @Mock
    private EnvioRepository envioRepository;

    /** Cliente HTTP simulado de courier-service. */
    @Mock
    private CourierClient courierClient;

    /** Servicio bajo prueba. */
    @InjectMocks
    private AsignacionServiceImpl asignacionService;

    /** Identificador del envío utilizado en las pruebas. */
    private UUID envioId;

    /** Identificador del repartidor utilizado en las pruebas. */
    private UUID repartidorId;

    /** Envío de prueba en estado PENDIENTE. */
    private Envio envio;

    /** Solicitud de asignación. */
    private AsignacionRequest request;

    /** Cabecera de autorización simulada. */
    private static final String AUTHORIZATION_HEADER = "Bearer test-admin-token";

    /**
     * Prepara un envío pendiente y una solicitud de asignación.
     */
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

    /**
     * Verifica que un envío inexistente produce un error 404.
     */
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

    /**
     * Verifica que un repartidor no disponible no se asigna.
     */
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

        verify(envioRepository, never()).save(any(Envio.class));
        verify(courierClient).estaDisponible(
                repartidorId, AUTHORIZATION_HEADER);
    }

    /**
     * Verifica que un envío que no está PENDIENTE no se puede asignar.
     */
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
        verify(envioRepository, never()).save(any(Envio.class));
    }

    /**
     * Verifica que un envío que ya tiene repartidor no admite otra asignación.
     */
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
        verify(envioRepository, never()).save(any(Envio.class));
    }

    /**
     * Verifica que una asignación válida persiste el UUID sin cambiar el estado.
     */
    @Test
    void debeAsignarElRepartidorSinCambiarElEstadoDelEnvio() {
        when(envioRepository.findById(envioId))
                .thenReturn(Optional.of(envio));
        when(courierClient.estaDisponible(
                repartidorId, AUTHORIZATION_HEADER))
                .thenReturn(true);
        when(envioRepository.save(any(Envio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AsignacionResponse respuesta = asignacionService.asignarRepartidor(
                envioId, request, AUTHORIZATION_HEADER);

        assertEquals(repartidorId, respuesta.getRepartidorId());
        assertEquals(repartidorId, envio.getRepartidorId());
        assertEquals(EstadoEnvio.PENDIENTE, envio.getEstado());

        verify(courierClient).estaDisponible(
                repartidorId, AUTHORIZATION_HEADER);
        verify(envioRepository).save(envio);
    }
}
