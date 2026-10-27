
package com.logitrack.shipment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.model.Envio;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, UUID> {

    Optional<Envio> findByCodigoSeguimiento(UUID codigoSeguimiento);

    List<Envio> findByEstado(EstadoEnvio estado);

    /**
     * Asigna un repartidor únicamente si el envío conserva el estado esperado
     * y todavía no tiene un repartidor asignado.
     *
     * @return 1 si se actualizó una fila; 0 si no se cumplieron las condiciones.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        update Envio e
           set e.repartidorId = :repartidorId,
               e.version = e.version + 1
         where e.id = :envioId
           and e.estado = :estado
           and e.repartidorId is null
        """)
    int asignarRepartidorSiDisponible(
            @Param("envioId") UUID envioId,
            @Param("repartidorId") UUID repartidorId,
            @Param("estado") EstadoEnvio estado);

    /**
     * Compatibilidad con el nombre anterior del método.
     */
    default int asignarRepartidorSiPendienteSinAsignacion(
            UUID envioId,
            UUID repartidorId) {

        return asignarRepartidorSiDisponible(
                envioId,
                repartidorId,
                EstadoEnvio.PENDIENTE);
    }

    List<Envio> findByClienteId(UUID clienteId);

    List<Envio> findByClienteIdAndEstado(
            UUID clienteId, EstadoEnvio estado);

    List<Envio> findByRepartidorId(UUID repartidorId);

    List<Envio> findByRepartidorIdAndEstado(
            UUID repartidorId, EstadoEnvio estado);
}
