# Cambios propuestos para cerrar shipment-service (responsable A)

## Cambios incluidos

1. **Seguridad JWT local**: Resource Server con decoder RSA de algoritmo RS256; validación de firma, issuer, vigencia, `exp` obligatorio, `sub` UUID y `roles` con un único rol permitido. El material de clave pública de prueba vive exclusivamente en `src/test/resources`; el contenedor exige configurar la clave pública montada.

2. **Autorización por rol y pertenencia**: reglas HTTP por endpoint y verificaciones en `EnvioServiceImpl` para lectura por ID/tracking, cancelación y cambio de estado. Para CLIENTE, el propietario se deriva del `sub` y se ignora el `clienteId` enviado en el cuerpo.

3. **Errores comunes**: 401 y 403 del filtro de seguridad en JSON; excepciones de autorización de la capa de servicio devuelven 403; timestamps de errores con offset UTC.

4. **Asignación segura**: se elimina la transacción que envolvía la llamada HTTP a `courier-service`; tras consultar disponibilidad, la asignación se realiza mediante actualización condicional atómica. La entidad añade `@Version` y la migración V3 agrega la columna correspondiente.

5. **Cliente de courier**: URL y timeouts configurables; traducción de errores externos conforme al contrato; respuesta inválida o inconsistente produce 502, no disponibilidad produce 409, timeout/5xx produce 503, 404 conserva 404 y 401/403 externos se traducen a 502.

6. **Configuración y operación**: import opcional de Config Server, puerto contractual 8083, `ddl-auto: validate`, Actuator healthcheck, Dockerfile multietapa con usuario no root y Compose local de shipment-service + MySQL.

7. **Pruebas añadidas/ajustadas**: integración de seguridad 401/403, pruebas de pertenencia, Testcontainers con MySQL real y prueba de asignaciones concurrentes.

8. **Documentación**: README actualizado y guía de migración con política explícita para identidades históricas no vinculables.

## Verificación pendiente obligatoria

La versión candidata debe validarse con JDK 25 y Docker:

```powershell
.\mvnw.cmd -DskipTests test-compile
.\mvnw.cmd test
.\mvnw.cmd "-Dtest=EnvioRepositoryTest" test