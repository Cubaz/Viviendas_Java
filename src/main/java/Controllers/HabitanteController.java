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

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Map;

public class HabitanteController {
    //JavaFX - Crear habitante
    @FXML private Button btn_cancelarAsignar;
    @FXML private Button btn_confirmarAsignar;
    @FXML private Button btn_preguntarAsignar;
    @FXML private TextField in_idPersonaRegistro;
    @FXML private TextField in_idViviendaRegistro;
    @FXML private Text out_infoOperacion;
    @FXML private TextField out_nombrePerRegistro;
    @FXML private TextField out_rol;
    @FXML private TextField out_rolRegistro;
    @FXML private TextField out_nombreCalRegistro;
    @FXML private TextField out_numExtRegistro;
    @FXML private TextField out_numIntRegistro;
    @FXML private TextField out_tipoVivRegistro;
    @FXML private Button btn_asignarOtra;
    @FXML private Button btn_volverInicio;
    @FXML private ComboBox<String> in_comboRol;
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

    //JavaFX - Busqueda de habitantes
    @FXML private Button btn_buscar;
    @FXML private Button btn_buscarOtro;
    @FXML private AnchorPane pane_entradaBusqueda;
    @FXML private AnchorPane pane_tablaBusqueda;
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

    private final Color colorAdvertencia = new Color(1.0f, 1.0f, 0.0f, 1.0f);
    private final Color colorExito = new Color(0.0f, 1.0f, 0.1529f, 1.0f);
    private final Color colorBlanco = new Color(1.0f, 1.0f, 1.0f, 1.0f);
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

    @FXML
    public void initialize() {
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
    }

    //JavaFX - Eventos Crear habitante
    @FXML
    void preguntarAsignar(ActionEvent event) {
        if(!validarCamposCorrectos()) return;

        int idPersona = Integer.parseInt(in_idPersonaRegistro.getText());
        int idVivienda = Integer.parseInt(in_idViviendaRegistro.getText());
        String rol = in_comboRol.getValue();

        if(!consultarDatosHabitante(idPersona, idVivienda, rol)) return;

        mostrarDatos();
        invertirCamposEntrada();
        invertirCamposMostrarDatos();
    }

    @FXML
    void asignarHabitante(ActionEvent event) {
        DBHabitante.insertarHabitante(habitanteBD.getIdPersona(), habitanteBD.getIdVivienda(), habitanteBD.getRol());

        out_infoOperacion.setText("Habitante asignado correctamente");

        invertirCamposMostrarDatos();
        invertirCamposFinales();
    }

    @FXML
    void cancelarAsignar(ActionEvent event) {
        in_idPersonaRegistro.clear();
        in_idViviendaRegistro.clear();
        in_comboRol.setValue(null);
        invertirCamposMostrarDatos();
        invertirCamposEntrada();
    }

    @FXML
    void asignarOtraVivienda(ActionEvent event) {
        in_idPersonaRegistro.clear();
        in_idViviendaRegistro.clear();
        in_comboRol.setValue(null);
        invertirCamposFinales();
        invertirCamposEntrada();
    }

