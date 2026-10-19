
package com.logitrack.shipment.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.service.interfaces.EnvioService;

/**
 * Pruebas web del controlador {@link EnvioController}.
 *
 * <p>Se utiliza {@link WebMvcTest} para probar el comportamiento HTTP del
 * controlador sin cargar toda la aplicación ni utilizar los repositorios
 * reales. El servicio se reemplaza por un mock de Mockito.</p>
 */
@WebMvcTest(controllers = EnvioController.class)
class EnvioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EnvioService envioService;

    /**
     * Comprueba que GET /envios devuelve HTTP 200 y una lista vacía
     * cuando el servicio no encuentra envíos.
     *
     * @throws Exception si ocurre un error durante la solicitud HTTP simulada
     */
    @Test
    void debeListarEnviosSinFiltro() throws Exception {
        when(envioService.listar(null)).thenReturn(List.of());

        mockMvc.perform(get("/envios"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(envioService).listar(null);
    }

    /**
     * Comprueba que GET /envios?estado=PENDIENTE devuelve HTTP 200
     * y transmite al servicio el estado solicitado.
     *
     * @throws Exception si ocurre un error durante la solicitud HTTP simulada
     */
    @Test
    void debeListarEnviosFiltrandoPorEstado() throws Exception {
        when(envioService.listar(EstadoEnvio.PENDIENTE))
                .thenReturn(List.of());

        mockMvc.perform(get("/envios")
                        .param("estado", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(envioService).listar(EstadoEnvio.PENDIENTE);
    }
}
