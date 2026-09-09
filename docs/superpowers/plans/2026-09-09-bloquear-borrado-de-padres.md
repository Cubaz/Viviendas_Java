# Bloqueo total de borrado de padres Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Impedir en MySQL la eliminación de colonia, familia, persona, edificio y vivienda, aun cuando no tengan relaciones hijas, y permitir borrar calle cuando no tenga viviendas.

**Architecture:** Una migración SQL instala cinco disparadores `BEFORE DELETE` que emiten SQLSTATE `45000`. La clave foránea de vivienda sigue protegiendo calle. `DataAccessException` convierte el error del disparador en un mensaje legible para JavaFX.

**Tech Stack:** MySQL 8, JDBC, Java 25, JavaFX, JUnit 5.

---

### Task 1: Probar el bloqueo desde MySQL

**Files:**
- Modify: `src/test/java/ObjetosBD/MySqlIntegrationTest.java`
- Modify: `src/test/java/ObjetosBD/ErroresYConexionTest.java`
- Create: `src/main/resources/sql/2026-09-09-bloquear-borrado-padres.sql`

- [x] **Step 1: Escribir la prueba fallida**

Actualizar la prueba de integración para aplicar el recurso SQL a su base temporal y comprobar que un padre sin hijos tampoco se puede borrar:

```java
assertThrows(DataAccessException.class, () -> colonias.eliminarColonia(colonia));
assertThrows(DataAccessException.class, () -> familias.eliminarFamilia(familia));
assertThrows(DataAccessException.class, () -> edificios.eliminarEdificio(edificio));
```

- [x] **Step 2: Ejecutar la prueba en rojo**

Run: `sh mvnw -B -Dtest=MySqlIntegrationTest -Dviviendas.test.mysql=true test`
Expected: falla porque aún no existe la migración que instala los disparadores.

- [x] **Step 3: Crear la migración SQL**

Crear cinco `CREATE TRIGGER ... BEFORE DELETE` que emitan:

```sql
SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'No se permite eliminar registros superiores.';
```

- [x] **Step 4: Ejecutar la prueba en verde**

Run: `sh mvnw -B -Dtest=MySqlIntegrationTest -Dviviendas.test.mysql=true test`
Expected: pasa y conserva los padres en la base temporal.

### Task 2: Mostrar el motivo y aplicar la migración real

**Files:**
- Modify: `src/main/java/ObjetosBD/DataAccessException.java`
- Modify: `src/test/java/ObjetosBD/ErroresYConexionTest.java`

- [x] **Step 1: Escribir la prueba fallida del mensaje**

```java
assertEquals(
    "Eliminar: No se permite eliminar registros superiores. Elimine únicamente relaciones hijas.",
    ErroresUI.mensaje(new DataAccessException("Eliminar", new SQLException("", "45000", 1644)))
);
```

- [x] **Step 2: Ejecutar la prueba en rojo**

Run: `sh mvnw -B -Dtest=ErroresYConexionTest test`
Expected: falla con el mensaje genérico actual.

- [x] **Step 3: Traducir el error 1644**

Añadir en `DataAccessException` el caso `causa.getErrorCode() == 1644` con el texto definido por la regla.

- [x] **Step 4: Aplicar la migración a la base `viviendas`**

Ejecutar el recurso SQL en el contenedor local de MySQL y consultar `information_schema.TRIGGERS` para verificar las cinco reglas instaladas.

- [x] **Step 5: Ejecutar la suite completa**

Run: `sh mvnw -B clean test -Dviviendas.test.mysql=true -Dviviendas.test.ui=true`
Expected: compilación limpia y cero fallos.

### Task 3: Preparar Git

**Files:**
- Include: migración SQL, Java, pruebas y documentación

- [ ] **Step 1: Revisar el cambio**

Run: `git diff --check && git status --short`
Expected: sin errores de espacios y sin archivos de configuración `.idea`.

- [ ] **Step 2: Crear el commit de la regla**

```bash
git add src/main/resources/sql/2026-09-09-bloquear-borrado-padres.sql src/main/java/ObjetosBD/DataAccessException.java src/test/java/ObjetosBD/MySqlIntegrationTest.java src/test/java/ObjetosBD/ErroresYConexionTest.java docs/superpowers
git commit -m "Bloquea borrado de registros superiores"
```
