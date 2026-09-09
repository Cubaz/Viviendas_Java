# Borrado condicionado por relaciones

La aplicación permite borrar cualquier registro cuando no tiene hijos
relacionados. MySQL conserva las claves foráneas existentes para bloquear los
borrados que dejarían relaciones inválidas.

La migración `src/main/resources/sql/2026-09-09-bloquear-borrado-padres.sql`
elimina los disparadores de bloqueo total que se habían instalado antes. Las
pruebas de integración verifican que un padre no se borra mientras tenga hijos
y que se puede borrar después de retirarlos.
