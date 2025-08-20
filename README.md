# Backend E-commerce 

## Configuración del Proyecto

Para ejecutar este proyecto, es necesario configurar los secretos de la aplicación.

1.  Cree un archivo llamado `application-local.properties` en la carpeta `src/main/resources`.
2.  Añada las siguientes propiedades al archivo con sus valores correspondientes:

    ```properties
    # Contraseña de la base de datos PostgreSQL
    spring.datasource.password=<su_contraseña_de_bd>

    # Clave secreta para firmar los tokens JWT
    app.jwt-secret=<su_clave_secreta_larga_y_aleatoria>

    # Duración del token en milisegundos (ej. 7 días)
    app.jwt-expiration-milliseconds=604800000
    ```
