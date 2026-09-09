# Borrado protegido por relaciones Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Impedir el borrado de registros padre con relaciones hijas y comunicar el motivo al usuario.

**Architecture:** MySQL conserva sus claves foráneas restrictivas. Los DAO propagan el error de integridad como `DataAccessException`; `ErroresUI` lo traduce a un mensaje legible. La única eliminación compuesta, `ViviendaService.eliminar`, deja de borrar hijos y delega la protección a la relación foránea.

**Tech Stack:** Java 25, JavaFX, JDBC/MySQL 8, JUnit 5.

---

### Task 1: Pruebas de relaciones protegidas

**Files:**
- Modify: `src/test/java/ObjetosBD/MySqlIntegrationTest.java`
- Modify: `src/test/java/ObjetosBD/ErroresYConexionTest.java`

- [x] **Step 1: Escribir pruebas fallidas para vivienda con relaciones**

```java
assertThrows(DataAccessException.class, () -> service.eliminar(viviendaId));
assertEquals(1, count("vivienda"));
assertEquals(1, count("propietario"));
assertEquals(1, count("departamento"));
assertEquals(1, count("habitantes"));
```

- [x] **Step 2: Ejecutar la prueba y comprobar que falla**

Run: `sh mvnw -B -Dtest=MySqlIntegrationTest -Dviviendas.test.mysql=true test`

Expected: falla porque `ViviendaService.eliminar` borra propietario y departamento antes de borrar vivienda.

- [x] **Step 3: Añadir una prueba parametrizada de padres e hijos**

```java
assertThrows(DataAccessException.class, () -> colonias.eliminarColonia(colonia));
assertThrows(DataAccessException.class, () -> calles.eliminarCalle(calle));
assertThrows(DataAccessException.class, () -> familias.eliminarFamilia(familia));
assertThrows(DataAccessException.class, () -> edificios.eliminarEdificio(edificio));
assertThrows(DataAccessException.class, () -> personas.eliminarPersona(persona));
```

Crear después los hijos en el orden inverso y verificar que cada padre se puede eliminar sólo entonces.

- [x] **Step 4: Probar el mensaje de interfaz**

```java
assertEquals(
    "No se puede eliminar porque tiene datos relacionados. Elimine primero las relaciones hijas.",
    ErroresUI.mensaje(new DataAccessException("Eliminar", new SQLException("", "23000", 1451)))
);
```

- [x] **Step 5: Ejecutar las pruebas de rojo**

Run: `sh mvnw -B -Dtest=MySqlIntegrationTest,ErroresYConexionTest -Dviviendas.test.mysql=true test`

Expected: al menos la prueba de vivienda y la del mensaje fallan antes de la implementación.

### Task 2: Proteger los padres y mostrar el motivo

**Files:**
- Modify: `src/main/java/ObjetosBD/Vivienda/ViviendaService.java:107-127`
- Modify: `src/main/java/ObjetosBD/DataAccessException.java:12-22`
- Modify: `src/test/java/ObjetosBD/MySqlIntegrationTest.java`
- Modify: `src/test/java/ObjetosBD/ErroresYConexionTest.java`

- [x] **Step 1: Cambiar la eliminación de vivienda a una sola sentencia**

```java
try (var ps = connection.prepareStatement("DELETE FROM vivienda WHERE id_vivienda=?")) {
    ps.setInt(1, id);
    return ps.executeUpdate() > 0;
}
```

Eliminar la consulta previa de habitantes y los `DELETE` de `propietario` y `departamento`. Si existen relaciones, MySQL arroja el error `1451`, la transacción se revierte y ningún registro cambia.

- [x] **Step 2: Traducir la restricción foránea**

```java
else if (causa.getErrorCode() == 1451)
    detalle = "No se puede eliminar porque tiene datos relacionados. "
            + "Elimine primero las relaciones hijas.";
```

- [x] **Step 3: Ejecutar pruebas específicas en verde**

Run: `sh mvnw -B -Dtest=MySqlIntegrationTest,ErroresYConexionTest -Dviviendas.test.mysql=true test`

Expected: todas pasan; los hijos no se eliminan de forma automática.

- [ ] **Step 4: Ejecutar la suite completa**

Run: `sh mvnw -B clean test -Dviviendas.test.mysql=true -Dviviendas.test.ui=true`

Expected: compilación limpia y cero fallos.

- [ ] **Step 5: Revisar el cambio**

Run: `git diff --check && git diff -- src/main/java/ObjetosBD/Vivienda/ViviendaService.java src/main/java/ObjetosBD/DataAccessException.java`

Expected: sin errores de espacios y sin `ON DELETE CASCADE`, triggers ni eliminaciones automáticas de relaciones hijas.
