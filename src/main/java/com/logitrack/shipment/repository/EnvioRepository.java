package com.logitrack.shipment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.model.Envio;

/**
 * Repositorio de persistencia de envíos.
 *
 * @author dominox45
 */
@Repository
public interface EnvioRepository extends JpaRepository<Envio, UUID> {

    /**
     * Busca un envío por su código de seguimiento.
     *
     * @param codigoSeguimiento código de seguimiento único del envío
     * @return el envío, o vacío si no existe
     */
    Optional<Envio> findByCodigoSeguimiento(UUID codigoSeguimiento);

    /**
     * Lista los envíos que se encuentran en un estado determinado.
     *
     * @param estado estado a filtrar
     * @return envíos en ese estado; lista vacía si no hay ninguno
     */
    List<Envio> findByEstado(EstadoEnvio estado);

}
