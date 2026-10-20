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
import com.logitrack.shipment.model.Cliente;
import com.logitrack.shipment.model.Direccion;
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
 * Pruebas unitarias obligatorias de la Issue 04 (asignación de repartidores)
 * para {@link AsignacionServiceImpl}.
 *
 * <p>Las dependencias del servicio ({@link EnvioRepository} y
 * {@link RepartidorRepository}) se reemplazan por mocks de Mockito, de modo que
 * las pruebas verifican únicamente la lógica de negocio de
 * {@link AsignacionServiceImpl#asignarRepartidor(UUID, AsignacionRequest)}
 * sin acceder a la base de datos.</p>
 *
 * <p>Reglas de negocio cubiertas:</p>
 * <ul>
 *   <li>El envío debe existir (si no, error 404).</li>
 *   <li>El repartidor debe existir (si no, error 404).</li>
 *   <li>El repartidor debe estar {@link EstadoRepartidor#ACTIVO} (si no, error 409).</li>
 *   <li>El envío debe estar {@link EstadoEnvio#PENDIENTE} (si no, error 409).</li>
 *   <li>El envío no debe tener ya un repartidor asignado (si no, error 409).</li>
 *   <li>Una asignación exitosa guarda el repartidor sin modificar el estado del envío.</li>
 * </ul>
 *
 * @author TheMiggaCool
 * @see AsignacionServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class AsignacionServiceImplTest {

    /** Mock del repositorio de envíos. */
    @Mock
    private EnvioRepository envioRepository;

    /** Mock del repositorio de repartidores. */
    @Mock
    private RepartidorRepository repartidorRepository;

    /** Servicio bajo prueba, con los mocks anteriores inyectados. */
    @InjectMocks
    private AsignacionServiceImpl asignacionService;

    /** Identificador del envío usado en las búsquedas simuladas. */
    private UUID envioId;

    /** Identificador del repartidor usado en las búsquedas simuladas. */
    private UUID repartidorId;

    /** Envío de prueba, recién creado (estado {@code PENDIENTE} y sin repartidor). */
    private Envio envio;

    /** Repartidor de prueba, en estado {@code ACTIVO}. */
    private Repartidor repartidor;

    /** Cliente asociado al envío de prueba. */
    private Cliente cliente;

    /** Solicitud de asignación que referencia al repartidor de prueba. */
    private AsignacionRequest request;

    /**
     * Prepara los datos base de cada prueba: ids aleatorios, un cliente, un
     * envío nuevo, un repartidor activo y la solicitud de asignación.
     */
    @BeforeEach
    void setUp() {
        envioId = UUID.randomUUID();
        repartidorId = UUID.randomUUID();

        cliente = new Cliente(
                "Juan Pérez",
                "juan+" + UUID.randomUUID() + "@example.com",
                "3884123456");
        Direccion direccion = new Direccion("Calle", "123", "San Salvador de Jujuy", "4600");
        envio = new Envio(cliente, direccion, direccion, 1.0, 1.0, 1.0, 1.0);

        repartidor = Repartidor.builder()
                .id(repartidorId)
                .nombre("Juan")
                .estadoRepartidor(EstadoRepartidor.ACTIVO)
                .build();

        request = AsignacionRequest.builder().repartidorId(repartidorId).build();
    }

    /**
     * Si el envío no existe, el servicio lanza {@link RecursoNoEncontradoException}
     * (404) y ni siquiera consulta el repositorio de repartidores.
     */
    @Test
    void debeLanzar404SiElEnvioNoExiste() {
        when(envioRepository.findById(envioId)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> asignacionService.asignarRepartidor(envioId, request));

        verifyNoInteractions(repartidorRepository);
    }

    /**
     * Si el envío existe pero el repartidor no, el servicio lanza
     * {@link RecursoNoEncontradoException} (404) y no guarda ningún cambio.
     */
    @Test
    void debeLanzar404SiElRepartidorNoExiste() {
        when(envioRepository.findById(envioId)).thenReturn(Optional.of(envio));
        when(repartidorRepository.findById(repartidorId)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> asignacionService.asignarRepartidor(envioId, request));

        verify(envioRepository, never()).save(any());
    }

    /**
     * Si el repartidor está {@link EstadoRepartidor#INACTIVO}, el servicio lanza
     * {@link TransicionEstadoInvalidaException} (409) y no guarda el envío.
     */
    @Test
    void debeLanzar409SiElRepartidorEstaInactivo() {
        repartidor.setEstadoRepartidor(EstadoRepartidor.INACTIVO);
        when(envioRepository.findById(envioId)).thenReturn(Optional.of(envio));
        when(repartidorRepository.findById(repartidorId)).thenReturn(Optional.of(repartidor));

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> asignacionService.asignarRepartidor(envioId, request));

        verify(envioRepository, never()).save(any());
    }

    /**
     * Si el envío ya no está {@link EstadoEnvio#PENDIENTE} (aquí, {@code EN_RUTA}),
     * el servicio lanza {@link TransicionEstadoInvalidaException} (409) y no guarda.
     *
     * <p>Para llegar a {@code EN_RUTA} por una vía válida, primero se asigna otro
     * repartidor al envío, ya que la entidad lo exige para esa transición.</p>
     */
    @Test
    void debeLanzar409SiElEnvioNoEstaPendiente() {
        envio.asignarRepartidor(Repartidor.builder().id(UUID.randomUUID()).build());
        envio.cambiarEstado(EstadoEnvio.EN_RUTA);
        when(envioRepository.findById(envioId)).thenReturn(Optional.of(envio));
        when(repartidorRepository.findById(repartidorId)).thenReturn(Optional.of(repartidor));

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> asignacionService.asignarRepartidor(envioId, request));

        verify(envioRepository, never()).save(any());
    }

    /**
     * Si el envío ya tiene un repartidor asignado (aunque siga {@code PENDIENTE}),
     * el servicio lanza {@link TransicionEstadoInvalidaException} (409) y no guarda.
     */
    @Test
    void debeLanzar409SiElEnvioYaTieneUnRepartidorAsignado() {
        envio.asignarRepartidor(Repartidor.builder().id(UUID.randomUUID()).build());
        when(envioRepository.findById(envioId)).thenReturn(Optional.of(envio));
        when(repartidorRepository.findById(repartidorId)).thenReturn(Optional.of(repartidor));

        assertThrows(TransicionEstadoInvalidaException.class,
                () -> asignacionService.asignarRepartidor(envioId, request));

        verify(envioRepository, never()).save(any());
    }

    /**
     * Caso exitoso: con un envío {@code PENDIENTE} sin repartidor y un repartidor
     * {@code ACTIVO}, el servicio asigna el repartidor, guarda el envío una vez y
     * devuelve una respuesta con el id del repartidor. El estado del envío
     * permanece {@code PENDIENTE}.
     */
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