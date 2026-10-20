package com.logitrack.shipment.repository;

import com.logitrack.shipment.model.HistorialEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Clase con métodos del negocio implementados con funciones de JpaRepository
 * @author TheMiggaCool
 */
@Repository
public interface HistorialEstadoRepository extends JpaRepository<HistorialEstado, UUID> {

    // ============================================
    // Búsqueda básica por identificador único
    // ============================================

    /**
     * Lista el historial de un envío ordenado cronológicamente.
     * Útil para las pruebas de integración y para exponer el historial de un envío.
     *
     * @param envioId id del envío
     * @return registros de historial del envío, del más antiguo al más reciente.
     */
    //Optional<HistorialEstado> findByIdHistorialEstado(Long id);
    List<HistorialEstado> findByEnvioIdOrderByFechaHoraAsc(UUID envioId);
}
