# 🏔️ Grupo Cordillera - Plataforma Analítica Estratégica

## 📌 Descripción General

**Grupo Cordillera** es una plataforma empresarial desarrollada para modernizar y optimizar los procesos operativos de una organización del sector retail mediante una arquitectura basada en microservicios.

Su objetivo principal es eliminar la dependencia de procesos manuales, automatizar el flujo de información entre ventas, inventario y finanzas, y proporcionar indicadores estratégicos en tiempo real para apoyar la toma de decisiones.

---

## 🚀 Objetivos

- Automatizar procesos críticos del negocio.
- Centralizar la gestión de ventas, inventario y finanzas.
- Reducir errores operativos derivados de procesos manuales.
- Proporcionar KPIs e indicadores en tiempo real.
- Garantizar escalabilidad, disponibilidad y resiliencia.

---

## 🏗️ Arquitectura de la Solución

La plataforma implementa una **Arquitectura de Microservicios Políglota**, diseñada para ofrecer desacoplamiento, resiliencia y escalabilidad.

### 🔄 Comunicación basada en eventos

Utilización de RabbitMQ para garantizar la comunicación asíncrona entre servicios y evitar bloqueos en procesos críticos.

### 🛡️ Resiliencia y Alta Disponibilidad

Implementación de patrones como **Circuit Breaker**, permitiendo aislar fallos y mantener la continuidad operativa.

### 📈 Escalabilidad Horizontal

Infraestructura contenerizada preparada para Kubernetes y autoescalado dinámico.

### 🔐 Seguridad Centralizada

Autenticación y autorización mediante JWT gestionadas desde API Gateway.

### 📊 Analítica Empresarial

Motor especializado para el cálculo de KPIs y generación de métricas estratégicas.

---

## 🛠️ Stack Tecnológico

### Backend

- Java 17 / 21
- Spring Boot 3.2.x
- Spring Cloud
- Spring Security
- OpenFeign
- WebClient

### Analítica

- Python 3.9+

### Frontend

- React

### Mensajería

- RabbitMQ

### Bases de Datos

- PostgreSQL
- MongoDB
- Redis

### Infraestructura

- Docker
- Docker Compose
- Kubernetes

---

## 🧩 Ecosistema de Microservicios

| Servicio | Responsabilidad |
|-----------|----------------|
| `eureka_server` | Descubrimiento de servicios |
| `api_gateway` | Gateway, seguridad y enrutamiento |
| `gc_auth` | Autenticación y autorización |
| `gc_ventas` | Gestión de ventas |
| `gc_inventario_compras` | Control de inventario |
| `gc_finanzas` | Gestión financiera |
| `gc_kpi_analytics` | Cálculo de KPIs |
| `gc_bff_reportes` | Backend for Frontend |
| `gc_reportes_pdf` | Generación de reportes PDF |

---

## 📂 Estructura del Proyecto

```text
GRUPO-CORDILLERA-BACKEND
│
├── .github
├── backend-cordillera
│   ├── api_gateway
│   ├── eureka_server
│   ├── gc_auth
│   ├── gc_bff_reportes
│   ├── gc_finanzas
│   ├── gc_inventario_compras
│   ├── gc_kpi_analytics
│   ├── gc_reportes_pdf
│   ├── gc_ventas
│   └── docker-compose.yml
```

---

## 🚀 Ejecución Local

### Prerrequisitos

- Java JDK 17+
- Maven
- Docker
- Docker Compose
- Python 3.9+

### Levantar Infraestructura Base

```bash
cd backend-cordillera

docker-compose up -d postgres rabbitmq mongo redis
```

### Despliegue Completo

```bash
cd backend-cordillera

docker-compose up -d --build
```

---

## 🧪 Testing y Calidad

### Ejecutar Pruebas

```bash
cd backend-cordillera/gc_ventas

./mvnw clean test
```

### SonarQube

```bash
./mvnw sonar:sonar \
-Dsonar.projectKey=grupo-cordillera-backend \
-Dsonar.host.url=http://localhost:9000 \
-Dsonar.login=TU_TOKEN
```

---

## 📖 Documentación API

Swagger UI disponible en:

```text
http://localhost:8080/webjars/swagger-ui/index.html
```

### Módulos Disponibles

- 🔐 Autenticación
- 🛒 Ventas
- 📦 Inventario
- 💰 Finanzas
- 📊 Reportes

---

## 👥 Equipo de Desarrollo

Desarrollado bajo metodologías ágiles (**Scrum**) y principios de arquitectura empresarial moderna.

- Camilo Tapia

---

## 🎓 Contexto Académico

Proyecto desarrollado para la asignatura **Desarrollo Fullstack III**.
