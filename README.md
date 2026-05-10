# TechStore Chile API

Microservicio RESTful desarrollado en Java con Spring Boot para la gestión de productos de la tienda ficticia **TechStore Chile**.

El proyecto permite administrar productos mediante operaciones CRUD, proteger los endpoints mediante autenticación JWT, conectar la aplicación con una base de datos relacional PostgreSQL, generar un archivo ejecutable `.jar` utilizando Maven y levantar el entorno completo mediante Docker Compose.

---

## Integrantes

- Integrante 1: Renato Valenzuela
- Integrante 2: Danae Miranda

---

## Objetivo del proyecto

Desarrollar un microservicio RESTful en Java con Spring Boot que permita gestionar el catálogo de productos de TechStore Chile, aplicando arquitectura en capas, persistencia con JPA/Hibernate, autenticación JWT, control de versiones con Git/GitHub, empaquetado mediante Maven y orquestación con Docker Compose.

---

## Tecnologías utilizadas

- Java 17
- Spring Boot
- Maven
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- Hibernate / JPA
- Git y GitHub
- Docker
- Docker Compose
- Postman

---

## Arquitectura del proyecto

El proyecto está organizado siguiendo una arquitectura en capas:

```text
src/main/java/cl/techstore/api/
├── controller/
│   ├── AuthController.java
│   └── ProductoController.java
│
├── dto/
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
│   └── ProductoService.java
│
└── ApiApplication.java
```

---

## Descripción de capas

| Capa | Responsabilidad |
|---|---|
| `controller` | Recibe las peticiones HTTP y expone los endpoints REST. |
| `service` | Contiene la lógica de negocio del sistema. |
| `repository` | Permite acceder a la base de datos mediante Spring Data JPA. |
| `model` | Define las entidades JPA que representan tablas en la base de datos. |
| `dto` | Define objetos para recibir y devolver datos en las peticiones. |
| `security` | Contiene la configuración de seguridad, generación y validación JWT. |

---

## Modelo de datos

La entidad principal del sistema es `Producto`.

### Entidad Producto

| Campo | Tipo Java | Descripción |
|---|---|---|
| `id` | Long | Identificador único del producto. |
| `nombre` | String | Nombre del producto. |
| `descripcion` | String | Descripción breve del producto. |
| `precio` | Double | Precio del producto en CLP. |
| `stock` | Integer | Cantidad disponible en bodega. |
| `categoria` | String | Categoría del producto. |
| `activo` | Boolean | Indica si el producto está activo en el catálogo. |

La eliminación de productos se realiza mediante **borrado lógico**. Esto significa que el registro no se elimina físicamente de la base de datos. En su lugar, el campo `activo` cambia a `false`.

---

## Base de datos

El proyecto utiliza PostgreSQL como base de datos relacional.

La base de datos contiene exclusivamente la información relacionada con productos. Los usuarios para autenticación no se almacenan en la base de datos, ya que las credenciales de login están definidas en el archivo de configuración del proyecto.

### Configuración local en `application.properties`

```properties
spring.application.name=api

spring.datasource.url=jdbc:postgresql://localhost:5432/techstore
spring.datasource.username=admin
spring.datasource.password=admin123
spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

server.port=8080

# JWT
app.jwt.secret=MiClaveSecretaTechStoreChile2025JWTMuySegura123456789
app.jwt.expiration-ms=3600000

# Usuario fijo para login
app.security.username=admin@techstore.cl
app.security.password=Admin1234
```

---

## Seguridad y autenticación JWT

El sistema implementa autenticación mediante JWT.

Para acceder a los endpoints protegidos, primero se debe realizar login en:

```http
POST /auth/login
```

Si las credenciales son correctas, el sistema devuelve un token JWT. Ese token debe enviarse luego en el header `Authorization` para acceder a los endpoints de productos.

### Credenciales de prueba

```text
Usuario: admin@techstore.cl
Contraseña: Admin1234
```

### Ejemplo de login

```http
POST http://localhost:8080/auth/login
Content-Type: application/json
```

Body:

