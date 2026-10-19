package com.logitrack.shipment.service.impl;

import com.logitrack.shipment.repository.EnvioRepository;
import com.logitrack.shipment.service.interfaces.EnvioService;
import com.logitrack.shipment.dto.request.EnvioRequest;
import com.logitrack.shipment.dto.request.RepartidorRequest;
import com.logitrack.shipment.dto.response.EnvioResponse;
import com.logitrack.shipment.model.Cliente;
import com.logitrack.shipment.model.Envio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Slf4j
public class EnvioServiceImpl implements EnvioService {

    private final EnvioRepository envioRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Crea y guarda un envío.
     * <p>DEBE VERIFICARSE QUE EL CLIENTE EXISTA A LA HORA DE REALIZAR UN PEDIDO.
     * En caso de no existir, es creado.</p>
     * El envío creado debe ser asignado al cliente que lo solicitó.
     * <p>FALTA IMPLEMENTAR/ARREGLAR</p>
     *
     * @param request
     * @return envioResponse
     */

    @Override
    @Transactional
    public EnvioResponse crearEnvio(EnvioRequest request){
        log.info("Iniciando proceso de creación de envío");

        // Tomamos los datos del cliente y generamos un nuevo registro
        Cliente cliente = new Cliente (request.getCliente().getNombre(), request.getCliente().getEmail(), request.getCliente().getTelefono());

        Envio envio = Envio.builder()
                .peso(request.getPeso())
                .ancho(request.getAncho())
                .alto(request.getAlto())
                .largo(request.getLargo())
                .origen(request.getOrigen())
                .destino(request.getDestino())
                .cliente(cliente)
                .build();

        Envio envioGuardado = envioRepository.save(envio);
        log.info("Envio registrado exitosamente con codigo de seguimiento: {}", envio.getCodigoSeguimiento());

        return toResponse(envioGuardado);
    }

    /**
     * Actualiza el estado de un envío.
     * FALTA IMPLEMENTAR
     *
      * @param envio
     * @return envioResponse
     */
    @Override
    @Transactional
    public EnvioResponse actualizarEstado(EnvioRequest envio) {
        return null;
    }

    /**
     * Cancela un envío en estado PENDIENTE.
     * <p>FALTA IMPLEMENTAR</p>
     *
     * @param envio
     * @return envioResponse
     */
    @Override
    public EnvioResponse cancelarEnvio(EnvioRequest envio) {
        return null;
    }

    /**
     * Asigna un repartidor ACTIVO a un envío en estado EN_RUTA.
     * <p>FALTA IMPLEMENTAR</p>
     *
     * @param envio
     * @param repartidor
     * @return envioResponse
     */
    @Override
    public EnvioResponse asignarRepartidor(EnvioRequest envio, RepartidorRequest repartidor) {
        return null;
    }

    /**
     * Convierte un Envio a un envioResponse
     * <p>FALTA IMPLEMENTAR</p>
     *
     * @param envio
     * @return envioResponse
     */
    public EnvioResponse toResponse(Envio envio){

        return EnvioResponse.builder().build();
    }
}
