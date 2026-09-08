package Controllers;

import ObjetosBD.Calle.CalleBD;
import ObjetosBD.Calle.JDCalle;
import ObjetosBD.Habitante.HabitanteBD;
import ObjetosBD.Habitante.JDHabitante;
import ObjetosBD.Persona.JDPersona;
import ObjetosBD.Persona.PersonaBD;
import ObjetosBD.Vivienda.JDVivienda;
import ObjetosBD.Vivienda.ViviendaBD;
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


enum operacion{
    CREAR("Crear"),
    BUSCAR("Buscar"),
    BORRAR("Borrar"),
    ACTUALIZAR("Seleccionar habitante");

    private final String texto;

    operacion(String texto){
        this.texto = texto;
    }

    public String getTextoBoton(){
        return this.texto;
    }
}

public class HabitanteController {
    //Componentes generales
    @FXML
    private Button btn_menuPrincipal;
    @FXML
    private AnchorPane pane_inicio;

    @FXML
    private Text out_infoOperacion;
    @FXML
    private AnchorPane pane_entrada;
    @FXML
    private ComboBox<PersonaBD> in_idPersona;
    @FXML
    private Text txt_viviendaEntrada;
    @FXML
    private ComboBox<ViviendaBD> in_idVivienda;
    @FXML
    private Text txt_rolEntrada;
    @FXML
    private ComboBox<String> in_comboRol;
    @FXML
    private Button btn_entrada;

    @FXML
    private Button btn_confirmacionActualizar;
    @FXML
    private Button btn_confirmacionCrear;
    @FXML
    private AnchorPane pane_confirmacion;
    @FXML
    private Button btn_volver;

    //JavaFX - Eventos Generales
    @FXML
    public void initialize() {
        pane_actual = pane_inicio;

        in_idPersona.setItems(DBPersona.obtenerPersona());
        in_idVivienda.setItems(DBVivienda.obtenerVivienda());

        field_idPersonaActualizar.setItems(DBPersona.obtenerPersona());
        field_idViviendaActualizar.setItems(DBVivienda.obtenerVivienda());

        in_comboRol.getItems().addAll(
                "Jefe Casa",
                "Madre",
                "Padre",
                "Hija",
                "Hijo",
                "Sobrina",
                "Sobrino",
                "Otro"
        );

        combo_rolActualizar.getItems().addAll(
                "Jefe Casa",
                "Madre",
                "Padre",
                "Hija",
                "Hijo",
                "Sobrina",
                "Sobrino",
                "Otro"
        );

        configurarTablaBusqueda();
        initPadrePane();
    }

    @FXML
    void elegirOpcionActualizar(ActionEvent event) {
        opSeleccionada = operacion.ACTUALIZAR;
        operacionSeleccionada();
    }

    @FXML
    void elegirOpcionBorrar(ActionEvent event) {
        opSeleccionada = operacion.BORRAR;
        operacionSeleccionada();
    }

    @FXML
    void elegirOpcionBuscar(ActionEvent event) {
        opSeleccionada = operacion.BUSCAR;
        operacionSeleccionada();
    }

    @FXML
    void elegirOpcionCrear(ActionEvent event) {
        opSeleccionada = operacion.CREAR;
        operacionSeleccionada();
    }

