package Controllers;

import Validation.Validaciones;
import ObjetosBD.Colonia.ColoniaBD;
import ObjetosBD.Familia.FamiliaBD;
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
import ObjetosBD.Familia.JDFamilia;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;


public class FamiliaController {

    private JDFamilia FDB;

    /// ELEMENTOS DEL FORMULARIO
    @FXML private TextField apellidos;

    /// FUNCIONES
    @FXML void initialize(){
        FDB = new JDFamilia();
        criterio.getItems().addAll("ID", "Nombre");
        configurarTablaBusqueda();
        tabla_familia.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                FamiliaBD seleccionada = tabla_familia.getSelectionModel().getSelectedItem();
                if (seleccionada != null) {
                    apellidos.setText(seleccionada.getApellidos());
                }
            }
        });
    } /// Inicializar una nueva instancia de tipo FAMILIA

    @FXML void registrarFamilia(ActionEvent event){validar();} ///Realiza la acción de REGISTRO a la FAMILIA al PRESIONAR el BOTÓN


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


    @FXML private TableColumn<FamiliaBD,String> colApellidosFamilia;
    @FXML private TableColumn<FamiliaBD, Integer> colIdFamilia;
    @FXML private ComboBox<String> criterio;
    @FXML private Text out_infoOperacion;
    @FXML private TextField parametro;
    @FXML private TableView<FamiliaBD> tabla_familia;

    private final Color colorAdvertencia = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    private final Color colorExito = new Color(0.0f, 1.0f, 0.1529f, 1.0f);
    private final Color colorBlanco = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private String mensajeOperacion;

    @FXML void actualizar_familia(ActionEvent event) {

        FamiliaBD seleccionada = tabla_familia.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mensajeOperacion = "Debe seleccionar una familia con doble clic";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        apellidos.setText(Validaciones.texto(apellidos.getText(), "Apellidos", 120));

        boolean actualizado = FDB.actualizarFamilia(seleccionada.getId(), apellidos.getText());

        if (actualizado) {
            seleccionada.setApellidos(apellidos.getText());
            tabla_familia.refresh();
            mensajeOperacion = "Familia actualizada correctamente";
            out_infoOperacion.fillProperty().set(colorExito);
        } else {
            mensajeOperacion = "No se pudo actualizar la familia";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
        }

        mostrarInfoOperacion();


    }

    @FXML
    void buscar_familia(ActionEvent event) {

        tabla_familia.getItems().clear();

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

        ObservableList<FamiliaBD> resultadoBusqueda = FXCollections.observableArrayList();

        if(criterio.getValue().equals("ID")){
            int busqueda = Validaciones.entero(parametro.getText(), "ID", 1, Integer.MAX_VALUE);
            resultadoBusqueda = FDB.buscarFamiliaIDTABLA(busqueda);
            if(resultadoBusqueda.isEmpty()){
                mensajeOperacion = "No se encontraron resultados";
                out_infoOperacion.fillProperty().set(colorAdvertencia);
            } else {
                mensajeOperacion = "Resultados encontrados: " + resultadoBusqueda.size();
                out_infoOperacion.fillProperty().set(colorBlanco);
                tabla_familia.setItems(resultadoBusqueda);
            }
        } else if(criterio.getValue().equals("Nombre")){
            resultadoBusqueda = FDB.buscarFamiliaApellidos(Validaciones.texto(parametro.getText(), "Apellidos", 120));
            if(resultadoBusqueda.isEmpty()){
                mensajeOperacion = "No se encontraron resultados";
                out_infoOperacion.fillProperty().set(colorAdvertencia);
            } else {
                mensajeOperacion = "Resultados encontrados: " + resultadoBusqueda.size();
                out_infoOperacion.fillProperty().set(colorBlanco);
                tabla_familia.setItems(resultadoBusqueda);
            }
        }

        mostrarInfoOperacion();

    }

    @FXML
    void eliminar_familia(ActionEvent event) {

        FamiliaBD seleccionada = tabla_familia.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mensajeOperacion = "Debe seleccionar una familia para eliminar";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }


        boolean eliminado = FDB.eliminarFamilia(seleccionada.getId());

        if (eliminado) {
            mensajeOperacion = "Familia eliminada correctamente";
            out_infoOperacion.fillProperty().set(colorExito);


            String mensajeExito = mensajeOperacion;
            buscar_familia(null);
            mensajeOperacion = mensajeExito;
            out_infoOperacion.setFill(colorExito);


            apellidos.clear();
        } else {
            mensajeOperacion = "No se pudo eliminar la familia";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
        }

        mostrarInfoOperacion();


    }


    private void validar(){
        String apellido;
        apellidos.setText(Validaciones.texto(apellidos.getText(), "Apellidos", 120));
       apellido = apellidos.getText();

        int id= FDB.insertarFamilia(apellido);
        if(id > 0){
            mensajeOperacion = "Familia registrada. ID: " + id;
            out_infoOperacion.setFill(colorExito);
            apellidos.clear();
        } else {
            mensajeOperacion = "Error en el registro de familia";
            out_infoOperacion.setFill(colorAdvertencia);
        }
        mostrarInfoOperacion();
    }

    private void configurarTablaBusqueda(){
        colIdFamilia.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        colApellidosFamilia.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getApellidos()));
    }

    public void mostrarInfoOperacion(){
        out_infoOperacion.setText(this.mensajeOperacion);
        out_infoOperacion.setVisible(true);
    }

}
