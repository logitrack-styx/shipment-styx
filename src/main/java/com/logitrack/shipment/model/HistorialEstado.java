package com.logitrack.shipment.model;
import com.logitrack.shipment.enums.EstadoEnvio;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "historial_estados")
@Entity
/**
 * Registro de auditoría de un cambio (o alta) de estado de un envío.
 *
 * <p>Contrato fijado en la sección 2.6 del plan de trabajo. Lo persiste
 * exclusivamente {@code HistorialEstadoListener}: {@code Envio.historial} no
 * usa cascada de persistencia, por lo que esta entidad nunca se guarda en
 * cascada desde Envio.</p>
 *
 * <p>Cada evento aceptado genera exactamente un registro. Los cambios de
 * estado inválidos no generan historial porque nunca llegan a publicarse
 * como evento.</p>
 *
 * @author TheMiggaCool
 */
public class HistorialEstado {

    /**
     * Id del historial
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Envío relacionado
     * Relación envío 1-1 historial de estados
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "envio", nullable = false)
    private Envio envio;

    /**
     * Estado previo al cambio. Nulo únicamente en el registro de alta inicial.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior")
    private EstadoEnvio estadoAnterior;

    /**
     * Estado posterior al cambio (o estado inicial, en el alta).
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false)
    private EstadoEnvio estadoNuevo;

    /**
     * Fecha y hora a la que se realizó el cambio de estado
     */
    @NotNull
    private LocalDateTime fechaHora;

    /**
     * Crea un registro de historial.
     *
     * @param envio          envío al que pertenece el registro
     * @param estadoAnterior estado previo (null para el alta inicial)
     * @param estadoNuevo    estado posterior al cambio
     * @param fechaHora      momento en que se produjo el cambio
     */
    @Builder
    public HistorialEstado(Envio envio, EstadoEnvio estadoAnterior, EstadoEnvio estadoNuevo, LocalDateTime fechaHora) {
        this.envio = envio;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.fechaHora = fechaHora;
    }
}
