
package com.logitrack.shipment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
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
import com.logitrack.shipment.exceptions.RecursoNoEncontradoException;
import com.logitrack.shipment.model.Cliente;
import com.logitrack.shipment.model.Envio;
import com.logitrack.shipment.repository.ClienteRepository;
import com.logitrack.shipment.repository.EnvioRepository;

/**
 * Pruebas unitarias de {@link EnvioServiceImpl}.
 *
 * <p>Se verifican el registro de un envío y el comportamiento del servicio
 * cuando el cliente solicitado no existe. Los repositorios y el publicador
 * de eventos se simulan mediante Mockito, por lo que estas pruebas no
 * requieren una base de datos real ni levantar el contexto de Spring.</p>
 */
@ExtendWith(MockitoExtension.class)
class EnvioServiceImplTest {

    @Mock
    private EnvioRepository envioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private EnvioServiceImpl envioService;

    private UUID clienteId;
    private Cliente cliente;
    private EnvioRequest request;

    /**
     * Prepara los datos comunes utilizados por las pruebas.
     *
     * <p>Se crea un cliente válido y una solicitud con direcciones y
     * dimensiones de ejemplo. Cada prueba recibe una preparación nueva.</p>
     */
    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();
        cliente = new Cliente("Cliente de prueba",
                "cliente.prueba@example.com", "3884000000");

        request = new EnvioRequest(
                clienteId,
                new DireccionDto("Belgrano", "123", "San Salvador de Jujuy", "4600"),
                new DireccionDto("Alvear", "456", "San Salvador de Jujuy", "4600"),
                2.5,
                20.0,
                15.0,
                30.0
        );
    }

    /**
     * Comprueba que un envío se registra con estado inicial PENDIENTE
     * y que se publica el evento correspondiente.
     *
     * <p>El repositorio simulado devuelve la misma entidad recibida en
     * {@code save}, reproduciendo el comportamiento necesario para esta
     * prueba sin persistir datos realmente.</p>
     */
    @Test
    void debeRegistrarEnvioPendienteYPublicarEvento() {
        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.of(cliente));

        when(envioRepository.save(any(Envio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EnvioResponse response = envioService.registrar(request);

        assertNotNull(response);
        assertEquals(EstadoEnvio.PENDIENTE, response.estado());
        assertEquals("Cliente de prueba", response.clienteNombre());
        assertEquals(2.5, response.peso());
        assertNotNull(response.codigoSeguimiento());

        ArgumentCaptor<Envio> envioCaptor =
                ArgumentCaptor.forClass(Envio.class);

        verify(envioRepository).save(envioCaptor.capture());

        Envio envioGuardado = envioCaptor.getValue();

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
     * Comprueba que el registro falla si el cliente indicado no existe.
     *
     * <p>El servicio debe lanzar {@link RecursoNoEncontradoException},
     * no guardar el envío y no publicar un evento de cambio de estado.</p>
     */
    @Test
    void noDebeRegistrarEnvioSiElClienteNoExiste() {
        when(clienteRepository.findById(clienteId))
                .thenReturn(Optional.empty());

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> envioService.registrar(request)
        );

        verify(envioRepository, never()).save(any(Envio.class));
        verify(eventPublisher, never()).publishEvent(any());
    }
}
