package com.logitrack.shipment.repository;

import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.model.Envio;
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
public interface EnvioRepository extends JpaRepository<Envio, UUID> {

    // ============================================
    // Búsqueda básica por identificador único
    // ============================================

    /**
     * Buscar un envío por su código de seguimiento
     *
     * @param codigoSeguimiento
     * @return {@link Optional} con el envío.
     */
    Optional<Envio> findByCodigoSeguimiento(UUID codigoSeguimiento);

    /**
     * Buscar un envío por su estado
     *
     * @param estado
     * @return {@link Optional} con el envío.
     */
    List<Envio> findByEstado(EstadoEnvio estado);

}
