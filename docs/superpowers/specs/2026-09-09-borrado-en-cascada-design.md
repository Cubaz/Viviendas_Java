# Borrado protegido por relaciones

## Objetivo

Impedir siempre la eliminación de los registros superiores de la base
`viviendas`, tengan o no relaciones hijas. La aplicación debe informar el
motivo sin borrar datos.

## Reglas aprobadas

| Registro eliminado | Comportamiento |
| --- | --- |
| Colonia | Siempre se bloquea. |
| Calle | Se puede eliminar si no tiene viviendas. |
| Vivienda | Siempre se bloquea. |
| Edificio | Siempre se bloquea. |
| Familia | Siempre se bloquea. |
| Persona | Siempre se bloquea. |
| Departamento, propietario y habitante | Se pueden eliminar individualmente; son relaciones hijas. |

## Diseño

Una migración de MySQL instalará un disparador `BEFORE DELETE` para colonia,
familia, persona, edificio y vivienda. El disparador devolverá el error
controlado `45000`, incluso si la fila no tiene hijos. La calle se conserva como
relación hija de colonia y se puede eliminar cuando no tiene viviendas. Las
claves foráneas existentes seguirán protegiendo la integridad ante operaciones
externas.

La capa de interfaz traducirá tanto el error de relación foránea como el error
del disparador a un mensaje claro. La eliminación de propietario, departamento
y habitante no cambia: son relaciones menores que pueden retirarse por separado.

## Verificación

Las pruebas crearán una base temporal con el mismo esquema, aplicarán la
migración y comprobarán que cada padre se mantiene al intentar borrarlo con o
sin hijos. También comprobarán que las relaciones hijas continúan eliminándose.
