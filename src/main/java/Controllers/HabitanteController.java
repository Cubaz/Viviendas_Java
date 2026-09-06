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
    CREAR,
    BUSCAR,
    BORRAR,
    ACTUALIZAR;
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
    @FXML private Button btn_ejecutarOpcion;

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
        operacionSeleccionada("Actualizar");
    }
    @FXML void elegirOpcionBorrar(ActionEvent event) {
        opSeleccionada = operacion.BORRAR;
        operacionSeleccionada("Borrar");
    }
    @FXML void elegirOpcionBuscar(ActionEvent event) {
        opSeleccionada = operacion.BUSCAR;
        operacionSeleccionada("Buscar");
    }
    @FXML void elegirOpcionCrear(ActionEvent event) {
        opSeleccionada = operacion.CREAR;
        operacionSeleccionada("Crear");
    }
    @FXML void ejecutarOpcion(ActionEvent event) {
        switch (opSeleccionada){
            case BUSCAR:
                tabla_busquedaHabitante.getItems().clear();
                operacionBuscar();
                break;
            default:
        }
    }

    @FXML void volverMenuPrincipal(ActionEvent event) {
        //TODO: Cambiar de ventana
    }

    @FXML void volverVentanaAnterior(ActionEvent event) {
        pane_actual.setVisible(false);
        padrePane.get(pane_actual).setVisible(true);
        pane_actual = padrePane.get(pane_actual);
        out_infoOperacion.setVisible(false);

        if(pane_actual == pane_inicio) btn_volver.setVisible(false);
    }

    //JavaFX - Crear habitante
    @FXML private Button btn_cancelarAsignar;
    @FXML private Button btn_confirmarAsignar;
    @FXML private Button btn_preguntarAsignar;
    @FXML private TextField out_nombrePerRegistro;
    @FXML private TextField out_rol;
    @FXML private TextField out_rolRegistro;
    @FXML private TextField out_nombreCalRegistro;
    @FXML private TextField out_numExtRegistro;
    @FXML private TextField out_numIntRegistro;
    @FXML private TextField out_tipoVivRegistro;
    @FXML private Button btn_asignarOtra;
    @FXML private Button btn_volverInicio;
    @FXML private Text txt_idPersona;
    @FXML private Text txt_idVivienda;

    @FXML private Text txt_rol;
    @FXML private Text txt_datosPersona;
    @FXML private Separator separadorPersona;
    @FXML private Text txt_nombrePerRegistro;
    @FXML private Text txt_rolRegistro;
    @FXML private Text txt_datosVivienda;
    @FXML private Separator separadorVivienda;
    @FXML private Text txt_nombreCalRegistro;
    @FXML private Text txt_tipoVivRegistro;
    @FXML private Text txt_numExtRegistro;
    @FXML private Text txt_numIntRegistro;
    @FXML private Text txt_confirmacion;

    //JavaFX - Eventos Crear habitante
    @FXML void asignarHabitante(ActionEvent event) {
        DBHabitante.insertarHabitante(habitanteBD.getIdPersona(), habitanteBD.getIdVivienda(), habitanteBD.getRol());

        out_infoOperacion.setText("Habitante asignado correctamente");

        invertirCamposMostrarDatos();

        //TODO: Cambiar de ventana (falta incluir panes en viviendas)
        mensajeOperacion = "Habitante asignado correctamente";
        mostrarInfoOperacion();
    }

    @FXML void cancelarAsignar(ActionEvent event) {
        in_idPersona.clear();
        in_idVivienda.clear();
        in_comboRol.setValue(null);
        invertirCamposMostrarDatos();
        pane_entrada.setVisible(false);
    }

    @FXML void asignarOtraVivienda(ActionEvent event) {
        in_idPersona.clear();
        in_idVivienda.clear();
        in_comboRol.setValue(null);
        //TODO: Cambiar de ventana (falta incluir panes en viviendas)
        pane_entrada.setVisible(true);
    }

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

    private JDHabitante DBHabitante = new JDHabitante();
    private JDPersona DBPersona = new JDPersona();
    private JDVivienda DBVivienda = new JDVivienda();
    private JDCalle DBCalle = new JDCalle();
    private Map<AnchorPane, AnchorPane> padrePane = new HashMap<>();
    private AnchorPane pane_actual;

    //Eventos generales
    public void mostrarInfoOperacion(){
        out_infoOperacion.setText(this.mensajeOperacion);
        out_infoOperacion.setVisible(true);
    }

    public boolean validarIdPersona(){
        out_infoOperacion.fillProperty().set(colorAdvertencia);

        if(in_idPersona.getText().isBlank()){
            this.mensajeOperacion = "El campo de Id de persona es necesario";

            return false;
        }

        if(!in_idPersona.getText().matches("[0-9]+")){
            this.mensajeOperacion = "El campo de Id de persona debe ser un numero";

            return false;
        }

        return true;
    }

    public boolean validarIdVivienda(){
        out_infoOperacion.fillProperty().set(colorAdvertencia);

        if(in_idVivienda.getText().isBlank()){
            this.mensajeOperacion = "El campo de Id de vivienda es necesario";

            return false;
        }

        if(!in_idVivienda.getText().matches("[0-9]+")){
            this.mensajeOperacion = "El campo de Id de vivienda debe ser un numero";

            return false;
        }

        return true;
    }

    public boolean validarRol(){
        out_infoOperacion.fillProperty().set(colorAdvertencia);

        if(in_comboRol.getValue() == null){
            this.mensajeOperacion = "El campo de rol es necesario";
            return false;
        }

        return true;
    }

    public boolean verificarExistenciaPersona(int idPersona){
        personaBD = DBPersona.buscarPersonaID(idPersona);
        if(personaBD == null){
            mensajeOperacion = "La persona no existe";
            mostrarInfoOperacion();
            return false;
        }

        return true;
    }

    public boolean verificarExistenciaVivienda(int idVivienda){
        viviendaBD = DBVivienda.buscarVivienda(idVivienda);
        if(viviendaBD == null){
            mensajeOperacion = "La vivienda no existe";
            mostrarInfoOperacion();
            return false;
        }

        return true;
    }

    public void cargarPaneDatosHabitante() {
        out_nombrePerRegistro.setText(personaBD.getNombre());
        out_rolRegistro.setText(habitanteBD.getRol());
        out_tipoVivRegistro.setText(viviendaBD.getTipo());
        out_nombreCalRegistro.setText(calleBD.getNombre());
        out_numExtRegistro.setText(String.valueOf(viviendaBD.getNum_ext()));
        out_numIntRegistro.setText(String.valueOf(viviendaBD.getNum_int()).equals("0") ? "S/N" : String.valueOf(viviendaBD.getNum_int()));
    }

    public void initPadrePane() {
        padrePane.put(pane_inicio, pane_inicio);
        padrePane.put(pane_entrada, pane_inicio);
        padrePane.put(pane_resultadoBusqueda, pane_entrada);
    }

    public void operacionSeleccionada(String texto){
        pane_inicio.setVisible(false);
        pane_entrada.setVisible(true);
        btn_volver.setVisible(true);
        pane_actual = pane_entrada;
        btn_ejecutarOpcion.setText(texto);
    }

    //Eventos - Creacion de habitante
    public boolean validarCamposCorrectos() {
        out_infoOperacion.fillProperty().set(colorAdvertencia);

        if(!validarIdPersona()){
            mostrarInfoOperacion();
            in_idPersona.requestFocus();
            return false;
        }

        if(!validarIdVivienda()){
            mostrarInfoOperacion();
            in_idVivienda.requestFocus();
            return false;
        }

        if(!validarRol()){
            mostrarInfoOperacion();
            in_comboRol.requestFocus();
            return false;
        }

        return true;
    }

    //Eventos - Busqueda de habitantes
    public void operacionBuscar(){


        if(!in_idPersona.getText().isBlank() && !in_idPersona.getText().matches("[0-9]+")){
            this.mensajeOperacion = "El campo de Id de persona debe ser un numero";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        if(!in_idVivienda.getText().isBlank() && !in_idVivienda.getText().matches("[0-9]+")){
            this.mensajeOperacion = "El campo de Id de vivienda debe ser un numero";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        ObservableList<Map<String, Object>> resultadoBusqueda = null;
        Integer idPersona = null;
        Integer idVivienda = null;
        String rol = in_comboRol.getValue();

        if(validarIdPersona()){
            idPersona = Integer.parseInt(in_idPersona.getText());
            if(!verificarExistenciaPersona(idPersona)) return;
        }
        if(validarIdVivienda()){
            idVivienda = Integer.parseInt(in_idVivienda.getText());
            if(!verificarExistenciaVivienda(idVivienda)) return;

        }

        resultadoBusqueda = DBHabitante.buscarHabitantes(idPersona, idVivienda, rol);

        if(resultadoBusqueda == null){
            mensajeOperacion = "No se encontraron resultados";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }
        else{
            try{
                mensajeOperacion = "Resultados encontrados: " + resultadoBusqueda.size();
                out_infoOperacion.fillProperty().set(colorBlanco);

                tabla_busquedaHabitante.getItems().clear();
                for(Map<String, Object> fila : resultadoBusqueda){
                    tabla_busquedaHabitante.getItems().add(fila);
                }
            }
            catch(Exception e){
                System.out.println("Error al obtener datos de la tabla: " + e.getMessage());
            }
        }

        mostrarInfoOperacion();
        pane_actual = pane_resultadoBusqueda;
        pane_resultadoBusqueda.setVisible(true);
        pane_entrada.setVisible(false);
    }

    public void invertirCamposMostrarDatos(){
        txt_datosPersona.setVisible(!txt_datosPersona.isVisible());
        separadorPersona.setVisible(!separadorPersona.isVisible());
        txt_nombrePerRegistro.setVisible(!txt_nombrePerRegistro.isVisible());
        txt_rolRegistro.setVisible(!txt_rolRegistro.isVisible());
        txt_datosVivienda.setVisible(!txt_datosVivienda.isVisible());
        separadorVivienda.setVisible(!separadorVivienda.isVisible());
        txt_nombreCalRegistro.setVisible(!txt_nombreCalRegistro.isVisible());
        txt_tipoVivRegistro.setVisible(!txt_tipoVivRegistro.isVisible());
        txt_numExtRegistro.setVisible(!txt_numExtRegistro.isVisible());
        txt_numIntRegistro.setVisible(!txt_numIntRegistro.isVisible());
        txt_confirmacion.setVisible(!txt_confirmacion.isVisible());

        btn_cancelarAsignar.setVisible(!btn_cancelarAsignar.isVisible());
        btn_confirmarAsignar.setVisible(!btn_confirmarAsignar.isVisible());
        out_nombrePerRegistro.setVisible(!out_nombrePerRegistro.isVisible());
        out_rolRegistro.setVisible(!out_rolRegistro.isVisible());
        out_nombreCalRegistro.setVisible(!out_nombreCalRegistro.isVisible());
        out_numExtRegistro.setVisible(!out_numExtRegistro.isVisible());
        out_numIntRegistro.setVisible(!out_numIntRegistro.isVisible());
        out_tipoVivRegistro.setVisible(!out_tipoVivRegistro.isVisible());

        btn_cancelarAsignar.setDisable(!btn_cancelarAsignar.isDisable());
        btn_confirmarAsignar.setDisable(!btn_confirmarAsignar.isDisable());
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
