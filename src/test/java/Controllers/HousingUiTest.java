package Controllers;

import ObjetosBD.Calle.CalleBD;
import ObjetosBD.Habitante.HabitanteBD;
import ObjetosBD.Habitante.JDHabitante;
import ObjetosBD.Persona.JDPersona;
import ObjetosBD.Persona.PersonaBD;
import ObjetosBD.Vivienda.JDVivienda;
import ObjetosBD.Vivienda.ViviendaBD;
import ObjetosBD.Vivienda.ViviendaService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/** Controles reales y persistencia en memoria: no abre ventanas ni conexiones SQL. */
@EnabledIfSystemProperty(named = "viviendas.test.ui", matches = "true")
class HousingUiTest {
    @BeforeAll static void iniciarJavaFx() throws Exception {
        CompletableFuture<Void> listo = new CompletableFuture<>();
        try { Platform.startup(() -> listo.complete(null)); }
        catch (IllegalStateException iniciado) { Platform.runLater(() -> listo.complete(null)); }
        listo.get(15, TimeUnit.SECONDS);
    }

    private static void enFx(Runnable prueba) throws Exception {
        CompletableFuture<Void> resultado = new CompletableFuture<>();
        Platform.runLater(() -> {
            try { prueba.run(); resultado.complete(null); }
            catch (Throwable fallo) { resultado.completeExceptionally(fallo); }
        });
        resultado.get(15, TimeUnit.SECONDS);
    }

    private static void campo(Object controller, String nombre, Object valor) {
        try {
            var field = controller.getClass().getDeclaredField(nombre);
            field.setAccessible(true);
            field.set(controller, valor);
        } catch (ReflectiveOperationException error) { throw new AssertionError(error); }
    }

    @SuppressWarnings("unchecked")
    private static <T> T campo(Object controller, String nombre) {
        try {
            var field = controller.getClass().getDeclaredField(nombre);
            field.setAccessible(true);
            return (T) field.get(controller);
        } catch (ReflectiveOperationException error) { throw new AssertionError(error); }
    }

