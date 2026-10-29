package com.logitrack.shipment.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.UUID;

import com.logitrack.shipment.client.CourierClient;
import com.logitrack.shipment.dto.request.AsignacionRequest;
import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.model.Direccion;
import com.logitrack.shipment.model.Envio;
import com.logitrack.shipment.repository.EnvioRepository;
import com.logitrack.shipment.service.interfaces.AsignacionService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Comprueba la asignación con los proxies de seguridad y repositorios reales.
 *
 * <p>Usa H2 y un CourierClient simulado. Verifica el límite transaccional de
 * shipment; no reemplaza la prueba concurrente sobre MySQL/Testcontainers.</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class AsignacionServiceIntegrationTest {

    @Autowired
    private AsignacionService asignacionService;

    @Autowired
    private EnvioRepository envioRepository;

    @MockitoBean
    private CourierClient courierClient;

    /** Comprueba que la consulta externa se realiza sin una transacción activa. */
    @Test
    @WithMockUser(roles = "ADMIN")
    void consultaCourierSinTransaccionYConservaEstadoPendiente() {
        Direccion direccion = new Direccion("Belgrano", "123", "Jujuy", "4600");
        Envio envio = envioRepository.saveAndFlush(new Envio(UUID.randomUUID(),
                direccion, direccion, 1, 1, 1, 1));
        UUID repartidor = UUID.randomUUID();
        var request = AsignacionRequest.builder().repartidorId(repartidor).build();
        when(courierClient.estaDisponible(repartidor, "Bearer test"))
                .thenAnswer(invocation -> {
                    assertFalse(TransactionSynchronizationManager.isActualTransactionActive());
                    return true;
                });
        try {
            asignacionService.asignarRepartidor(envio.getId(), request, "Bearer test");
            Envio actualizado = envioRepository.findById(envio.getId()).orElseThrow();
            assertEquals(repartidor, actualizado.getRepartidorId());
            assertEquals(EstadoEnvio.PENDIENTE, actualizado.getEstado());
            assertEquals(envio.getVersion() + 1, actualizado.getVersion());
        } finally {
            envioRepository.deleteById(envio.getId());
        }
    }

    /** Comprueba el permiso ADMIN incluso cuando se invoca directamente el servicio. */
    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeInvocarAsignacionDirectamente() {
        var request = AsignacionRequest.builder().repartidorId(UUID.randomUUID()).build();
        assertThrows(AccessDeniedException.class, () -> asignacionService.asignarRepartidor(
                UUID.randomUUID(), request, "Bearer test"));
    }
}
