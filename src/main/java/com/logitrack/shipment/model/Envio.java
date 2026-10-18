package com.logitrack.shipment.model;
import com.logitrack.shipment.audit.AuditableEntity;
import com.logitrack.shipment.enums.EstadoEnvio;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "envios")
@Entity

/**
 * Representa un envio en LogiTrack
 *
 * <p>Esta entidad almacena los datos de los envios en la base de datos.
 * Para exponer información mediante la API REST deben utilizarse DTOs.</p>
 *
 * @author TheMiggaCool
 */
public class Envio extends AuditableEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Builder.Default
    @Column(name = "codigo_seguimiento", nullable = false, unique = true)
    private UUID codigoSeguimiento = UUID.randomUUID();

    @NotNull
    @Positive
    @Column(name = "peso", nullable = false)
    private float peso;

    @NotNull
    @Positive
    @Column(name = "ancho", nullable = false)
    private float ancho;

    @NotNull
    @Positive
    @Column(name = "alto", nullable = false)
    private float alto;

    @NotNull
    @Positive
    @Column(name = "largo", nullable = false)
    private float largo;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoEnvio estado = EstadoEnvio.PENDIENTE;

    /**
     * Dado que ambas propiedades, tanto Origen como Destino, utilizan la misma clase,
     * sus atributos producirán nombres de columnas duplicados. Para evitarlo, utilizamos
     * @AttributeOverrides
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "origen_calle")),
            @AttributeOverride(name = "numero", column = @Column(name = "origen_numero")),
            @AttributeOverride(name = "ciudad", column = @Column(name = "origen_ciudad")),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "origen_codigo_postal"))
    })
    private Direccion origen;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "destino_calle")),
            @AttributeOverride(name = "numero", column = @Column(name = "destino_numero")),
            @AttributeOverride(name = "ciudad", column = @Column(name = "destino_ciudad")),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "destino_codigo_postal"))
    })
    private Direccion destino;

    /** Relación envios n-1 cliente
      */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;


    /** Relación envios n-1 repartidor
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "repartidor_id", nullable = true)
    private Repartidor repartidor;


    /** Métodos propios de los envíos
     */
    public boolean puedeCancelarse(){
        return this.estado == EstadoEnvio.PENDIENTE;
    };
}
