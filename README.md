# TechStore Chile API

Microservicio RESTful desarrollado en Java con Spring Boot para la gestión de productos de la tienda ficticia **TechStore Chile**.

El proyecto permite administrar productos mediante operaciones CRUD, proteger los endpoints mediante autenticación JWT, conectar la aplicación con una base de datos relacional PostgreSQL y generar un archivo ejecutable `.jar` utilizando Maven.

---

## Integrantes

- Integrante 1: Renato Valenzuela
- Integrante 2: Danae Miranda

---

## Objetivo del proyecto

Desarrollar un microservicio RESTful en Java con Spring Boot que permita gestionar el catálogo de productos de TechStore Chile, aplicando arquitectura en capas, persistencia con JPA/Hibernate, autenticación JWT, control de versiones con Git/GitHub y empaquetado mediante Maven.

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
- Docker / Docker Compose
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
````

### Descripción de capas

| Capa         | Responsabilidad                                                      |
| ------------ | -------------------------------------------------------------------- |
| `controller` | Recibe las peticiones HTTP y expone los endpoints REST.              |
| `service`    | Contiene la lógica de negocio del sistema.                           |
| `repository` | Permite acceder a la base de datos mediante Spring Data JPA.         |
| `model`      | Define las entidades JPA que representan tablas en la base de datos. |
| `dto`        | Define objetos para recibir y devolver datos en las peticiones.      |
| `security`   | Contiene la configuración de seguridad, generación y validación JWT. |

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

La eliminación de productos se realiza mediante **borrado lógico**, por lo tanto el registro no se elimina físicamente de la base de datos. En su lugar, el campo `activo` cambia a `false`.

---

## Base de datos

El proyecto está configurado para utilizar PostgreSQL.

La base de datos debe contener exclusivamente la información relacionada con productos. Los usuarios para autenticación no se almacenan en la base de datos, ya que las credenciales de login están definidas en el archivo de configuración del proyecto.

### Configuración actual en `application.properties`

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

## Endpoints disponibles

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

---

## Ejemplos de uso en Postman

### 1. Login

```http
POST http://localhost:8080/auth/login
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

---

### 3. Crear producto

```http
POST http://localhost:8080/api/productos
```

Header:

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

Header:

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

### 4. Verificar dependencias y compilar

En Windows:

```bash
mvnw.cmd clean package -DskipTests
```

En Git Bash o Linux/Mac:

```bash
./mvnw clean package -DskipTests
```

Si la compilación es correcta, debe aparecer:

```text
BUILD SUCCESS
```

---

## Ejecución local

Para ejecutar el proyecto localmente, primero debe existir una base de datos PostgreSQL disponible con esta configuración:

```text
Base de datos: techstore
Usuario: admin
Contraseña: admin123
Puerto: 5432
```

Luego ejecutar:

```bash
java -jar target/api-0.0.1-SNAPSHOT.jar
```

La aplicación quedará disponible en:

```text
http://localhost:8080
```

---

## Docker y Docker Compose

El proyecto considera el uso de Docker Compose para levantar el entorno completo, incluyendo el microservicio y la base de datos PostgreSQL.

Esta parte corresponde a la etapa de integración y pruebas finales del proyecto.

Servicios esperados:

```text
1. PostgreSQL
2. Microservicio Spring Boot
```

El archivo `docker-compose.yml` debe permitir levantar ambos servicios y conectar la aplicación con la base de datos PostgreSQL.

---

## Flujo de trabajo con Git

El proyecto utiliza un flujo de trabajo basado en ramas:

```text
main → versión final estable para entrega
dev → rama principal de desarrollo
feature/... → ramas opcionales para funcionalidades específicas
```

### Ramas principales

* `main`: contiene la versión final del proyecto.
* `dev`: contiene el desarrollo integrado.
* `feature/...`: ramas para funcionalidades específicas como Docker, JWT o CRUD.

### Commits realizados

Algunos commits relevantes del proyecto:

```text
Inicializa proyecto Spring Boot para TechStore
Implementa estructura en capas y CRUD de productos
Configura conexion a base de datos PostgreSQL
Implementa autenticacion JWT
```

El merge hacia `main` debe realizarse solo cuando el proyecto esté finalizado, probado y operativo.

---

## Estado actual del proyecto

Actualmente el proyecto cuenta con:

* Proyecto Spring Boot generado con Maven.
* Arquitectura en capas.
* Entidad `Producto` mapeada con JPA/Hibernate.
* Repositorio `ProductoRepository`.
* Servicio `ProductoService`.
* Controlador `ProductoController`.
* CRUD de productos.
* Borrado lógico mediante campo `activo`.
* Configuración de PostgreSQL.
* Login con JWT.
* Protección de endpoints mediante Spring Security.
* Compilación exitosa con Maven.

---

## Pendientes de integración

* Probar conexión real con PostgreSQL.
* Levantar PostgreSQL mediante Docker.
* Crear o validar `Dockerfile`.
* Crear o validar `docker-compose.yml`.
* Probar endpoints en Postman.
* Verificar persistencia de datos en la base de datos.
* Realizar pruebas finales para la presentación.
* Hacer merge de `dev` a `main` cuando todo esté operativo.

---

## Evidencias sugeridas para la presentación

Para la presentación en video se recomienda mostrar:

1. Repositorio GitHub y ramas `main` / `dev`.
2. Estructura del proyecto en capas.
3. Entidad `Producto`.
4. Repositorio, servicio y controlador.
5. Configuración de PostgreSQL en `application.properties`.
6. Login JWT.
7. Pruebas en Postman.
8. Generación del `.jar` con Maven.
9. Ejecución local del proyecto.
10. Docker Compose levantando el entorno completo.
11. Evidencia de datos persistidos en PostgreSQL.

---

## Autores

Proyecto desarrollado por estudiantes de la asignatura **Java: Diseño y Construcción de Soluciones Nativas en Nube**.