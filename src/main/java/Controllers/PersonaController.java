package Controllers;

import ObjetosBD.Familia.FamiliaBD;
import ObjetosBD.Familia.JDFamilia;
import ObjetosBD.Persona.JDPersona;
import ObjetosBD.Persona.PersonaBD;
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

enum operacionPersona {
    CREAR("Crear"),
    BUSCAR("Buscar"),
    BORRAR("Borrar"),
    ACTUALIZAR("Seleccionar persona");

    private final String texto;

    operacionPersona(String texto){
        this.texto = texto;
    }

    public String getTextoBoton(){
        return this.texto;
    }
}

public class PersonaController {
    //Componentes generales
    @FXML private Button btn_menuPrincipal;
    @FXML private Text out_infoOperacion;
    @FXML private Button btn_volver;

    //Paneles
    @FXML private AnchorPane pane_inicio;
    @FXML private AnchorPane pane_entrada;
    @FXML private AnchorPane pane_actualizar;
    @FXML private AnchorPane pane_resultadoBusqueda;
    @FXML private AnchorPane pane_confirmacion;

    //Campos de entrada (pane_entrada)
    @FXML private ComboBox<PersonaBD> in_idPersona;
    @FXML private TextField in_nombre;
    @FXML private TextField in_edad;
    @FXML private ComboBox<FamiliaBD> in_comboFamilia;
    @FXML private Button btn_entrada;
    @FXML private Text txt_nombreEntrada;
    @FXML private Text txt_edadEntrada;
    @FXML private Text txt_familiaEntrada;

    //Campos de actualizar (pane_actualizar)
    @FXML private ComboBox<PersonaBD> field_idPersonaActualizar;
    @FXML private TextField field_nombreActualizar;
    @FXML private TextField field_edadActualizar;
    @FXML private ComboBox<FamiliaBD> combo_familiaActualizar;
    @FXML private Button btn_confirmacion;

    //Campos de confirmacion (pane_confirmacion)
    @FXML private TextField out_nombreRegistro;
    @FXML private TextField out_edadRegistro;
    @FXML private TextField out_familiaRegistro;
    @FXML private Button btn_confirmacionFinal;

    //Tabla de busqueda
    @FXML private TableView<Map<String, Object>> tabla_busquedaPersona;
    @FXML private TableColumn<Map<String, Object>, Object> colIdPersona;
    @FXML private TableColumn<Map<String, Object>, Object> colNombre;
    @FXML private TableColumn<Map<String, Object>, Object> colEdad;
    @FXML private TableColumn<Map<String, Object>, Object> colIdFamilia;
    @FXML private TableColumn<Map<String, Object>, Object> colApellidosFamilia;

    //Variables de control
    private final Color colorAdvertencia = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    private final Color colorExito = new Color(0.0f, 1.0f, 0.1529f, 1.0f);
    private operacionPersona opSeleccionada;
    private final JDPersona DBPersona = new JDPersona();
    private final JDFamilia DBFamilia = new JDFamilia();
    private PersonaBD personaBD;
    private FamiliaBD familiaBD;
    private final Map<AnchorPane, AnchorPane> padrePane = new HashMap<>();
    private AnchorPane pane_actual;

    @FXML
    public void initialize() {
        pane_actual = pane_inicio;

        in_idPersona.setItems(DBPersona.obtenerPersona());
        field_idPersonaActualizar.setItems(DBPersona.obtenerPersona());

        in_comboFamilia.setItems(DBFamilia.obtenerFamilias());
        combo_familiaActualizar.setItems(DBFamilia.obtenerFamilias());

        configurarTablaBusqueda();
        initPadrePane();
    }

    private void initPadrePane() {
        padrePane.put(pane_inicio, pane_inicio);
        padrePane.put(pane_entrada, pane_inicio);
        padrePane.put(pane_confirmacion, pane_entrada);
        padrePane.put(pane_actualizar, pane_entrada);
        padrePane.put(pane_resultadoBusqueda, pane_entrada);
    }

    private void cambiarPane(AnchorPane origen, AnchorPane destino) {
        origen.setVisible(false);
        destino.setVisible(true);
        pane_actual = destino;
    }

    @FXML
    void elegirOpcionCrear(ActionEvent event) {
        opSeleccionada = operacionPersona.CREAR;
        operacionSeleccionada();
    }

    @FXML
    void elegirOpcionBuscar(ActionEvent event) {
        opSeleccionada = operacionPersona.BUSCAR;
        operacionSeleccionada();
    }

