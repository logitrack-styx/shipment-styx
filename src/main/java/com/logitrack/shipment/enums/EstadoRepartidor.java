package com.logitrack.shipment.enums;

/**
 * Define los posibles estados de un repartidor dentro del sistema.
 * <p>
 * Este enumerado permite identificar si un repartidor se encuentra
 * habilitado para realizar sus actividades o si está inactivo.
 * </p>
 *
 * @author NikoRKM
 * @version 1.0
 * @see Repartidor
 */
public enum EstadoRepartidor {
	/**
     * Indica que el repartidor se encuentra activo y habilitado
     * para realizar sus actividades de reparto.
     */
	ACTIVO,
	/**
     * Indica que el repartidor se encuentra inactivo y no está
     * habilitado para realizar sus actividades de reparto.
     */
	INACTIVO
}
