
package com.logitrack.shipment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.logitrack.shipment.dto.request.DireccionDto;
import com.logitrack.shipment.dto.request.EnvioRequest;
import com.logitrack.shipment.dto.response.EnvioResponse;
import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.event.EnvioEstadoCambiadoEvent;
import com.logitrack.shipment.model.Envio;
import com.logitrack.shipment.repository.EnvioRepository;

/**
 * Pruebas unitarias de {@link EnvioServiceImpl}.
 *
 * <p>Verifica el registro de envíos, la conservación de la identidad
 * del cliente y la publicación del evento inicial. También comprueba
 * que un fallo de persistencia no provoque la publicación del evento.</p>
 *
 * @author dominox45
 */
@ExtendWith(MockitoExtension.class)
class EnvioServiceImplTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EnvioServiceImpl envioService;

    private UUID clienteId;
    private EnvioRequest request;

    /**
     * Prepara una solicitud válida para las pruebas.
     */
    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();

        request = new EnvioRequest(
                clienteId,
                new DireccionDto(
                        "Belgrano",
                        "123",
                        "San Salvador de Jujuy",
                        "4600"
                ),
                new DireccionDto(
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
    }

    /**
     * Comprueba que el envío se registra como PENDIENTE,
     * conserva el UUID del cliente y publica el evento inicial.
     */
    @Test
    void debeRegistrarEnvioPendienteYPublicarEvento() {
        when(envioRepository.save(any(Envio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EnvioResponse response = envioService.registrar(request);

        assertNotNull(response);
        assertEquals(clienteId, response.clienteId());
        assertEquals(EstadoEnvio.PENDIENTE, response.estado());
        assertEquals(2.5, response.peso());
        assertNotNull(response.codigoSeguimiento());

        ArgumentCaptor<Envio> envioCaptor =
                ArgumentCaptor.forClass(Envio.class);

        verify(envioRepository).save(envioCaptor.capture());

        Envio envioGuardado = envioCaptor.getValue();

        assertEquals(clienteId, envioGuardado.getClienteId());
        assertEquals(EstadoEnvio.PENDIENTE, envioGuardado.getEstado());
        assertNotNull(envioGuardado.getCodigoSeguimiento());

        ArgumentCaptor<EnvioEstadoCambiadoEvent> eventoCaptor =
                ArgumentCaptor.forClass(EnvioEstadoCambiadoEvent.class);

        verify(eventPublisher).publishEvent(eventoCaptor.capture());

        EnvioEstadoCambiadoEvent evento = eventoCaptor.getValue();

        assertEquals(envioGuardado, evento.envio());
        assertEquals(null, evento.estadoAnterior());
        assertEquals(EstadoEnvio.PENDIENTE, evento.estadoNuevo());
    }

    /**
     * Comprueba que un fallo al guardar el envío se propaga
     * y no publica el evento de registro.
     */
    @Test
    void noDebePublicarEventoSiFallaLaPersistencia() {
        when(envioRepository.save(any(Envio.class)))
                .thenThrow(new RuntimeException("Error de persistencia"));

        assertThrows(
                RuntimeException.class,
                () -> envioService.registrar(request)
        );

        verify(eventPublisher, never()).publishEvent(any());
    }
}