    @FXML
    void elegirOpcionActualizar(ActionEvent event) {
        opSeleccionada = operacionPersona.ACTUALIZAR;
        operacionSeleccionada();
    }

    @FXML
    void elegirOpcionBorrar(ActionEvent event) {
        opSeleccionada = operacionPersona.BORRAR;
        operacionSeleccionada();
    }

    private void operacionSeleccionada() {
        btn_entrada.setText(opSeleccionada.getTextoBoton());
        in_idPersona.getSelectionModel().clearSelection();
        in_nombre.clear();
        in_edad.clear();
        in_comboFamilia.getSelectionModel().clearSelection();

        // Para Crear y Buscar mostramos campos extras.
        // En Borrar y Actualizar solo pedimos el ID inicialmente.
        boolean mostrarExtras = (opSeleccionada == operacionPersona.CREAR || opSeleccionada == operacionPersona.BUSCAR);
        in_nombre.setVisible(mostrarExtras);
        txt_nombreEntrada.setVisible(mostrarExtras);
        in_edad.setVisible(mostrarExtras);
        txt_edadEntrada.setVisible(mostrarExtras);
        in_comboFamilia.setVisible(mostrarExtras);
        txt_familiaEntrada.setVisible(mostrarExtras);

        // Si es CREAR, el ID no se debe pedir (es auto-incremental)
        if(opSeleccionada == operacionPersona.CREAR) {
            in_idPersona.setDisable(true);
        } else {
            in_idPersona.setDisable(false);
        }

        cambiarPane(pane_inicio, pane_entrada);
        btn_volver.setVisible(true);
    }

    @FXML
    void ejecutarOperacionEntrada(ActionEvent event) {
        switch (opSeleccionada) {
            case CREAR:
                if(!validarCamposCorrectos()) return;
                familiaBD = in_comboFamilia.getValue();
                cargarPaneConfirmacion();
                btn_confirmacionFinal.setText("Crear persona");
                cambiarPane(pane_entrada, pane_confirmacion);
                break;

            case BUSCAR:
                tabla_busquedaPersona.getItems().clear();
                operacionBuscar();
                break;

            case ACTUALIZAR:
                if(!validarIdPersona(in_idPersona)) return;
                personaBD = in_idPersona.getValue();
                if(personaBD == null){
                    mostrarInfoOperacion("La persona no existe", colorAdvertencia);
                    return;
                }
                field_idPersonaActualizar.setValue(personaBD);
                field_nombreActualizar.setText(personaBD.getNombre());
                field_edadActualizar.setText(String.valueOf(personaBD.getEdadPersona()));
                // Buscar familia en el combo
                for(FamiliaBD f : combo_familiaActualizar.getItems()){
                    if(f.getId() == personaBD.getIdFamilia()){
                        combo_familiaActualizar.getSelectionModel().select(f);
                        break;
                    }
                }
                out_infoOperacion.setVisible(false);
                btn_confirmacion.setText("Actualizar");
                cambiarPane(pane_entrada, pane_actualizar);
                break;

            case BORRAR:
                if(!validarIdPersona(in_idPersona)) return;
                personaBD = in_idPersona.getValue();
                if(personaBD == null){
                    mostrarInfoOperacion("La persona no existe", colorAdvertencia);
                    return;
                }
                familiaBD = DBFamilia.buscarFamiliaID(personaBD.getIdFamilia());
                cargarPaneConfirmacion();
                btn_confirmacionFinal.setText("Borrar persona");
                cambiarPane(pane_entrada, pane_confirmacion);
                break;
        }
    }

    @FXML
    void ejecutarOperacionConfirmacion(ActionEvent event) {
        switch (opSeleccionada) {
            case CREAR:
                operacionCrear();
                break;
            case ACTUALIZAR:
                operacionActualizar();
                break;
            case BORRAR:
                operacionBorrar();
                break;
        }
    }

    private void operacionCrear() {
        int idGenerado = DBPersona.insertarPersona(in_nombre.getText(), in_comboFamilia.getValue().getId(), Integer.parseInt(in_edad.getText()));
        if(idGenerado != -1){
            mostrarInfoOperacion("Persona creada con ID: " + idGenerado, colorExito);
            in_nombre.clear();
            in_edad.clear();
            in_comboFamilia.getSelectionModel().clearSelection();
        } else {
            mostrarInfoOperacion("Error al crear la persona", colorAdvertencia);
        }
        cambiarPane(pane_confirmacion, pane_entrada);
    }

