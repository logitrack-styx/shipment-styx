package com.logitrack.shipment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class Direccion {
    @Column(name = "calle", nullable = false)
    private String calle;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "ciudad", nullable = false)
    private String ciudad;

    @Column(name = "codigo_postal", nullable = false)
    private String codigoPostal;

}