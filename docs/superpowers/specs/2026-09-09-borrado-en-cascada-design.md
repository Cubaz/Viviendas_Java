# Borrado protegido por relaciones

## Objetivo

Evitar que un registro padre se elimine mientras conserve relaciones hijas en
la base `viviendas`. La aplicación debe informar al usuario sin borrar datos.

## Reglas aprobadas

| Registro eliminado | Comportamiento |
| --- | --- |
| Colonia | Se bloquea si tiene calles. |
| Calle | Se bloquea si tiene viviendas. |
| Vivienda | Se bloquea si tiene departamento, propietarios o habitantes. |
| Edificio | Se bloquea si tiene departamentos. |
| Familia | Se bloquea si tiene personas. |
| Persona | Se bloquea si es propietario o habitante. |
| Departamento, propietario y habitante | Se pueden eliminar individualmente; son relaciones hijas. |

## Diseño

Las claves foráneas conservarán el comportamiento restrictivo de MySQL. No se
añadirán cascadas ni triggers destructivos. La capa de interfaz traducirá el
error de relación foránea a: “No se puede eliminar porque tiene datos
relacionados. Elimine primero las relaciones hijas.”

La eliminación de una vivienda no retirará propietario, departamento ni
habitantes de forma automática. Para eliminar una vivienda, el usuario debe
eliminar antes esas relaciones menores.

## Verificación

Las pruebas crearán una base temporal con el mismo esquema y comprobarán que
cada padre se mantiene al intentar borrarlo con hijos. También comprobarán que
al eliminar primero los hijos, el padre queda disponible para eliminarse.