```json
{
  "username": "admin@techstore.cl",
  "password": "Admin1234"
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

### Uso del token

En las peticiones a productos se debe enviar el token en el header:

```http
Authorization: Bearer <token>
```

---

## Endpoints implementados

### Autenticación

| Método | Endpoint | Descripción | Requiere token |
|---|---|---|---|
| POST | `/auth/login` | Genera un token JWT. | No |

### Productos

| Método | Endpoint | Descripción | Código esperado | Requiere token |
|---|---|---|---|---|
| GET | `/api/productos` | Lista todos los productos activos. | 200 OK | Sí |
| GET | `/api/productos/{id}` | Busca un producto por ID. | 200 OK | Sí |
| POST | `/api/productos` | Crea un nuevo producto. | 201 Created | Sí |
| PUT | `/api/productos/{id}` | Modifica un producto existente. | 200 OK | Sí |
| DELETE | `/api/productos/{id}` | Realiza borrado lógico del producto. | 204 No Content | Sí |

---

## Ejemplos de uso en Postman

### 1. Login

```http
POST http://localhost:8080/auth/login
Content-Type: application/json
```

Body:

```json
{
  "username": "admin@techstore.cl",
  "password": "Admin1234"
}
```

Copiar el valor del campo `token`.

---

### 2. Listar productos

```http
GET http://localhost:8080/api/productos
```

Header:

```http
Authorization: Bearer <token>
```

Respuesta esperada:

```http
200 OK
```

---

### 3. Crear producto

```http
POST http://localhost:8080/api/productos
```

Headers:

```http
Authorization: Bearer <token>
Content-Type: application/json
```

Body:

```json
{
  "nombre": "Laptop Lenovo IdeaPad",
  "descripcion": "Notebook 15.6 pulgadas, 8GB RAM, 512GB SSD",
  "precio": 499990,
  "stock": 15,
  "categoria": "Computación",
  "activo": true
}
```

Respuesta esperada:

```http
201 Created
```

---

### 4. Modificar producto

```http
PUT http://localhost:8080/api/productos/1
```

Headers:

```http
Authorization: Bearer <token>
Content-Type: application/json
```

Body:

```json
{
  "nombre": "Laptop Lenovo IdeaPad Actualizada",
  "descripcion": "Notebook 15.6 pulgadas, 16GB RAM, 512GB SSD",
  "precio": 549990,
  "stock": 10,
  "categoria": "Computación",
  "activo": true
}
```

Respuesta esperada:

```http
200 OK
```

---

### 5. Eliminar producto

```http
DELETE http://localhost:8080/api/productos/1
```

Header:

```http
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

---

## Instrucciones para clonar y ejecutar el proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/dvnxeee/TechStore-Chile.git
```

### 2. Entrar a la carpeta del proyecto

```bash
cd TechStore-Chile
```

### 3. Cambiar a la rama de desarrollo

```bash
git checkout dev
```

---

## Compilación con Maven

Antes de ejecutar el proyecto o construir la imagen Docker, se debe generar el archivo `.jar`.

En Windows:

```bash
mvnw.cmd clean package -DskipTests
```

En Git Bash, Linux o Mac:

```bash
./mvnw clean package -DskipTests
```

Si la compilación es correcta, debe aparecer:

```text
BUILD SUCCESS
```

El archivo `.jar` se genera en:

```text
target/api-0.0.1-SNAPSHOT.jar
```

---

## Ejecución local sin Docker

Para ejecutar el proyecto localmente sin Docker, primero debe existir una base de datos PostgreSQL disponible con la siguiente configuración:

```text
Base de datos: techstore
Usuario: admin
Contraseña: admin123
Puerto: 5432
```

Luego se puede ejecutar la aplicación con:

```bash
java -jar target/api-0.0.1-SNAPSHOT.jar
```

La API quedará disponible en:

```text
http://localhost:8080
```

---

## Docker y Docker Compose

El proyecto incluye configuración para levantar el entorno completo mediante Docker Compose, integrando la aplicación Spring Boot y la base de datos PostgreSQL.

Se incluyen dos servicios principales:

```text
postgres       → base de datos PostgreSQL 15
microservicio  → aplicación Spring Boot
```

---

### Dockerfile

El archivo `Dockerfile` permite construir la imagen del microservicio a partir del archivo `.jar` generado por Maven.

```dockerfile
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Descripción del Dockerfile:

