<h1 align="center">📦 Sistema LogiTrack - Servicio de Envíos</h1>

<p align="center"> 
  <strong>Desarrollo y Arquitecturas Avanzadas de Software</strong><br> 
  Ingeniería en Informática · Universidad Nacional de Jujuy<br><br> 
  <img src="https://img.shields.io/badge/Java-25-orange?logo=openjdk&logoColor=white" alt="Java"> 
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.14-brightgreen?logo=springboot&logoColor=white" alt="Spring Boot"> 
  <img src="https://img.shields.io/badge/Spring%20Data-JPA-6DB33F?logo=spring&logoColor=white" alt="Spring Data JPA"> 
  <img src="https://img.shields.io/badge/MySQL-8.4-4479A1?logo=mysql&logoColor=white" alt="MySQL"> 
  <img src="https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white" alt="Docker"> 
  <img src="https://img.shields.io/badge/Maven-Wrapper-C71A36?logo=apachemaven&logoColor=white" alt="Maven"> 
  <img src="https://img.shields.io/badge/JUnit-Testing-25A162?logo=junit5&logoColor=white" alt="JUnit"> 
</p>

<hr>

<h2>📌 Descripción</h2>

<p> Microservicio de gestión y seguimiento de envíos desarrollado para la asignatura <strong>Desarrollo y Arquitecturas Avanzadas de Software</strong> de la carrera Ingeniería en Informática de la Universidad Nacional de Jujuy. </p>

<p> El proyecto modela el núcleo de un sistema logístico (LogiTrack), contemplando la gestión de clientes, repartidores, envíos y el historial de cambios de estado de los paquetes, asegurando su persistencia mediante JPA/Hibernate sobre MySQL. </p>

<p>La implementación aplica conceptos de:</p>

<ul> 
  <li>Programación Orientada a Objetos y Arquitectura en Capas.</li> 
  <li>Relaciones entre entidades y objetos de valor (Embeddables).</li> 
  <li>Tipos enumerados y validación de datos.</li> 
  <li>Persistencia mediante Jakarta Persistence (JPA).</li> 
  <li>Auditoría automática de entidades (JPA Auditing).</li> 
  <li>Eventos de dominio (Spring Events) para el registro histórico.</li> 
  <li>Manejo global de excepciones.</li> 
  <li>Pruebas unitarias y de integración.</li> 
  <li>Contenerización mediante Docker y Docker Compose.</li> 
</ul>

<hr>

<h2>🚚 Dominio del sistema</h2>

<p> El sistema representa las entidades clave en el ciclo logístico de envíos. </p>

<h3>👤 Clientes</h3>

<p> Un <code>Cliente</code> representa a la persona que solicita un envío. Cada cliente posee: </p>

<ul> 
  <li>Identificador UUID.</li> 
  <li>Nombre completo.</li> 
  <li>Email (Único).</li> 
  <li>Teléfono.</li> 
  <li>Datos de auditoría (Creación y modificación).</li> 
</ul>

<h3>🛵 Repartidores</h3>

<p> Un <code>Repartidor</code> es el encargado de transportar los envíos. Posee: </p>

<ul> 
  <li>Identificador UUID.</li> 
  <li>Nombre completo.</li> 
  <li>Tipo de vehículo.</li> 
  <li>Estado: <code>ACTIVO</code> o <code>INACTIVO</code>.</li> 
</ul>

<h3>📦 Envíos</h3>

<p> La entidad central <code>Envio</code> representa un paquete a transportar. Incluye: </p>

<ul> 
  <li>Identificador UUID.</li> 
  <li>Código de seguimiento (UUID único).</li> 
  <li>Dimensiones y peso (alto, ancho, largo).</li> 
  <li>Dirección de Origen y Destino (Objetos Embebidos).</li> 
  <li>Cliente asociado y Repartidor asignado.</li> 
  <li>Estado del envío.</li> 
</ul>

<p>El ciclo de vida de un envío transita por los siguientes estados (<code>EstadoEnvio</code>):</p>

<ul> 
  <li><code>PENDIENTE</code></li> 
  <li><code>EN_RUTA</code></li> 
  <li><code>ENTREGADO</code></li> 
  <li><code>CANCELADO</code></li> 
