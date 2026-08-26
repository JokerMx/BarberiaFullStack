# 🪒 Barbería Full Stack

Sistema de gestión de barbería compuesto por una API REST en Spring Boot y un frontend TypeScript servido con Vite/Nginx. Incluye autenticación por sesión HTTP, gestión de usuarios, servicios y reservas.

## 📋 Tabla de Contenidos

- [Tecnologias](#tecnologias)
- [Frontend](#frontend)
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
| Spring Security | 6.x | Autenticacion por sesion y autorizacion por roles |
| SpringDoc OpenAPI | 2.5.0 | Documentacion de API (Swagger UI) |
| TypeScript | 6.x | Frontend |
| Vite | 8.x | Desarrollo y build del frontend |
| Nginx | Alpine | Servidor frontend y proxy `/api` |

---

## Frontend

El frontend se encuentra en [`frontend/`](frontend/) y contiene las siguientes vistas:

- Login y registro de usuarios.
- Dashboard con estadisticas y reservas.
- Catalogo y gestion CRUD de servicios para administradores.
- Gestion de usuarios, filtro por rol, cambio de estado, rol, edicion y eliminacion.
- Creacion y cancelacion de reservas.
- Filtros de reservas por cliente, fecha y estado.

En desarrollo, Vite redirige `/api` a `http://localhost:8080` mediante [`frontend/vite.config.ts`](frontend/vite.config.ts). En Docker, Nginx usa el servicio `backend` como proxy.

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
- Node.js 20+
- npm 10+
- PostgreSQL 12+
- Docker y Docker Compose (opcional)
- IDE recomendado: IntelliJ IDEA / Eclipse / VS Code

---

## ⚙️ Configuracion

### 1. Clonar el repositorio

```bash
git clone <url-del-repositorio>
cd BarberiaFullStack
```

### 2. Configurar base de datos

El proyecto incluye una base de datos en memoria **H2** para desarrollo, por lo que no es necesario configurar PostgreSQL localmente en el perfil `dev`.

Para **produccion**, crear la base de datos en PostgreSQL:

```sql
CREATE DATABASE barberia_db;
```

### 3. Configurar propiedades

El archivo [`application.properties`](backend/src/main/resources/application.properties) contiene la configuracion base. Los perfiles especificos se encuentran en:

- [`application-dev.properties`](backend/src/main/resources/application-dev.properties) - Desarrollo (H2 en memoria, Flyway desactivado, Swagger activado)
- [`application-prod.properties`](backend/src/main/resources/application-prod.properties) - Produccion (PostgreSQL, Flyway activado, Swagger desactivado)
- [`application-test.properties`](backend/src/main/resources/application-test.properties) - Testing (H2 en memoria, Flyway desactivado, Swagger desactivado)

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

Backend, desde `backend/`:

```bash
./mvnw spring-boot:run
```

O con Maven instalado:

```bash
mvn spring-boot:run
```

Frontend, desde `frontend/`:

```bash
npm install
npm run dev
```

El frontend queda disponible en `http://localhost:5173` y la API en `http://localhost:8080`.

### Docker Compose

Desde la raiz del proyecto:

```bash
copy .env.example .env
# Edita .env y usa una contraseña local segura
docker compose up --build
```

Esto inicia PostgreSQL, backend y frontend. La aplicación web queda en `http://localhost:5173`.
Los secretos se inyectan mediante variables de entorno y no se guardan en Git. El perfil `prod`
desactiva Swagger y las imágenes se ejecutan sin root.

### Script multiplataforma

También puedes ejecutar todo el ciclo de parada, compilación y arranque desde la raíz:

**Windows (PowerShell o CMD):**

```powershell
.\start-project.bat
```

o:

```powershell
.\start-project.ps1
```

**Linux y macOS:**

```bash
chmod +x start-project.sh
./start-project.sh
```

El script detecta automáticamente el sistema, Maven o `mvnw`, y la variante disponible de Docker Compose (`docker compose` o `docker-compose`).

### Compilar y ejecutar JAR

```bash
./mvnw clean package
java -jar target/barberia-api-1.0.0.jar
```

### Ejecutar tests

```bash
./mvnw test
```

### Validar frontend

Desde `frontend/`:

```bash
npm run build
```

---

## 🔌 Endpoints

### Autenticacion

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| `POST` | `/api/auth/login` | Autenticacion de usuario |
| `POST` | `/api/auth/logout` | Cierra la sesion HTTP |
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

### Servicios

| Metodo | Endpoint | Permiso | Descripcion |
|--------|----------|---------|-------------|
| `GET` | `/api/servicios` | Publico | Listar servicios activos |
| `GET` | `/api/servicios/{id}` | Publico | Obtener un servicio |
| `POST` | `/api/servicios` | `ADMIN` | Crear servicio |
| `PUT` | `/api/servicios/{id}` | `ADMIN` | Actualizar servicio |
| `DELETE` | `/api/servicios/{id}` | `ADMIN` | Desactivar servicio |

### Reservas

| Metodo | Endpoint | Permiso | Descripcion |
|--------|----------|---------|-------------|
| `GET` | `/api/reservas` | Autenticado | Listar reservas |
| `GET` | `/api/reservas/cliente/{clienteId}` | Autenticado | Filtrar por cliente |
| `GET` | `/api/reservas/fecha/{fecha}` | Autenticado | Filtrar por fecha `YYYY-MM-DD` |
| `GET` | `/api/reservas/estado/{estado}` | Autenticado | Filtrar por estado |
| `POST` | `/api/reservas` | Autenticado | Crear reserva |
| `PUT` | `/api/reservas/{id}/estado?estado={estado}` | `ADMIN` | Cambiar estado |
| `DELETE` | `/api/reservas/{id}` | Autenticado | Cancelar reserva |

### Web

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| `GET` | `/` | Pagina de inicio |

---

## 🗄 Base de Datos

### Migraciones (Flyway)

Las migraciones se encuentran en [`backend/src/main/resources/db/migration/`](backend/src/main/resources/db/migration/):

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
- **Sesion HTTP**: el login crea una sesion y el frontend la conserva mediante cookie `JSESSIONID`
- **Autorizacion**: Spring Security protege las rutas de usuarios y las operaciones administrativas
- **Roles**: `ADMIN` administra usuarios, servicios y estados de reservas; los usuarios autenticados pueden crear y consultar reservas
- **Nota**: el endpoint publico de registro recibe el rol enviado por el cliente; para impedir que un usuario se registre como `ADMIN`, el backend debe restringir ese valor a `CLIENTE`

---

## 📁 Estructura del Proyecto

La estructura principal separa la aplicación backend y el cliente web:

```
BarberiaFullStack/
├── backend/     # Spring Boot, REST, seguridad, JPA y Flyway
├── frontend/    # TypeScript, Vite, vistas y servicios API
├── docker-compose.yml
└── README.md
```

### Estructura del backend

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