    @FXML
    void ejecutarOperacionEntrada(ActionEvent event) {
        switch (opSeleccionada) {
            case CREAR:
                if (!validarCamposCorrectos()) return;

                personaBD = in_idPersona.getValue();
                viviendaBD = in_idVivienda.getValue();
                calleBD = DBCalle.buscarCalleID(viviendaBD.getIdCalle());

                cargarPaneDatosHabitante();
                btn_confirmacionCrear.setText("Crear habitante");
                cambiarPane(pane_entrada, pane_confirmacion);
                break;

            case BUSCAR:
                tabla_busquedaHabitante.getItems().clear();
                operacionBuscar();
                break;

            case ACTUALIZAR:
                if (in_idPersona.getValue() == null) {
                    mostrarInfoOperacion("Debe seleccionar una persona", colorAdvertencia);
                    return;
                }
                habitanteBD = DBHabitante.buscarHabitante(in_idPersona.getValue().getIdPersona());
                if (habitanteBD == null) {
                    mostrarInfoOperacion("El habitante no existe", colorAdvertencia);
                    cambiarPane(pane_actualizar, pane_entrada);
                    return;
                }

                field_idPersonaActualizar.setValue(in_idPersona.getValue());

                ViviendaBD vivHabitante = DBVivienda.buscarVivienda(habitanteBD.getIdVivienda());
                if (vivHabitante != null) {
                    for (ViviendaBD v : field_idViviendaActualizar.getItems()) {
                        if (v.getId_vivienda() == vivHabitante.getId_vivienda()) {
                            field_idViviendaActualizar.setValue(v);
                            break;
                        }
                    }
                }

                combo_rolActualizar.getSelectionModel().select(habitanteBD.getRol());
                out_infoOperacion.setVisible(false);

                btn_confirmacionActualizar.setText("Actualizar");
                cambiarPane(pane_entrada, pane_actualizar);
                break;

            case BORRAR:
                if (in_idPersona.getValue() == null) {
                    mostrarInfoOperacion("Debe seleccionar una persona", colorAdvertencia);
                    return;
                }
                habitanteBD = DBHabitante.buscarHabitante(in_idPersona.getValue().getIdPersona());
                if (habitanteBD == null) {
                    mostrarInfoOperacion("El habitante no existe", colorAdvertencia);
                    cambiarPane(pane_actualizar, pane_entrada);
                    return;
                }

                personaBD = in_idPersona.getValue();
                if (!existeVivienda(habitanteBD.getIdVivienda())) return;
                calleBD = DBCalle.buscarCalleID(viviendaBD.getIdCalle());

                out_infoOperacion.setVisible(false);
                cargarPaneDatosHabitante();

                btn_confirmacionCrear.setText("Borrar habitante");
                cambiarPane(pane_entrada, pane_confirmacion);
                break;
        }
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

    //JavaFX - Crear habitante
    @FXML
    private TextField out_nombrePerRegistro;
    @FXML
    private TextField out_rol;
    @FXML
    private TextField out_rolRegistro;
    @FXML
    private TextField out_nombreCalRegistro;
    @FXML
    private TextField out_numExtRegistro;
    @FXML
    private TextField out_numIntRegistro;
    @FXML
    private TextField out_tipoVivRegistro;

    //JavaFX - Busqueda de habitantes
    @FXML
    private AnchorPane pane_resultadoBusqueda;
    @FXML
    private TableView<Map<String, Object>> tabla_busquedaHabitante;
    @FXML
    private TableColumn<Map<String, Object>, Object> colIdPersona;
    @FXML
    private TableColumn<Map<String, Object>, Object> colNombre;
    @FXML
    private TableColumn<Map<String, Object>, Object> colRol;
    @FXML
    private TableColumn<Map<String, Object>, Object> colIdVivienda;
    @FXML
    private TableColumn<Map<String, Object>, Object> colTipoVivienda;
    @FXML
    private TableColumn<Map<String, Object>, Object> colNoExt;
    @FXML
    private TableColumn<Map<String, Object>, Object> colNoInt;
    @FXML
    private TableColumn<Map<String, Object>, Object> colMetrosCuadrados;
    @FXML
    private TableColumn<Map<String, Object>, Object> colIdCalle;
    @FXML
    private TableColumn<Map<String, Object>, Object> colNombreCalle;

    //JavaFX - Actualizar habitante
    @FXML
    private AnchorPane pane_actualizar;
    @FXML
    private ComboBox<PersonaBD> field_idPersonaActualizar;
    @FXML
    private ComboBox<ViviendaBD> field_idViviendaActualizar;
    @FXML
    private ComboBox<String> combo_rolActualizar;


    //Componentes generales
    private final Color colorAdvertencia = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    private final Color colorExito = new Color(0.0f, 1.0f, 0.1529f, 1.0f);
    private operacion opSeleccionada;

    private PersonaBD personaBD;
    private ViviendaBD viviendaBD;
    private CalleBD calleBD;
    private HabitanteBD habitanteBD;
    private ArrayList<HabitanteBD> habitantesBD;

    private final JDHabitante DBHabitante = new JDHabitante();
    private final JDPersona DBPersona = new JDPersona();
    private final JDVivienda DBVivienda = new JDVivienda();
    private final JDCalle DBCalle = new JDCalle();
    private final Map<AnchorPane, AnchorPane> padrePane = new HashMap<>();
    private AnchorPane pane_actual;

    //Eventos generales
    public void mostrarInfoOperacion(String mensaje, Color color) {
        out_infoOperacion.fillProperty().set(color);
        out_infoOperacion.setText(mensaje);
        out_infoOperacion.setVisible(true);
    }

    public boolean validarIdPersona(ComboBox<PersonaBD> in_idPersona) {
        if (in_idPersona.getValue() == null) {
            mostrarInfoOperacion("El campo de persona es necesario", colorAdvertencia);
            return false;
        }

        return true;
    }

    public boolean validarIdVivienda(ComboBox<ViviendaBD> in_idVivienda) {
        if (in_idVivienda.getValue() == null) {
            mostrarInfoOperacion("El campo de vivienda es necesario", colorAdvertencia);
            return false;
        }

        return true;
    }

    public boolean validarRol(ComboBox<String> in_comboRol) {
        if (in_comboRol.getValue() == null) {
            mostrarInfoOperacion("El campo de rol es necesario", colorAdvertencia);
            return false;
        }

        return true;
    }

    public boolean existePersona(int idPersona) {
        personaBD = DBPersona.buscarPersonaID(idPersona);
        if (personaBD == null) {
            mostrarInfoOperacion("La persona no existe", colorAdvertencia);
            return false;
        }

        return true;
    }

    public boolean existeVivienda(int idVivienda) {
        viviendaBD = DBVivienda.buscarVivienda(idVivienda);
        if (viviendaBD == null) {
            mostrarInfoOperacion("La vivienda no existe", colorAdvertencia);
            return false;
        }

        return true;
    }

    public void cargarPaneDatosHabitante() {
        out_nombrePerRegistro.setText(personaBD.getNombre());
        out_rolRegistro.setText(habitanteBD != null ? habitanteBD.getRol() : in_comboRol.getValue());
        out_tipoVivRegistro.setText(viviendaBD.getTipo());
        out_nombreCalRegistro.setText(calleBD.getNombre());
        out_numExtRegistro.setText(String.valueOf(viviendaBD.getNum_ext()));
        out_numIntRegistro.setText(String.valueOf(viviendaBD.getNum_int()).equals("0") ? "S/N" : String.valueOf(viviendaBD.getNum_int()));
    }

    public void initPadrePane() {
        padrePane.put(pane_inicio, pane_inicio);
        padrePane.put(pane_entrada, pane_inicio);

        padrePane.put(pane_confirmacion, pane_entrada);
        padrePane.put(pane_actualizar, pane_entrada);

        padrePane.put(pane_resultadoBusqueda, pane_entrada);
    }

    public void cambiarPane(AnchorPane origen, AnchorPane destino) {
        origen.setVisible(false);
        destino.setVisible(true);
        pane_actual = destino;
    }

    public void operacionSeleccionada() {
        btn_entrada.setText(opSeleccionada.getTextoBoton());

        in_idPersona.getSelectionModel().clearSelection();
        in_idVivienda.getSelectionModel().clearSelection();
        in_comboRol.getSelectionModel().clearSelection();

        field_idPersonaActualizar.getSelectionModel().clearSelection();
        field_idViviendaActualizar.getSelectionModel().clearSelection();
        combo_rolActualizar.getSelectionModel().clearSelection();

        in_idVivienda.setVisible(opSeleccionada.ordinal() < 2);
        txt_viviendaEntrada.setVisible(opSeleccionada.ordinal() < 2);
        in_comboRol.setVisible(opSeleccionada.ordinal() < 2);
        txt_rolEntrada.setVisible(opSeleccionada.ordinal() < 2);

        cambiarPane(pane_inicio, pane_entrada);
        btn_volver.setVisible(true);
    }

    //Eventos - Creacion de habitante
    public boolean validarCamposCorrectos() {
        out_infoOperacion.fillProperty().set(colorAdvertencia);

        if (!validarIdPersona(in_idPersona)) {
            in_idPersona.requestFocus();
            return false;
        }

        if (!validarIdVivienda(in_idVivienda)) {
            in_idVivienda.requestFocus();
            return false;
        }

        if (!validarRol(in_comboRol)) {
            in_comboRol.requestFocus();
            return false;
        }

        return true;
    }

    public void operacionCrear() {
        boolean resultado = DBHabitante.insertarHabitante(in_idPersona.getValue().getIdPersona(), in_idVivienda.getValue().getId_vivienda(), in_comboRol.getValue());

        if (resultado) {
            in_idPersona.getSelectionModel().clearSelection();
            in_idVivienda.getSelectionModel().clearSelection();
            in_comboRol.getSelectionModel().clearSelection();
            mostrarInfoOperacion("Habitante creado correctamente", colorExito);
        } else {
            mostrarInfoOperacion("Error al crear el habitante. Intente de nuevo", colorAdvertencia);
        }

        cambiarPane(pane_confirmacion, pane_entrada);
    }

    //Eventos - Busqueda de habitantes
    public void operacionBuscar() {
        ObservableList<Map<String, Object>> resultadoBusqueda = null;
        Integer idPersona = null;
        Integer idVivienda = null;
        String rol = in_comboRol.getValue();

        if (in_idPersona.getValue() != null) {
            idPersona = in_idPersona.getValue().getIdPersona();
        }
        if (in_idVivienda.getValue() != null) {
            idVivienda = in_idVivienda.getValue().getId_vivienda();
        }

        resultadoBusqueda = DBHabitante.buscarHabitantes(idPersona, idVivienda, rol);

        if (resultadoBusqueda == null) {
            mostrarInfoOperacion("No se encontraron resultados", colorAdvertencia);
            return;
        } else {
            try {
                tabla_busquedaHabitante.getItems().clear();
                for (Map<String, Object> fila : resultadoBusqueda) {
                    tabla_busquedaHabitante.getItems().add(fila);
                }
            } catch (Exception e) {
                System.out.println("Error al obtener datos de la tabla: " + e.getMessage());
            }
        }

        mostrarInfoOperacion("Resultados encontrados: " + resultadoBusqueda.size(), colorExito);
        cambiarPane(pane_entrada, pane_resultadoBusqueda);
    }

    private void configurarTablaBusqueda() {
        colIdPersona.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().get("IdPersona")));

