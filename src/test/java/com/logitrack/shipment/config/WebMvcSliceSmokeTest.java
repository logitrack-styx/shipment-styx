package com.logitrack.shipment.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.logitrack.shipment.service.interfaces.AsignacionService;
import com.logitrack.shipment.service.interfaces.ClienteService;
import com.logitrack.shipment.service.interfaces.EnvioService;
import com.logitrack.shipment.service.interfaces.RepartidorService;

/**
 * Comprueba que un slice web arranca sin cargar el metamodelo JPA.
 *
 * <p>{@link WebMvcTest} sin controladores específicos carga todos los
 * controladores de la aplicación, pero no los servicios. Por eso se
 * reemplazan por mocks los servicios de los que depende cada controlador;
 * al sumar un controlador nuevo hay que agregar aquí su servicio.</p>
 */
@WebMvcTest
class WebMvcSliceSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnvioService envioService;

    @MockitoBean
    private ClienteService clienteService;

    @MockitoBean
    private RepartidorService repartidorService;

    @MockitoBean
    private AsignacionService asignacionService;

    /**
     * Si el contexto web carga correctamente, la prueba pasa.
     */
    @Test
    void debeArrancarElSliceWebSinErrorDeMetamodeloJpa() {
        // Si el contexto carga, el test pasa.
    }
}