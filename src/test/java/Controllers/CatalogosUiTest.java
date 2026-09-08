package Controllers;

import ObjetosBD.Calle.CalleBD;
import ObjetosBD.Colonia.ColoniaBD;
import ObjetosBD.Colonia.JDColonia;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/** Sólo controles en memoria; no FXMLLoader, ventanas ni conexiones a bases de datos. */
@EnabledIfSystemProperty(named = "viviendas.test.ui", matches = "true")
class CatalogosUiTest {
    @BeforeAll static void iniciarJavaFx() throws Exception {
        CompletableFuture<Void> listo = new CompletableFuture<>();
        try {
            Platform.startup(() -> listo.complete(null));
        } catch (IllegalStateException yaIniciado) {
            Platform.runLater(() -> listo.complete(null));
        }
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
        } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
    }

    @Test void coloniaRechazaNombreVacioSinAccederADao() throws Exception {
        enFx(() -> {
            var controller = new ColoniaController();
            campo(controller, "nom_col", new TextField("   "));
            assertThrows(IllegalArgumentException.class, () -> controller.registrar_colonia(null));
        });
    }

    @Test void coloniaRechazaIdsInvalidosYLimpiaResultadosAnteriores() throws Exception {
        enFx(() -> {
            var controller = new ColoniaController();
            var criterio = new ComboBox<String>();
            criterio.setValue("ID");
            var parametro = new TextField();
            var tabla = new TableView<ColoniaBD>();
            campo(controller, "criterio", criterio);
            campo(controller, "parametro", parametro);
            campo(controller, "tabla_colonia", tabla);
            for (String invalido : new String[]{"abc", "-1", "0", "999999999999999"}) {
                tabla.getItems().add(new ColoniaBD(1, "Anterior"));
                parametro.setText(invalido);
                assertThrows(IllegalArgumentException.class, () -> controller.buscar_colonia(null));
                assertTrue(tabla.getItems().isEmpty());
            }
        });
    }

    @Test void calleValidaNombreDelFormularioEnVezDeCabecera() throws Exception {
        enFx(() -> {
            var controller = new CalleController();
            var tabla = new TableView<CalleBD>();
            tabla.getItems().add(new CalleBD(1, "Actual", 1));
            tabla.getSelectionModel().selectFirst();
            campo(controller, "tabla_calle", tabla);
            campo(controller, "nom_calle", new TextField("  "));
            campo(controller, "colNombreCalle", new TableColumn<CalleBD, String>("Nombre de calle"));
            assertThrows(IllegalArgumentException.class, () -> controller.actualizar_calle(null));
        });
    }

    @Test void coloniaSinSeleccionNoIntentaActualizarNiEliminar() throws Exception {
        enFx(() -> {
            var controller = new ColoniaController();
            var aviso = new Text();
            campo(controller, "tabla_colonia", new TableView<ColoniaBD>());
            campo(controller, "out_infoOperacion", aviso);
            assertDoesNotThrow(() -> controller.actualizar_colonia(null));
            assertTrue(aviso.getText().contains("seleccionar"));
            assertDoesNotThrow(() -> controller.eliminar_colonia(null));
            assertTrue(aviso.getText().contains("seleccionar"));
        });
    }

    @Test void renombrarColoniaNoModificaSuperficieConstruida() throws Exception {
        enFx(() -> {
            var controller = new ColoniaController();
            var colonia = new ColoniaBD(7, "Anterior");
            colonia.setSup_construida(250.5f);
            var tabla = new TableView<ColoniaBD>();
            tabla.getItems().add(colonia);
            tabla.getSelectionModel().selectFirst();
            campo(controller, "tabla_colonia", tabla);
            campo(controller, "nom_col", new TextField("  Nueva  "));
            campo(controller, "out_infoOperacion", new Text());
            campo(controller, "CDB", new JDColonia() {
                @Override public boolean actualizarColonia(int id, String nombre) {
                    assertEquals(7, id);
                    assertEquals("Nueva", nombre);
                    return true;
                }
                @Override public boolean actualizarColonia(int id, String nombre, float superficie) {
                    fail("El formulario no debe sobrescribir la superficie construida");
                    return false;
                }
            });
            controller.actualizar_colonia(null);
            assertEquals("Nueva", colonia.getNombre());
            assertEquals(250.5f, colonia.getSup_construida());
        });
    }
}
