package Controllers;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class ColoniaController {

    private JDColonia CDB;

    // ELEMENTOS DEL FORMULARIO
    @FXML private TextField nom_col;
    @FXML private ComboBox<String> criterio;
    @FXML private TextField parametro;
    @FXML private TableView<ColoniaBD> tabla_colonia;
    @FXML private TableColumn<ColoniaBD, Integer> colIdColonia;
    @FXML private TableColumn<ColoniaBD, String> colNombreColonia;

    @FXML private Text out_infoOperacion;

    private final Color colorAdvertencia = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    private final Color colorExito = new Color(0.0f, 1.0f, 0.1529f, 1.0f);
    private final Color colorBlanco = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private String mensajeOperacion;

    // Inicializar
    @FXML void initialize(){
        CDB = new JDColonia();
        criterio.getItems().addAll("ID", "Nombre");
        configurarTablaBusqueda();
        tabla_colonia.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                ColoniaBD seleccionada = tabla_colonia.getSelectionModel().getSelectedItem();
                if (seleccionada != null) {
                    nom_col.setText(seleccionada.getNombre());
                }
            }
        });
    }

    // Registrar colonia
    @FXML
    void registrar_colonia(ActionEvent event) {
        if(nom_col.getText().isBlank()){
            mensajeOperacion = "El nombre de la colonia es necesario";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            nom_col.requestFocus();
            return;
        }

        String nombre = nom_col.getText();
        int id = CDB.insertarColonia(nombre);

        if(id != 1){
            mensajeOperacion = "Registro exitoso. ID: " + id;
            out_infoOperacion.fillProperty().set(colorExito);
            nom_col.clear();
        } else {
            mensajeOperacion = "Error en el registro de COLONIA";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
        }
        mostrarInfoOperacion();
    }

    // Buscar colonia
    @FXML
    void buscar_colonia(ActionEvent event) {
        tabla_colonia.getItems().clear();

        if(criterio.getValue() == null || criterio.getValue().isBlank()){
            mensajeOperacion = "Debe escoger un criterio de búsqueda";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        if(parametro.getText().isBlank()){
            mensajeOperacion = "Debe escribir un parámetro de búsqueda";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        ObservableList<ColoniaBD> resultadoBusqueda = FXCollections.observableArrayList();

        if(criterio.getValue().equals("ID")){
            int busqueda = Integer.parseInt(parametro.getText());
            resultadoBusqueda = CDB.buscarColoniaID(busqueda);
            if(resultadoBusqueda.isEmpty()){
                mensajeOperacion = "No se encontraron resultados";
                out_infoOperacion.fillProperty().set(colorAdvertencia);
            } else {
                mensajeOperacion = "Resultados encontrados: " + resultadoBusqueda.size();
                out_infoOperacion.fillProperty().set(colorBlanco);
                tabla_colonia.setItems(resultadoBusqueda);
            }
        } else if(criterio.getValue().equals("Nombre")){
            resultadoBusqueda = CDB.buscarColoniaNombre(parametro.getText());
            if(resultadoBusqueda.isEmpty()){
                mensajeOperacion = "No se encontraron resultados";
                out_infoOperacion.fillProperty().set(colorAdvertencia);
            } else {
                mensajeOperacion = "Resultados encontrados: " + resultadoBusqueda.size();
                out_infoOperacion.fillProperty().set(colorBlanco);
                tabla_colonia.setItems(resultadoBusqueda);
            }
        }

        mostrarInfoOperacion();
    }

    // Actualizar colonia
    @FXML
    void actualizar_colonia(ActionEvent event) {
        ColoniaBD seleccionada = tabla_colonia.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mensajeOperacion = "Debe seleccionar una colonia con doble clic";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        if (nom_col.getText().isBlank()) {
            mensajeOperacion = "El nombre no puede estar vacío";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        boolean actualizado = CDB.actualizarColonia(seleccionada.getId_colonia(), nom_col.getText(), 0.0f);

        if (actualizado) {
            mensajeOperacion = "Colonia actualizada correctamente";
            out_infoOperacion.fillProperty().set(colorExito);
        } else {
            mensajeOperacion = "No se pudo actualizar la colonia";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
        }

        mostrarInfoOperacion();
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


    // Eliminar colonia
    @FXML
    void eliminar_colonia(ActionEvent event) {
        ColoniaBD seleccionada = tabla_colonia.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mensajeOperacion = "Debe seleccionar una colonia para eliminar";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }


        boolean eliminado = CDB.eliminarColonia(seleccionada.getId_colonia());

        if (eliminado) {
            mensajeOperacion = "Colonia eliminada correctamente";
            out_infoOperacion.fillProperty().set(colorExito);


            buscar_colonia(null);


            nom_col.clear();
        } else {
            mensajeOperacion = "No se pudo eliminar la colonia";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
        }

        mostrarInfoOperacion();
    }

    private void configurarTablaBusqueda(){
        colIdColonia.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId_colonia()));
        colNombreColonia.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getNombre()));
    }

    public void mostrarInfoOperacion(){
        out_infoOperacion.setText(this.mensajeOperacion);
        out_infoOperacion.setVisible(true);
    }
}