    private void operacionActualizar() {
        if(!validarNombre(field_nombreActualizar)) return;
        if(!validarEdad(field_edadActualizar)) return;
        if(!validarFamilia(combo_familiaActualizar)) return;

        boolean exito = DBPersona.actualizarPersona(
                field_idPersonaActualizar.getValue().getIdPersona(),
                field_nombreActualizar.getText(),
                combo_familiaActualizar.getValue().getId(),
                Integer.parseInt(field_edadActualizar.getText())
        );

        if(exito){
            mostrarInfoOperacion("Persona actualizada correctamente", colorExito);
        } else {
            mostrarInfoOperacion("Error al actualizar la persona", colorAdvertencia);
        }
        cambiarPane(pane_actualizar, pane_entrada);
    }

    private void operacionBorrar() {
        boolean exito = DBPersona.eliminarPersona(personaBD.getIdPersona());
        if(exito){
            mostrarInfoOperacion("Persona borrada correctamente", colorExito);
        } else {
            mostrarInfoOperacion("Error al borrar la persona", colorAdvertencia);
        }
        cambiarPane(pane_confirmacion, pane_entrada);
    }

    private void operacionBuscar() {
        Integer id = null;
        if(in_idPersona.getValue() != null){
            id = in_idPersona.getValue().getIdPersona();
        }
        String nombre = in_nombre.getText();
        Integer edad = null;
        if(!in_edad.getText().isEmpty() && in_edad.getText().matches("[0-9]+")) edad = Integer.parseInt(in_edad.getText());
        Integer idFam = (in_comboFamilia.getValue() != null) ? in_comboFamilia.getValue().getId() : null;

        ObservableList<Map<String, Object>> resultados = DBPersona.buscarPersonas(id, nombre, idFam, edad);
        if(resultados != null){
            tabla_busquedaPersona.setItems(resultados);
            mostrarInfoOperacion("Resultados encontrados: " + resultados.size(), colorExito);
            cambiarPane(pane_entrada, pane_resultadoBusqueda);
        } else {
            mostrarInfoOperacion("No se encontraron resultados", colorAdvertencia);
        }
    }

    private void cargarPaneConfirmacion() {
        out_nombreRegistro.setText(opSeleccionada == operacionPersona.BORRAR ? personaBD.getNombre() : in_nombre.getText());
        out_edadRegistro.setText(String.valueOf(opSeleccionada == operacionPersona.BORRAR ? personaBD.getEdadPersona() : in_edad.getText()));
        out_familiaRegistro.setText(familiaBD != null ? familiaBD.getApellidos() : "Sin familia");
    }

    private void configurarTablaBusqueda() {
        colIdPersona.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get("IdPersona")));
        colNombre.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get("Nombre")));
        colEdad.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get("Edad")));
        colIdFamilia.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get("IdFamilia")));
        colApellidosFamilia.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().get("ApellidosFamilia")));
    }

    private boolean validarCamposCorrectos() {
        if(!validarNombre(in_nombre)) return false;
        if(!validarEdad(in_edad)) return false;
        if(!validarFamilia(in_comboFamilia)) return false;
        return true;
    }

    private boolean validarIdPersona(ComboBox<PersonaBD> field) {
        if(field.getValue() == null){
            mostrarInfoOperacion("Debe seleccionar una persona", colorAdvertencia);
            field.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validarNombre(TextField field) {
        if(field.getText().isBlank()){
            mostrarInfoOperacion("El nombre es obligatorio", colorAdvertencia);
            field.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validarEdad(TextField field) {
        if(field.getText().isBlank() || !field.getText().matches("[0-9]+")){
            mostrarInfoOperacion("La edad debe ser un número", colorAdvertencia);
            field.requestFocus();
            return false;
        }
        return true;
    }

    private boolean validarFamilia(ComboBox<FamiliaBD> combo) {
        if(combo.getValue() == null){
            mostrarInfoOperacion("Debe seleccionar una familia", colorAdvertencia);
            combo.requestFocus();
            return false;
        }
        return true;
    }

    public void mostrarInfoOperacion(String mensaje, Color color) {
        out_infoOperacion.setFill(color);
        out_infoOperacion.setText(mensaje);
        out_infoOperacion.setVisible(true);
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

    @FXML
    void volverVentanaAnterior(ActionEvent event) {
        cambiarPane(pane_actual, padrePane.get(pane_actual));
        out_infoOperacion.setVisible(false);
        if (pane_actual == pane_inicio) btn_volver.setVisible(false);
    }
}
