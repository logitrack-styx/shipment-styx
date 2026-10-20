package com.logitrack.shipment.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.logitrack.shipment.dto.request.RepartidorRequest;
import com.logitrack.shipment.dto.response.RepartidorResponse;
import com.logitrack.shipment.enums.EstadoRepartidor;

/**
 * Define las operaciones de servicio relacionadas con la gestión
 * de repartidores.
 * <p>
 * Proporciona métodos para registrar repartidores, consultar un
 * repartidor por su identificador y obtener un listado de repartidores
 * filtrado por su estado.
 * </p>
 *
 * @author NikoRKM
 * @version 1.0
 * @see Repartidor
 * @see RepartidorRequest
 * @see RepartidorResponse
 * @see EstadoRepartidor
 */
public interface RepartidorService {

	 /**
     * Registra un nuevo repartidor en el sistema.
     *
     * @param repartidorRequest objeto que contiene los datos necesarios
     *                          para registrar al repartidor.
     * @return un objeto {@link RepartidorResponse} con la información
     *         del repartidor creado.
     */
	public RepartidorResponse create(RepartidorRequest repartidorRequest);
	
	/**
     * Busca un repartidor por su identificador único.
     *
     * @param id identificador UUID del repartidor que se desea consultar.
     * @return un objeto {@link RepartidorResponse} con la información
     *         del repartidor encontrado.
     */
	public RepartidorResponse findById(UUID id);
	
	/**
	 * Obtiene una lista de repartidores, opcionalmente filtrada por su estado.
	 * <p>
	 * Si se proporciona un estado, retorna los repartidores que coinciden
	 * con dicho estado. Si el parámetro es {@code null}, retorna todos
	 * los repartidores registrados en el sistema, independientemente
	 * de su estado.
	 * </p>
	 *
	 * @param estadoRepartidor estado por el cual se desea filtrar la búsqueda;
	 *                         si es {@code null}, no se aplica ningún filtro
	 *                         por estado.
	 * @return una lista de objetos {@link RepartidorResponse} que cumplen
	 *         con el criterio de búsqueda, o todos los repartidores si
	 *         {@code estadoRepartidor} es {@code null}. La lista estará vacía
	 *         si no existen repartidores que coincidan con el criterio.
	 */
	public List<RepartidorResponse> findAll(EstadoRepartidor estadoRepartidor);
	
}
