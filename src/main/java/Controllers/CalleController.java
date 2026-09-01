package Controllers;

import ObjetosBD.Calle.JDCalle;
import ObjetosBD.Colonia.ColoniaBD;
import ObjetosBD.Colonia.JDColonia;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class CalleController {
    private JDCalle FDC;
    private JDColonia DBColonia;

    @FXML private TextField nom_calle;
    @FXML private ComboBox<ColoniaBD> combo_colonia;

    @FXML
    void initialize(){
        FDC = new JDCalle();
        DBColonia = new JDColonia();
        combo_colonia.setItems(DBColonia.obtenerColonias());
    }

    @FXML
    void registrarCalle(ActionEvent event){
        validar();
    }

    private void validar(){
        if(nom_calle.getText().isBlank()){
            System.out.println("El nombre de la calle es necesario");
            nom_calle.requestFocus();
            return;
        }
        if(combo_colonia.getValue() == null){
            System.out.println("El numero de colonia es necesario");
            combo_colonia.requestFocus();
            return;
        }

        String nombreCalle = nom_calle.getText();

        ColoniaBD coloniaSeleccionada = combo_colonia.getValue();
        int IdColonia = coloniaSeleccionada.getId_colonia();

        int id= FDC.insertarCalle(nombreCalle, IdColonia);
        if(id != -1){
            System.out.println("Registro exitoso \\nID de asignado:" + id);

        }else{
            System.out.println("Error en el registro de CALLE");
        }
    }
}
