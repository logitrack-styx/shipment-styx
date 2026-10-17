
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
 * <p>Spring Data JPA registra automáticamente las fechas de creación
 * y última modificación.</p>
 *
 * @author dominox45
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

    /** Fecha y hora de creación del registro. */
    @CreatedDate
    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    /** Fecha y hora de la última modificación del registro. */
    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false)
    private Instant lastModifiedDate;
}
