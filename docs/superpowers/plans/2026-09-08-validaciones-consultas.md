# Correcciones de validación y persistencia

**Objetivo:** corregir formularios y consultas contra el esquema MySQL existente.
**Arquitectura:** conservar controladores/FXML y DAO; centralizar validación y errores. Una transacción para vivienda con propietario/departamento. No migrar ni modificar datos existentes.

- [x] Validaciones comunes: texto requerido y longitud SQL, enteros sin overflow, ID positivo, edad 0–150, piso/habitantes 0–65535, superficie decimal positiva y finita.
- [x] Conexiones por operación con cierre automático, configuración por propiedades/entorno y errores recuperables.
- [x] DAO: claves generadas, IDs exactos, listas vacías, claves compuestas habitantes/propietarios y consultas compatibles con ONLY_FULL_GROUP_BY.
- [x] Formularios: validar antes de escribir, mensajes reales, selección limpia, refrescar opciones y tablas.
- [x] ViviendaService: crear/actualizar/borrar con rollback, cambios de tipo consistentes y rechazo de operaciones ambiguas sobre copropietarios.
- [x] Pruebas JUnit de validación, consultas y errores. Pruebas MySQL en un esquema temporal propio con las nueve tablas; altas, consultas, actualizaciones, borrados y rollback.
- [x] Cargar las nueve pantallas y comprobar validaciones de entrada sin tocar datos existentes. Documentar comandos y límites.

## Verificación final

`sh mvnw -B clean test -Dviviendas.test.mysql=true -Dviviendas.test.ui=true`: 43 pruebas, 0 fallos, 0 errores, 0 omitidas. Incluye 11 pruebas MySQL en esquema temporal, 13 pruebas de controladores JavaFX y validaciones/precisión/errores. Nueve FXML renderizados sin los errores CSS anteriores. Permanecen avisos de dependencias por acceso nativo/configuración classpath en Java 25.
