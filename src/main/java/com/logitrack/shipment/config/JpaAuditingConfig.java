package com.logitrack.shipment.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Habilita la auditoría de Spring Data JPA.
 *
 * <p>Está separada de la clase principal para que los tests de slice
 * que no usan JPA (por ejemplo, {@code @WebMvcTest}) no la carguen.</p>
 *
 * @author dominox45
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}