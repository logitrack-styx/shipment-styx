# Arranque local de LogiTrack y estado de integración

Revisión del 29 de octubre de 2026. Este documento distingue el estado observado
en Git, el arranque que el usuario comprobó en Windows y lo pendiente de verificar.
No certifica la entrega completa del equipo.

## Referencias revisadas

| Repositorio | Rama | SHA revisado |
|---|---|---|
| shipment-styx | develop | 1532eb8a6d574d5f5489a74f23b34825a5539d60 |
| security-styx | develop | 108e4ad8b4b159970899d6291c354b66d97a8d5c |
| courier-service | develop | 1b8f31850d397e8d2ee94cbef0353e051653c4b9 |
| config-server | develop | a74d5ccbf7a3cd4d32772557d5b70b511ca6b6fa |
| styx-config | main | 8daf96a (identificador abreviado de la revisión local) |

## Lo comprobado en la sesión de Windows

- Shipment arrancó en 8083 con Java 25 y clave pública externa.
- La base local heredada `logitrack` contiene `envios` e `historial_estados`.
- El usuario verificó ausencia de direcciones nulas y de columna `version` antes de migrar.
- Se utilizó baseline 1 exclusivamente para esa base: Flyway aplicó V2 y V3.
- El historial mostró BASELINE 1, SQL 2 y SQL 3 exitosos; `version` es BIGINT NOT NULL DEFAULT 0.
- No se ejecuta nuevamente baseline, no se modifican V1–V3 y no se borran tablas heredadas.
- Security no llegó a arrancar: se observaron fallos de generación Lombok en Java 25.
- La verificación de JWT reales emitidos por security y el flujo E2E siguen pendientes.

Las credenciales MySQL y los PEM reales permanecen fuera de Git. El arranque previo
no demuestra que las pruebas de esta nueva rama hayan pasado.

## Clonar sin anidar repositorios

Desde la raíz de shipment (en Windows puede llamarse `Spring Web`):

```powershell
& .\scripts\Clone-LogiTrack.ps1
```

El script conserva shipment en su carpeta actual y clona como hermanos
`security-styx`, `courier-service`, `config-server` y `styx-config`.
No cambia repositorios existentes por defecto. Para actualizar árboles limpios:

```powershell
& .\scripts\Clone-LogiTrack.ps1 -UpdateExisting
```

No ejecutar mientras haya cambios sin guardar. Una actualización de ramas no
sustituye la coordinación con B/C ni integra automáticamente esta rama en develop.

## Preparar y ejecutar shipment

Requisitos: JDK 25, MySQL accesible, Git y la clave pública del par de security.
Docker debe estar instalado e iniciado para los tests MySQL/Testcontainers.

Copiar únicamente `public.pem` a `shipment/secrets/jwt-public.pem`.
La clave privada permanece exclusivamente en security. No generar un par distinto
para cada servicio. No incluir las claves reales en imágenes ni commits.

Para la base heredada ya migrada de esta sesión:

```powershell
& .\scripts\Start-ShipmentLocal.ps1 -Database logitrack -DatabaseUser shipment_app
```

Para una base propia preparada por el equipo:

```powershell
& .\scripts\Start-ShipmentLocal.ps1 -Database logitrack_shipment -DatabaseUser shipment_app
```

El script pide la contraseña sin mostrarla. Fija `ddl-auto: validate`, desactiva
baseline automático y configura issuer, clave pública y URLs locales. No crea ni
migra identidades históricas. Mantener abierta la terminal del proceso.

Comprobar desde otra terminal:

```powershell
Invoke-RestMethod http://localhost:8083/actuator/health
Start-Process http://localhost:8083/swagger-ui.html
```

El endpoint de salud es público. Las operaciones de negocio requieren un JWT real.
Swagger permite Authorize mediante `bearerAuth`; ingresar el token sin compartirlo.

## Config Server

En otra terminal, desde `config-server`:

```powershell
$env:CONFIG_GIT_URI = 'https://github.com/logitrack-styx/styx-config.git'
.\mvnw.cmd spring-boot:run
```

