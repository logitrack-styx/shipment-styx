package com.logitrack.shipment.model;
import com.logitrack.shipment.enums.EstadoEnvio;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "historial_estados")
@Entity
public class HistorialEstado {

    /**
     * Id del historial
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Envío relacionado
     * Relación envío 1-1 historial de estados
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "envio", nullable = false)
    private Envio envio;

    /**
     * Listado con estados de un envío
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private EstadoEnvio estado;

    /**
     * Fecha y hora a la que se realizó el cambio de estado
     */
    @NotNull
    private LocalDateTime fechaHora;
}
