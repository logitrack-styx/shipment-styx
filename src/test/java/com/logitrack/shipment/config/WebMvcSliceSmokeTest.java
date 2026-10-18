package com.logitrack.shipment.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Comprueba que un slice web arranca sin cargar el metamodelo JPA.
 */
@WebMvcTest
class WebMvcSliceSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void debeArrancarElSliceWebSinErrorDeMetamodeloJpa() {
        // Si el contexto carga, el test pasa.
    }
}