</ul>

<h3>📜 Historial de Estados</h3>

<p> Para garantizar la trazabilidad, se registra cada transición mediante la entidad <code>HistorialEstado</code>, la cual almacena: </p>

<ul> 
  <li>Envío referenciado.</li> 
  <li>Estado anterior y Estado nuevo.</li> 
  <li>Fecha y hora exacta del cambio.</li> 
</ul>

<hr>

<h2>🧱 Arquitectura</h2>

<p> La aplicación sigue un diseño multicapa basado en REST, utilizando DTOs para el aislamiento del dominio y Eventos de Spring para desacoplar el registro histórico. </p>

<pre><code>┌─────────────────────────────┐
│       Capa Controlador      │
│  Endpoints REST / Swagger   │
└──────────────┬──────────────┘
               │
┌──────────────▼──────────────┐
│        Capa Servicio        │
│   Lógica de Negocio + DTOs  │
└──────────────┬──────────────┘
               │ Eventos
┌──────────────▼──────────────┐
│         Repository          │
│ Spring Data JPA / Hibernate │
└──────────────┬──────────────┘
               │
┌──────────────▼──────────────┐
│            MySQL            │
│  Base de datos Relacional   │
└─────────────────────────────┘</code></pre>

<hr>

<h2>🛠️ Tecnologías</h2>

<table> 
  <thead> 
    <tr> <th>Tecnología</th> <th>Versión / Uso</th> </tr> 
  </thead> 
  <tbody> 
    <tr> <td><strong>Java</strong></td> <td>25</td> </tr> 
    <tr> <td><strong>Spring Boot</strong></td> <td>3.5.14</td> </tr> 
    <tr> <td><strong>Spring Data JPA</strong></td> <td>Persistencia y repositorios</td> </tr> 
    <tr> <td><strong>MySQL</strong></td> <td>8.4</td> </tr> 
    <tr> <td><strong>Docker & Compose</strong></td> <td>Contenerización de BD y App</td> </tr> 
    <tr> <td><strong>Lombok</strong></td> <td>Generación de código (Getters/Setters/Builders)</td> </tr> 
    <tr> <td><strong>Springdoc OpenAPI</strong></td> <td>Documentación automática de API</td> </tr> 
    <tr> <td><strong>JUnit 5 & H2</strong></td> <td>Pruebas automatizadas</td> </tr> 
  </tbody> 
</table>

<hr>

<h2>🗄️ Persistencia e integridad</h2>

<p> El mapeo objeto-relacional se realiza con Hibernate. </p>

<h3>Identificadores y Base de Datos</h3>

<p> Todas las entidades utilizan identificadores de tipo <code>UUID</code> generados automáticamente. Para optimizar el rendimiento en MySQL 8, estos se almacenan como <code>BINARY(16)</code>. El script inicial (<code>init.sql</code>) define explícitamente las tablas, claves foráneas, restricciones de unicidad (ej. email y código de seguimiento) y validaciones CHECK para los estados. </p>

<h3>Objetos Embebidos (Embeddables)</h3>

<p> La dirección se modela como un objeto de valor (Value Object) <code>Direccion</code> utilizando la anotación <code>@Embeddable</code>. Esto permite que los atributos <code>calle</code>, <code>numero</code>, <code>ciudad</code> y <code>codigoPostal</code> se almacenen directamente en la tabla de envíos, agrupando lógicamente el concepto en el dominio Java. </p>

<hr>

<h2>🔗 Relaciones y Eventos</h2>

<h3>Cliente — Envío — Repartidor</h3>

<p> Un cliente puede realizar múltiples envíos (1:N) y un repartidor puede tener asignados múltiples envíos (1:N). La entidad <code>Envio</code> actúa como propietaria de estas relaciones mediante claves foráneas. </p>

<h3>Desacoplamiento con Spring Events</h3>

<p> Cuando un envío cambia de estado, en lugar de inyectar dependencias directas, el servicio de envíos publica un evento de dominio (<code>EnvioEstadoCambiadoEvent</code>). Un <i>Listener</i> especializado (<code>HistorialEstadoListener</code>) captura este evento y persiste el nuevo registro en la tabla <code>historial_estados</code>. </p>

