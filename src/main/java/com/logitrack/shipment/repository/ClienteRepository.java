package com.logitrack.shipment.repository;

import com.logitrack.shipment.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Clase con métodos del negocio implementados con funciones de JpaRepository
 * @author TheMiggaCool
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    // ============================================
    // Búsqueda básica por identificador único
    // ============================================

    /**
     * Buscar un cliente por su ID
     *
     * @param id
     * @return {@link Optional} con datos del cliente.
     */
    Optional<Cliente> findByIdCliente(UUID id);
}