        colNombre.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().get("Nombre")));

        colRol.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().get("Rol")));

        colIdVivienda.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().get("IdVivienda")));

        colTipoVivienda.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().get("TipoVivienda")));

        colNoExt.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().get("NumExt")));

        colNoInt.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().get("NumInt")));

        colMetrosCuadrados.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().get("MetrosCuadrados")));

        colIdCalle.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().get("IdCalle")));

        colNombreCalle.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().get("Calle")));
    }

    //Eventos - Actualizar habitante
    public void operacionActualizar() {
        if (field_idPersonaActualizar.getValue() == null) {
            mostrarInfoOperacion("Debe seleccionar una persona", colorAdvertencia);
            return;
        }

        int idPersona = field_idPersonaActualizar.getValue().getIdPersona();

        if (!validarIdVivienda(field_idViviendaActualizar)) return;
        if (!validarRol(combo_rolActualizar)) return;

        int nuevaVivienda = field_idViviendaActualizar.getValue().getId_vivienda();
        String nuevoRol = combo_rolActualizar.getValue();

        boolean exito = DBHabitante.actualizarHabitante(idPersona, nuevaVivienda, nuevoRol);
        if (exito) {
            mostrarInfoOperacion("Habitante actualizado correctamente", colorExito);
        } else {
            mostrarInfoOperacion("Error al actualizar el habitante. Intente de nuevo", colorAdvertencia);
        }

        cambiarPane(pane_actualizar, pane_entrada);
    }

    public void operacionBorrar() {
        boolean exito = DBHabitante.borrarHabitante(habitanteBD.getIdPersona());
        if (exito) {
            mostrarInfoOperacion("Habitante borrado correctamente", colorExito);
        } else {
            mostrarInfoOperacion("Error al borrar el habitante. Intente de nuevo", colorAdvertencia);
        }

        cambiarPane(pane_confirmacion, pane_entrada);
    }
}
