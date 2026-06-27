# TechStore Chile API

Microservicio RESTful desarrollado en Java con Spring Boot para la gestión de productos de la tienda ficticia **TechStore Chile**.

El proyecto permite administrar productos mediante operaciones CRUD, proteger endpoints mediante autenticación JWT, persistir información en PostgreSQL y desplegar la solución en una arquitectura cloud utilizando AWS Academy.

En la Evaluación Parcial N°3, el microservicio fue modernizado para ejecutarse en AWS mediante Amazon ECS Fargate, almacenar su imagen Docker en Amazon ECR, exponer la API mediante API Gateway y Application Load Balancer, utilizar Amazon RDS PostgreSQL como base de datos, y agregar auditoría asíncrona mediante Amazon SQS, AWS Lambda y Amazon CloudWatch Logs.

Además, se incorporó automatización CI/CD con GitHub Actions para compilar el proyecto con Maven, construir la imagen Docker, subirla a Amazon ECR y actualizar automáticamente el servicio ECS Fargate.

---

## Integrantes

* Integrante 1: Renato Valenzuela
* Integrante 2: Danae Miranda

---

## Objetivo del proyecto

Desarrollar, contenerizar, desplegar y automatizar un microservicio RESTful en Java con Spring Boot para la gestión del catálogo de productos de **TechStore Chile**, aplicando arquitectura en capas, persistencia con JPA/Hibernate, autenticación JWT, despliegue en AWS, integración asíncrona con SQS y automatización CI/CD mediante GitHub Actions.

---

## Tecnologías utilizadas

### Backend

* Java 17
* Spring Boot
* Maven
* Spring Web
* Spring Data JPA
* Spring Security
* JWT
* Hibernate / JPA
* PostgreSQL

### Contenedores y automatización

* Docker
* Docker Compose
* Git
* GitHub
* GitHub Actions
* GitHub Secrets

### Servicios AWS

* AWS Academy Learner Lab
* Amazon ECR
* Amazon ECS Fargate
* Amazon RDS PostgreSQL
* Application Load Balancer
* Amazon API Gateway
* Amazon SQS
* AWS Lambda
* Amazon CloudWatch Logs

### Pruebas

* Postman

---

## Arquitectura general del proyecto

El proyecto sigue una arquitectura en capas:

```text
src/main/java/cl/techstore/api/
├── config/
│   └── SqsConfig.java
│
├── controller/
│   ├── AuthController.java
│   └── ProductoController.java
│
├── dto/
│   ├── AuditMessage.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   └── ProductoDTO.java
│
├── model/
│   └── Producto.java
│
├── repository/
│   └── ProductoRepository.java
│
├── security/
│   ├── JwtUtil.java
│   ├── JwtFilter.java
│   └── SecurityConfig.java
│
├── service/
│   ├── AuditProducer.java
│   └── ProductoService.java
│
└── ApiApplication.java
```

---

## Descripción de capas

| Capa         | Responsabilidad                                                      |
| ------------ | -------------------------------------------------------------------- |
| `controller` | Recibe las peticiones HTTP y expone los endpoints REST.              |
| `service`    | Contiene la lógica de negocio del sistema.                           |
| `repository` | Permite acceder a la base de datos mediante Spring Data JPA.         |
| `model`      | Define las entidades JPA que representan tablas en la base de datos. |
| `dto`        | Define objetos para recibir y devolver datos en las peticiones.      |
| `security`   | Contiene la configuración de seguridad, generación y validación JWT. |
| `config`     | Contiene configuración de servicios externos, como Amazon SQS.       |

---

## Modelo de datos

La entidad principal del sistema es `Producto`.

### Entidad Producto

| Campo         | Tipo Java | Descripción                                       |
| ------------- | --------- | ------------------------------------------------- |
| `id`          | Long      | Identificador único del producto.                 |
| `nombre`      | String    | Nombre del producto.                              |
| `descripcion` | String    | Descripción breve del producto.                   |
| `precio`      | Double    | Precio del producto en CLP.                       |
| `stock`       | Integer   | Cantidad disponible en bodega.                    |
| `categoria`   | String    | Categoría del producto.                           |
| `activo`      | Boolean   | Indica si el producto está activo en el catálogo. |

