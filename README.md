🏔️ Grupo Cordillera - Plataforma Analítica Estratégica

📌 Descripción del Proyecto
Este repositorio contiene el código fuente y el diseño arquitectónico de la solución tecnológica desarrollada para Grupo Cordillera, una organización del sector retail.

El propósito principal de este proyecto es modernizar una infraestructura tecnológica históricamente fragmentada, eliminando la dependencia de procesos manuales (como la consolidación en hojas de cálculo) para resolver cuellos de botella operativos. A través de buenas prácticas de ingeniería de software, buscamos habilitar la toma de decisiones estratégicas basadas en datos en tiempo real.

🚀 La Solución
Para cumplir con los objetivos del negocio, se ha diseñado una Arquitectura de Microservicios políglota y resiliente. El sistema automatiza el flujo de información entre ventas, inventario y finanzas, integrando un motor analítico avanzado para el cálculo de KPIs.

Características Principales:
Desacoplamiento Operativo: Uso de comunicación asíncrona impulsada por eventos para garantizar que el sistema de cajas/ventas nunca se bloquee.

Tolerancia a Fallos: Implementación de patrones de resiliencia como Circuit Breaker para aislar errores y mantener la disponibilidad.

Escalabilidad Dinámica: Infraestructura totalmente contenerizada y orquestada para soportar picos de alta demanda transaccional de forma automática.

Seguridad y Transparencia: Control de accesos centralizado mediante tokens JWT y generación de reportes mensuales inmutables para auditoría.

🛠️ Stack Tecnológico
El ecosistema ha sido construido combinando las herramientas más robustas de la industria actual:

Backend & Ecosistema de Microservicios:

Java 17/21 & Spring Boot 3.2.x (Microservicios Transaccionales: Auth, Ventas, Inventario, Finanzas, BFF, PDF)

Python 3.9 (Motor de analítica matemática y KPIs)

Spring Cloud (API Gateway, Eureka Server, Config Server)

Frontend:

React (Single Page Application para visualización de Dashboards Gerenciales)

Comunicación & Mensajería:

RabbitMQ (Message Broker / Arquitectura Pub-Sub)

OpenFeign / WebClient (Comunicación síncrona REST)

Bases de Datos (Políglota):

PostgreSQL (Persistencia relacional centralizada cordillera_db)

MongoDB (Persistencia documental para analítica)

Redis (Caché en memoria)

Infraestructura & Despliegue:

Docker & Docker Compose (Contenerización y Multi-stage builds)

Kubernetes (Orquestación, gestión de red interna y auto-escalado horizontal HPA)

🧩 Ecosistema de Microservicios (Backend)
El backend está dividido en dominios de negocio aislados para garantizar la alta disponibilidad:

eureka_server: Servidor de descubrimiento. Directorio central donde todos los nodos registran su estado e IP.

api_gateway: Puerta de enlace única y proxy inverso. Centraliza CORS (habilitado para React), Swagger UI y gestiona el AuthenticationFilter para validar firmas criptográficas JWT.

gc_auth: Proveedor de identidades. Gestiona el registro administrativo y la emisión de tokens.

gc_ventas: Motor transaccional. Registra ventas y emite eventos hacia RabbitMQ para notificar a otras áreas.

gc_inventario_compras: Sistema de gestión de stock. Escucha los eventos de ventas para descontar existencias en tiempo real.

gc_finanzas: Módulo contable. Gestiona la caja y validaciones de transacciones.

gc_kpi_analytics (Python): Motor de analítica de datos para la toma de decisiones comerciales.

gc_bff_reportes (Backend-For-Frontend): Capa de adaptación que optimiza y formatea los datos masivos específicamente para el consumo del cliente web React.

gc_reportes_pdf: Generador de documentos pesados. Automatiza el ciclo de facturación mensual.

📂 Estructura Estricta de Directorios y Paquetes
El repositorio mantiene una separación estricta entre configuraciones de entorno y el código fuente. Cada microservicio de Java respeta una topología de paquetes detallada (Ejemplo referencial de gc_ventas):

