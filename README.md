# Viviendas Java

Aplicación de escritorio JavaFX con MySQL.

## Requisitos y ejecución

- JDK 25, indicado por `pom.xml`.
- MySQL iniciado en `localhost:3306`, con el esquema `viviendas` existente.
  En este equipo se usa el contenedor `mysql-container`.
- Internet para descargar dependencias en la primera ejecución.

Desde IntelliJ IDEA, abrir `pom.xml`, seleccionar JDK 25, recargar Maven y ejecutar
`Applications.Inicio`. La clase `ViviendaApplication` contiene la ventana JavaFX;
`Inicio` es el punto de entrada compatible con la ejecución directa del IDE.

En macOS/Linux, desde la raíz del proyecto:

```sh
./mvnw javafx:run
```

En Windows: `mvnw.cmd javafx:run`.

## Conexión

`Conexion` conserva los valores locales anteriores por defecto. Se pueden cambiar
sin editar código mediante estas variables de entorno:

| Variable | Propiedad Java equivalente |
| --- | --- |
| `VIVIENDAS_DB_URL` | `viviendas.db.url` |
| `VIVIENDAS_DB_USER` | `viviendas.db.user` |
| `VIVIENDAS_DB_PASSWORD` | `viviendas.db.password` |

Las propiedades Java tienen prioridad. Para `javafx:run`, que crea otra JVM,
utilizar las variables de entorno. Las conexiones tienen tiempos límite por
defecto y se cierran después de cada operación. Un error de MySQL muestra un
mensaje recuperable en la aplicación, sin terminar el proceso.

El repositorio no incluye una migración del esquema: para instalarlo en otro
equipo hay que importar la base correspondiente.

## Validaciones e integridad

- Nombres y apellidos requeridos, sin espacios exteriores y con las longitudes
  máximas del esquema; se permiten acentos y apóstrofes.
- ID positivo; edad entre 0 y 150; habitantes y piso entre 0 y 65535; números
  exterior e interior no negativos.
- Superficie positiva, hasta dos decimales y máximo `9999999999.99`, conservada
  como `BigDecimal` en el formulario, la lectura y el guardado. Se admite coma
  o punto decimal. No se aceptan `NaN`, infinito ni desbordamientos.
- Vivienda exige calle y propietario; edificio y piso sólo para departamentos.
- Vivienda, propietario y departamento se guardan en una transacción. Cualquier
  fallo revierte el conjunto, incluidos los cambios de tipo.
- Borrar una vivienda con habitantes está bloqueado; al borrar una vivienda sin
  habitantes se eliminan sus relaciones de propietario/departamento juntas.
- Editar/borrar un habitante utiliza persona y vivienda. Una relación distinta
  de la misma persona permanece intacta.
- El formulario de vivienda admite un propietario: si hay varios, se rechaza
  su reemplazo ambiguo y se conserva la copropiedad existente.
- Las relaciones foráneas y duplicados producen mensajes claros. Las búsquedas
  por ID son exactas; los textos conservan búsqueda parcial parametrizada.

## Pruebas

Validaciones, construcción de consultas, precisión y errores sin MySQL ni GUI:

```sh
./mvnw test
```

Suite completa con MySQL y JavaFX disponibles:

```sh
./mvnw test -Dviviendas.test.mysql=true -Dviviendas.test.ui=true
```

La integración lee el esquema configurado, crea una base de nombre único
`viviendas_test_<uuid>`, ejecuta las operaciones allí y elimina únicamente esa
base al terminar. Requiere permiso para crear/eliminar bases. No escribe ni
borra registros de la base original. Incluye CRUD, todas las combinaciones de
filtros, claves compuestas, rollback, decimales exactos y carga de los nueve FXML.
Las pruebas JavaFX adicionales validan formularios con persistencia en memoria.

Las dependencias JavaFX 21 y Maven actuales pueden emitir advertencias de acceso
nativo o APIs obsoletas bajo Java 25; no se suprimen ni impiden las pruebas.
