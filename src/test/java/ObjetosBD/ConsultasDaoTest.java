package ObjetosBD;

import ObjetosBD.Habitante.JDHabitante;
import ObjetosBD.Calle.JDCalle;
import ObjetosBD.Colonia.JDColonia;
import ObjetosBD.Edificio.JDEdificio;
import ObjetosBD.Familia.JDFamilia;
import ObjetosBD.Propietario.JDPropietario;
import ObjetosBD.Vivienda.JDVivienda;
import ObjetosBD.Persona.JDPersona;
import ObjetosBD.Departamento.JDDepartamento;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConsultasDaoTest {
    @Test void conservaTodasLasViviendasDeCadaHabitante() {
        String sql = new JDHabitante().construirSentenciaBuscarHabitantes(12, 5, "Titular");
        assertFalse(sql.toUpperCase().contains("GROUP BY"));
        assertTrue(sql.contains("persona.id_persona = ?"));
        assertTrue(sql.contains("vivienda.id_vivienda = ?"));
        assertEquals(3, sql.chars().filter(c -> c == '?').count());
    }
    @Test void rechazaIdentificadoresInvalidosEnFiltros() {
        assertThrows(IllegalArgumentException.class, () -> new JDHabitante().construirSentenciaBuscarHabitantes(0, null, null));
        assertThrows(IllegalArgumentException.class, () -> new JDDepartamento().construirSentenciaBuscarDepartamentos(null, -1, null, null));
    }
    @Test void validaRangosDeEdadYPisoEnFiltros() {
        assertThrows(IllegalArgumentException.class, () -> new JDPersona().construirSentenciaBuscarPersonas(null, null, null, 151));
        assertThrows(IllegalArgumentException.class, () -> new JDDepartamento().construirSentenciaBuscarDepartamentos(null, null, null, 65536));
    }
    @Test void validaAltasAntesDeAbrirLaConexion() {
        assertThrows(IllegalArgumentException.class, () -> new JDColonia().insertarColonia("  "));
        assertThrows(IllegalArgumentException.class, () -> new JDCalle().insertarCalle("Calle", 0));
        assertThrows(IllegalArgumentException.class, () -> new JDFamilia().insertarFamilia(null));
        assertThrows(IllegalArgumentException.class, () -> new JDEdificio().insertarEdificio("a".repeat(121)));
        assertThrows(IllegalArgumentException.class, () -> new JDPersona().insertarPersona("Ana", 1, -1));
        assertThrows(IllegalArgumentException.class, () -> new JDVivienda().insertarVivienda("Departamento", 0, 1, 0, 1, Float.NaN));
        assertThrows(IllegalArgumentException.class, () -> new JDDepartamento().insertarDepartamento(1, 1, -1));
        assertThrows(IllegalArgumentException.class, () -> new JDHabitante().insertarHabitante(1, 1, "a".repeat(41)));
        assertThrows(IllegalArgumentException.class, () -> new JDPropietario().insertarPropietario(0, 1));
    }
    @Test void filtrosVaciosNoGeneranCondicionesIncompletas() {
        String sql = new JDHabitante().construirSentenciaBuscarHabitantes(null, null, "");
        assertFalse(sql.contains("WHERE"));
        assertEquals(0, sql.chars().filter(c -> c == '?').count());
    }
}
