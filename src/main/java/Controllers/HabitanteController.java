package Controllers;

import ObjetosBD.Calle.CalleBD;
import ObjetosBD.Calle.JDCalle;
import ObjetosBD.Habitante.HabitanteBD;
import ObjetosBD.Habitante.JDHabitante;
import ObjetosBD.Persona.JDPersona;
import ObjetosBD.Persona.PersonaBD;
import ObjetosBD.Vivienda.JDVivienda;
import ObjetosBD.Vivienda.ViviendaBD;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

import java.util.*;


enum operacion{
    CREAR("Crear"),
    BUSCAR("Buscar"),
    BORRAR("Borrar"),
    ACTUALIZAR("Actualizar");

    private final String texto;

    operacion(String texto){
        this.texto = texto;
    }

    public String getTexto(){
        return this.texto;
    }
}

public class HabitanteController {
    //Componentes generales
    @FXML private Button btn_menuPrincipal;
    @FXML private AnchorPane pane_inicio;

    @FXML private Text out_infoOperacion;
    @FXML private AnchorPane pane_entrada;
    @FXML private TextField in_idPersona;
    @FXML private TextField in_idVivienda;
    @FXML private ComboBox<String> in_comboRol;
    @FXML private Button btn_entrada;

    @FXML private Button btn_confirmacion;
    @FXML private AnchorPane pane_confirmacion;
    @FXML private Button btn_volver;

    //JavaFX - Eventos Generales
    @FXML public void initialize() {
        pane_actual = pane_inicio;

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

        configurarTablaBusqueda();
        initPadrePane();
    }

    @FXML void elegirOpcionActualizar(ActionEvent event) {
        opSeleccionada = operacion.ACTUALIZAR;
        operacionSeleccionada();
    }

    @FXML void elegirOpcionBorrar(ActionEvent event) {
        opSeleccionada = operacion.BORRAR;
        operacionSeleccionada();
    }

    @FXML void elegirOpcionBuscar(ActionEvent event) {
        opSeleccionada = operacion.BUSCAR;
        operacionSeleccionada();
    }

    @FXML void elegirOpcionCrear(ActionEvent event) {
        opSeleccionada = operacion.CREAR;
        operacionSeleccionada();
    }

    @FXML void ejecutarOperacionEntrada(ActionEvent event) {
        switch (opSeleccionada) {
            case CREAR:
                if(!validarCamposCorrectos()) return;

                if(!existePersona(Integer.parseInt(in_idPersona.getText()))) return;
                if(!existeVivienda(Integer.parseInt(in_idVivienda.getText()))) return;
                calleBD = DBCalle.buscarCalleID(viviendaBD.getId_calle());

                cargarPaneDatosHabitante();
                btn_confirmacion.setText("Crear habitante");
                cambiarPane(pane_entrada, pane_confirmacion);
                break;

            case BUSCAR:
                tabla_busquedaHabitante.getItems().clear();
                operacionBuscar();
                break;

            case ACTUALIZAR:
                break;

            case BORRAR:
                break;
        }
    }

    @FXML void volverMenuPrincipal(ActionEvent event) {
        //TODO: Cambiar de ventana
    }

    @FXML void volverVentanaAnterior(ActionEvent event) {
        cambiarPane(pane_actual, padrePane.get(pane_actual));
        out_infoOperacion.setVisible(false);

        if (pane_actual == pane_inicio) btn_volver.setVisible(false);
    }

    @FXML void confirmacionEjecutarOperacion(ActionEvent event) {
        switch (opSeleccionada) {
            case CREAR:
                operacionCrear();
                break;

            case ACTUALIZAR:
                break;

            case BORRAR:
                break;
        }
    }

    //JavaFX - Crear habitante
    @FXML private TextField out_nombrePerRegistro;
    @FXML private TextField out_rol;
    @FXML private TextField out_rolRegistro;
    @FXML private TextField out_nombreCalRegistro;
    @FXML private TextField out_numExtRegistro;
    @FXML private TextField out_numIntRegistro;
    @FXML private TextField out_tipoVivRegistro;

    //JavaFX - Busqueda de habitantes
    @FXML private AnchorPane pane_resultadoBusqueda;
    @FXML private TableView<Map<String, Object>> tabla_busquedaHabitante;
    @FXML private TableColumn<Map<String, Object>, Object> colIdPersona;
    @FXML private TableColumn<Map<String, Object>, Object> colNombre;
    @FXML private TableColumn<Map<String, Object>, Object> colRol;
    @FXML private TableColumn<Map<String, Object>, Object> colIdVivienda;
    @FXML private TableColumn<Map<String, Object>, Object> colTipoVivienda;
    @FXML private TableColumn<Map<String, Object>, Object> colNoExt;
    @FXML private TableColumn<Map<String, Object>, Object> colNoInt;
    @FXML private TableColumn<Map<String, Object>, Object> colMetrosCuadrados;
    @FXML private TableColumn<Map<String, Object>, Object> colIdCalle;
    @FXML private TableColumn<Map<String, Object>, Object> colNombreCalle;

    //Componentes generales
    private final Color colorAdvertencia = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    private final Color colorExito = new Color(0.0f, 1.0f, 0.1529f, 1.0f);
    private final Color colorBlanco = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private operacion opSeleccionada;
    private String mensajeOperacion;

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
    public void mostrarInfoOperacion() {
        out_infoOperacion.setText(this.mensajeOperacion);
        out_infoOperacion.setVisible(true);
    }

