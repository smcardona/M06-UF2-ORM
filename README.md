# M06-UF2-JDBC

El projecto cuenta con plugins para ejectutar el programa usando mvn o para compilar un jar.

## Comandos 

Primero que nada asegurarse de estar en la misma carpeta del pom.xml (demo)

`cd demo` <br>

Para ejecutar: `mvn compile exec:java`<br>
Para compilar a jar: `mvn package`<br>


## Notas

- Si no se cierra el programa correctamente, la base de datos persistirá en el dispositivo.
- !IMPORTANTE! Por favor cambiar los valores de conexion en el fichero de [propiedades](./demo/src/main/resources/config.properties)
