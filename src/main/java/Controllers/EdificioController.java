package Controllers;

import ObjetosBD.Edificio.JDEdificio;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;

public class EdificioController {
    private JDEdificio FDB;

    @FXML private TextField edificio_nombre;

    @FXML void initialize(){FDB = new JDEdificio();}
    @FXML void registrarEdificio(ActionEvent event){ validar();}

    private void validar(){
        String nombre = edificio_nombre.getText();
        if(edificio_nombre.getText().isBlank()){
            System.out.println("El nombre es un campo necesario");
            edificio_nombre.requestFocus();
            return;
        }

        int id = FDB.insertarEdificio(nombre);
        if(id != -1){
            System.out.println("Registro exitoso \\nID del edificio:" + id);
            edificio_nombre.clear();
        }else{
            System.out.println("Error en el registro de EDIFICIO");
        }
    }
}
