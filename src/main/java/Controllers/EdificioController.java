package Controllers;

import ObjetosBD.Edificio.EdificioBD;
import ObjetosBD.Edificio.JDEdificio;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;

enum operacionEdificio {
    CREAR("Crear"),
    BUSCAR("Buscar"),
    BORRAR("Borrar"),
    ACTUALIZAR("Seleccionar edificio");

    private final String texto;

    operacionEdificio(String texto){
        this.texto = texto;
    }

    public String getTextoBoton(){
        return this.texto;
    }
}

public class EdificioController {
    // Componentes generales
    @FXML private Button btn_menuPrincipal;
    @FXML private Text out_infoOperacion;
    @FXML private Button btn_volver;

    // Paneles
    @FXML private AnchorPane pane_inicio;
    @FXML private AnchorPane pane_entrada;
    @FXML private AnchorPane pane_actualizar;
    @FXML private AnchorPane pane_resultadoBusqueda;
    @FXML private AnchorPane pane_confirmacion;

    // Campos de entrada (pane_entrada)
    @FXML private ComboBox<EdificioBD> in_idEdificio;
    @FXML private TextField in_nombreEdificio;
    @FXML private Button btn_entrada;
    @FXML private Text txt_idEdificioEntrada;
    @FXML private Text txt_nombreEdificioEntrada;

    // Campos de actualizar (pane_actualizar)
    @FXML private ComboBox<EdificioBD> field_idEdificioActualizar;
    @FXML private TextField field_nombreEdificioActualizar;
    @FXML private Button btn_confirmacionActualizar;

    // Campos de confirmacion (pane_confirmacion)
    @FXML private TextField out_idEdificioRegistro;
    @FXML private TextField out_nombreEdificioRegistro;
    @FXML private Button btn_confirmacionFinal;

    // Tabla de busqueda
    @FXML private TableView<Map<String, Object>> tabla_busquedaEdificio;
    @FXML private TableColumn<Map<String, Object>, Object> colIdEdificio;
    @FXML private TableColumn<Map<String, Object>, Object> colNombreEdificio;

    // Variables de control
    private final Color colorAdvertencia = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    private final Color colorExito = new Color(0.0f, 1.0f, 0.1529f, 1.0f);
    private operacionEdificio opSeleccionada;
    private final Map<AnchorPane, AnchorPane> padrePane = new HashMap<>();
    private AnchorPane pane_actual;

    // Objetos BD
    private final JDEdificio DBEdificio = new JDEdificio();
    private EdificioBD edificioBD;

    @FXML
    public void initialize() {
        pane_actual = pane_inicio;

        in_idEdificio.setItems(DBEdificio.obtenerEdificio());
        field_idEdificioActualizar.setItems(DBEdificio.obtenerEdificio());

        initPadrePane();
        configurarTablaBusqueda();
    }

    private void initPadrePane() {
        padrePane.put(pane_inicio, pane_inicio);
        padrePane.put(pane_entrada, pane_inicio);
        padrePane.put(pane_confirmacion, pane_entrada);
        padrePane.put(pane_actualizar, pane_entrada);
        padrePane.put(pane_resultadoBusqueda, pane_entrada);
    }

