package com.logitrack.shipment.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.logitrack.shipment.audit.AuditableEntity;
import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.exception.TransicionEstadoInvalidaException;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Representa un envío en LogiTrack y protege las reglas de su ciclo de vida.
 *
 * <p>Flujo permitido: {@code PENDIENTE -> EN_RUTA -> ENTREGADO}, o
 * {@code PENDIENTE -> CANCELADO}. Pasar a {@code EN_RUTA} requiere un
 * repartidor asignado. El estado y el repartidor solo se modifican mediante
 * los métodos de esta clase, por eso no hay setters.</p>
 *
 * <p>Para exponer información mediante la API REST deben utilizarse DTOs.</p>
 *
 * @author TheMiggaCool
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "envios")
public class Envio extends AuditableEntity {

    /** Identificador único del envío. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Código de seguimiento público; se genera al crear el envío y no cambia. */
    @Column(name = "codigo_seguimiento", nullable = false, unique = true, updatable = false)
    private UUID codigoSeguimiento;

    /** Peso del paquete. */
    @Column(nullable = false)
    private double peso;

    /** Ancho del paquete. */
    @Column(nullable = false)
    private double ancho;

    /** Alto del paquete. */
    @Column(nullable = false)
    private double alto;

    /** Largo del paquete. */
    @Column(nullable = false)
    private double largo;

    /** Estado actual del envío. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEnvio estado;

    /**
     * Dirección de origen. Origen y destino usan la misma clase, por lo que se
     * renombran las columnas con {@code @AttributeOverrides} para evitar duplicados.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "origen_calle")),
            @AttributeOverride(name = "numero", column = @Column(name = "origen_numero")),
            @AttributeOverride(name = "ciudad", column = @Column(name = "origen_ciudad")),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "origen_codigo_postal"))
    })
    private Direccion origen;

    /** Dirección de destino. */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "destino_calle")),
            @AttributeOverride(name = "numero", column = @Column(name = "destino_numero")),
            @AttributeOverride(name = "ciudad", column = @Column(name = "destino_ciudad")),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "destino_codigo_postal"))
    })
    private Direccion destino;

    /** Cliente que solicitó el envío (relación n-1). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /** Repartidor asignado (relación n-1). Es nulo hasta que se asigna, antes de pasar a EN_RUTA. */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "repartidor_id", nullable = true)
    private Repartidor repartidor;

    /**
     * Historial de cambios de estado (relación 1-n). Sin cascada: el listener
     * de auditoría guarda cada registro de forma explícita.
     */
    @OneToMany(mappedBy = "envio")
    private List<HistorialEstado> historial = new ArrayList<>();

    /**
     * Crea un envío nuevo en estado {@code PENDIENTE} con un código de seguimiento generado.
     *
     * @param cliente cliente que solicita el envío
     * @param origen  dirección de origen
     * @param destino dirección de destino
     * @param peso    peso del paquete
     * @param ancho   ancho del paquete
     * @param alto    alto del paquete
     * @param largo   largo del paquete
     */
    public Envio(Cliente cliente, Direccion origen, Direccion destino,
                 double peso, double ancho, double alto, double largo) {
        this.cliente = cliente;
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
        this.ancho = ancho;
        this.alto = alto;
        this.largo = largo;
        this.estado = EstadoEnvio.PENDIENTE;
        this.codigoSeguimiento = UUID.randomUUID();
    }

    /**
     * Indica si el envío puede cancelarse.
     *
     * @return {@code true} únicamente si el estado es {@code PENDIENTE}
     */
    public boolean puedeCancelarse() {
        return estado == EstadoEnvio.PENDIENTE;
    }

    /**
     * Asocia un repartidor al envío sin modificar su estado.
     *
     * @param repartidor repartidor a asignar
     * @throws TransicionEstadoInvalidaException si el envío no está PENDIENTE o ya tiene repartidor
     */
    public void asignarRepartidor(Repartidor repartidor) {
        if (estado != EstadoEnvio.PENDIENTE) {
            throw new TransicionEstadoInvalidaException(
                    "Solo se puede asignar repartidor a un envío PENDIENTE");
        }
        if (this.repartidor != null) {
            throw new TransicionEstadoInvalidaException(
                    "El envío ya tiene un repartidor asignado");
        }
        this.repartidor = repartidor;
    }

    /**
     * Cambia el estado del envío respetando la máquina de estados.
     *
     * @param nuevo estado de destino
     * @throws TransicionEstadoInvalidaException si la transición no está permitida
     *         o se intenta pasar a EN_RUTA sin repartidor
     */
    public void cambiarEstado(EstadoEnvio nuevo) {
        if (!esTransicionPermitida(nuevo)) {
            throw new TransicionEstadoInvalidaException(
                    "Transición no permitida: " + estado + " -> " + nuevo);
        }
        if (nuevo == EstadoEnvio.EN_RUTA && repartidor == null) {
            throw new TransicionEstadoInvalidaException(
                    "No se puede pasar a EN_RUTA sin repartidor asignado");
        }
        this.estado = nuevo;
    }

    /**
     * Cancela el envío; solo es válido desde {@code PENDIENTE}.
     *
     * @throws TransicionEstadoInvalidaException si el envío no puede cancelarse
     */
    public void cancelar() {
        cambiarEstado(EstadoEnvio.CANCELADO);
    }

    /**
     * Comprueba si la transición desde el estado actual al indicado está permitida.
     *
     * @param nuevo estado de destino
     * @return {@code true} si la transición es válida
     */
    private boolean esTransicionPermitida(EstadoEnvio nuevo) {
        return switch (estado) {
            case PENDIENTE -> nuevo == EstadoEnvio.EN_RUTA || nuevo == EstadoEnvio.CANCELADO;
            case EN_RUTA -> nuevo == EstadoEnvio.ENTREGADO;
            case ENTREGADO, CANCELADO -> false;
        };
    }
}