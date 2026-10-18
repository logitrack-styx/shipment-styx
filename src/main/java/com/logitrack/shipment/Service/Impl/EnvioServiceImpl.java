package com.logitrack.shipment.Service.Impl;

import com.logitrack.shipment.Repository.EnvioRepository;
import com.logitrack.shipment.Repository.HistorialEstadoRepository;
import com.logitrack.shipment.Repository.RepartidorRepository;
import com.logitrack.shipment.Service.Interface.EnvioService;
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

    @Override
    @Transactional
    public EnvioResponse actualizarEstado(EnvioRequest envio) {
        return null;
    }

    @Override
    public EnvioResponse cancelarEnvio(EnvioRequest envio) {
        return null;
    }

    @Override
    public EnvioResponse asignarRepartidor(EnvioRequest envio, RepartidorRequest repartidor) {
        return null;
    }

    /**
     * Conviertimos un Envio a un envioResponse
     */
    public EnvioResponse toResponse(Envio envio){
        return EnvioResponse.builder().build();
    }
}