La eliminación de productos se realiza mediante **borrado lógico**. Esto significa que el registro no se elimina físicamente desde la base de datos; en su lugar, el campo `activo` cambia a `false`.

---

## Seguridad y autenticación JWT

El sistema implementa autenticación mediante JWT.

Para acceder a los endpoints protegidos, primero se debe realizar login en:

```http
POST /auth/login
```

Si las credenciales son correctas, el sistema devuelve un token JWT. Ese token debe enviarse en el header `Authorization` para consumir los endpoints protegidos.

### Uso del token

```http
Authorization: Bearer <token>
```

### Credenciales de prueba

```text
Usuario: admin@techstore.cl
Contraseña: ********
```

> Por seguridad, las credenciales reales no deben quedar expuestas en el repositorio.

---

## Endpoints implementados

### Autenticación

| Método | Endpoint      | Descripción          | Requiere token |
| ------ | ------------- | -------------------- | -------------- |
| POST   | `/auth/login` | Genera un token JWT. | No             |

### Productos

| Método | Endpoint              | Descripción                          | Código esperado | Requiere token |
| ------ | --------------------- | ------------------------------------ | --------------- | -------------- |
| GET    | `/api/productos`      | Lista todos los productos activos.   | 200 OK          | Sí             |
| GET    | `/api/productos/{id}` | Busca un producto por ID.            | 200 OK          | Sí             |
| POST   | `/api/productos`      | Crea un nuevo producto.              | 201 Created     | Sí             |
| PUT    | `/api/productos/{id}` | Modifica un producto existente.      | 200 OK          | Sí             |
| DELETE | `/api/productos/{id}` | Realiza borrado lógico del producto. | 204 No Content  | Sí             |

Las operaciones `POST`, `PUT` y `DELETE` generan un evento de auditoría que es enviado a Amazon SQS.

---

## Arquitectura Cloud implementada en AWS

La solución fue desplegada en AWS Academy siguiendo el siguiente flujo:

```text
Cliente / Postman
      ↓
Amazon API Gateway
      ↓
Application Load Balancer
      ↓
Amazon ECS Fargate
      ↓
TechStore API - Spring Boot
      ↓
Amazon RDS PostgreSQL
```

Además, para auditoría asíncrona de operaciones de escritura:

```text
POST / PUT / DELETE Producto
      ↓
TechStore API
      ↓
Amazon SQS: techstore-audit-queue
      ↓
AWS Lambda: techstore-audit-logger
      ↓
Amazon CloudWatch Logs
```

---

## Recursos AWS configurados

| Servicio AWS              | Recurso                                                  | Descripción                                                            |
| ------------------------- | -------------------------------------------------------- | ---------------------------------------------------------------------- |
| Amazon ECR                | `techstore-api`                                          | Repositorio privado para almacenar la imagen Docker del microservicio. |
| Amazon ECS Fargate        | `techstore-cluster`                                      | Cluster donde se ejecuta la aplicación contenerizada.                  |
| ECS Service               | `techstore-api-service`                                  | Servicio encargado de mantener activa la tarea del microservicio.      |
| ECS Task Definition       | `techstore-api-task`                                     | Definición de ejecución del contenedor.                                |
| Contenedor ECS            | `techstore-api-container`                                | Contenedor que ejecuta la API Spring Boot.                             |
| Application Load Balancer | `techstore-alb-2091094716.us-east-1.elb.amazonaws.com`   | Balanceador que distribuye el tráfico hacia ECS Fargate.               |
| Amazon API Gateway        | `https://x1pcfosgw6.execute-api.us-east-1.amazonaws.com` | Punto de entrada público para consumir la API.                         |
| Amazon RDS PostgreSQL     | `techstore-bd.cantuvyqvbz2.us-east-1.rds.amazonaws.com`  | Base de datos relacional administrada.                                 |
| Amazon SQS                | `techstore-audit-queue`                                  | Cola estándar para mensajes de auditoría.                              |
| AWS Lambda                | `techstore-audit-logger`                                 | Función serverless que procesa mensajes desde SQS.                     |
| Amazon CloudWatch Logs    | Logs de Lambda                                           | Servicio utilizado para revisar la auditoría generada.                 |

