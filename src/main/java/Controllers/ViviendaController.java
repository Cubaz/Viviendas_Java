package Controllers;

import ObjetosBD.Calle.CalleBD;
import ObjetosBD.Calle.JDCalle;
import ObjetosBD.Departamento.DepartamentoBD;
import ObjetosBD.Departamento.JDDepartamento;
import ObjetosBD.Edificio.EdificioBD;
import ObjetosBD.Edificio.JDEdificio;
import ObjetosBD.Persona.JDPersona;
import ObjetosBD.Persona.PersonaBD;
import ObjetosBD.Propietario.JDPropietario;
import ObjetosBD.Propietario.PropietarioBD;
import ObjetosBD.Vivienda.JDVivienda;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ViviendaController {

    private JDVivienda VDB;
    private JDCalle CDB;
    private JDEdificio EDB;
    private JDDepartamento DDB;
    private JDPersona PDB;
    private JDPropietario PRDB;

    @FXML private ComboBox<CalleBD> calle;
    @FXML private ComboBox<EdificioBD> edificio;
    @FXML private ComboBox<PersonaBD> propietario;
    @FXML private TextField piso;
    @FXML private ComboBox<String> vivienda;
    @FXML private TextField habitantes;
    @FXML private TextField mts_cuadrados;
    @FXML private TextField num_ext;
    @FXML private TextField num_int;
    @FXML void registrar_vivienda(ActionEvent event) {validar();}



    @FXML
    void initialize() {
        VDB = new JDVivienda();
        CDB = new JDCalle();
        EDB = new JDEdificio();
        DDB = new JDDepartamento();
        PDB = new JDPersona();
        PRDB = new JDPropietario();

        edificio.setItems(EDB.obtenerEdificio());
        calle.setItems(CDB.obtenerCalle());
        propietario.setItems(PDB.obtenerPersona());
        vivienda.getItems().addAll("Unifamiliar", "Departamento");


        edificio.setDisable(true);
        piso.setDisable(true);


        vivienda.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean esDepto = "Departamento".equals(newVal);
            edificio.setDisable(!esDepto);
            piso.setDisable(!esDepto);
        });
    }



    private void validar(){

        String tipo;
        int num_hab, numext, numint, mtscuadrados, num_piso = 0;


        if(vivienda.getValue() == null){
            System.out.println("Es necesario seleccionar el tipo de vivienda");
            vivienda.requestFocus();
            return;
        }

        if(calle.getValue() == null){
            System.out.println("Es necesario seleccionar una calle");
            calle.requestFocus();
            return;
        }

        if(habitantes.getText().isBlank()){
            System.out.println("Es necesario especificar el número");
            habitantes.requestFocus();
            return;
        }

        if(num_ext.getText().isBlank()){
            System.out.println("Es necesario indicar el número exterior");
            num_ext.requestFocus();
            return;
        }

        if(num_int.getText().isBlank()){
            System.out.println("Es necesario indicar el número interior");
            num_int.requestFocus();
            return;
        }

        if(mts_cuadrados.getText().isBlank()){
            System.out.println("Es necesario indicar los metros cuadrados construidos");
            mts_cuadrados.requestFocus();
            return;
        }

        if(vivienda.getValue().equals("Departamento")){
            edificio.setDisable(true);
            piso.setDisable(true);

            if(edificio.getValue() == null){
                System.out.println("Es necesario indicar el edificio de la vivienda");
                edificio.requestFocus();
                return;
            }

            if(piso.getText().isBlank()){
                System.out.println("Es necesario indicar el piso de la vivienda");
                piso.requestFocus();
                return;
            }else {
                num_piso = Integer.parseInt(piso.getText());
            }
        }

        tipo = vivienda.getValue();
        CalleBD Calle = calle.getValue();
        int idCalle = Calle.getId_calle();

        num_hab = Integer.parseInt(habitantes.getText());
        numext = Integer.parseInt(num_ext.getText());
        numint = Integer.parseInt(num_int.getText());
        mtscuadrados = Integer.parseInt(mts_cuadrados.getText());


        int id = VDB.insertarVivienda(tipo, num_hab, numext, numint, idCalle, mtscuadrados);
            if (id != -1) {
                System.out.println("Registro exitoso \\nID de usuario: " + id);
                if(tipo.equals("Departamento")){
                    EdificioBD Depa = edificio.getValue();
                    int idEdificio = Depa.getIdEdificio();
                    int ide = DDB.insertarDepartamento(idEdificio, id, num_piso);
                    if(ide != -1){
                        System.out.println("Registro de departamento exitoso \\nID de edificio: " + ide);
                    }else{
                        System.out.println("Error en el registro de DEPARTAMENTO");
                    }
                }
            } else {
                System.out.println("Error en el registro de VIVIENDA");
            }

        PersonaBD Duenio = propietario.getValue();
        int idPropietario = Duenio.getIdPersona();
        int ideP = PRDB.insertarPropietario(id, idPropietario);
        if(ideP !=-1){
            System.out.println("Registro de propietario exitoso");
        }else{
            System.out.println("Error en el registro de PROPIETARIO");
        }
    }



}
