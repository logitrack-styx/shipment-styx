package com.logitrack.shipment.repository;

import com.logitrack.shipment.enums.EstadoRepartidor;
import com.logitrack.shipment.model.Repartidor;
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
public interface RepartidorRepository extends JpaRepository<Repartidor, UUID> {

    // ============================================
    // Búsqueda básica por identificador único
    // ============================================
    /**
     * Buscar un repartidor por su estado
     *
     * @param estado
     * @return {@link Optional} con datos del repartidor.
     */
    //Optional<Repartidor> findByEstadoRepartidor(EstadoRepartidor estado);
	
	public List<Repartidor> findByEstadoRepartidor(EstadoRepartidor estado);
}
