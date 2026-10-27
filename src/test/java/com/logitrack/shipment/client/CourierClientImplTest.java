
package com.logitrack.shipment.client;

import com.logitrack.shipment.exception.CourierIntegrationException;
import com.logitrack.shipment.exception.RecursoNoEncontradoException;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias y de integración HTTP del cliente de courier-service.
 *
 * <p>Utiliza un servidor HTTP local para simular respuestas válidas,
 * errores HTTP y respuestas incompatibles con el contrato de integración.</p>
 */
class CourierClientImplTest {

    private HttpServer server;
    private CourierClientImpl courierClient;
    private int status;
    private String responseBody;
    private UUID repartidorId;

    /**
     * Inicializa un servidor HTTP local en un puerto disponible y configura
     * una instancia del cliente que apunta a dicho servidor.
     *
     * @throws IOException si no se puede iniciar el servidor de prueba
     */
    @BeforeEach
    void setUp() throws IOException {
        server = HttpServer.create(
                new InetSocketAddress("localhost", 0), 0);

        status = 200;
        repartidorId = UUID.randomUUID();
        responseBody = "";

        server.createContext("/couriers/", exchange -> {
            byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders()
                    .set("Content-Type", "application/json");

            exchange.sendResponseHeaders(status, body.length);

            try (var output = exchange.getResponseBody()) {
                output.write(body);
            }
        });

        server.start();

        courierClient = new CourierClientImpl(
                "http://localhost:" + server.getAddress().getPort(),
                2000,
                3000);
    }