Plaintext
📦 GRUPO-CORDILLERA-BACKEND
 ┣ 📂 .github                      # Pipelines de CI/CD
 ┗ 📂 backend-cordillera           # Raíz del ecosistema backend
   ┣ 📂 api_gateway
   ┣ 📂 eureka_server
   ┣ 📂 gc_auth
   ┣ 📂 gc_bff_reportes
   ┣ 📂 gc_finanzas
   ┣ 📂 gc_inventario_compras
   ┣ 📂 gc_kpi_analytics           # Entorno Python
   ┣ 📂 gc_reportes_pdf
   ┣ 📂 gc_ventas
   ┃ ┣ 📂 src/main/java/com/grupocordillera/ventas
   ┃ ┃ ┣ 📂 config                 # Configuraciones (RabbitMQ, Swagger)
   ┃ ┃ ┣ 📂 controller             # Endpoints REST protegidos
   ┃ ┃ ┣ 📂 dto                    # Objetos de entrada/salida blindados con @Valid
   ┃ ┃ ┣ 📂 entity                 # Entidades JPA (Venta, DetalleVenta)
   ┃ ┃ ┣ 📂 exception              # Manejo de errores global
   ┃ ┃ ┣ 📂 messaging              # Productores/Consumidores RabbitMQ
   ┃ ┃ ┣ 📂 repository             # Interfaces de Spring Data PostgreSQL
   ┃ ┃ ┣ 📂 security               # Filtros internos y utilidades JWT
   ┃ ┃ ┗ 📂 service                # Lógica de negocio core (Interfaces e impl)
   ┃ ┣ 📂 src/main/resources
   ┃ ┃ ┣ 📜 application.yml        # Variables de entorno y perfil local
   ┃ ┃ ┗ 📜 application-docker.yml # Perfil exclusivo para contenedores
   ┃ ┗ 📜 pom.xml
   ┗ 📜 docker-compose.yml         # Orquestación global de la arquitectura
🚀 Guía de Ejecución Local
Prerrequisitos
Docker y Docker Compose instalados.

Java JDK y Maven instalados.

Python 3.9+ (para el módulo de analítica).

Paso 1: Levantar la Infraestructura Base
Posiciónate en la carpeta de microservicios y levanta primero las bases de datos y la mensajería:

Bash
cd backend-cordillera
docker-compose up -d postgres rabbitmq mongo redis
Paso 2: Orden de Inicialización
Debido a las dependencias de red, los servicios deben arrancarse en el siguiente orden:

eureka_server: Esperar a que el puerto 8761 esté activo.

Módulos de Negocio: Iniciar los microservicios core (gc_auth, gc_ventas, etc.) usando ./mvnw spring-boot:run.

api_gateway: Iniciar al final para asegurar el mapeo de los servicios ya registrados en Eureka.

Alternativa: Full Docker
Para levantar toda la arquitectura contenerizada y enlazada automáticamente:

Bash
cd backend-cordillera
docker-compose up -d --build
🧪 Testing, Cobertura y Calidad de Código
El proyecto exige un alto estándar de calidad. Cada microservicio cuenta con un set de pruebas riguroso (JUnit, Mockito, MockMvc) aislando capas.

Ejecutar Pruebas y Reporte JaCoCo:

Bash
cd backend-cordillera/gc_ventas
./mvnw clean test
(El reporte HTML de cobertura se generará en target/site/jacoco/index.html excluyendo entidades y DTOs).

Análisis Estático con SonarQube:

Bash
./mvnw sonar:sonar -Dsonar.projectKey=grupo-cordillera-backend -Dsonar.host.url=http://localhost:9000 -Dsonar.login=tu_token
📖 Endpoints y Documentación (Swagger)
La documentación de la API está unificada a través del API Gateway mediante Springdoc OpenAPI. Una vez iniciados los servicios, accede al panel central interactivo:

👉 http://localhost:8080/webjars/swagger-ui/index.html

Módulos disponibles en el menú desplegable:

🔐 Módulo de Autenticación (/api/auth/)

🛒 Módulo de Ventas (/ventas/)

📦 Módulo de Inventario (/api/inventario/)

(Nota: Para consumir las rutas protegidas, genera un token en /api/auth/login y autoriza la sesión en el botón "Authorize" de la UI).

👥 Equipo de Desarrollo (Fullstack)
Proyecto desarrollado aplicando metodologías ágiles (Scrum) y principios avanzados de diseño arquitectónico.

Camilo Tapia

Desarrollado para la asignatura de Desarrollo Fullstack III.
