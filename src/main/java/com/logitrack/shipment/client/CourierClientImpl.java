package com.logitrack.shipment.client;

import java.time.Duration;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.logitrack.shipment.exception.RecursoNoEncontradoException;
import com.logitrack.shipment.exception.CourierIntegrationException;

/**
 * Implementación HTTP de la comunicación con courier-service.
 *
 * <p>Consulta la disponibilidad de un repartidor mediante el endpoint
 * acordado para Sprint 2. No accede a entidades ni repositorios del otro
 * microservicio y no realiza reintentos automáticos.</p>
 *
 * <p>La URL y los tiempos de espera se configuran mediante variables
 * de entorno para admitir ejecución local y Docker Compose.</p>
 *
 * @author dominox45
 * @version 1.0
 * @see CourierClient
 */
@Component
public class CourierClientImpl implements CourierClient {

    private final RestClient restClient;
    private final String courierServiceUrl;

    /**
     * Construye el cliente HTTP con URL y tiempos de espera configurables.
     *
     * @param courierServiceUrl URL base de courier-service
     * @param connectTimeoutMs tiempo máximo de conexión, en milisegundos
     * @param readTimeoutMs tiempo máximo de espera de respuesta, en milisegundos
     */
    public CourierClientImpl(
            @Value("${courier.service.url:${COURIER_SERVICE_URL:http://localhost:8082}}")
            String courierServiceUrl,
            @Value("${courier.service.connect-timeout-ms:${COURIER_CONNECT_TIMEOUT_MS:2000}}")
            long connectTimeoutMs,
            @Value("${courier.service.read-timeout-ms:${COURIER_READ_TIMEOUT_MS:3000}}")
            long readTimeoutMs) {

        if (connectTimeoutMs <= 0 || readTimeoutMs <= 0) {
            throw new IllegalArgumentException(
                    "Los tiempos de espera HTTP deben ser mayores que cero");
        }

        this.courierServiceUrl = courierServiceUrl.replaceAll("/+$", "");

        SimpleClientHttpRequestFactory requestFactory =
                new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }

    /**
     * Consulta la disponibilidad del repartidor en courier-service.
     *
     * @param repartidorId UUID del repartidor
     * @param authorizationHeader cabecera Bearer del administrador autenticado
     * @return {@code true} si está disponible; {@code false} si no lo está
     * @throws RecursoNoEncontradoException si courier-service responde 404
     * @throws CourierIntegrationException si el servicio externo falla
     */
    @Override
    public boolean estaDisponible(
            UUID repartidorId,
            String authorizationHeader) {

        CourierDisponibilidadResponse response;

        try {
            response = restClient.get()
                    .uri(
                            courierServiceUrl
                                    + "/couriers/{repartidorId}/disponibilidad",
                            repartidorId)
                    .header(
                            HttpHeaders.AUTHORIZATION,
                            authorizationHeader)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(CourierDisponibilidadResponse.class);

        } catch (HttpStatusCodeException ex) {
            int status = ex.getStatusCode().value();

            if (status == HttpStatus.NOT_FOUND.value()) {
                throw new RecursoNoEncontradoException(
                        "No existe un repartidor con id " + repartidorId);
            }

            if (status == HttpStatus.UNAUTHORIZED.value()
                    || status == HttpStatus.FORBIDDEN.value()) {
                throw new CourierIntegrationException(
                        HttpStatus.BAD_GATEWAY,
                        "Falló la autenticación entre shipment-service "
                                + "y courier-service");
            }

            if (ex.getStatusCode().is5xxServerError()) {
                throw new CourierIntegrationException(
                        HttpStatus.SERVICE_UNAVAILABLE,
                        "courier-service no está disponible");
            }

            throw new CourierIntegrationException(
                    HttpStatus.BAD_GATEWAY,
                    "courier-service respondió con un estado HTTP inesperado");

        } catch (ResourceAccessException ex) {
            throw new CourierIntegrationException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No fue posible conectar con courier-service "
                            + "dentro del tiempo esperado");

        } catch (RestClientException ex) {
            throw new CourierIntegrationException(
                    HttpStatus.BAD_GATEWAY,
                    "No se pudo interpretar la respuesta de courier-service");
        }

        if (response == null
                || response.id() == null
                || response.disponible() == null
                || response.estado() == null
                || response.estado().isBlank()
                || !java.util.Set.of("ACTIVO", "INACTIVO")
                .contains(response.estado())
                || !repartidorId.equals(response.id())
                || response.disponible().booleanValue()
                != "ACTIVO".equals(response.estado())) {

            throw new CourierIntegrationException(
                    HttpStatus.BAD_GATEWAY,
                    "courier-service devolvió una respuesta inválida");
        }

        return "ACTIVO".equals(response.estado());
    }

    /**
     * Representa los campos relevantes de la respuesta de disponibilidad.
     *
     * @param id UUID del repartidor consultado
     * @param disponible indica si el repartidor puede recibir asignaciones
     * @param estado estado operativo informado por courier-service
     */
    public record CourierDisponibilidadResponse(
            UUID id,
            Boolean disponible,
            String estado) {
    }
}