<hr>

<h2>🕒 Auditoría</h2>

<p> Las entidades principales extienden la clase base abstracta <code>AuditableEntity</code> (marcada con <code>@MappedSuperclass</code>). </p>

<p>A través de JPA Auditing se registran de forma transparente:</p>

<pre><code class="language-java">@CreatedDate
private Instant createdDate;

@LastModifiedDate
private Instant lastModifiedDate;</code></pre>

<hr>

<h2>📐 Modelo de dominio</h2>

<p>A continuación se presenta una aproximación del modelo de clases. Puedes consultar también el archivo adjunto <code>Diagra de Clases - LogiTrack.jpg.jpeg</code> para visualizar el diagrama completo original.</p>

```mermaid
classDiagram

    class AuditableEntity {
        <<MappedSuperclass>>
        Instant createdDate
        Instant lastModifiedDate
    }

    class Cliente {
        UUID id
        String nombre
        String email
        String telefono
    }

    class Repartidor {
        UUID id
        String nombre
        String tipoVehiculo
        EstadoRepartidor estadoRepartidor
    }

    class Envio {
        UUID id
        UUID codigoSeguimiento
        Double peso
        Double ancho
        Double alto
        Double largo
        EstadoEnvio estado
    }
    
    class Direccion {
        <<Embeddable>>
        String calle
        String numero
        String ciudad
        String codigoPostal
    }

    class HistorialEstado {
        UUID id
        EstadoEnvio estadoAnterior
        EstadoEnvio estadoNuevo
        LocalDateTime fechaHora
    }

    AuditableEntity <|-- Cliente
    AuditableEntity <|-- Repartidor
    AuditableEntity <|-- Envio
    
    Envio *-- Direccion : origen
    Envio *-- Direccion : destino

    Cliente "1" <-- "0..*" Envio : solicita
    Repartidor "0..1" <-- "0..*" Envio : asignado
    Envio "1" <-- "0..*" HistorialEstado : posee
```

<hr>

<h2>⚙️ Requisitos previos</h2>

<p>Para ejecutar el proyecto, se requiere contar con:</p>

<ul> 
  <li>JDK 25</li> 
  <li>Docker y Docker Compose (Recomendado para el entorno completo)</li> 
  <li>Alternativa: MySQL 8.4 local</li> 
</ul>

<hr>

<h2>▶️ Ejecución del Proyecto</h2>

<p>El proyecto incluye configuración completa para ejecutarse mediante Docker.</p>

<h3>1. Configurar variables de entorno</h3>
<p>Crear un archivo <code>.env</code> en la raíz del proyecto basándose en la plantilla:</p>
<pre><code class="language-bash">cp .env.example .env</code></pre>

<h3>2. Ejecutar con Docker Compose</h3>
<p>Construir e iniciar los contenedores (MySQL y la aplicación):</p>
<pre><code class="language-bash">docker-compose up --build -d</code></pre>

<p> La aplicación estará disponible en <code>http://localhost:8080</code>. Puede acceder a la documentación interactiva Swagger en <code>http://localhost:8080/swagger-ui.html</code>. </p>

<h3>Alternativa local (Maven Wrapper)</h3>
<p>Si desea ejecutar solo la app localmente contra una base de datos externa:</p>
<pre><code class="language-bash">.\mvnw spring-boot:run</code></pre>

<hr>

<h2>🧪 Testing</h2>

<p>El proyecto incorpora una sólida batería de pruebas unitarias y de integración que verifican la capa de controladores (WebMvc), repositorios (Slice), y lógica de servicios.</p>

<p><strong>Para ejecutar todos los tests:</strong></p>
<pre><code class="language-powershell">.\mvnw test</code></pre>

<hr>

<h2>👨‍💻 Autores</h2>

<ul> 
  <li><strong>Soliz Mauro Francisco</strong></li> 
  <li><strong>Tolaba Catari Facundo Ezequiel</strong></li> 
  <li><strong>Velásquez Nicolás Santiago</strong></li> 
</ul>

<p> <strong>Ingeniería en Informática — UNJu</strong><br> 
Desarrollo y Arquitecturas Avanzadas de Software · 2026 </p>

<p><em>Proyecto académico</em></p>