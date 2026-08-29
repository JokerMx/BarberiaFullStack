# 🪒 Barbería API

API REST para sistema de gestión de barbería, desarrollada con arquitectura **Domain-Driven Design (DDD)** utilizando Spring Boot 3 y Java 21.

## 📋 Tabla de Contenidos

- [Tecnologias](#tecnologias)
- [Arquitectura](#arquitectura)
- [Requisitos Previos](#requisitos-previos)
- [Configuracion](#configuracion)
- [Ejecucion](#ejecucion)
- [Endpoints](#endpoints)
- [Base de Datos](#base-de-datos)
- [Perfiles](#perfiles)
- [Roles de Usuario](#roles-de-usuario)
- [DTOs y Servicios](#dtos-y-servicios)
- [Seguridad](#seguridad)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Notas Adicionales](#notas-adicionales)
- [Licencia](#licencia)

---

## 🛠 Tecnologias

| Tecnologia | Version | Descripcion |
|------------|---------|-------------|
| Java | 21 | Lenguaje de programacion |
| Spring Boot | 3.2.8 | Framework principal |
| Maven | 3.9.16 (wrapper) | Gestion de dependencias |
| PostgreSQL | - | Base de datos (produccion) |
| H2 | - | Base de datos en memoria (desarrollo) |
| Flyway | - | Migraciones de base de datos |
| Lombok | 1.18.34 | Reduccion de codigo boilerplate |
| Thymeleaf | - | Motor de plantillas |
| BCrypt (jbcrypt) | 0.4 | Encriptacion de contrasenas |
| Spring Validation | - | Validacion de datos |
| SpringDoc OpenAPI | 2.5.0 | Documentacion de API (Swagger UI) |

---

## 🏗 Arquitectura

El proyecto sigue los principios de **Domain-Driven Design (DDD)** con una separacion clara de responsabilidades en capas:

```
src/main/java/cl/Barberia/
├── application/           # Casos de uso y logica de aplicacion
│   ├── authentication/    # Servicios de autenticacion
│   └── usermanagement/    # Servicios de gestion de usuarios
├── domain/                # Logica de negocio pura
│   ├── authentication/    # Entidades y value objects de autenticacion
│   └── usermanagement/    # Entidades y value objects de usuarios
├── infrastructure/        # Implementaciones tecnicas
│   ├── config/            # Configuraciones (OpenAPI)
│   ├── exception/         # Manejo global de excepciones
│   └── persistence/       # Acceso a datos (JPA, Flyway)
└── interfaces/            # Capa de presentacion
    └── rest/              # Controladores REST
```

### Patrones Utilizados

- **Value Objects**: `Username`, `Email`, `NombreCompleto`, `PasswordHash`, `IntentosFallidos`
- **Builder Pattern**: Entidad `Usuario`
- **Repository Pattern**: Abstraccion de acceso a datos
- **Service Layer**: Logica de aplicacion desacoplada
- **DTOs**: Objetos de transferencia para requests/responses (`RegistroUsuarioRequest`, `UsuarioResponse`, `ActualizarUsuarioRequest`)
- **Excepciones de Dominio**: `UsuarioNoEncontradoException`, `CredencialesInvalidasException`, `CuentaBloqueadaException`

---

## 📋 Requisitos Previos

- Java 21+
- Maven 3.8+
- PostgreSQL 12+
- IDE recomendado: IntelliJ IDEA / Eclipse / VS Code

---

## ⚙️ Configuracion

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd Barberia
```

### 2. Configurar base de datos

El proyecto incluye una base de datos en memoria **H2** para desarrollo, por lo que no es necesario configurar PostgreSQL localmente en el perfil `dev`.

Para **produccion**, crear la base de datos en PostgreSQL:

```sql
CREATE DATABASE barberia_db;
```

### 3. Configurar propiedades

El archivo [`application.properties`](src/main/resources/application.properties) contiene la configuracion base. Los perfiles especificos se encuentran en:

- [`application-dev.properties`](src/main/resources/application-dev.properties) - Desarrollo (H2 en memoria, Flyway desactivado, Swagger activado)
- [`application-prod.properties`](src/main/resources/application-prod.properties) - Produccion (PostgreSQL, Flyway activado, Swagger desactivado)
- [`application-test.properties`](src/main/resources/application-test.properties) - Testing (H2 en memoria, Flyway desactivado, Swagger desactivado)

**Configuracion por defecto (desarrollo):**
- Puerto: `8080`
- Base de datos: H2 en memoria (`jdbc:h2:mem:barberia_db`)
- Consola H2: `http://localhost:8080/h2-console`
- Usuario H2: `sa` / sin password
- Perfil activo: `dev`

## Perfiles

El proyecto incluye tres perfiles de ejecucion:

| Perfil | Archivo | Uso |
|--------|---------|-----|
| `dev` | `application-dev.properties` | Desarrollo local |
| `prod` | `application-prod.properties` | Produccion |
| `test` | `application-test.properties` | Pruebas automatizadas |

El perfil activo se define en `application.properties` mediante la propiedad `spring.profiles.active`.

---

## 🚀 Ejecucion

### Modo desarrollo

```bash
./mvnw spring-boot:run
```

O con Maven instalado:

```bash
mvn spring-boot:run
```

### Compilar y ejecutar JAR

```bash
./mvnw clean package
java -jar target/barberia-api-1.0.0.jar
```

### Ejecutar tests

```bash
./mvnw verify
```

En Windows, ejecutar `mvnw.cmd verify`. El informe de cobertura JaCoCo se genera en
`target/site/jacoco/index.html`. Con Docker disponible se ejecuta una prueba de integracion contra
PostgreSQL que registra un usuario mediante HTTP y verifica el dato persistido; sin Docker, esa
prueba se omite automaticamente.

### Evidencia de calidad

Resultado de la ultima ejecucion validada con `mvnw.cmd verify`:

| Indicador | Resultado |
|-----------|-----------|
| Pruebas JUnit 5 | 79 |
| Fallos y errores | 0 |
| Pruebas omitidas | 1 (integracion PostgreSQL sin Docker disponible) |
| Cobertura JaCoCo global | 99,82% |

La medicion excluye exclusivamente `BarberiaApplication`, el punto de arranque sin reglas de negocio.
El informe HTML permite revisar el detalle por clase y paquete.

---

## 🔌 Endpoints

### Autenticacion

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| `POST` | `/api/auth/login` | Autenticacion de usuario |
| `GET` | `/api/auth/health` | Health check de la API |

### Usuarios

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| `POST` | `/api/usuarios/registro` | Registro de nuevo usuario |
| `GET` | `/api/usuarios` | Listar todos los usuarios |
| `GET` | `/api/usuarios/{id}` | Obtener usuario por ID |
| `GET` | `/api/usuarios/username/{username}` | Obtener usuario por username |
| `GET` | `/api/usuarios/rol/{rol}` | Listar usuarios por rol (ADMIN, BARBERO, CLIENTE) |
| `PUT` | `/api/usuarios/{id}` | Actualizar usuario |
| `DELETE` | `/api/usuarios/{id}` | Eliminar usuario |

### Web

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| `GET` | `/` | Pagina de inicio |

---

## 🗄 Base de Datos

### Migraciones (Flyway)

Las migraciones se encuentran en [`src/main/resources/db/migration/`](src/main/resources/db/migration/):

- **V1__create_usuarios_table.sql**: Crea la tabla `usuarios` con indices

### Esquema de la tabla `usuarios`

| Campo | Tipo | Descripcion |
|-------|------|-------------|
| `id` | BIGSERIAL | Identificador unico (PK) |
| `username` | VARCHAR(50) | Nombre de usuario (unico) |
| `email` | VARCHAR(100) | Correo electronico (unico) |
| `password_hash` | VARCHAR(255) | Contrasena encriptada con BCrypt |
| `nombre_completo` | VARCHAR(100) | Nombre completo del usuario |
| `rol` | VARCHAR(20) | Rol del usuario (ADMIN, BARBERO, CLIENTE) |
| `activo` | BOOLEAN | Estado de la cuenta |
| `intentos_fallidos` | INT | Contador de intentos fallidos de login |
| `bloqueado_hasta` | TIMESTAMP | Fecha/hasta la que esta bloqueada la cuenta |
| `fecha_creacion` | TIMESTAMP | Fecha de creacion |
| `fecha_actualizacion` | TIMESTAMP | Fecha de ultima actualizacion |

---

## 👥 Roles de Usuario

| Rol | Descripcion |
|-----|-------------|
| `ADMIN` | Administrador del sistema |
| `BARBERO` | Profesional de la barberia |
| `CLIENTE` | Cliente del servicio |

---

## 📦 DTOs y Servicios

### DTOs de Usuario

| DTO | Uso |
|-----|-----|
| `RegistroUsuarioRequest` | Request para registro de usuario |
| `UsuarioResponse` | Response para lectura de usuarios |
| `ActualizarUsuarioRequest` | Request para actualizacion parcial de usuario |

### Servicios de Gestion de Usuarios

| Servicio | Responsabilidad |
|----------|----------------|
| `RegistrarUsuarioService` | Registro de nuevos usuarios |
| `ListarUsuariosService` | Listado y filtrado por rol |
| `ObtenerUsuarioService` | Busqueda por ID o username |
| `ActualizarUsuarioService` | Actualizacion de datos de usuario |
| `EliminarUsuarioService` | Eliminacion logica/fisica de usuario |

---

## 🔒 Seguridad

- **Encriptacion de contrasenas**: BCrypt
- **Bloqueo de cuenta**: 5 intentos fallidos bloquean la cuenta por 30 minutos
- **Validacion de datos**: Spring Validation en DTOs

---

## 📁 Estructura del Proyecto

```
Barberia/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
├── .gitattributes
├── src/
│   ├── main/
│   │   ├── java/cl/Barberia/
│   │   │   ├── BarberiaApplication.java
│   │   │   ├── application/
│   │   │   │   ├── authentication/
│   │   │   │   │   ├── LoginService.java
│   │   │   │   │   └── DTOs/
│   │   │   │   │       ├── LoginRequest.java
│   │   │   │   │       └── LoginResponse.java
│   │   │   │   └── usermanagement/
│   │   │   │       ├── RegistrarUsuarioService.java
│   │   │   │       ├── ListarUsuariosService.java
│   │   │   │       ├── ObtenerUsuarioService.java
│   │   │   │       ├── ActualizarUsuarioService.java
│   │   │   │       ├── EliminarUsuarioService.java
│   │   │   │       └── DTOs/
│   │   │   │           ├── RegistroUsuarioRequest.java
│   │   │   │           ├── UsuarioResponse.java
│   │   │   │           └── ActualizarUsuarioRequest.java
│   │   │   ├── domain/
│   │   │   │   ├── authentication/
│   │   │   │   │   ├── IntentosFallidos.java
│   │   │   │   │   ├── PasswordHash.java
│   │   │   │   │   ├── Username.java
│   │   │   │   │   ├── UsuarioAutenticado.java
│   │   │   │   │   └── exceptions/
│   │   │   │   │       ├── CredencialesInvalidasException.java
│   │   │   │   │       └── CuentaBloqueadaException.java
│   │   │   │   └── usermanagement/
│   │   │   │       ├── Email.java
│   │   │   │       ├── NombreCompleto.java
│   │   │   │       ├── Rol.java
│   │   │   │       ├── Usuario.java
│   │   │   │       ├── UsuarioRepository.java
│   │   │   │       └── exceptions/
│   │   │   │           └── UsuarioNoEncontradoException.java
│   │   │   ├── infrastructure/
│   │   │   │   ├── exception/
│   │   │   │   │   └── GlobalExceptionHandler.java
│   │   │   │   ├── persistence/
│   │   │   │   │   ├── adapter/
│   │   │   │   │   │   └── UsuarioRepositoryAdapter.java
│   │   │   │   │   ├── entity/
│   │   │   │   │   │   └── UsuarioEntity.java
│   │   │   │   │   └── repository/
│   │   │   │   │       └── UsuarioRepositoryJpa.java
│   │   │   │   └── security/
│   │   │   └── interfaces/
│   │   │       └── rest/
│   │   │           ├── AuthController.java
│   │   │           ├── HomeController.java
│   │   │           └── UsuarioController.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       ├── application-prod.properties
│   │       ├── application-test.properties
│   │       ├── db/migration/
│   │       │   └── V1__create_usuarios_table.sql
│   │       ├── static/
│   │       │   └── css/
│   │       │       └── home.css
│   │       └── templates/
│   │           └── home.html
│   └── test/
│       └── java/cl/Barberia/
│           └── BarberiaApplicationTests.java
```

---

## 📝 Notas Adicionales

- El proyecto utiliza **Lombok** para reducir codigo boilerplate (getters, setters, builders)
- Las migraciones de base de datos se gestionan automaticamente con **Flyway**
- El manejo de excepciones global se realiza en [`GlobalExceptionHandler`](src/main/java/cl/Barberia/infrastructure/exception/GlobalExceptionHandler.java)
- La capa de presentacion incluye vistas **Thymeleaf** para la pagina de inicio

---

## 📄 Licencia

Este proyecto es de uso educativo y demostrativo.