    //JavaFX - Eventos Busqueda de habitantes
    @FXML
    void buscarHabitante(ActionEvent event) {
        if(!in_idPersonaRegistro.getText().isBlank() && !in_idPersonaRegistro.getText().matches("[0-9]+")){
            this.mensajeOperacion = "El campo de Id de persona debe ser un numero";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        if(!in_idViviendaRegistro.getText().isBlank() && !in_idViviendaRegistro.getText().matches("[0-9]+")){
            this.mensajeOperacion = "El campo de Id de vivienda debe ser un numero";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
            mostrarInfoOperacion();
            return;
        }

        ObservableList<Map<String, Object>> resultadoBusqueda = null;
        Integer idPersona = null;
        Integer idVivienda = null;
        String rol = null;

        if(validarIdPersona()) idPersona = Integer.parseInt(in_idPersonaRegistro.getText());
        if(validarIdVivienda()) idVivienda = Integer.parseInt(in_idViviendaRegistro.getText());
        if(validarRol()) rol = in_comboRol.getValue();

        resultadoBusqueda = DBHabitante.buscarHabitantes(idPersona, idVivienda, rol);

        if(resultadoBusqueda == null){
            mensajeOperacion = "No se encontraron resultados";
            out_infoOperacion.fillProperty().set(colorAdvertencia);
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
        pane_tablaBusqueda.setVisible(true);
        pane_entradaBusqueda.setVisible(false);
    }

    @FXML
    void buscarOtroHabitante(ActionEvent event) {
        in_idPersonaRegistro.clear();
        in_idViviendaRegistro.clear();
        in_comboRol.setValue(null);
        tabla_busquedaHabitante.getItems().clear();

        out_infoOperacion.setVisible(false);
        pane_tablaBusqueda.setVisible(false);
        pane_entradaBusqueda.setVisible(true);
    }

    //JavaFX - Eventos Generales
    @FXML
    void volverInicio(ActionEvent event) {
        //TODO: Cambiar de ventana
    }

    public boolean validarCamposCorrectos() {
        out_infoOperacion.fillProperty().set(colorAdvertencia);

        if(!validarIdPersona()){
            mostrarInfoOperacion();
            in_idPersonaRegistro.requestFocus();
            return false;
        }

        if(!validarIdVivienda()){
            mostrarInfoOperacion();
            in_idViviendaRegistro.requestFocus();
            return false;
        }

        if(!validarRol()){
            mostrarInfoOperacion();
            in_comboRol.requestFocus();
            return false;
        }

        return true;
    }

    public boolean validarIdPersona(){
        out_infoOperacion.fillProperty().set(colorAdvertencia);

        if(in_idPersonaRegistro.getText().isBlank()){
            this.mensajeOperacion = "El campo de Id de persona es necesario";

            return false;
        }

        if(!in_idPersonaRegistro.getText().matches("[0-9]+")){
            this.mensajeOperacion = "El campo de Id de persona debe ser un numero";

            return false;
        }

        return true;
    }

    public boolean validarIdVivienda(){
        out_infoOperacion.fillProperty().set(colorAdvertencia);

        if(in_idViviendaRegistro.getText().isBlank()){
            this.mensajeOperacion = "El campo de Id de vivienda es necesario";

            return false;
        }

        if(!in_idViviendaRegistro.getText().matches("[0-9]+")){
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

    public void mostrarInfoOperacion(){
        out_infoOperacion.setText(this.mensajeOperacion);
        out_infoOperacion.setVisible(true);
    }

    public boolean consultarDatosHabitante(int idPersona, int idVivienda, String rol){
        personaBD = DBPersona.obtenerPersona(idPersona);
        if(personaBD == null){
            System.out.println("La persona no existe");
            return false;
        }

        viviendaBD = DBVivienda.obtenerVivienda(idVivienda);
        if(viviendaBD == null){
            System.out.println("La vivienda no existe");
            return false;
        }

        calleBD = DBCalle.obtenerCalle(viviendaBD.getId_calle());
        if(calleBD == null){
            System.out.println("La calle no existe");
            return false;
        }

        habitanteBD = new HabitanteBD(idPersona, idVivienda, rol);

        return true;
    }

    public void invertirCamposEntrada(){
        txt_idPersona.setVisible(!txt_idPersona.isVisible());
        txt_idVivienda.setVisible(!txt_idVivienda.isVisible());
        txt_rol.setVisible(!txt_rol.isVisible());

        btn_preguntarAsignar.setVisible(!btn_preguntarAsignar.isVisible());
        in_idPersonaRegistro.setVisible(!in_idPersonaRegistro.isVisible());
        in_idViviendaRegistro.setVisible(!in_idViviendaRegistro.isVisible());
        in_comboRol.setVisible(!in_comboRol.isVisible());

        btn_preguntarAsignar.setDisable(!btn_preguntarAsignar.isDisable());
        in_idPersonaRegistro.setDisable(!in_idPersonaRegistro.isDisable());
        in_idViviendaRegistro.setDisable(!in_idViviendaRegistro.isDisable());
        in_comboRol.setDisable(!in_comboRol.isDisable());
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

    public void invertirCamposFinales(){
        out_infoOperacion.fillProperty().set(colorExito);
        out_infoOperacion.setVisible(!out_infoOperacion.isVisible());
        btn_asignarOtra.setVisible(!btn_asignarOtra.isVisible());
        btn_volverInicio.setVisible(!btn_volverInicio.isVisible());

        btn_asignarOtra.setDisable(!btn_asignarOtra.isDisable());
        btn_volverInicio.setDisable(!btn_volverInicio.isDisable());
    }

    public void mostrarDatos() {
        out_nombrePerRegistro.setText(personaBD.getNombre());
        out_rolRegistro.setText(habitanteBD.getRol());
        out_tipoVivRegistro.setText(viviendaBD.getTipo());
        out_nombreCalRegistro.setText(calleBD.getNombre());
        out_numExtRegistro.setText(String.valueOf(viviendaBD.getNum_ext()));
        out_numIntRegistro.setText(String.valueOf(viviendaBD.getNum_int()).equals("0") ? "S/N" : String.valueOf(viviendaBD.getNum_int()));
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