---

## Configuración de base de datos en AWS

La aplicación desplegada en ECS Fargate se conecta a una base de datos PostgreSQL administrada en Amazon RDS.

Variables de entorno configuradas en ECS:

```text
SPRING_DATASOURCE_URL=jdbc:postgresql://techstore-bd.cantuvyqvbz2.us-east-1.rds.amazonaws.com:5432/techstore
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=********
AWS_REGION=us-east-1
APP_SQS_AUDIT_QUEUE_URL=https://sqs.us-east-1.amazonaws.com/034485424217/techstore-audit-queue
```

> Las contraseñas reales y credenciales sensibles no deben almacenarse directamente en el repositorio.

---

## Integración con Amazon SQS

Para implementar la auditoría asíncrona, se agregó integración con Amazon SQS mediante AWS SDK para Java.

Archivos relacionados:

```text
src/main/java/cl/techstore/api/config/SqsConfig.java
src/main/java/cl/techstore/api/dto/AuditMessage.java
src/main/java/cl/techstore/api/service/AuditProducer.java
src/main/java/cl/techstore/api/controller/ProductoController.java
src/main/java/cl/techstore/api/service/ProductoService.java
src/main/resources/application.properties
pom.xml
```

La cola utilizada es:

```text
techstore-audit-queue
```

URL de la cola:

```text
https://sqs.us-east-1.amazonaws.com/034485424217/techstore-audit-queue
```

Cada vez que se realiza una operación de escritura sobre productos, el microservicio publica un mensaje de auditoría en SQS.

### Estructura del mensaje de auditoría

```json
{
  "accion": "CREAR / MODIFICAR / ELIMINAR",
  "productoId": 1,
  "nombre": "Nombre del producto",
  "usuario": "usuario@techstore.cl",
  "fecha": "2026-06-27T00:00:00"
}
```

---

## Procesamiento serverless con AWS Lambda

La cola SQS se configuró como trigger de la función Lambda:

```text
techstore-audit-logger
```

Esta función consume automáticamente los mensajes de auditoría y los registra en Amazon CloudWatch Logs.

Este diseño permite desacoplar la operación principal del microservicio respecto al registro de auditoría, evitando que el proceso de creación, modificación o eliminación de productos dependa directamente del procesamiento del log.

---

## Despliegue en Amazon ECS Fargate

La aplicación se ejecuta como contenedor Docker dentro de Amazon ECS Fargate.

Configuración principal:

```text
Región AWS: us-east-1
Cluster ECS: techstore-cluster
Servicio ECS: techstore-api-service
Task Definition: techstore-api-task
Contenedor: techstore-api-container
Puerto del contenedor: 8080
Imagen ECR: 034485424217.dkr.ecr.us-east-1.amazonaws.com/techstore-api:latest
```

ECS Fargate permite ejecutar contenedores sin administrar servidores manualmente. El servicio mantiene las tareas activas, permite reemplazar tareas ante fallas y facilita la actualización de la aplicación mediante nuevas imágenes Docker publicadas en Amazon ECR.

En comparación con un entorno local con Docker Compose, ECS Fargate permite definir límites de CPU y memoria, administrar réplicas, reinicios y despliegues desde la infraestructura administrada de AWS.

---

## Exposición mediante API Gateway y ALB

La API se expone mediante Amazon API Gateway, que actúa como punto de entrada público.

URL pública:

```text
https://x1pcfosgw6.execute-api.us-east-1.amazonaws.com
```

El API Gateway enruta las peticiones hacia el Application Load Balancer:

```text
techstore-alb-2091094716.us-east-1.elb.amazonaws.com
```

Luego, el ALB distribuye las solicitudes hacia las tareas activas de ECS Fargate.

---

## CI/CD con GitHub Actions

Se configuró un pipeline de GitHub Actions en:

