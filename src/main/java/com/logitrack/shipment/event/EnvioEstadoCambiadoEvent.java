package com.logitrack.shipment.event;

import com.logitrack.shipment.enums.EstadoEnvio;
import com.logitrack.shipment.model.Envio;

/**
 * Evento de dominio que comunica un alta o un cambio de estado de un envío.
 *
 * <p>Contrato fijado en la Issue 00 (sección 2.5 del plan de trabajo).
 * Propietario de la clase: Facu (Issue 02). Mauro la consume desde
 * {@code HistorialEstadoListener} (Issue 01), sin modificarla.</p>
 *
 * <p>Reglas de uso:</p>
 * <ul>
 *   <li>Al crear el envío: {@code estadoAnterior = null}, {@code estadoNuevo = PENDIENTE}.</li>
 *   <li>Al cambiar el estado: ambos valores deben contener los estados reales,
 *       anterior y nuevo.</li>
 *   <li>Se publica dentro de la misma transacción del servicio que origina el cambio.</li>
 * </ul>
 *
 * <p>El evento es un contrato de comunicación entre módulos: no contiene
 * lógica de negocio ni acceso a repositorios.</p>
 *
 * @author dominox45
 */
public record EnvioEstadoCambiadoEvent(
        Envio envio,
        EstadoEnvio estadoAnterior,
        EstadoEnvio estadoNuevo
) {
}
