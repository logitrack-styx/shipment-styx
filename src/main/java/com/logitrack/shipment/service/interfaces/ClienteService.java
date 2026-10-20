package com.logitrack.shipment.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.logitrack.shipment.dto.request.ClienteRequest;
import com.logitrack.shipment.dto.response.ClienteResponse;

/**
 * Define las operaciones de servicio relacionadas con la gestión
 * de clientes en el sistema.
 * <p>
 * Proporciona métodos para registrar nuevos clientes, obtener el listado
 * de clientes registrados y consultar un cliente por su identificador único.
 * </p>
 *
 * @author NikoRKM
 * @version 1.0
 * @see ClienteRequest
 * @see ClienteResponse
 */
public interface ClienteService {

	 /**
     * Registra un nuevo cliente en el sistema.
     *
     * @param clienteRequest objeto que contiene los datos necesarios
     *                       para registrar al cliente.
     * @return un objeto {@link ClienteResponse} con la información
     *         del cliente creado.
     */
	public ClienteResponse create(ClienteRequest clienteRequest);
	
	/**
     * Obtiene la lista de todos los clientes registrados en el sistema.
     *
     * @return una lista de objetos {@link ClienteResponse} con la
     *         información de los clientes registrados. La lista estará
     *         vacía si no existen clientes.
     */
	public List<ClienteResponse> findAll();
	
	/**
     * Busca un cliente por su identificador único.
     *
     * @param id identificador UUID del cliente que se desea consultar.
     * @return un objeto {@link ClienteResponse} con la información
     *         del cliente encontrado.
     */
	public ClienteResponse findById(UUID id);
	
}
