package Controllers;

import ObjetosBD.Calle.CalleBD;
import ObjetosBD.Calle.JDCalle;
import ObjetosBD.Habitante.HabitanteBD;
import ObjetosBD.Habitante.JDHabitante;
import ObjetosBD.Persona.JDPersona;
import ObjetosBD.Persona.PersonaBD;
import ObjetosBD.Vivienda.JDVivienda;
import ObjetosBD.Vivienda.ViviendaBD;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class HabitanteController {
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

    private final Color colorAdvertencia = new Color(1.0, 1.0, 0.0, 1.0);
    private final Color colorExito = new Color(0.0, 1.0, 0.1529, 1.0);

    private PersonaBD personaBD;
    private ViviendaBD viviendaBD;
    private CalleBD calleBD;
    private HabitanteBD habitanteBD;
    
    private JDHabitante FDB;
    private JDPersona DBPersona;
    private JDVivienda DBVivienda;
    private JDCalle DBCalle;

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
    }

    @FXML
    void preguntarAsignar(ActionEvent event) throws InterruptedException {
        if(!validarCamposCorrectos()) return;

        FDB = new JDHabitante();
        DBPersona = new JDPersona();
        DBVivienda = new JDVivienda();
        DBCalle = new JDCalle();

        int idPersona = Integer.parseInt(in_idPersonaRegistro.getText());
        int idVivienda = Integer.parseInt(in_idViviendaRegistro.getText());
        String rol = in_comboRol.getValue();

        if(!consultarDatos(idPersona, idVivienda, rol)) return;

        mostrarDatos();
        invertirCamposEntrada();
        invertirCamposMostrarDatos();
    }

    @FXML
    void asignarHabitante(ActionEvent event) {
        FDB.insertarHabitante(habitanteBD.getIdPersona(), habitanteBD.getIdVivienda(), habitanteBD.getRol());

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

    @FXML
    void volverInicio(ActionEvent event) {
        //TODO: Cambiar de ventana
    }

    public boolean validarCamposCorrectos() throws InterruptedException {
        out_infoOperacion.fillProperty().set(colorAdvertencia);
        if(in_idPersonaRegistro.getText().isBlank() && in_idViviendaRegistro.getText().isBlank()){
            out_infoOperacion.setText("Debe ingresar los campos de Id de persona y vivienda");
            out_infoOperacion.setVisible(true);

            return false;
        }

        if(in_idPersonaRegistro.getText().isBlank()){
            out_infoOperacion.setText("El campo de Id de persona es necesario");
            out_infoOperacion.setVisible(true);
            in_idPersonaRegistro.requestFocus();

            return false;
        }

        if(in_idViviendaRegistro.getText().isBlank()){
            out_infoOperacion.setText("El campo de Id de vivienda es necesario");
            out_infoOperacion.setVisible(true);
            in_idViviendaRegistro.requestFocus();

            return false;
        }

        if(!in_idPersonaRegistro.getText().matches("[0-9]+")
                && !in_idViviendaRegistro.getText().matches("[0-9]+")){
            out_infoOperacion.setText("Los campos de Id de persona y vivienda deben ser numeros");
            out_infoOperacion.setVisible(true);

            return false;
        }

        if(!in_idPersonaRegistro.getText().matches("[0-9]+")){
            out_infoOperacion.setText("El campo de Id de persona debe ser un numero");
            out_infoOperacion.setVisible(true);
            in_idPersonaRegistro.requestFocus();

            return false;
        }

        if(!in_idViviendaRegistro.getText().matches("[0-9]+")){
            out_infoOperacion.setText("El campo de Id de vivienda debe ser un numero");
            out_infoOperacion.setVisible(true);
            in_idViviendaRegistro.requestFocus();

            return false;
        }

        if(in_comboRol.getValue() == null){
            out_infoOperacion.setText("Debe seleccionar un rol");
            out_infoOperacion.setVisible(true);
            in_comboRol.requestFocus();

            return false;
        }

        out_infoOperacion.setVisible(false);

        return true;
    }

    public boolean consultarDatos(int idPersona, int idVivienda, String rol){
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

        habitanteBD = new HabitanteBD(idPersona, idVivienda, in_comboRol.getValue());

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
}