    private void configurarTablaBusqueda() {
        colIdEdificio.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get("IdEdificio")));
        colNombreEdificio.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get("Nombre")));
    }

    @FXML
    void elegirOpcionCrear(ActionEvent event) {
        opSeleccionada = operacionEdificio.CREAR;
        prepararEntrada();
    }

    @FXML
    void elegirOpcionBuscar(ActionEvent event) {
        opSeleccionada = operacionEdificio.BUSCAR;
        prepararEntrada();
    }

    @FXML
    void elegirOpcionActualizar(ActionEvent event) {
        opSeleccionada = operacionEdificio.ACTUALIZAR;
        prepararEntrada();
    }

    @FXML
    void elegirOpcionBorrar(ActionEvent event) {
        opSeleccionada = operacionEdificio.BORRAR;
        prepararEntrada();
    }

    private void prepararEntrada() {
        btn_entrada.setText(opSeleccionada.getTextoBoton());
        in_idEdificio.getSelectionModel().clearSelection();
        in_nombreEdificio.clear();
        out_infoOperacion.setVisible(false);

        // Configuración según operación
        boolean esCrear = opSeleccionada == operacionEdificio.CREAR;
        boolean esBuscar = opSeleccionada == operacionEdificio.BUSCAR;

        in_idEdificio.setDisable(esCrear);
        
        in_nombreEdificio.setVisible(esCrear || esBuscar);
        txt_nombreEdificioEntrada.setVisible(esCrear || esBuscar);

        cambiarPane(pane_inicio, pane_entrada);
        btn_volver.setVisible(true);
    }

    @FXML
    void ejecutarOperacionEntrada(ActionEvent event) {
        switch (opSeleccionada) {
            case CREAR:
                if (validarCamposCrear()) {
                    out_idEdificioRegistro.setText("Auto");
                    out_nombreEdificioRegistro.setText(in_nombreEdificio.getText());
                    btn_confirmacionFinal.setText("Crear Edificio");
                    cambiarPane(pane_entrada, pane_confirmacion);
                }
                break;
            case BUSCAR:
                operacionBuscar();
                break;
            case ACTUALIZAR:
                if (validarId(in_idEdificio)) {
                    edificioBD = in_idEdificio.getValue();
                    if (edificioBD != null) {
                        field_idEdificioActualizar.setValue(edificioBD);
                        field_nombreEdificioActualizar.setText(edificioBD.getNombre());
                        cambiarPane(pane_entrada, pane_actualizar);
                    } else {
                        mostrarInfoOperacion("El edificio no existe", colorAdvertencia);
                    }
                }
                break;
            case BORRAR:
                if (validarId(in_idEdificio)) {
                    edificioBD = in_idEdificio.getValue();
                    if (edificioBD != null) {
                        out_idEdificioRegistro.setText(String.valueOf(edificioBD.getIdEdificio()));
                        out_nombreEdificioRegistro.setText(edificioBD.getNombre());
                        btn_confirmacionFinal.setText("Borrar Edificio");
                        cambiarPane(pane_entrada, pane_confirmacion);
                    } else {
                        mostrarInfoOperacion("El edificio no existe", colorAdvertencia);
                    }
                }
                break;
        }
    }

    private void operacionBuscar() {
        Integer id = (in_idEdificio.getValue() != null) ? in_idEdificio.getValue().getIdEdificio() : null;
        String nombre = in_nombreEdificio.getText().isBlank() ? null : in_nombreEdificio.getText();

        ObservableList<Map<String, Object>> resultados = DBEdificio.buscarEdificios(id, nombre);
        if (resultados != null) {
            tabla_busquedaEdificio.getItems().clear();
            tabla_busquedaEdificio.setItems(resultados);
            cambiarPane(pane_entrada, pane_resultadoBusqueda);
            mostrarInfoOperacion("Resultados encontrados: " + resultados.size(), colorExito);
        } else {
            mostrarInfoOperacion("No se encontraron edificios", colorAdvertencia);
        }
    }

    @FXML
    void ejecutarOperacionConfirmacion(ActionEvent event) {
        if (opSeleccionada == operacionEdificio.CREAR) {
            int id = DBEdificio.insertarEdificio(in_nombreEdificio.getText());
            if (id != -1) {
                mostrarInfoOperacion("Edificio creado. ID: " + id, colorExito);
                in_nombreEdificio.clear();
            } else {
                mostrarInfoOperacion("Error al crear edificio", colorAdvertencia);
            }
        } else if (opSeleccionada == operacionEdificio.BORRAR) {
            if (DBEdificio.eliminarEdificio(edificioBD.getIdEdificio())) {
                mostrarInfoOperacion("Edificio borrado correctamente", colorExito);
            } else {
                mostrarInfoOperacion("Error al borrar edificio", colorAdvertencia);
            }
        }
        cambiarPane(pane_confirmacion, pane_entrada);
    }

    @FXML
    void ejecutarOperacionActualizar(ActionEvent event) {
        if (field_nombreEdificioActualizar.getText().isBlank()) {
            mostrarInfoOperacion("El nombre es necesario", colorAdvertencia);
            return;
        }

        if (DBEdificio.actualizarEdificio(field_idEdificioActualizar.getValue().getIdEdificio(), field_nombreEdificioActualizar.getText())) {
            mostrarInfoOperacion("Edificio actualizado correctamente", colorExito);
            cambiarPane(pane_actualizar, pane_entrada);
        } else {
            mostrarInfoOperacion("Error al actualizar edificio", colorAdvertencia);
        }
    }

    @FXML
    void volverVentanaAnterior(ActionEvent event) {
        AnchorPane anterior = padrePane.get(pane_actual);
        cambiarPane(pane_actual, anterior);
        out_infoOperacion.setVisible(false);
        if (pane_actual == pane_inicio) btn_volver.setVisible(false);
    }

    @FXML
    void volverMenuPrincipal(ActionEvent event) throws IOException {
        /// CARGA LA VISTA DE LA INTERFAZ DE LOGIN
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Interfaces/MenuPrincipal.fxml"));
        Parent root = loader.load();

        /// OBTIENE LA VENTANA ACTUAL
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

        // ANIMACIÓN DE SALIDA
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), stage.getScene().getRoot());
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(e -> {

            ///CAMBIA A LA ESCENA DE LOGIN
            Scene nuevaEscena = new Scene(root);
            stage.setScene(nuevaEscena);

            // ANIMACIÓN DE ENTRADA
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
        });

        fadeOut.play();
    }

    private void cambiarPane(AnchorPane origen, AnchorPane destino) {
        origen.setVisible(false);
        destino.setVisible(true);
        pane_actual = destino;
    }

    private boolean validarCamposCrear() {
        if (in_nombreEdificio.getText().isBlank()) {
            mostrarInfoOperacion("El nombre es necesario", colorAdvertencia);
            in_nombreEdificio.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validarId(ComboBox<EdificioBD> field) {
        if (field.getValue() == null) {
            mostrarInfoOperacion("Debe seleccionar un edificio", colorAdvertencia);
            field.requestFocus();
            return false;
        }
        return true;
    }

    private void mostrarInfoOperacion(String msj, Color color) {
        out_infoOperacion.setText(msj);
        out_infoOperacion.setFill(color);
        out_infoOperacion.setVisible(true);
    }
}
