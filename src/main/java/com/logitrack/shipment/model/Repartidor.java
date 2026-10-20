package com.logitrack.shipment.model;

import java.util.UUID;

import com.logitrack.shipment.enums.EstadoRepartidor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.logitrack.shipment.audit.AuditableEntity;

/**
 * Representa a un repartidor registrado en LogiTrack.
 * <p>
 * Esta entidad se persiste en la tabla {@code repartidores} y contiene
 * la información de identificación, nombre, tipo de vehículo y estado
 * actual del repartidor.
 * </p>
 *
 * <p>
 * Hereda de {@link AuditableEntity} para incorporar los atributos
 * y funcionalidades de auditoría definidos en la clase base.
 * </p>
 *
 * <p>
 * Lombok genera automáticamente los métodos de acceso, comparación,
 * constructores y el patrón Builder.
 * </p>
 *
 * @author NikoRKM
 * @version 1.0
 * @see AuditableEntity
 * @see EstadoRepartidor
 */
@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "repartidores")
public class Repartidor extends AuditableEntity {
	
	/**
     * Identificador único del repartidor.
     * <p>
     * Se genera automáticamente mediante la estrategia UUID
     * al persistir la entidad.
     * </p>
     */
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	/**
     * Nombre del repartidor.
     * <p>
     * Es obligatorio y debe contener entre 2 y 100 caracteres.
     * Se almacena en una columna no nula con una longitud máxima
     * de 100 caracteres.
     * </p>
     */
	@NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
	private String nombre;
	
	/**
     * Tipo de vehículo utilizado por el repartidor.
     * <p>
     * Debe contener entre 2 y 100 caracteres. La columna asociada
     * no permite valores nulos y tiene una longitud máxima de
     * 30 caracteres.
     * </p>
     */
	@NotBlank
	@Size(min = 2, max = 100)
    @Column(nullable = false, length = 30)
	private String tipoVehiculo;
	
	/**
     * Estado actual del repartidor dentro del sistema.
     * <p>
     * El valor de la enumeración se almacena como una cadena de texto
     * en una columna no nula con una longitud máxima de 20 caracteres.
     * Los valores permitidos están definidos en {@link EstadoRepartidor}.
     * </p>
     */
	@Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
	private EstadoRepartidor estadoRepartidor;

}
