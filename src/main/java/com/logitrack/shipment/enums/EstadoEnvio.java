package com.logitrack.shipment.enums;

/**
 * Define los estados posibles durante el ciclo de vida de un envío.
 *
 * <p>Las transiciones permitidas entre estos estados deben
 * controlarse en la lógica de negocio.</p>
 *
 * @author dominox45
 */
public enum EstadoEnvio {

    /** El envío fue registrado, pero todavía no está en ruta. */
    PENDIENTE,

    /** El envío se encuentra en proceso de entrega. */
    EN_RUTA,

    /** El envío fue entregado al destinatario. */
    ENTREGADO,

    /** El envío fue cancelado. */
    CANCELADO
}