```text
.github/workflows/deploy.yml
```

El workflow se ejecuta automáticamente cuando se realiza un push o merge hacia la rama `main`. También puede ejecutarse manualmente mediante `workflow_dispatch`.

### Etapas del pipeline

```text
1. Descargar código del repositorio.
2. Configurar Java 17.
3. Dar permisos al Maven Wrapper.
4. Ejecutar pruebas con Maven.
5. Empaquetar la aplicación Spring Boot.
6. Configurar credenciales temporales de AWS Academy.
7. Iniciar sesión en Amazon ECR.
8. Construir la imagen Docker.
9. Etiquetar la imagen como latest.
10. Subir la imagen a Amazon ECR.
11. Actualizar el servicio ECS Fargate.
```

El pipeline fue ejecutado correctamente desde GitHub Actions, validando:

```text
[✓] Ejecución de Maven.
[✓] Empaquetado de la aplicación.
[✓] Construcción de imagen Docker.
[✓] Push de imagen hacia Amazon ECR.
[✓] Actualización automática del servicio ECS Fargate.
```

---

## GitHub Secrets

Las credenciales temporales de AWS Academy se configuraron como secretos del repositorio en GitHub.

Secrets utilizados:

```text
AWS_ACCESS_KEY_ID
AWS_SECRET_ACCESS_KEY
AWS_SESSION_TOKEN
```

Ruta de configuración:

```text
Settings → Secrets and variables → Actions → New repository secret
```

> Debido a que AWS Academy utiliza credenciales temporales, estos valores deben actualizarse cada vez que se reinicia la sesión del laboratorio.

---

## Docker y Docker Compose

El proyecto también conserva soporte para ejecución local mediante Docker Compose, útil para pruebas de desarrollo.

Servicios principales:

```text
postgres       → base de datos PostgreSQL 15
microservicio  → aplicación Spring Boot
```

---

## Dockerfile

El archivo `Dockerfile` permite construir la imagen del microservicio para su posterior despliegue.

Versión actual utilizada por el proyecto:

```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Descripción:

| Línea                                        | Función                                                   |
| -------------------------------------------- | --------------------------------------------------------- |
| `FROM eclipse-temurin:17-jre`                | Utiliza una imagen base con Java 17.                      |
| `WORKDIR /app`                               | Define el directorio de trabajo dentro del contenedor.    |
| `COPY target/api-0.0.1-SNAPSHOT.jar app.jar` | Copia el archivo `.jar` generado por Maven al contenedor. |
| `EXPOSE 8080`                                | Expone el puerto interno 8080.                            |
| `ENTRYPOINT ["java", "-jar", "app.jar"]`     | Ejecuta la aplicación Spring Boot dentro del contenedor.  |

### Dockerfile recomendado para optimización

Para una versión más optimizada, se puede utilizar un Dockerfile multi-stage que compile el `.jar` y luego ejecute la aplicación con una imagen liviana:

```dockerfile
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

USER appuser

ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## docker-compose.yml

El archivo `docker-compose.yml` permite levantar PostgreSQL y el microservicio de forma local.

```yaml
version: "3.8"

services:
  postgres:
    image: postgres:15
    container_name: techstore_db
    environment:
      POSTGRES_DB: techstore
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin123
    ports:
      - "5432:5432"

  microservicio:
    build: .
    container_name: techstore_api
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/techstore
      SPRING_DATASOURCE_USERNAME: admin
      SPRING_DATASOURCE_PASSWORD: admin123
      APP_SQS_AUDIT_QUEUE_URL: https://sqs.us-east-1.amazonaws.com/034485424217/techstore-audit-queue
      AWS_REGION: us-east-1
    depends_on:
      - postgres
```

Dentro de Docker Compose, la aplicación no se conecta a `localhost`, sino al servicio llamado `postgres`. Por eso se utiliza:

```text
jdbc:postgresql://postgres:5432/techstore
```

---

## Ejecución local

### 1. Clonar el repositorio

```bash
git clone https://github.com/dvnxeee/TechStore-Chile.git
```

### 2. Entrar al proyecto

