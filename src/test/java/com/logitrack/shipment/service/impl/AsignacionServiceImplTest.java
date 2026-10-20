package com.logitrack.shipment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import com.logitrack.shipment.dto.request.AsignacionRequest;
import com.logitrack.shipment.dto.response.AsignacionResponse;
import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.enums.EstadoRepartidor;
import com.logitrack.shipment.exception.RecursoNoEncontradoException;
import com.logitrack.shipment.exception.TransicionEstadoInvalidaException;
import com.logitrack.shipment.model.Envio;
import com.logitrack.shipment.model.Repartidor;
import com.logitrack.shipment.repository.EnvioRepository;
import com.logitrack.shipment.repository.RepartidorRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas obligatorias de la Issue 04 (asignación de repartidores).
 *
 * @author TheMiggaCool
 */
@ExtendWith(MockitoExtension.class)
class AsignacionServiceImplTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private RepartidorRepository repartidorRepository;

    @InjectMocks
    private AsignacionServiceImpl asignacionService;

    private UUID envioId;
    private UUID repartidorId;
    private Envio envio;
    private Repartidor repartidor;
    private AsignacionRequest request;

    @BeforeEach
    void setUp() {
        envioId = UUID.randomUUID();
        repartidorId = UUID.randomUUID();

        envio = Envio.builder()
                .estado(EstadoEnvio.PENDIENTE)
                .peso(1f).ancho(1f).alto(1f).largo(1f)
                .build();

        repartidor = Repartidor.builder()
                .id(repartidorId)
                .nombre("Juan")
                .estadoRepartidor(EstadoRepartidor.ACTIVO)
                .build();

        request = AsignacionRequest.builder().repartidorId(repartidorId).build();
    }

    @Test
    void debeLanzar404SiElEnvioNoExiste() {
        when(envioRepository.findById(envioId)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> asignacionService.asignarRepartidor(envioId, request));

        verifyNoInteractions(repartidorRepository);
    }

    @Test
    void debeLanzar404SiElRepartidorNoExiste() {
        when(envioRepository.findById(envioId)).thenReturn(Optional.of(envio));
        when(repartidorRepository.findById(repartidorId)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> asignacionService.asignarRepartidor(envioId, request));

        verify(envioRepository, never()).save(any());
    }

    @Test
    void debeLanzar409SiElRepartidorEstaInactivo() {
        repartidor.setEstadoRepartidor(EstadoRepartidor.INACTIVO);
        when(envioRepository.findById(envioId)).thenReturn(Optional.of(envio));
        when(repartidorRepository.findById(repartidorId)).thenReturn(Optional.of(repartidor));

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> asignacionService.asignarRepartidor(envioId, request));

        verify(envioRepository, never()).save(any());
    }

    @Test
    void debeLanzar409SiElEnvioNoEstaPendiente() {
        envio.setEstado(EstadoEnvio.EN_RUTA);
        when(envioRepository.findById(envioId)).thenReturn(Optional.of(envio));
        when(repartidorRepository.findById(repartidorId)).thenReturn(Optional.of(repartidor));

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> asignacionService.asignarRepartidor(envioId, request));

        verify(envioRepository, never()).save(any());
    }

    @Test
    void debeLanzar409SiElEnvioYaTieneUnRepartidorAsignado() {
        envio.setRepartidor(Repartidor.builder().id(UUID.randomUUID()).build());
        when(envioRepository.findById(envioId)).thenReturn(Optional.of(envio));
        when(repartidorRepository.findById(repartidorId)).thenReturn(Optional.of(repartidor));

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> asignacionService.asignarRepartidor(envioId, request));

        verify(envioRepository, never()).save(any());
    }

    @Test
    void debeAsignarElRepartidorSinCambiarElEstadoDelEnvio() {
        when(envioRepository.findById(envioId)).thenReturn(Optional.of(envio));
        when(repartidorRepository.findById(repartidorId)).thenReturn(Optional.of(repartidor));
        when(envioRepository.save(any(Envio.class))).thenAnswer(inv -> inv.getArgument(0));

        AsignacionResponse respuesta = asignacionService.asignarRepartidor(envioId, request);

        assertEquals(repartidorId, respuesta.getRepartidorId());
        assertEquals(EstadoEnvio.PENDIENTE, envio.getEstado());
        assertSame(repartidor, envio.getRepartidor());
        verify(envioRepository).save(envio);
    }
}
