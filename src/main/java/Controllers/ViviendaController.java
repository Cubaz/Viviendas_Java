package Controllers;

import ObjetosBD.Colonia.JDColonia;
import ObjetosBD.Departamento.DepartamentoBD;
import ObjetosBD.Departamento.JDDepartamento;
import ObjetosBD.Vivienda.JDVivienda;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class ViviendaController {

    private JDVivienda VDB;
//    private JDCalle CDB;
//    private JDEdificio EDB;
    private JDDepartamento DDB;

    @FXML void initialize(){
        VDB = new JDVivienda();
//        CDB = new JDCalle();
//        EDB = new JDEdificio();
//        DDB = new JDDepartamento();
//        edificio.setItems(EDB.obtenerEdificio());
//        calle.setItems(CDB.obtenerCalle());
        vivienda.getItems().add("Unifamiliar");
        vivienda.getItems().add("Edificio");
        }

    @FXML private ChoiceBox<?> calle;
    @FXML private TextField piso;
    @FXML private ChoiceBox<?> edificio;
    @FXML private TextField habitantes;
    @FXML private TextField mts_cuadrados;
    @FXML private TextField num_ext;
    @FXML private TextField num_int;
    @FXML private ChoiceBox<String> vivienda;
    @FXML void registrar_vivienda(ActionEvent event) {validar();}

    private void validar(){

        String tipo;
        int num_calle, num_hab, numext, numint, mtscuadrados, num_edificio = 0, num_piso = 0;


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

        if(vivienda.getValue().equals("Edificio")){
            edificio.setDisable(false);
            piso.setDisable(false);

            if(edificio.getValue() == null){
                System.out.println("Es necesario indicar el edificio de la vivienda");
                edificio.requestFocus();
                return;
            }else{
                num_edificio = Integer.parseInt(edificio.getValue().toString());
            }

            if(piso.getText().isBlank()){
                System.out.println("Es necesario indicar el piso de la vivienda");
                piso.requestFocus();
                return;
            }else{
                num_piso = Integer.parseInt(piso.getText());
            }
        }

        tipo = vivienda.getValue();
        num_hab = Integer.parseInt(habitantes.getText());
        numext = Integer.parseInt(num_ext.getText());
        numint = Integer.parseInt(num_int.getText());
        num_calle = Integer.parseInt(calle.getValue().toString());
        mtscuadrados = Integer.parseInt(mts_cuadrados.getText());


        int id = VDB.insertarVivienda(tipo, num_hab, numext, numint, num_calle, mtscuadrados);
            if (id != -1) {
                System.out.println("Registro exitoso \\nID de usuario: " + id);
                if(tipo.equals("Edificio")){
                    int ide = DDB.insertarDepartamento(id , num_edificio, num_piso);
                    if(ide != -1){
                        System.out.println("Registro de departamento exitoso \\nID de edificio: " + ide);
                    }else{
                        System.out.println("Error en el registro de DEPARTAMENTO");
                    }
                }
            } else {
                System.out.println("Error en el registro de VIVIENDA");
            }




    }



}
