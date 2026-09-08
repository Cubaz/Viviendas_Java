package Validation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ValidacionesTest {
 @Test void textoAdmiteAcentosYRecortaEspacios() {assertEquals("María O'Connor", Validaciones.texto("  María O'Connor  ", "Nombre",100));}
 @Test void textoVacioOLargoSeRechaza() {assertThrows(IllegalArgumentException.class,()->Validaciones.texto("  ","Nombre",100));assertThrows(IllegalArgumentException.class,()->Validaciones.texto("abcd","Nombre",3));}
 @Test void enterosNoDesbordanNiAceptanDecimales() {for(String s:new String[]{"2147483648","1.5","abc","","-1"})assertThrows(IllegalArgumentException.class,()->Validaciones.entero(s,"Edad",0,150));}
 @Test void limitesEdad() {assertEquals(0,Validaciones.entero("0","Edad",0,150));assertEquals(150,Validaciones.entero(" 150 ","Edad",0,150));assertThrows(IllegalArgumentException.class,()->Validaciones.entero("151","Edad",0,150));}
 @Test void superficieAceptaDecimalesYComa() {assertEquals(12.25f,Validaciones.decimal("12.25","Superficie"));assertEquals(12.25f,Validaciones.decimal("12,25","Superficie"));}
 @Test void superficieRechazaNoFinitosCeroYFueraDeEscala() {for(String s:new String[]{"NaN","Infinity","-Infinity","0","-1","1e30","10000000000","1.234"})assertThrows(IllegalArgumentException.class,()->Validaciones.decimal(s,"Superficie"),s);}
 @Test void referenciasObligatorias() {assertThrows(IllegalArgumentException.class,()->Validaciones.requerido(null,"Familia"));assertThrows(IllegalArgumentException.class,()->Validaciones.id(0,"Familia"));}
 @Test void conservaSuperficieExactaHastaLimiteSql() {
  for(String valor:new String[]{"131072.01","262144.01","9999999999.99"})
   assertEquals(new java.math.BigDecimal(valor),Validaciones.decimalExacto(valor,"Superficie"));
 }
 @Test void apiFloatNoRedondeaValoresIncompatibles() {
  assertThrows(IllegalArgumentException.class,()->Validaciones.decimal("262144.01","Superficie"));
  assertThrows(IllegalArgumentException.class,()->Validaciones.positivo(0.001f,"Superficie"));
  assertThrows(IllegalArgumentException.class,()->Validaciones.positivo(12.345f,"Superficie"));
 }
}