    private static void controles(Object controller) {
        try {
            for (var field : controller.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(FXML.class))
                    campo(controller, field.getName(), field.getType().getConstructor().newInstance());
            }
        } catch (ReflectiveOperationException error) { throw new AssertionError(error); }
    }

    private static void texto(Object controller, String nombre, String valor) {
        HousingUiTest.<TextField>campo(controller, nombre).setText(valor);
    }

    private static <T> void seleccionar(Object controller, String nombre, T valor) {
        HousingUiTest.<ComboBox<T>>campo(controller, nombre).setValue(valor);
    }

    private static ViviendaBD vivienda(int id) {
        try {
            var constructor = ViviendaBD.class.getDeclaredConstructor(int.class, String.class, int.class,
                    int.class, int.class, int.class, BigDecimal.class);
            constructor.setAccessible(true);
            return constructor.newInstance(id, "Unifamiliar", 3, 1, 0, 7, new BigDecimal("90.50"));
        } catch (ReflectiveOperationException error) { throw new AssertionError(error); }
    }

    private static final class ServicioMemoria extends ViviendaService {
        int llamadas;
        String tipoGuardado;
        Integer edificioGuardado;
        Integer pisoGuardado;
        BigDecimal metrosGuardados;
        @Override public int crear(String tipo, int habitantes, int exterior, int interior, int calle,
                                   float metros, int propietario, Integer edificio, Integer piso) {
            throw new AssertionError("El formulario debe enviar metros cuadrados exactos, sin convertir a float");
        }
        @Override public boolean actualizar(int id, String tipo, int habitantes, int exterior, int interior,
                                            int calle, float metros, int propietario, Integer edificio, Integer piso) {
            throw new AssertionError("El formulario debe enviar metros cuadrados exactos, sin convertir a float");
        }
        @Override public int crear(String tipo, int habitantes, int exterior, int interior, int calle,
                                   BigDecimal metros, int propietario, Integer edificio, Integer piso) {
            llamadas++;
            tipoGuardado = tipo;
            edificioGuardado = edificio;
            pisoGuardado = piso;
            metrosGuardados = metros;
            return 11;
        }
        @Override public boolean actualizar(int id, String tipo, int habitantes, int exterior, int interior,
                                            int calle, BigDecimal metros, int propietario, Integer edificio, Integer piso) {
            assertEquals(11, id);
            crear(tipo, habitantes, exterior, interior, calle, metros, propietario, edificio, piso);
            return true;
        }
    }

    private static ViviendaController formularioVivienda(ServicioMemoria servicio) {
        var controller = new ViviendaController();
        controles(controller);
        campo(controller, "servicio", servicio);
        campo(controller, "VDB", new JDVivienda() {
            @Override public ObservableList<ViviendaBD> buscarViviendaTabla(int id) {
                return FXCollections.observableArrayList(vivienda(id));
            }
        });
        HousingUiTest.<ComboBox<String>>campo(controller, "vivienda").getItems().addAll("Unifamiliar", "Departamento");
        seleccionar(controller, "vivienda", "Unifamiliar");
        seleccionar(controller, "calle", new CalleBD(7, "Calle", 1));
        seleccionar(controller, "propietario", new PersonaBD(3, "Persona", 1, 30));
        texto(controller, "habitantes", "3");
        texto(controller, "num_ext", "1");
        texto(controller, "num_int", "0");
        texto(controller, "mts_cuadrados", "90.50");
        return controller;
    }

    @Test void crearUnifamiliarAdmiteMetrosDecimalesSinEdificioNiPiso() throws Exception {
        enFx(() -> {
            var servicio = new ServicioMemoria();
            var controller = formularioVivienda(servicio);
            controller.registrar_vivienda(null);
            assertEquals(1, servicio.llamadas);
            assertEquals("Unifamiliar", servicio.tipoGuardado);
            assertEquals(new BigDecimal("90.50"), servicio.metrosGuardados);
            assertNull(servicio.edificioGuardado);
            assertNull(servicio.pisoGuardado);
        });
    }

    @Test void superficieGrandeConservaLosCentimosAlCrear() throws Exception {
        enFx(() -> {
            var servicio = new ServicioMemoria();
            var controller = formularioVivienda(servicio);
            texto(controller, "mts_cuadrados", "131072.01");
            controller.registrar_vivienda(null);
            assertEquals(new BigDecimal("131072.01"), servicio.metrosGuardados);
        });
    }

    @Test void actualizarUnifamiliarNoIntentaLeerPisoDeDepartamento() throws Exception {
        enFx(() -> {
            var servicio = new ServicioMemoria();
            var controller = formularioVivienda(servicio);
            var tabla = HousingUiTest.<TableView<ViviendaBD>>campo(controller, "tabla_vivienda");
            tabla.getItems().add(vivienda(11));
            tabla.getSelectionModel().selectFirst();
            texto(controller, "piso", "no aplica");
            controller.actualizar_vivienda(null);
            assertEquals(1, servicio.llamadas);
            assertNull(servicio.edificioGuardado);
            assertNull(servicio.pisoGuardado);
        });
    }

    @Test void propietarioEsObligatorioAntesDePersistir() throws Exception {
        enFx(() -> {
            var servicio = new ServicioMemoria();
            var controller = formularioVivienda(servicio);
            seleccionar(controller, "propietario", null);
            var error = assertThrows(IllegalArgumentException.class, () -> controller.registrar_vivienda(null));
            assertTrue(error.getMessage().contains("Propietario"));
            assertEquals(0, servicio.llamadas);
        });
    }

    @Test void numerosNegativosFueraDeRangoOInvalidosNoLleganAlServicio() throws Exception {
        enFx(() -> {
            for (String[] caso : new String[][]{
                    {"habitantes", "-1"}, {"habitantes", "65536"}, {"habitantes", "2147483648"},
                    {"num_ext", "-1"}, {"num_int", "999999999999999999"},
                    {"mts_cuadrados", "NaN"}, {"mts_cuadrados", "Infinity"}, {"mts_cuadrados", "-1"}}) {
                var servicio = new ServicioMemoria();
                var controller = formularioVivienda(servicio);
                texto(controller, caso[0], caso[1]);
                assertThrows(IllegalArgumentException.class, () -> controller.registrar_vivienda(null),
                        caso[0] + "=" + caso[1]);
                assertEquals(0, servicio.llamadas);
            }
        });
    }

    @Test void departamentoExigeEdificioYPisoValidoAntesDePersistir() throws Exception {
        enFx(() -> {
            var servicio = new ServicioMemoria();
            var controller = formularioVivienda(servicio);
            seleccionar(controller, "vivienda", "Departamento");
            assertThrows(IllegalArgumentException.class, () -> controller.registrar_vivienda(null));
            seleccionar(controller, "edificio", new ObjetosBD.Edificio.EdificioBD(9, "Edificio"));
            for (String piso : new String[]{"", "-1", "65536", "abc"}) {
                texto(controller, "piso", piso);
                assertThrows(IllegalArgumentException.class, () -> controller.registrar_vivienda(null));
            }
            assertEquals(0, servicio.llamadas);
        });
    }

    private static final class HabitantesMemoria extends JDHabitante {
        final Map<String, HabitanteBD> filas = new HashMap<>();
        HabitantesMemoria() {
            filas.put("3:11", new HabitanteBD(3, 11, "Hijo"));
            filas.put("3:12", new HabitanteBD(3, 12, "Padre"));
        }
        @Override public boolean actualizarHabitante(int persona, int anterior, int nueva, String rol) {
            HabitanteBD existente = filas.remove(persona + ":" + anterior);
            if (existente == null) return false;
            filas.put(persona + ":" + nueva, new HabitanteBD(persona, nueva, rol));
            return true;
        }
        @Override public boolean borrarHabitante(int persona, int vivienda) {
            return filas.remove(persona + ":" + vivienda) != null;
        }
        @Override public boolean actualizarHabitante(int persona, int vivienda, String rol) {
            throw new AssertionError("La edición debe identificar la vivienda anterior");
        }
        @Override public boolean borrarHabitante(int persona) {
            throw new AssertionError("El borrado debe identificar la vivienda seleccionada");
        }
    }

    private static HabitanteController formularioHabitante(HabitantesMemoria dao) {
        var controller = new HabitanteController();
        controles(controller);
        campo(controller, "DBHabitante", dao);
        campo(controller, "DBPersona", new JDPersona() {
            @Override public ObservableList<PersonaBD> obtenerPersona() { return FXCollections.observableArrayList(); }
        });
        campo(controller, "DBVivienda", new JDVivienda() {
            @Override public ObservableList<ViviendaBD> obtenerVivienda() { return FXCollections.observableArrayList(); }
        });
        campo(controller, "habitanteBD", dao.filas.get("3:11"));
        campo(controller, "pane_actual", HousingUiTest.<AnchorPane>campo(controller, "pane_actualizar"));
        seleccionar(controller, "field_idPersonaActualizar", new PersonaBD(3, "Persona", 1, 30));
        seleccionar(controller, "field_idViviendaActualizar", vivienda(13));
        HousingUiTest.<ComboBox<String>>campo(controller, "combo_rolActualizar").getItems().addAll("Hijo", "Padre");
        seleccionar(controller, "combo_rolActualizar", "Hijo");
        return controller;
    }

    @Test void actualizarHabitanteConservaOtraViviendaDeLaMismaPersona() throws Exception {
        enFx(() -> {
            var dao = new HabitantesMemoria();
            var controller = formularioHabitante(dao);
            controller.operacionActualizar();
            assertNull(dao.filas.get("3:11"));
            assertEquals("Hijo", dao.filas.get("3:13").getRol());
            assertEquals("Padre", dao.filas.get("3:12").getRol());
            assertNull(campo(controller, "habitanteBD"));
        });
    }

    @Test void borrarHabitanteEliminaSoloLaRelacionSeleccionada() throws Exception {
        enFx(() -> {
            var dao = new HabitantesMemoria();
            var controller = formularioHabitante(dao);
            controller.operacionBorrar();
            assertNull(dao.filas.get("3:11"));
            assertEquals("Padre", dao.filas.get("3:12").getRol());
            assertNull(campo(controller, "habitanteBD"));
            assertThrows(IllegalArgumentException.class, controller::operacionBorrar);
            assertEquals(1, dao.filas.size());
        });
    }
}
