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
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Representa una entidad de envío dentro del sistema LogiTrack.
 * Gestiona el ciclo de vida del paquete, sus dimensiones, direcciones de origen
 * y destino, asignación de repartidores y validaciones de cambio de estado.
 *
 * Extiende de {@link AuditableEntity} para el control de auditoría (fechas de creación/modificación).
 *
 * @author dominox45
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "envios")
public class Envio extends AuditableEntity {

    /**
     * Identificador único universal del envío (Clave primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Código único generado automáticamente para el seguimiento externo del envío.
     */
    @Column(name = "codigo_seguimiento", nullable = false, unique = true, updatable = false)
    private UUID codigoSeguimiento;

    /**
     * Peso del paquete en kilogramos.
     */
    @Column(nullable = false)
    private double peso;

    /**
     * Ancho del paquete en centímetros.
     */
    @Column(nullable = false)
    private double ancho;

    /**
     * Alto del paquete en centímetros.
     */
    @Column(nullable = false)
    private double alto;

    /**
     * Largo del paquete en centímetros.
     */
    @Column(nullable = false)
    private double largo;

    /**
     * Estado actual en el que se encuentra el envío.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEnvio estado;

    /**
     * Dirección física de origen de donde se retira el paquete.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "origen_calle", nullable = false)),
            @AttributeOverride(name = "numero", column = @Column(name = "origen_numero", nullable = false)),
            @AttributeOverride(name = "ciudad", column = @Column(name = "origen_ciudad", nullable = false)),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "origen_codigo_postal", nullable = false))
    })
    private Direccion origen;

    /**
     * Dirección física de destino a donde se entregará el paquete.
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "calle", column = @Column(name = "destino_calle", nullable = false)),
            @AttributeOverride(name = "numero", column = @Column(name = "destino_numero", nullable = false)),
            @AttributeOverride(name = "ciudad", column = @Column(name = "destino_ciudad", nullable = false)),
            @AttributeOverride(name = "codigoPostal", column = @Column(name = "destino_codigo_postal", nullable = false))
    })
    private Direccion destino;

    /**
     * Identificador del cliente que solicita y es dueño del envío.
     */
    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    /**
     * Identificador del repartidor encargado de trasladar el envío (puede ser nulo si no ha sido asignado).
     */
    @Column(name = "repartidor_id")
    private UUID repartidorId;

    /**
     * Historial de cambios de estado registrados para este envío.
     */
    @OneToMany(mappedBy = "envio")
    private List<HistorialEstado> historial = new ArrayList<>();

    /**
     * Constructor principal para crear una nueva instancia de un envío.
     * Al crearse, el envío se inicializa por defecto en estado {@link EstadoEnvio#PENDIENTE}
     * y se le genera un código de seguimiento único.
     *
     * @param clienteId Identificador único del cliente (obligatorio).
     * @param origen    Dirección de origen del envío.
     * @param destino   Dirección de destino del envío.
     * @param peso      Peso del paquete.
     * @param ancho     Ancho del paquete.
     * @param alto      Alto del paquete.
     * @param largo     Largo del paquete.
     * @throws IllegalArgumentException si el {@code clienteId} es nulo.
     */
    public Envio(
            UUID clienteId,
            Direccion origen,
            Direccion destino,
            double peso,
            double ancho,
            double alto,
            double largo) {

        if (clienteId == null) {
            throw new IllegalArgumentException("El clienteId es obligatorio");
        }

        this.clienteId = clienteId;
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
     * Verifica si el envío se encuentra en condiciones de ser cancelado.
     * Un envío solo puede cancelarse si está en estado {@link EstadoEnvio#PENDIENTE}.
     *
     * @return true si puede cancelarse, false en caso contrario.
     */
    public boolean puedeCancelarse() {
        return estado == EstadoEnvio.PENDIENTE;
    }

    /**
     * Asigna un repartidor al envío.
     * Requiere que el envío esté en estado {@link EstadoEnvio#PENDIENTE}, que no posea
     * un repartidor asignado previamente y que el ID recibido sea válido.
     *
     * @param repartidorId Identificador único del repartidor a asignar.
     * @throws TransicionEstadoInvalidaException si el envío no está pendiente o ya cuenta con un repartidor.
     * @throws IllegalArgumentException          si el {@code repartidorId} es nulo.
     */
    public void asignarRepartidor(UUID repartidorId) {
        if (estado != EstadoEnvio.PENDIENTE) {
            throw new TransicionEstadoInvalidaException(
                    "Solo se puede asignar repartidor a un envío PENDIENTE");
        }

        if (this.repartidorId != null) {
            throw new TransicionEstadoInvalidaException(
                    "El envío ya tiene un repartidor asignado");
        }

        if (repartidorId == null) {
            throw new IllegalArgumentException("El repartidorId es obligatorio");
        }

        this.repartidorId = repartidorId;
    }

    /**
     * Cambia el estado actual del envío a un nuevo estado, validando las reglas de negocio
     * y las transiciones permitidas.
     *
     * @param nuevo El nuevo estado al cual se desea transicionar.
     * @throws TransicionEstadoInvalidaException si el nuevo estado es nulo, la transición no está permitida,
     *                                           o si se intenta pasar a {@link EstadoEnvio#EN_RUTA} sin un repartidor asignado.
     */
    public void cambiarEstado(EstadoEnvio nuevo) {
        if (nuevo == null || !esTransicionPermitida(nuevo)) {
            throw new TransicionEstadoInvalidaException(
                    "Transición no permitida: " + estado + " -> " + nuevo);
        }

        if (nuevo == EstadoEnvio.EN_RUTA && repartidorId == null) {
            throw new TransicionEstadoInvalidaException(
                    "No se puede pasar a EN_RUTA sin repartidor asignado");
        }

        this.estado = nuevo;
    }

    /**
     * Cancela el envío cambiando su estado a {@link EstadoEnvio#CANCELADO}.
     *
     * @throws TransicionEstadoInvalidaException si el envío no se encuentra en un estado que permita su cancelación.
     */
    public void cancelar() {
        cambiarEstado(EstadoEnvio.CANCELADO);
    }

    /**
     * Evalúa si una transición desde el estado actual hacia el estado especificado es válida.
     * <ul>
     *   <li>{@link EstadoEnvio#PENDIENTE} permite pasar a {@link EstadoEnvio#EN_RUTA} o {@link EstadoEnvio#CANCELADO}.</li>
     *   <li>{@link EstadoEnvio#EN_RUTA} permite pasar a {@link EstadoEnvio#ENTREGADO}.</li>
     *   <li>{@link EstadoEnvio#ENTREGADO} y {@link EstadoEnvio#CANCELADO} son estados finales (no permiten más transiciones).</li>
     * </ul>
     *
     * @param nuevo El estado destino a evaluar.
     * @return true si la transición es permitida, false en caso contrario.
     */
    private boolean esTransicionPermitida(EstadoEnvio nuevo) {
        return switch (estado) {
            case PENDIENTE ->
                    nuevo == EstadoEnvio.EN_RUTA || nuevo == EstadoEnvio.CANCELADO;
            case EN_RUTA -> nuevo == EstadoEnvio.ENTREGADO;
            case ENTREGADO, CANCELADO -> false;
        };
    }
}