package com.logitrack.shipment.Repository;

import com.logitrack.shipment.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface EnvioRepository extends JpaRepository<Envio, UUID> {

    // ============================================
    // Búsqueda básica por identificador único
    // ============================================

    /**
     * Buscar un envío por su código de seguimiento
     * @param codigoSeguimiento
     * @return {@link Optional} con el envío.
     */
    Optional<Envio> findByNumeroSeguimiento(UUID codigoSeguimiento);

}
