package com.logitrack.shipment.model;

import java.util.UUID;

import com.logitrack.shipment.enums.EstadoRepartidor;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.logitrack.shipment.audit.AuditableEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Repartidores")
public class Repartidor extends AuditableEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	private String nombre;
	private String tipoVehiculo;
	@Enumerated(EnumType.STRING)
	private EstadoRepartidor estadoRepartidor;

}
