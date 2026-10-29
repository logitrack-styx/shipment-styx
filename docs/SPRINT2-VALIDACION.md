# Validación del cierre de shipment para Sprint 2

Fecha: 29/10/2026. Responsable A. Base: `develop`,
`1532eb8a6d574d5f5489a74f23b34825a5539d60`.
Rama de trabajo: `fix/shipment-integration-readiness`; destino del PR: `develop`.

## Cambios implementados

- La actualización condicional de asignación actualiza `lastModifiedDate` y `version`.
  Mantiene una sola escritura ganadora y no cambia el estado PENDIENTE.
- La operación de asignación exige ADMIN también en el proxy del servicio.
- Los conflictos optimistas se traducen a 409 con el DTO común de errores.
- El decoder exige UUID canónico en sub, además de firma RS256, issuer, exp y rol único.
- Pruebas JWT con firma real RSA efímera: válido, vencido, issuer/firma/algoritmo
  incorrectos, subject inválido/no canónico, roles incorrectos y ausencia de exp.
- Pruebas del formato 401/403/409 y del límite transaccional durante CourierClient.
- HTTP de courier: verificación de Authorization, Accept y URL, más timeout de lectura.
- Pruebas de auditoría masiva sobre H2 y MySQL; carrera de dos hilos sobre MySQL.
- Mockito se carga como javaagent en Surefire para evitar auto-attach en Java 25.
- El JAR ejecutable se publica con classifier exec y mainClass explícita. El JAR
  sin classifier no es ejecutable. Docker copia exclusivamente `*-exec.jar`.
- OpenAPI documenta Bearer JWT y elimina el 404 de Cliente inexistente que el alta
  actual no verifica mediante consulta a security.
- Compose de desarrollo consume las URLs de dependencias definidas en .env.
- Scripts PowerShell y guía de clones, claves, arranque y migración local controlada.

Los cambios Java incorporan JavaDocs. No se modificaron las migraciones V1–V3
que ya se ejecutaron en la base local del usuario, ni repositorios de B/C.

## Evidencia ejecutada en este entorno

Entorno de validación: Linux, Temurin JDK 25.0.5, Maven Wrapper del repositorio.
Se configuró un proxy temporal solo para descargar dependencias; no forma parte
del repositorio ni debe copiarse al entorno del usuario.

| Verificación | Resultado real |
|---|---|
| Suite completa `sh mvnw -o -B test` | 84 reportados, failures 0, errors 1, skipped 0; BUILD FAILURE |
| Causa del único error | EnvioRepositoryTest: Testcontainers no encontró Docker |
| `sh mvnw -o -B '-Dtest=!EnvioRepositoryTest' clean package` | 83 tests, failures 0, errors 0, skipped 0; BUILD SUCCESS |
| Manifiesto de `*-exec.jar` | JarLauncher y Start-Class correctos; 98 entradas de dependencias BOOT-INF/lib |
| Arranque del JAR con H2 externo para smoke test | Shipment inició y GET /actuator/health devolvió HTTP 200, status UP |
| PowerShell Parser en PowerShell 7.6.9 | Ambos scripts sin errores de sintaxis |
| application.yaml y docker-compose.yml | Parseo YAML correcto |
| `git diff --check` | Sin errores |

La selección de 83 pruebas excluye explícitamente la clase MySQL completa. No son
83 pruebas más una prueba MySQL aprobada: los métodos de esa clase no pudieron
iniciarse. No se ocultó el error con disabledWithoutDocker ni se alteró el resultado
de la suite completa. La prueba concurrente MySQL está implementada, sin ejecutar.

El smoke del JAR utilizó PropertiesLauncher, el driver H2 externo de test,
Flyway desactivado, create-drop y la clave pública exclusivamente de pruebas.
El proceso se detuvo al concluir. No acredita Docker, MySQL, migraciones ni JWT
real de C, y esa configuración no se propone para producción.

Los scripts se validaron sintácticamente en PowerShell 7. No se ejecutaron contra
el equipo Windows ni se probó interacción con sus credenciales. El arranque MySQL
previo del usuario corresponde al código base; no certifica esta nueva rama.

## Bloqueos y acciones de integración

| Responsable | Pendiente |
|---|---|
| A, entorno Windows con Docker activo | Ejecutar mvnw test y mvnw clean package sin exclusiones; verificar carrera MySQL |
| A/B | Construir imagen Docker y comprobar healthcheck real; acordar namespace/tag/digest |
| B | Exponer /couriers/{id}/disponibilidad; identidad courier=userId; JWT y MySQL propios |
| B | Corregir YAML remoto de shipment: validate, security.jwt.*, courier.service.url |
| C | Confirmar arranque y token real con clave compartida; DTO/login del contrato y ADMIN inicial |
| A/B/C | Probar propiedad remota efectivamente consumida, flujo E2E y persistencia |
| A/B/C | Registrar adenda de tarifas y mecanismo de validación de userId del courier |
| A/B/C | Política de vinculación de UUID históricos; no inventar identidades ni borrar datos |

## Reporte obligatorio de A

```text
RESPONSABLE A — SHIPMENT
Repo: logitrack-styx/shipment-styx
Rama base: develop, 1532eb8a6d574d5f5489a74f23b34825a5539d60
Rama de trabajo: fix/shipment-integration-readiness
Archivos modificados: POM, Dockerfile, Compose local, README, configuración de
seguridad/OpenAPI, controlador, handler de errores, repositorio, servicio de
asignación, pruebas y docs/scripts de esta rama.
Requisitos comprobados: JWT firmado y filtros, ownership existente, dominio,
cliente HTTP, auditoría masiva H2, asignación sin transacción durante HTTP.
Pruebas: suite completa 84/0 failures/1 error/0 skipped (Docker ausente).
Selección independiente de Docker y package: 83/0/0/0, BUILD SUCCESS.
Docker build/healthcheck: no ejecutados; Docker no disponible.
JAR/healthcheck: ejecutable inspeccionado y smoke H2 HTTP 200 UP.
Imagen/tag/digest: sin publicar.
Dependencias B/C: detalladas en la tabla de bloqueos.
Adenda tarifas: pendiente de registro/aprobación compartida.
¿Listo para E2E?: NO; faltan MySQL/Docker y dependencias reales B/C.
```

## Cómo continuar en Windows

Con Docker Desktop iniciado y Java 25, desde shipment en esta rama:

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
& .\scripts\Clone-LogiTrack.ps1
& .\scripts\Start-ShipmentLocal.ps1 -Database logitrack -DatabaseUser shipment_app
```

El último comando corresponde únicamente a la base heredada ya migrada. La guía
`SPRINT2-ARRANQUE-LOCAL.md` explica la base aislada para la entrega final. No activar
baseline nuevamente. No cerrar el Sprint 2 grupal basándose solo en estas pruebas.
