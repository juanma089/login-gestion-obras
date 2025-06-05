# 🔐 API de autenticación y registro - Spring Boot

Este proyecto es una API REST para la autenticación y gestión de usuarios, desarrollada con Spring Boot, JWT para la autenticación y MySQL como base de datos.

## Requisitos del Sistema

Para ejecutar este proyecto necesitas:

- **Java 17+** - Requerido por la configuración del proyecto.
- **Maven** - Para gestión de dependencias (opcional, incluye wrapper)
- **MySQL 8.0+** - Base de datos (opcional para desarrollo local)
- **Git** - Para clonar el repositorio

## Configuración e Instalación

### 1. Clonar el Repositorio
```bash
git clone https://github.com/juanma089/login-gestion-obras.git
```
```bash
cd login-gestion-obras
```

### 2. Configurar Base de Datos
Crear `src/main/resources/application.properties` con la configuración necesaria:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/my_database
spring.datasource.username=root
spring.datasource.password=123456
spring.jpa.hibernate.ddl-auto=update
jwt.secret=super-secret-key
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=emailtosendcodes@example.co
spring.mail.password=app password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

### 3. Compilar y Ejecutar
Usando el Maven wrapper incluido:

```bash
./mvnw clean install
./mvnw spring-boot:run
```

## Endpoints Disponibles

La API expone endpoints de autenticación bajo `/auth`:

- `POST /auth/signup` - Registro de usuario
- `POST /auth/login` - Inicio de sesión
- `POST /auth/send-reset-code` - Envío de código de recuperación
- `POST /auth/reset-password` - Restablecimiento de contraseña

## Verificación del Despliegue

Una vez ejecutándose, puedes verificar:
- Swagger UI: `http://localhost:8081/swagger-ui.html`

**Notes**

El proyecto utiliza JWT para autenticación y tiene configuración CORS para desarrollo local . La aplicación principal se encuentra en `LoginApplication` y utiliza Spring Boot 3.4.3.

## Configuración de Base de Datos

### 1. Crear el archivo de configuración

Necesitas crear o editar el archivo `src/main/resources/application.properties` con la configuración de MySQL:

### 2. Configuración específica de la base de datos

Las propiedades principales que debes configurar son:

- **`spring.datasource.url`**: URL de conexión a MySQL (formato: `jdbc:mysql://localhost:3306/nombre_base_datos`)
- **`spring.datasource.username`**: Usuario de MySQL
- **`spring.datasource.password`**: Contraseña del usuario
- **`spring.jpa.hibernate.ddl-auto=update`**: Permite que Hibernate actualice automáticamente el esquema de la base de datos

### 3. Preparar la base de datos

1. **Instalar MySQL** (si no lo tienes)
2. **Crear la base de datos**:
   ```sql
   CREATE DATABASE my_database;
   ```
3. **Crear usuario** (opcional):
   ```sql
   CREATE USER 'tu_usuario'@'localhost' IDENTIFIED BY 'tu_contraseña';
   GRANT ALL PRIVILEGES ON my_database.* TO 'tu_usuario'@'localhost';
   ```

### 5. Verificar la configuración

Una vez configurado, ejecuta:
```bash
./mvnw spring-boot:run
```

Si la configuración es correcta, Hibernate creará automáticamente las tablas necesarias gracias a `ddl-auto=update`.

**Notes**

El proyecto utiliza Spring Data JPA con Hibernate para la persistencia. La configuración CORS está establecida para desarrollo local.

## Modificación Temporal de la Configuración de Seguridad

Este apartado es simplemente para la creacion del primer usuario admistardor.

Puedes comentar temporalmente la validación de autenticación en el método `signup` de `AuthenticationServiceImpl.java:55-67`:

### Pasos para la Modificación Temporal

1. **Comentar la validación de autenticación** en `AuthenticationServiceImpl.signup()`:

```java
@Transactional
public User signup(@Valid RegisterUserDto input) {
    // Comentar temporalmente estas líneas
    /*
    /*Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Debe estar autenticado como ADMINISTRADOR para crear usuarios.");
        }

        User currentUser = (User) authentication.getPrincipal();

        if (currentUser.getRole() != RoleType.ADMINISTRADOR) {
            throw new AccessDeniedException("Solo un ADMINISTRADOR puede crear nuevos usuarios.");

        if (input.getRole() != RoleType.ADMINISTRADOR) {
            user.setAdmin(currentUser);
        }
        }*/
    
    // Resto del código permanece igual...
```

2. **Modificar temporalmente la configuración de seguridad** para permitir acceso sin autenticación al endpoint `/auth/signup`:

Cambiar la línea 42 para incluir `/auth/signup` en los endpoints permitidos:
`SecurityConfiguration.java:42-44`
```java
.requestMatchers("/auth/signup", "/auth/login", "/auth/send-reset-code",
        "/auth/reset-password", "/auth/verify-reset-code",
        "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**").permitAll()
```

eliminar de `SecurityConfiguration.java:45`

```java
.requestMatchers("/auth/signup").hasRole("ADMINISTRADOR")
```
y eliminar de `AuthenticationController.java:53`

```java
@SecurityRequirement(name = "bearerAuth")
```

### Proceso Completo

1. **Hacer las modificaciones temporales**
2. **Compilar y ejecutar la aplicación**:
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```
3. **Crear el primer usuario administrador** desde el local con **swagger** usando el endpoint `http://localhost:8081/swagger-ui/index.html#/authentication-controller/register` o desde Postman `http://localhost:8081/auth/signup`:
   ```json
   POST /auth/signup
   {
     "email": "admin@example.com",
     "password": "admin123",
     "fullName": "Administrador Principal",
     "role": "ADMINISTRADOR",
     "numberID": "00000000"
   }
   ```
4. **Revertir los cambios** en todos los archivos
5. **Recompilar y reiniciar** la aplicación

### Consideraciones de Seguridad

- **Solo hazlo en desarrollo**: Esta modificación temporal debe hacerse únicamente en un entorno de desarrollo
- **Revierte inmediatamente**: Una vez creado el usuario administrador, revierte los cambios de inmediato
- **No subas los cambios**: Asegúrate de no hacer commit de estas modificaciones temporales

**Notes**

Esta solución es más rápida que crear un script de inicialización, pero requiere disciplina para revertir los cambios. El endpoint `/auth/signup` normalmente requiere autenticación JWT como se ve en el controlador, por lo que la modificación temporal en la configuración de seguridad es necesaria.

## 📝 License

This project is licensed under the MIT License. You are free to use, modify, and distribute it. 😊