```bash
cd TechStore-Chile
```

### 3. Compilar con Maven

En Git Bash, Linux o Mac:

```bash
./mvnw clean package -DskipTests
```

En Windows CMD o PowerShell:

```bash
mvnw.cmd clean package -DskipTests
```

Si la compilación es correcta, debe aparecer:

```text
BUILD SUCCESS
```

El archivo `.jar` se genera en:

```text
target/api-0.0.1-SNAPSHOT.jar
```

### 4. Levantar con Docker Compose

```bash
docker compose up --build
```

La API local queda disponible en:

```text
http://localhost:8080
```

PostgreSQL local queda disponible en:

```text
localhost:5432
```

Para detener los contenedores:

```bash
docker compose down
```

---

## Pruebas en Postman

### URL local

```text
http://localhost:8080
```

### URL en AWS

```text
https://x1pcfosgw6.execute-api.us-east-1.amazonaws.com
```

---

### 1. Login

```http
POST https://x1pcfosgw6.execute-api.us-east-1.amazonaws.com/auth/login
Content-Type: application/json
```

Body:

```json
{
  "username": "admin@techstore.cl",
  "password": "********"
}
```

Respuesta esperada:

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "expiracion": "3600"
}
```

---

### 2. Listar productos

```http
GET https://x1pcfosgw6.execute-api.us-east-1.amazonaws.com/api/productos
Authorization: Bearer <token>
```

Respuesta esperada:

```http
200 OK
```

---

### 3. Crear producto

```http
POST https://x1pcfosgw6.execute-api.us-east-1.amazonaws.com/api/productos
Authorization: Bearer <token>
Content-Type: application/json
```

Body:

```json
{
  "nombre": "Mouse Gamer Logitech",
  "descripcion": "Mouse gamer RGB",
  "precio": 29990,
  "stock": 20,
  "categoria": "Periféricos",
  "activo": true
}
```

Respuesta esperada:

```http
201 Created
```

Esta operación genera un evento de auditoría en Amazon SQS.

---

### 4. Modificar producto

```http
PUT https://x1pcfosgw6.execute-api.us-east-1.amazonaws.com/api/productos/{id}
Authorization: Bearer <token>
Content-Type: application/json
```

Body:

```json
{
  "nombre": "Mouse Gamer Logitech Actualizado",
  "descripcion": "Mouse gamer RGB actualizado",
  "precio": 34990,
  "stock": 15,
  "categoria": "Periféricos",
  "activo": true
}
```

Respuesta esperada:

```http
200 OK
```

Esta operación genera un evento de auditoría en Amazon SQS.

---

### 5. Eliminar producto

```http
DELETE https://x1pcfosgw6.execute-api.us-east-1.amazonaws.com/api/productos/{id}
Authorization: Bearer <token>
```

Respuesta esperada:

```http
204 No Content
```

La eliminación es lógica, por lo tanto el producto queda con:

```text
activo = false
```

Esta operación genera un evento de auditoría en Amazon SQS.

---

## Validación de auditoría

Para validar el flujo de auditoría:

```text
1. Realizar una operación POST, PUT o DELETE desde Postman.
2. Confirmar que la operación afecta la base de datos en RDS.
3. Verificar que el mensaje fue enviado a la cola SQS techstore-audit-queue.
4. Confirmar que Lambda techstore-audit-logger fue invocada.
5. Revisar en CloudWatch Logs el registro generado por Lambda.
```

---

## Verificación funcional

Checklist de validación del proyecto:

```text
[✓] Microservicio Spring Boot ejecutándose correctamente.
[✓] Login con JWT funcionando.
[✓] Endpoints protegidos mediante Authorization Bearer Token.
[✓] CRUD de productos implementado.
[✓] Borrado lógico mediante campo activo.
[✓] Persistencia en PostgreSQL.
[✓] Imagen Docker construida correctamente.
[✓] Imagen subida a Amazon ECR.
[✓] Servicio desplegado en Amazon ECS Fargate.
[✓] API expuesta mediante API Gateway.
[✓] Tráfico dirigido mediante Application Load Balancer.
[✓] Base de datos PostgreSQL funcionando en Amazon RDS.
[✓] Cola SQS techstore-audit-queue configurada.
[✓] Lambda techstore-audit-logger procesando mensajes.
[✓] Logs de auditoría visibles en CloudWatch.
[✓] Pipeline de GitHub Actions ejecutado correctamente.
[✓] Maven ejecutado dentro del pipeline CI/CD.
[✓] Actualización automática del servicio ECS Fargate.
```

---

## Flujo de trabajo con Git

El proyecto utiliza un flujo de trabajo basado en ramas:

```text
main → versión final estable para entrega
dev → rama principal de desarrollo
feature/... → ramas opcionales para funcionalidades específicas
```

### Ramas principales

| Rama          | Descripción                                                   |
| ------------- | ------------------------------------------------------------- |
| `main`        | Contiene la versión final estable del proyecto.               |
| `dev`         | Contiene el desarrollo integrado antes de pasar a producción. |
| `feature/...` | Ramas opcionales para funcionalidades específicas.            |

---

## Estado final del proyecto

Actualmente el proyecto cuenta con:

```text
[✓] Proyecto Spring Boot generado con Maven.
[✓] Arquitectura en capas.
[✓] Entidad Producto mapeada con JPA/Hibernate.
[✓] Repositorio ProductoRepository.
[✓] Servicio ProductoService.
[✓] Controlador ProductoController.
[✓] DTO para productos.
[✓] CRUD de productos implementado.
[✓] Borrado lógico mediante campo activo.
[✓] Configuración de PostgreSQL.
[✓] Login con JWT.
[✓] Protección de endpoints mediante Spring Security.
[✓] Dockerfile para construir la imagen del microservicio.
[✓] docker-compose.yml para ejecución local.
[✓] Despliegue cloud en AWS Academy.
[✓] Repositorio privado en Amazon ECR.
[✓] Ejecución en Amazon ECS Fargate.
[✓] Exposición mediante Application Load Balancer.
[✓] Entrada pública mediante Amazon API Gateway.
[✓] Base de datos Amazon RDS PostgreSQL.
[✓] Integración asíncrona con Amazon SQS.
[✓] Procesamiento serverless con AWS Lambda.
[✓] Logs de auditoría en Amazon CloudWatch.
[✓] Pipeline CI/CD con GitHub Actions.
[✓] Uso de GitHub Secrets para credenciales temporales de AWS Academy.
[✓] Actualización automática del servicio ECS Fargate desde GitHub Actions.
```

---

## Consideraciones de seguridad

* Las credenciales de AWS Academy no se almacenan en el código fuente.
* Las credenciales temporales se administran mediante GitHub Secrets.
* Las credenciales deben actualizarse cuando se reinicia el laboratorio de AWS Academy.
* Las contraseñas de base de datos no deben exponerse en el repositorio.
* Los endpoints de productos están protegidos mediante JWT.
* El microservicio recibe tráfico mediante API Gateway y ALB.
* El registro de auditoría se realiza de forma asíncrona para no acoplar la operación principal del sistema.

---

## Evidencias recomendadas para la entrega

Para la defensa o video demostrativo, se recomienda mostrar:

```text
[✓] GitHub Actions ejecutado en verde.
[✓] Paso de Maven completado correctamente.
[✓] Imagen latest en Amazon ECR.
[✓] Servicio ECS Fargate activo.
[✓] Task ECS ejecutándose correctamente.
[✓] API Gateway funcionando.
[✓] ALB asociado al servicio ECS.
[✓] RDS PostgreSQL conectado.
[✓] Pruebas en Postman con JWT.
[✓] Operación POST, PUT o DELETE generando evento.
[✓] Cola SQS recibiendo mensajes.
[✓] Lambda ejecutándose automáticamente.
[✓] CloudWatch mostrando logs de auditoría.
```

---

## Autores

Proyecto desarrollado por estudiantes de la asignatura **Java: Diseño y Construcción de Soluciones Nativas en Nube**.

**TechStore Chile API - Evaluación Parcial N°3**