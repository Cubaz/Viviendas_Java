# Borrado protegido por relaciones

## Objetivo

Permitir eliminar cualquier registro de la base `viviendas` cuando no tenga
relaciones hijas. La aplicación debe informar el motivo cuando MySQL proteja
una relación existente.

## Reglas aprobadas

| Registro eliminado | Comportamiento |
| --- | --- |
| Colonia | Se puede eliminar si no tiene calles. |
| Calle | Se puede eliminar si no tiene viviendas. |
| Vivienda | Se puede eliminar si no tiene departamento, propietarios ni habitantes. |
| Edificio | Se puede eliminar si no tiene departamentos. |
| Familia | Se puede eliminar si no tiene personas. |
| Persona | Se puede eliminar si no es propietario ni habitante. |
| Departamento, propietario y habitante | Se pueden eliminar individualmente; son relaciones hijas. |

## Diseño

La migración elimina los disparadores de bloqueo creados anteriormente. Las
claves foráneas restrictivas de MySQL son la única regla de borrado: permiten
eliminar una fila sin hijos e impiden hacerlo cuando sí existen dependencias.

La interfaz traduce el error de relación foránea a un mensaje claro. La
eliminación de propietario, departamento y habitante no cambia: son relaciones
menores que pueden retirarse por separado.

## Verificación

Las pruebas crearán una base temporal con el mismo esquema, aplicarán la
migración y comprobarán que los padres se mantienen al intentar borrarlos con
hijos, pero se pueden eliminar después de retirar las relaciones hijas.
