
package com.logitrack.shipment.audit;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import lombok.Getter;

/**
 * Proporciona los campos de auditoría comunes a las entidades persistentes.
 *
 * <p>Spring Data JPA completa estos campos automáticamente mediante
 * {@link AuditingEntityListener}, que requiere {@code @EnableJpaAuditing}
 * (ver {@code JpaAuditingConfig}).</p>
 *
 * <p>Comportamiento de los campos:</p>
 * <ul>
 *   <li>{@code createdDate}: se fija al insertar el registro y nunca cambia
 *       después (la columna es {@code updatable = false}).</li>
 *   <li>{@code lastModifiedDate}: se fija al insertar el registro y se
 *       actualiza en cada modificación posterior.</li>
 * </ul>
 *
 * <p>Ninguno de los dos campos tiene setter: no deben asignarse manualmente.</p>
 *
 * @author dominox45
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

    /** Fecha y hora de creación. Se fija al insertar y nunca cambia. */
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    /** Fecha y hora de la última modificación. Se fija al insertar y se actualiza en cada cambio. */
    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false)
    private Instant lastModifiedDate;
}