Comprobar `http://localhost:8888/shipment-service/dev`. Actualmente el YAML remoto
de shipment tiene `ddl-auto: update` y propiedades `logitrack.*` que Java no consume.
B debe corregirlo a `validate`, `security.jwt.*` y `courier.service.url`. No afirmar
centralización validada solo por recibir 200 del servidor. La variable
`SPRING_JPA_HIBERNATE_DDL_AUTO=validate` del script protege el arranque local;
la corrección del repositorio central continúa siendo necesaria.

## Security

En su propia terminal configurar DB_URL, DB_USERNAME y DB_PASSWORD para su base
propia `logitrack_security`, previamente preparada con permisos. No reutilizar la
base de shipment para la integración final. Instalar el par recibido en
`src/main/resources/certs/private.pem` y `public.pem` según su YAML actual.
Para diagnosticar los fallos Lombok observados:

```powershell
.\mvnw.cmd "-Dmaven.compiler.proc=full" clean spring-boot:run
```

Si persiste el error, C debe revisar versión de Lombok y annotationProcessorPaths.
No se modificó ese repositorio. La rama revisada expone `/auth` y `/users`, pero
sus DTO/login todavía utilizan `username`; el contrato exige `nombre`/login email.
El código revisado genera RS256, issuer `logitrack-security`, sub UUID y un rol.
C debe confirmar arranque, cuentas de prueba y conformidad de DTO antes de E2E.

## Courier y límites actuales

El develop revisado no contiene `/couriers/{id}/disponibilidad`, requiere cambios
de MySQL/JWT/Actuator y usa una identidad generada localmente. Clonarlo no basta
para completar la integración. B debe entregar el endpoint, la base propia y el
perfil con `id == userId`. La verificación de userId necesita la decisión contractual
compartida; no se inventó un nuevo endpoint de security.

Mientras B termina, la suite de CourierClient usa un servidor HTTP local de prueba.
Esos stubs demuestran el comportamiento de shipment, no el de courier real.

## Docker de shipment y entrega final

`docker-compose.yml` es exclusivamente el entorno de desarrollo de shipment.
Contiene build local y una base propia; ahora permite configurar explícitamente
las URLs de dependencias que se ejecutan en el host mediante `.env`.
No es el Compose final de siete servicios. B prepara el final con imágenes
publicadas, DNS internos, bases independientes y sin build.

```powershell
Copy-Item .env.example .env
# Completar credenciales propias y JWT_PUBLIC_KEY_FILE en .env.
docker compose config --quiet
docker compose up -d --build
```

No sobrescribir un `.env` existente con el comando anterior. No usar `down -v`.
Las URLs host.docker.internal son de desarrollo; dentro del Compose grupal se usa
`http://courier-service:8082` y `http://config-server:8888` en la misma red.

## Pruebas y cierre de A

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
```

El empaquetado genera dos JAR: el archivo `*-exec.jar` es el ejecutable Spring Boot;
el JAR sin classifier contiene únicamente las clases. Docker copia exclusivamente
`*-exec.jar` para evitar ambigüedad entre ambos.

La suite MySQL requiere Docker. No habilitar saltos automáticos si Docker falla:
el resultado debe reflejar la falta de entorno. Para ejecutar solo las pruebas
independientes de Docker, declarar explícitamente la exclusión:

```powershell
.\mvnw.cmd '-Dtest=!EnvioRepositoryTest' test
```

Eso NO reemplaza la suite completa ni acredita concurrencia/migraciones MySQL.
Pruebas añadidas: firma JWT real y casos negativos, errores 401/403/409,
propagación de Authorization/Accept, timeout HTTP, fecha de auditoría de asignación
y dos asignaciones simultáneas en transacciones MySQL independientes.

Pendientes para cierre: ejecución íntegra con Docker; build y healthcheck de imagen;
JWT real de C; propiedad remota efectivamente consumida; E2E con courier de B;
política para UUID históricos no vinculados; imagen/tag/digest acordados.
Tarifas continúan fuera del código implementado: el contrato v1.0 las exige y
la adenda de exclusión debe quedar aprobada y registrada por el equipo.