    /**
     * Detiene el servidor HTTP después de cada prueba para liberar sus recursos.
     */
    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop(0);
        }
    }

    /**
     * Comprueba que un repartidor ACTIVO con disponibilidad verdadera
     * sea considerado disponible.
     */
    @Test
    void debeDevolverTrueCuandoElRepartidorEstaActivo() {
        responseBody = """
                {
                    "id": "%s",
                    "disponible": true,
                    "estado": "ACTIVO"
                }
                """.formatted(repartidorId);

        boolean disponible = courierClient.estaDisponible(
                repartidorId, "Bearer token-prueba");

        assertTrue(disponible);
    }

    /**
     * Comprueba que un repartidor INACTIVO con disponibilidad falsa
     * sea considerado no disponible.
     */
    @Test
    void debeDevolverFalseCuandoElRepartidorEstaInactivo() {
        responseBody = """
                {
                    "id": "%s",
                    "disponible": false,
                    "estado": "INACTIVO"
                }
                """.formatted(repartidorId);

        boolean disponible = courierClient.estaDisponible(
                repartidorId, "Bearer token-prueba");

        assertFalse(disponible);
    }

    /**
     * Comprueba que una respuesta con disponibilidad contradictoria
     * respecto del estado sea rechazada como error de integración.
     */
    @Test
    void debeRechazarDisponibilidadContradictoria() {
        responseBody = """
                {
                    "id": "%s",
                    "disponible": true,
                    "estado": "INACTIVO"
                }
                """.formatted(repartidorId);

        assertThrows(
                CourierIntegrationException.class,
                () -> courierClient.estaDisponible(
                        repartidorId, "Bearer token-prueba"));
    }

    /**
     * Comprueba que un estado no contemplado por el contrato
     * sea rechazado como respuesta inválida del servicio remoto.
     */
    @Test
    void debeRechazarEstadoDesconocido() {
        responseBody = """
                {
                    "id": "%s",
                    "disponible": false,
                    "estado": "SUSPENDIDO"
                }
                """.formatted(repartidorId);

        assertThrows(
                CourierIntegrationException.class,
                () -> courierClient.estaDisponible(
                        repartidorId, "Bearer token-prueba"));
    }

    /**
     * Comprueba que el cliente rechace una respuesta cuyo identificador
     * no coincida con el repartidor solicitado.
     */
    @Test
    void debeRechazarIdentificadorDiferenteAlSolicitado() {
        responseBody = """
                {
                    "id": "%s",
                    "disponible": true,
                    "estado": "ACTIVO"
                }
                """.formatted(UUID.randomUUID());

        assertThrows(
                CourierIntegrationException.class,
                () -> courierClient.estaDisponible(
                        repartidorId, "Bearer token-prueba"));
    }

    /**
     * Comprueba que un HTTP 404 del servicio remoto se traduzca
     * en la excepción utilizada por el servicio para representar
     * un recurso inexistente.
     */
    @Test
    void debePropagarRecursoNoEncontradoCuandoCourierDevuelve404() {
        status = 404;
        responseBody = """
                {"mensaje": "Repartidor no encontrado"}
                """;

        assertThrows(
                RecursoNoEncontradoException.class,
                () -> courierClient.estaDisponible(
                        repartidorId, "Bearer token-prueba"));
    }

    /**
     * Comprueba que un HTTP 401 remoto se traduzca en un error 502,
     * sin exponerlo como un error de autenticación del cliente de envíos.
     */
    @Test
    void debeTraducir401RemotoABadGateway() {
        status = 401;
        responseBody = """
                {"mensaje": "No autorizado"}
                """;

        CourierIntegrationException excepcion = assertThrows(
                CourierIntegrationException.class,
                () -> courierClient.estaDisponible(
                        repartidorId, "Bearer token-prueba"));

        assertEquals(HttpStatus.BAD_GATEWAY, excepcion.getStatus());
    }

    /**
     * Comprueba que un HTTP 403 remoto se traduzca en un error 502,
     * conforme al contrato de integración entre microservicios.
     */
    @Test
    void debeTraducir403RemotoABadGateway() {
        status = 403;
        responseBody = """
                {"mensaje": "Acceso denegado"}
                """;

        CourierIntegrationException excepcion = assertThrows(
                CourierIntegrationException.class,
                () -> courierClient.estaDisponible(
                        repartidorId, "Bearer token-prueba"));

        assertEquals(HttpStatus.BAD_GATEWAY, excepcion.getStatus());
    }

    /**
     * Comprueba que un error HTTP 500 del servicio remoto se traduzca
     * en un error 503 para indicar que la dependencia no está disponible.
     */
    @Test
    void debeTraducirError500RemotoAServiceUnavailable() {
        status = 500;
        responseBody = """
                {"mensaje": "Error interno"}
                """;

        CourierIntegrationException excepcion = assertThrows(
                CourierIntegrationException.class,
                () -> courierClient.estaDisponible(
                        repartidorId, "Bearer token-prueba"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, excepcion.getStatus());
    }

    /**
     * Comprueba que una respuesta JSON malformada se considere un error
     * de integración y se traduzca en un HTTP 502.
     */
    @Test
    void debeTraducirJsonMalformadoABadGateway() {
        responseBody = """
                {"id": "esto-no-es-un-uuid", "disponible":
                """;

        CourierIntegrationException excepcion = assertThrows(
                CourierIntegrationException.class,
                () -> courierClient.estaDisponible(
                        repartidorId, "Bearer token-prueba"));

        assertEquals(HttpStatus.BAD_GATEWAY, excepcion.getStatus());
    }

    /**
     * Comprueba que un fallo al establecer la conexión con courier-service
     * se traduzca en un error 503.
     *
     * <p>Se utiliza un puerto local reservado que no corresponde
     * al servidor HTTP de prueba.</p>
     */
    @Test
    void debeTraducirFalloDeConexionAServiceUnavailable() {
        CourierClientImpl clienteSinServicio = new CourierClientImpl(
                "http://localhost:1",
                200,
                200);

        CourierIntegrationException excepcion = assertThrows(
                CourierIntegrationException.class,
                () -> clienteSinServicio.estaDisponible(
                        repartidorId, "Bearer token-prueba"));

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, excepcion.getStatus());
    }
}
