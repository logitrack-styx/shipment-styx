package com.logitrack.shipment.listener;

import java.time.LocalDateTime;

import com.logitrack.shipment.event.EnvioEstadoCambiadoEvent;
import com.logitrack.shipment.model.HistorialEstado;
import com.logitrack.shipment.repository.HistorialEstadoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persiste el historial de cambios de estado de un envío dentro de la misma
 * transacción que originó el cambio (contrato 2.5 y 2.6 del plan de trabajo).
 *
 * <p>Reglas respetadas por este listener:</p>
 * <ul>
 *   <li>{@code @EventListener} síncrono, sin {@code @Async}.</li>
 *   <li>{@code @Transactional(propagation = MANDATORY)}: exige que ya exista
 *       una transacción activa (la del servicio que publica el evento). Si no
 *       la hay, Spring lanza {@code IllegalTransactionStateException} en vez
 *       de abrir una nueva.</li>
 *   <li>No usa {@code @TransactionalEventListener(AFTER_COMMIT)}: si falla la
 *       persistencia del historial, toda la operación original se revierte.</li>
 *   <li>No modifica el estado del envío ni contiene lógica de asignación: solo
 *       traduce el evento en un registro de auditoría.</li>
 * </ul>
 *
 * @author TheMiggaCool
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class HistorialEstadoListener {

    private final HistorialEstadoRepository historialEstadoRepository;

    /**
     * Reacciona a un cambio (o alta) de estado de un envío persistiendo el
     * registro de historial correspondiente.
     *
     * @param evento evento publicado por el servicio de origen dentro de su transacción
     */
    @EventListener
    @Transactional(propagation = Propagation.MANDATORY)
    public void onEnvioEstadoCambiado(EnvioEstadoCambiadoEvent evento) {
        HistorialEstado historial = HistorialEstado.builder()
                .envio(evento.envio())
                .estadoAnterior(evento.estadoAnterior())
                .estadoNuevo(evento.estadoNuevo())
                .fechaHora(LocalDateTime.now())
                .build();

        historialEstadoRepository.save(historial);

        log.info("Historial registrado para envío {}: {} -> {}",
                evento.envio().getId(), evento.estadoAnterior(), evento.estadoNuevo());
    }
}
