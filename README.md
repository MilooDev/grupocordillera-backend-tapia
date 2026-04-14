# 🏔️ Grupo Cordillera - Plataforma Analítica Estratégica

## 📌 Descripción del Proyecto
Este repositorio contiene el código fuente y el diseño arquitectónico de la solución tecnológica desarrollada para **Grupo Cordillera**, una organización del sector retail. 

El propósito principal de este proyecto es modernizar una infraestructura tecnológica históricamente fragmentada, eliminando la dependencia de procesos manuales (como la consolidación en hojas de cálculo) para resolver cuellos de botella operativos. A través de buenas prácticas de ingeniería de software, buscamos habilitar la toma de decisiones estratégicas basadas en datos en tiempo real.

## 🚀 La Solución
Para cumplir con los objetivos del negocio, se ha diseñado una **Arquitectura de Microservicios** políglota y resiliente. El sistema automatiza el flujo de información entre ventas, inventario y finanzas, integrando un motor analítico avanzado para el cálculo de KPIs.

### Características Principales:
* **Desacoplamiento Operativo:** Uso de comunicación asíncrona impulsada por eventos para garantizar que el sistema de cajas/ventas nunca se bloquee.
* **Tolerancia a Fallos:** Implementación de patrones de resiliencia como *Circuit Breaker* para aislar errores y mantener la disponibilidad.
* **Escalabilidad Dinámica:** Infraestructura totalmente contenerizada y orquestada para soportar picos de alta demanda transaccional de forma automática.
* **Seguridad y Transparencia:** Control de accesos centralizado mediante tokens JWT y generación de reportes mensuales inmutables para auditoría.

## 🛠️ Stack Tecnológico
El ecosistema ha sido construido combinando las herramientas más robustas de la industria actual:

**Backend & Ecosistema de Microservicios:**
* **Java 17 & Spring Boot** (Microservicios Transaccionales: Auth, Ventas, Inventario, Finanzas, BFF, PDF)
* **Python 3.9** (Motor de analítica matemática y KPIs)
* **Spring Cloud** (API Gateway, Eureka Server, Config Server)

**Frontend:**
* **React** (Single Page Application para visualización de Dashboards Gerenciales)

**Comunicación & Mensajería:**
* **RabbitMQ** (Message Broker / Arquitectura Pub-Sub)
* **OpenFeign / WebClient** (Comunicación síncrona REST)

**Bases de Datos (Políglota):**
* **PostgreSQL** (Persistencia relacional)
* **MongoDB** (Persistencia documental para analítica)
* **Redis** (Caché en memoria)

**Infraestructura & Despliegue:**
* **Docker** (Contenerización de aplicaciones)
* **Kubernetes** (Orquestación, gestión de red interna y auto-escalado horizontal HPA)

## 👥 Equipo de Desarrollo (Fullstack)
Proyecto desarrollado aplicando metodologías ágiles (Scrum) y principios de diseño arquitectónico.

* **Camilo Tapia** * **Autumn Arcos**
* **Fabian Leal**
* **Benjamin Palomino**

---
*Desarrollado para la asignatura de Desarrollo Fullstack III.*
