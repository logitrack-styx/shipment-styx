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

@Data
@EqualsAndHashCode(callSuper=false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(name = "repartidores")
public class Repartidor extends AuditableEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
	private String nombre;
	
	@Size(min = 2, max = 100)
    @Column(nullable = false, length = 30)
	private String tipoVehiculo;
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
	private EstadoRepartidor estadoRepartidor;

}
