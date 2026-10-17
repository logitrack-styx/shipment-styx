package com.logitrack.shipment.model;
import com.logitrack.shipment.enums.EstadosEnvio;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "envios")
@Entity
public class Envio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "codigo_seguimiento", nullable = false, unique = true, length = 18)
    private long codigoSeguimiento;

    @Column(name = "peso", nullable = false)
    private float peso;

    @Column(name = "ancho", nullable = false)
    private float ancho;

    @Column(name = "alto", nullable = false)
    private float alto;

    @Column(name = "largo", nullable = false)
    private float largo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadosEnvio estado = EstadosEnvio.PENDIENTE;

    /**
     * Dado que ambas propiedades, tanto Origen como Destino utilizan la misma clase,
     * sus atributos producirán nombres de columnas duplicados. Para evitarlo, utilizamos
     * @AttributeOverrides
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "origen_calle")),
            @AttributeOverride(name = "numero", column = @Column(name = "origen_numero")),
            @AttributeOverride(name = "ciudad", column = @Column(name = "origen_ciudad")),
            @AttributeOverride(name = "provincia", column = @Column(name = "origen_provincia")),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "origen_codigo_postal"))
    })
    private Direccion origen;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "destino_calle")),
            @AttributeOverride(name = "numero", column = @Column(name = "destino_numero")),
            @AttributeOverride(name = "ciudad", column = @Column(name = "destino_ciudad")),
            @AttributeOverride(name = "provincia", column = @Column(name = "destino_provincia")),
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
    @JoinColumn(name = "repartidor_id", nullable = false)
    private Repartidor repartidor;


    /** Métodos propios de los envíos
     */
    public boolean puedeCancelarse(){
        if(this.estado.equals(EstadosEnvio.PENDIENTE)){
            return true;
        }else {
            return false;
        }
    }
}
