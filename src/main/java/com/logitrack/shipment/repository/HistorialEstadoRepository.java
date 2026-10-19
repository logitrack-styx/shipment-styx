package com.logitrack.shipment.repository;

import com.logitrack.shipment.model.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Clase con métodos del negocio implementados con funciones de JpaRepository
 * @author TheMiggaCool
 */
@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, Long> {

    // ============================================
    // Búsqueda básica por identificador único
    // ============================================

    /**
     * Buscar un historial por su ID
     *
     * @param id
     * @return {@link Optional} con datos del historial.
     */
    Optional<HistorialEstado> findByIdHistorialEstado(Long id);
}
