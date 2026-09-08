package Controllers;

import Validation.Validaciones;

import ObjetosBD.Calle.CalleBD;
import ObjetosBD.Calle.JDCalle;
import ObjetosBD.Colonia.ColoniaBD;
import ObjetosBD.Colonia.JDColonia;
import javafx.animation.FadeTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class CalleController {
    private JDCalle FDC;
    private JDColonia DBColonia;

    @FXML private TextField nom_calle;
    @FXML private ComboBox<ColoniaBD> combo_colonia;
    @FXML private TableColumn<CalleBD, Integer> colIdCalle;
    @FXML private TableColumn<CalleBD, Integer> colIdColonia;
    @FXML private TableColumn<CalleBD, String> colNombreCalle;
    @FXML private Button confirmar;
    @FXML private ComboBox<String> criterio;

    @FXML private Text out_infoOperacion;
    @FXML private TextField parametro;
    @FXML private TableView<CalleBD> tabla_calle;
    @FXML private Text texto_calle;

    private final Color colorAdvertencia = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    private final Color colorExito = new Color(0.0f, 1.0f, 0.1529f, 1.0f);
    private final Color colorBlanco = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private String mensajeOperacion;



    @FXML
    void eliminar_calle(ActionEvent event) {
        CalleBD seleccionada = tabla_calle.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mensajeOperacion = "Debe seleccionar una calle para eliminar";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }


        boolean eliminado = FDC.eliminarCalle(seleccionada.getId_calle());

        if (eliminado) {
            mensajeOperacion = "Calle eliminada correctamente";
            out_infoOperacion.fillProperty().set(colorExito);


            String mensajeExito = mensajeOperacion;
            buscar_calle(null);
            mensajeOperacion = mensajeExito;
            out_infoOperacion.setFill(colorExito);


            nom_calle.clear();
        } else {
            mensajeOperacion = "No se pudo eliminar la calle";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
        }

        mostrarInfoOperacion();


    }

    @FXML
    void initialize(){
        FDC = new JDCalle();
        DBColonia = new JDColonia();
        combo_colonia.setItems(DBColonia.obtenerColonias());

        criterio.getItems().addAll("ID", "Nombre");
        configurarTablaBusqueda();
        tabla_calle.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                CalleBD seleccionada = tabla_calle.getSelectionModel().getSelectedItem();
                if (seleccionada != null) {
                    // Llenar el campo de nombre de la calle
                    nom_calle.setText(seleccionada.getNombre());

                    // Seleccionar la colonia asociada en el ComboBox
                    for (ColoniaBD colonia : combo_colonia.getItems()) {
                        if (colonia.getId_colonia() == seleccionada.getId_colonia()) {
                            combo_colonia.setValue(colonia); // selecciona la colonia en el ComboBox
                            break;
                        }
                    }
                }
            }
        });


    }

    @FXML
    void registrarCalle(ActionEvent event){
        validar();
    }

    private void validar(){
        nom_calle.setText(Validaciones.texto(nom_calle.getText(), "Nombre de calle", 120));
        if(combo_colonia.getValue() == null){
            mensajeOperacion = "Debe seleccionar una colonia";
            out_infoOperacion.setFill(colorAdvertencia);
            mostrarInfoOperacion();
            combo_colonia.requestFocus();
            return;
        }

        String nombreCalle = nom_calle.getText();

        ColoniaBD coloniaSeleccionada = combo_colonia.getValue();
        int IdColonia = coloniaSeleccionada.getId_colonia();

        int id= FDC.insertarCalle(nombreCalle, IdColonia);
        if(id > 0){
            mensajeOperacion = "Calle registrada. ID: " + id;
            out_infoOperacion.setFill(colorExito);
            nom_calle.clear();
        } else {
            mensajeOperacion = "Error en el registro de calle";
            out_infoOperacion.setFill(colorAdvertencia);
        }
        mostrarInfoOperacion();
    }

    @FXML
    void actualizar_calle(ActionEvent event) {
        CalleBD seleccionada = tabla_calle.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mensajeOperacion = "Debe seleccionar una calle con doble clic";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        nom_calle.setText(Validaciones.texto(nom_calle.getText(), "Nombre de calle", 120));

        // 🔹 Obtener colonia seleccionada en el ComboBox
        ColoniaBD coloniaSeleccionada = (ColoniaBD) combo_colonia.getValue();
        if (coloniaSeleccionada == null) {
            mensajeOperacion = "Debe seleccionar una colonia en el ComboBox";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        int idcolonia = coloniaSeleccionada.getId_colonia();

        // 🔹 Ejecutar UPDATE
        boolean actualizado = FDC.actualizarCalle(seleccionada.getId_calle(), nom_calle.getText(), idcolonia);

        if (actualizado) {
            mensajeOperacion = "Calle actualizada correctamente";
            out_infoOperacion.fillProperty().set(colorExito);

            // Refrescar tabla
            String mensajeExito = mensajeOperacion;
            buscar_calle(null);
            mensajeOperacion = mensajeExito;
            out_infoOperacion.setFill(colorExito);
        } else {
            mensajeOperacion = "No se pudo actualizar la calle";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
        }

        mostrarInfoOperacion();
    }


    @FXML
    void buscar_calle(ActionEvent event) {
        tabla_calle.getItems().clear();

        if (criterio.getValue() == null || criterio.getValue().isBlank()) {
            mensajeOperacion = "Debe descoger un criterio de búsqueda";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        if (parametro.getText().isBlank()) {
            mensajeOperacion = "Debe escribir un parámetro de búsqueda";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        ObservableList<CalleBD> resultadoBusqueda = FXCollections.observableArrayList();

        if(criterio.getValue().equals("ID")){
            int busqueda = Validaciones.entero(parametro.getText(), "ID", 1, Integer.MAX_VALUE);
            resultadoBusqueda = FDC.buscarCalleIDTABLA(busqueda);
            if(resultadoBusqueda.isEmpty()){
                mensajeOperacion = "No se encontraron resultados";
                out_infoOperacion.fillProperty().set(colorAdvertencia);
            } else {
                mensajeOperacion = "Resultados encontrados: " + resultadoBusqueda.size();
                out_infoOperacion.fillProperty().set(colorBlanco);
                tabla_calle.setItems(resultadoBusqueda);
            }
        } else if(criterio.getValue().equals("Nombre")){
            resultadoBusqueda = FDC.buscarCalleaNombre(Validaciones.texto(parametro.getText(), "Nombre de calle", 120));
            if(resultadoBusqueda.isEmpty()){
                mensajeOperacion = "No se encontraron resultados";
                out_infoOperacion.fillProperty().set(colorAdvertencia);
            } else {
                mensajeOperacion = "Resultados encontrados: " + resultadoBusqueda.size();
                out_infoOperacion.fillProperty().set(colorBlanco);
                tabla_calle.setItems(resultadoBusqueda);
            }
        }

        mostrarInfoOperacion();

    }


    public void mostrarInfoOperacion(){
        out_infoOperacion.setText(this.mensajeOperacion);
        out_infoOperacion.setVisible(true);
    }

    private void configurarTablaBusqueda(){
        colIdCalle.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId_calle()));
        colNombreCalle.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getNombre()));
        colIdColonia.setCellValueFactory(data-> new SimpleObjectProperty<>(data.getValue().getId_colonia()));
    }

    @FXML
    void regresar(ActionEvent event) throws IOException {
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

}
