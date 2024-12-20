# M06-UF2-JDBC

El projecto cuenta con plugins para ejectutar el programa usando mvn o para compilar un jar.

## Preparación

Primero que nada asegurarse de estar en la misma carpeta del pom.xml (gestor_jdbc)

`cd gestor_jdbc` <br>

Para ejecutar: `mvn compile exec:java`<br>
Para compilar a jar: `mvn package`<br>

<br>

Recomiendo modificar las credenciales de conexion a la base de datos y si es necesario, la url también.


## Funcionamiento

El gestor ofrece las funciones básicas de un CRUD. Se conecta a la base de datos y crea las tablas necesarias (DDL).

Luego permite las siguientes opciones:

```bash
    1. CARREGAR DADES DE PROVA
    2. CONSULTAR DADES (READ)
    3. MODIFICAR DADES (UPDATE)
    4. ELIMINAR DADES (DELETE)
    5. INSERIR NOVES DADES (CREATE)
    6. SORTIR
```

Luego de que se ha conectado es recomendable darle a la opción 1, para cargar los datos de prueba o demo. <br>

### 1 : Cargar datos de prueba

Carga un script DML que hace inserts de datos demo a las tablas de la bbdd.

### 2 : Consultar datos

Permite leer las entradas de la tabla AZAFATA, pero dando opcion de obtener los datos de diferentes maneras:

```bash
    1. Per ID
    2. Per nom
    3. Per passaport
    4. Sense filtrar
    5. Sortir
```

Además, cuando la busqueda conlleva varios resultados, permite la opción de guardar los resultados en un xml en la ruta:
`xml/SystemMilis.xml`

```
    Vols emmagatzemar aquestes dades? 
```

#### Por ID

Busca una sola azafata por ID y muestra por pantalla esa sola azafata.

#### Por nombre

Busca varias azafatas aplicando un filtro en el nombre

#### Por pasaporte

Busca varias azafatas aplicando un filtro por pasaporte

#### Sin filtrar

Busca y muestra todas las azafatas


### 3 : Modificar datos

Permite modificar los valores de los campos de una azafata, su nombre, su pasaporte, su telefono y su instagram.

```
    Introdueix l'opcio tot seguit >> 3
    ID Hostessa >> 6
    Nom (Laura Díaz) >> Maria
    Passaport (P6789012) >> xd
    Telèfon (555-3456) >> 123123123
    Ig (@laura_diaz) >> @maria
    Hostessa actualitzada exitosament

```

### 4 : Eliminar datos

Permite tambié eliminar azafatas por su ID

```
    ID Hostessa a eliminar >> 6
    Hostessa eliminada exitosament
    Vols continuar eliminant hostesses? n
```

### 5 : Insertar nuevos datos

Tambien permite introducir los campos de la azafata y agregarla.

```
    Nom >> maria
    Passaport >> 1231
    Telèfon >> 09404924
    Ig >> @maria
    ID assignat: 21
    Persona afegida correctament
    Hostessa afegida correctament
```



## Notas

- Si no se cierra el programa correctamente, la base de datos persistirá en el dispositivo.
- !IMPORTANTE! Por favor cambiar los valores de conexion en el fichero de [propiedades](./demo/src/main/resources/config.properties)
