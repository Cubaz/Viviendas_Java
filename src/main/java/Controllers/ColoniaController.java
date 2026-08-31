package Controllers;

import ObjetosBD.Colonia.JDColonia;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;


public class ColoniaController {

    private JDColonia CDB;

    /// ELEMENTOS DEL FORMULARIOS
    @FXML private TextField nom_col;

    //FUNCIONES
    //Inicializar
    @FXML void initialize(){CDB = new JDColonia();}
    //Valida la información y registra la colonia al presionar el botón
    @FXML void registrar_colonia(ActionEvent event) {validar(); }

    private void validar(){
        String nombre;
        if(nom_col.getText().isBlank()){
            System.out.println("El nombre de la colonia es un campo NECESARIO");
            nom_col.requestFocus();
            return;
        }

        nombre = nom_col.getText();
        int id = CDB.insertarColonia(nombre);
        if(id!=-1){
            System.out.println("Registro exitoso \\\\nID de usuario:" + id);
            nom_col.clear();
        }else{
            System.out.println("Error en el registro de COLONIA");
        }
    }


}