    public boolean validarIdPersona() {
        out_infoOperacion.fillProperty().set(colorAdvertencia);

        if (in_idPersona.getText().isBlank()) {
            this.mensajeOperacion = "El campo de Id de persona es necesario";
            mostrarInfoOperacion();
            return false;
        }

        if (!in_idPersona.getText().matches("[0-9]+")) {
            this.mensajeOperacion = "El campo de Id de persona debe ser un numero";
            mostrarInfoOperacion();
            return false;
        }

        return true;
    }

    public boolean validarIdVivienda() {
        out_infoOperacion.fillProperty().set(colorAdvertencia);

        if (in_idVivienda.getText().isBlank()) {
            this.mensajeOperacion = "El campo de Id de vivienda es necesario";
            mostrarInfoOperacion();
            return false;
        }

        if (!in_idVivienda.getText().matches("[0-9]+")) {
            this.mensajeOperacion = "El campo de Id de vivienda debe ser un numero";
            mostrarInfoOperacion();
            return false;
        }

        return true;
    }

    public boolean validarRol() {
        out_infoOperacion.fillProperty().set(colorAdvertencia);

        if (in_comboRol.getValue() == null) {
            this.mensajeOperacion = "El campo de rol es necesario";
            mostrarInfoOperacion();
            return false;
        }

        return true;
    }

    public boolean existePersona(int idPersona) {
        personaBD = DBPersona.buscarPersonaID(idPersona);
        if (personaBD == null) {
            mensajeOperacion = "La persona no existe";
            mostrarInfoOperacion();
            return false;
        }

        return true;
    }

    public boolean existeVivienda(int idVivienda) {
        viviendaBD = DBVivienda.buscarVivienda(idVivienda);
        if (viviendaBD == null) {
            mensajeOperacion = "La vivienda no existe";
            mostrarInfoOperacion();
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

        padrePane.put(pane_resultadoBusqueda, pane_entrada);
    }

    public void cambiarPane(AnchorPane origen, AnchorPane destino) {
        origen.setVisible(false);
        destino.setVisible(true);
        pane_actual = destino;
    }

    public void operacionSeleccionada() {
        btn_entrada.setText(opSeleccionada.getTexto());

        in_idPersona.clear();
        in_idVivienda.clear();
        in_comboRol.getSelectionModel().clearSelection();

        cambiarPane(pane_inicio, pane_entrada);
        btn_volver.setVisible(true);
    }

    //Eventos - Creacion de habitante
    public boolean validarCamposCorrectos() {
        out_infoOperacion.fillProperty().set(colorAdvertencia);

        if (!validarIdPersona()) {
            in_idPersona.requestFocus();
            return false;
        }

        if (!validarIdVivienda()) {
            in_idVivienda.requestFocus();
            return false;
        }

        if (!validarRol()) {
            in_comboRol.requestFocus();
            return false;
        }

        return true;
    }

    public void operacionCrear() {
        boolean resultado = DBHabitante.insertarHabitante(Integer.parseInt(in_idPersona.getText()), Integer.parseInt(in_idVivienda.getText()), in_comboRol.getValue());

        if(resultado) {
            mensajeOperacion = "Habitante creado correctamente";
            out_infoOperacion.fillProperty().set(colorExito);

            in_idPersona.clear();
            in_idVivienda.clear();
            in_comboRol.getSelectionModel().clearSelection();
            mostrarInfoOperacion();
        } else {
            mensajeOperacion = "Error al crear el habitante. Intente de nuevo";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
        }

        cambiarPane(pane_confirmacion, pane_entrada);
    }

    //Eventos - Busqueda de habitantes
    public void operacionBuscar() {
        if (!in_idPersona.getText().isBlank() && !in_idPersona.getText().matches("[0-9]+")) {
            this.mensajeOperacion = "El campo de Id de persona debe ser un numero";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        if (!in_idVivienda.getText().isBlank() && !in_idVivienda.getText().matches("[0-9]+")) {
            this.mensajeOperacion = "El campo de Id de vivienda debe ser un numero";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        ObservableList<Map<String, Object>> resultadoBusqueda = null;
        Integer idPersona = null;
        Integer idVivienda = null;
        String rol = in_comboRol.getValue();

        if (validarIdPersona()) {
            idPersona = Integer.parseInt(in_idPersona.getText());
            if (!existePersona(idPersona)) return;
        }
        if (validarIdVivienda()) {
            idVivienda = Integer.parseInt(in_idVivienda.getText());
            if (!existeVivienda(idVivienda)) return;

        }

        resultadoBusqueda = DBHabitante.buscarHabitantes(idPersona, idVivienda, rol);

        if (resultadoBusqueda == null) {
            mensajeOperacion = "No se encontraron resultados";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        } else {
            try {
                mensajeOperacion = "Resultados encontrados: " + resultadoBusqueda.size();
                out_infoOperacion.fillProperty().set(colorBlanco);

                tabla_busquedaHabitante.getItems().clear();
                for (Map<String, Object> fila : resultadoBusqueda) {
                    tabla_busquedaHabitante.getItems().add(fila);
                }
            } catch (Exception e) {
                System.out.println("Error al obtener datos de la tabla: " + e.getMessage());
            }
        }

        mostrarInfoOperacion();
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
}
