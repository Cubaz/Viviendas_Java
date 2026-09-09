package ObjetosBD;
import Applications.ErroresUI;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;
class ErroresYConexionTest {
 @Test void muestraErrorDeValidacionEnvueltoPorFxml() {assertEquals("Nombre obligatorio",ErroresUI.mensaje(new RuntimeException(new java.lang.reflect.InvocationTargetException(new IllegalArgumentException("Nombre obligatorio")))));}
 @Test void fkDuplicadosYConexionTienenMensajesUtiles() {
  assertTrue(new DataAccessException("Guardar",new SQLException("sql", "23000",1062)).getMessage().contains("Ya existe"));
  assertTrue(new DataAccessException("Borrar",new SQLException("sql", "23000",1451)).getMessage().contains("relacionados"));
  assertTrue(new DataAccessException("Guardar",new SQLException("sql", "23000",1452)).getMessage().contains("ya no existe"));
  assertTrue(new DataAccessException("Consultar",new SQLException("sql", "08001",0)).getMessage().contains("MySQL"));
 }
 @Test void relacionHijaMuestraComoResolverElBorrado() {
  assertEquals("Eliminar: No se puede eliminar porque tiene datos relacionados. Elimine primero las relaciones hijas.",
   ErroresUI.mensaje(new DataAccessException("Eliminar",new SQLException("sql", "23000",1451))));
 }
 @Test void conexionFallidaNoCierraLaAplicacion() throws Exception {
  String anterior=System.getProperty("viviendas.db.url");
  System.setProperty("viviendas.db.url","jdbc:driver-inexistente:test");
  try(var conexion=new Conexion()) {assertThrows(SQLException.class,conexion::getConexion);}
  finally {if(anterior==null)System.clearProperty("viviendas.db.url");else System.setProperty("viviendas.db.url",anterior);}
 }
}
