package com.logitrack.shipment.dto.request;

import com.logitrack.shipment.model.Direccion;

import jakarta.validation.constraints.NotBlank;

/**
 * Dirección recibida y devuelta por la API, desacoplada de la entidad JPA.
 *
 * @param calle        nombre de la calle
 * @param numero       numeración
 * @param ciudad       ciudad
 * @param codigoPostal código postal
 * @author dominox45
 */
public record DireccionDto(
        @NotBlank String calle,
        @NotBlank String numero,
        @NotBlank String ciudad,
        @NotBlank String codigoPostal) {

    /**
     * Crea el DTO a partir de la entidad.
     *
     * @param direccion dirección persistida
     * @return DTO equivalente
     */
    public static DireccionDto from(Direccion direccion) {
        return new DireccionDto(direccion.getCalle(), direccion.getNumero(),
                direccion.getCiudad(), direccion.getCodigoPostal());
    }

    /**
     * Convierte el DTO en el objeto de valor embebido del dominio.
     *
     * @return dirección para usar en la entidad
     */
    public Direccion aEntidad() {
        return Direccion.builder().calle(calle).numero(numero)
                .ciudad(ciudad).codigoPostal(codigoPostal).build();
    }
}