| Línea | Función |
|---|---|
| `FROM eclipse-temurin:17-jre` | Utiliza una imagen base con Java 17. |
| `WORKDIR /app` | Define el directorio de trabajo dentro del contenedor. |
| `COPY target/api-0.0.1-SNAPSHOT.jar app.jar` | Copia el archivo `.jar` generado por Maven al contenedor. |
| `EXPOSE 8080` | Expone el puerto 8080 para acceder a la API. |
| `ENTRYPOINT ["java", "-jar", "app.jar"]` | Ejecuta la aplicación Spring Boot dentro del contenedor. |

---

### docker-compose.yml

El archivo `docker-compose.yml` permite levantar PostgreSQL y el microservicio en conjunto.

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
    depends_on:
      - postgres
```

Configuración de PostgreSQL utilizada por Docker Compose:

```text
Base de datos: techstore
Usuario: admin
Contraseña: admin123
Puerto externo: 5432
```

Configuración del microservicio:

```text
Nombre del contenedor: techstore_api
Puerto externo: 8080
URL interna de conexión: jdbc:postgresql://postgres:5432/techstore
```

Dentro de Docker Compose, la aplicación no se conecta a `localhost`, sino al servicio llamado `postgres`. Por eso se utiliza:

```text
jdbc:postgresql://postgres:5432/techstore
```

---

## Ejecución con Docker Compose

Primero se debe generar el `.jar`:

En Windows:

```bash
mvnw.cmd clean package -DskipTests
```

En Git Bash, Linux o Mac:

```bash
./mvnw clean package -DskipTests
```

Luego se levanta el entorno completo con:

```bash
docker compose up --build
```

La API quedará disponible en:

```text
http://localhost:8080
```

PostgreSQL quedará disponible en:

```text
localhost:5432
```

Para detener los contenedores:

```bash
docker compose down
```

---

## Verificación funcional

Para comprobar el funcionamiento del proyecto, se deben validar los siguientes puntos:

```text
[✓] Ejecutar Maven y generar el archivo .jar.
[✓] Levantar PostgreSQL y el microservicio con Docker Compose.
[✓] Obtener token JWT con POST /auth/login.
[✓] Listar productos con GET /api/productos.
[✓] Crear producto con POST /api/productos.
[✓] Modificar producto con PUT /api/productos/{id}.
[✓] Eliminar producto con DELETE /api/productos/{id}.
[✓] Verificar que la eliminación sea lógica mediante activo = false.
[✓] Verificar persistencia de datos en PostgreSQL.
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

- `main`: contiene la versión final estable del proyecto.
- `dev`: contiene el desarrollo integrado.
- `feature/...`: ramas para funcionalidades específicas como Docker, JWT o CRUD.

## Estado actual del proyecto

Actualmente el proyecto cuenta con:

- Proyecto Spring Boot generado con Maven.
- Arquitectura en capas.
- Entidad `Producto` mapeada con JPA/Hibernate.
- Repositorio `ProductoRepository`.
- Servicio `ProductoService`.
- Controlador `ProductoController`.
- DTO para productos.
- CRUD de productos implementado.
- Borrado lógico mediante campo `activo`.
- Configuración de PostgreSQL.
- Login con JWT.
- Protección de endpoints mediante Spring Security.
- Compilación exitosa con Maven.
- Dockerfile para construir la imagen del microservicio.
- docker-compose.yml para levantar PostgreSQL y el microservicio.
- Pruebas de endpoints consideradas mediante Postman.
- Persistencia de datos mediante PostgreSQL.

---

## Autores

Proyecto desarrollado por estudiantes de la asignatura **Java: Diseño y Construcción de Soluciones Nativas en Nube**.