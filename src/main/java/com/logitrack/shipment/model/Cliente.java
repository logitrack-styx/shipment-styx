package com.logitrack.shipment.model;

import java.util.UUID;
import java.util.Locale;

import com.logitrack.shipment.audit.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;

/**
 * Representa a un cliente registrado en LogiTrack.
 *
 * <p>Esta entidad almacena los datos del cliente en la base de datos.
 * Para exponer información mediante la API REST deben utilizarse DTOs.</p>
 *
 * @author dominox45
 */
@Entity
@Table(name = "clientes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)

public class Cliente extends AuditableEntity {

    /**
     * Identificador único del cliente. Lo genera la base de datos/JPA; no tiene setter.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Nombre del cliente.
     */
    @Setter
    @NotBlank
    @Size(min = 2, max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    /**
     * Correo electrónico del cliente.
     */
    @NotBlank
    @Email
    @Size(max = 254)
    @Column(nullable = false, unique = true, length = 254)
    private String email;

    /**
     * Número de teléfono del cliente.
     */
    @Setter
    @NotBlank
    @Size(max = 25)
    @Column(nullable = false, length = 25)
    private String telefono;

    /**
     * Crea un cliente con sus datos principales.
     *
     * @param nombre   nombre del cliente
     * @param email    correo electrónico del cliente
     * @param telefono número de teléfono del cliente
     */
    public Cliente(String nombre, String email, String telefono) {
        this.nombre = nombre;
        this.email = normalizarEmail(email);
        this.telefono = telefono;
    }

    /**
     * Asigna el correo electrónico normalizado (sin espacios laterales y en minúsculas).
     *
     * @param email correo electrónico del cliente
     */
    public void setEmail(String email) {
        this.email = normalizarEmail(email);
    }

    private static String normalizarEmail(String email) {
        return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
    }
}