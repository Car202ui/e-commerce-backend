# Backend E-commerce 

## Configuración del Proyecto

Este documento no solo explica cómo configurar y ejecutar el backend, sino que también incluye la guía detallada de todos los endpoints para probarlos con Postman, un plus que demuestra la calidad y la documentación exhaustiva de tu trabajo.

Backend E-commerce
Este repositorio contiene el código fuente del backend para  E-commerce, desarrollado con Spring Boot. La aplicación implementa una API REST completa para gestionar productos, inventarios, órdenes y usuarios, con un sistema de seguridad robusto basado en JWT y roles.

✨ Características Principales
Arquitectura Limpia: Código organizado en capas (controller, service, repository, domain) para máxima mantenibilidad.

Seguridad Robusta: Autenticación con JWT y autorización basada en roles (USER, ADMIN).

Gestión de Base de Datos Profesional: Uso de Liquibase para el versionado del esquema de la base de datos.

Manejo de Errores Centralizado: Un GlobalExceptionHandler que proporciona respuestas de error claras y consistentes.

Documentación de API: Documentación interactiva y profesional con Swagger (OpenAPI).

Auditoría Automática: Registro de eventos importantes (creación/actualización de entidades) mediante Listeners de JPA.

Código de Calidad: Cobertura de pruebas unitarias con JUnit 5 y Mockito para la lógica de negocio crítica.

🚀 Cómo Empezar
Sigue estas instrucciones para configurar y ejecutar el proyecto en tu entorno local.

1. Prerrequisitos
   Asegúrate de tener instalado el siguiente software:

Java 17 (o superior)

Apache Maven

PostgreSQL (Base de Datos)

Git (Control de Versiones)

Un cliente de API como Postman

2. Configuración de la Base de Datos
   Abre tu cliente de PostgreSQL (ej. pgAdmin).

Crea una nueva base de datos con el nombre ecommerce.

El script de Liquibase (001-initial-schema.sql) creará automáticamente todas las tablas y roles iniciales cuando ejecutes la aplicación por primera vez.

3. Configuración del Proyecto (Secretos)
   Este proyecto sigue la buena práctica de no guardar secretos en el control de versiones.

Navega a la carpeta src/main/resources.

Crea un nuevo archivo llamado application-local.properties.

Pega el siguiente contenido en el archivo y rellena los valores con tus secretos locales.

Properties

# Archivo de configuración local - NO SUBIR A GIT

# Contraseña de tu base de datos PostgreSQL
spring.datasource.password=1234

# Clave secreta para firmar los tokens JWT (debe ser larga y aleatoria)
app.jwt-secret=N2E1YjVjN2UtN2U3ZC00YjVjLTg2YzUtN2U3ZDRiNWM4NjU3N2E1YjVjN2UtN2U3ZC00YjVjLTg2YzUtN2U3ZDRiNWM4NjU3

# Duración del token en milisegundos (ej. 7 días)
app.jwt-expiration-milliseconds=604800000

# Configuración de la promoción de descuento por tiempo
promotion.discount.enabled=true
promotion.discount.start-date=2025-08-01T00:00:00
promotion.discount.end-date=2025-08-31T23:59:59
4. Ejecución
   Abre una terminal en la carpeta raíz del proyecto.

Ejecuta el siguiente comando Maven para iniciar la aplicación:

Bash

mvn spring-boot:run
El backend estará corriendo en http://localhost:9091.

5. Documentación de la API (Swagger)
   Una vez que la aplicación esté en ejecución, puedes acceder a la documentación interactiva de la API a través de Swagger UI en la siguiente URL:

http://localhost:8080/swagger-ui.html

Desde Swagger, puedes probar todos los endpoints, incluyendo la autorización con JWT.

➕ Guía de Endpoints para Postman
Aquí tienes una guía completa para probar cada endpoint de la API con Postman.

Nota Importante: Todos los endpoints, excepto los de /api/auth, requieren un Token de Autenticación. Primero haz login para obtener un accessToken y añádelo en la pestaña Authorization -> Bearer Token en cada petición.

1. Autenticación (/api/auth)
   Registrar un nuevo usuario

Método: POST

URL: http://localhost:9091/api/auth/register

Body (JSON):

JSON

{
"username": "nuevo_usuario",
"email": "correo@ejemplo.com",
"password": "password123"
}
Iniciar Sesión

Método: POST

URL: http://localhost:9091/api/auth/login

Body (JSON):

JSON

{
"username": "nuevo_usuario",
"password": "password123"
}
2. Gestión de Productos (/api/products)
   Crear un Producto

Authorization: Bearer Token

Método: POST

URL: http://localhost:9091/api/products

Body (JSON):

JSON

{
"name": "Laptop Pro",
"description": "Laptop de alto rendimiento",
"price": 1500.99,
"isActive": true
}
Obtener todos los Productos

Authorization: Bearer Token

Método: GET

URL: http://localhost:9091/api/products

Buscar Productos por Nombre

Authorization: Bearer Token

Método: GET

URL: http://localhost:9091/api/products/search?name=laptop

3. Gestión de Inventario (/api/inventory)
   Establecer Inventario para un Producto

Authorization: Bearer Token

Método: POST

URL: http://localhost:9091/api/inventory/{productId}?quantity=100 (reemplaza {productId} por un ID real).

Actualizar Inventario de un Producto

Authorization: Bearer Token

Método: PUT

URL: http://localhost:9091/api/inventory/{productId}?quantityChange=-5 (para restar 5 unidades).

Consultar Inventario de un Producto

Authorization: Bearer Token

Método: GET

URL: http://localhost:9091/api/inventory/{productId}

4. Gestión de Órdenes (/api/orders)
   Crear una Orden

Authorization: Bearer Token

Método: POST

URL: http://localhost:9091/api/orders

Body (JSON):

JSON

{
"items": [
{
"productId": 1,
"quantity": 2
}
],
"randomOrder": false
}
Ver mis Órdenes

Authorization: Bearer Token

Método: GET

URL: http://localhost:9091/api/orders/my-orders

5. Gestión de Usuarios (Admin) (/api/users)
   Obtener todos los Usuarios (Solo Admin)

Authorization: Bearer Token (de un usuario ADMIN)

Método: GET

URL: http://localhost:9091/api/users

6. Reportes (Admin) (/api/reports)
   Reporte de Productos Activos

Authorization: Bearer Token (de un usuario ADMIN)

Método: GET

URL: http://localhost:8080/api/reports/products/active

Reporte de Top 5 Productos Vendidos

Authorization: Bearer Token (de un usuario ADMIN)

Método: GET

URL: http://localhost:9091/api/reports/products/top-selling

Reporte de Top 5 Clientes Frecuentes

Authorization: Bearer Token (de un usuario ADMIN)

Método: GET

URL: http://localhost:9091/api/reports/customers/top-frequent