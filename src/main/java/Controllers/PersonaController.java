package Controllers;

import ObjetosBD.Familia.FamiliaBD;
import ObjetosBD.Familia.JDFamilia;
import ObjetosBD.Persona.JDPersona;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class PersonaController {
    private JDPersona FDC;
    private JDFamilia DBFamilia;

    @FXML private TextField nom_persona;
    @FXML private TextField edad_persona;
    @FXML private ComboBox<FamiliaBD> fam_persona;


    @FXML
    void initialize(){
        FDC = new JDPersona();
        DBFamilia = new JDFamilia();
        fam_persona.setItems(DBFamilia.obtenerFamilias());
    }

    @FXML
    void registrarPersona(ActionEvent event){
        validar();
    }

    private void validar(){
        if(nom_persona.getText().isBlank()){
            System.out.println("El nombre de la persona es necesario");
            nom_persona.requestFocus();
            return;
        }
        if(edad_persona.getText().isBlank()){
            System.out.println("La edad de la persona es necesario");
            edad_persona.requestFocus();
            return;
        }
        if(fam_persona.getValue() == null){
            System.out.println("El numero de familia es necesario");
            fam_persona.requestFocus();
            return;
        }

        String nombrePersona = nom_persona.getText();
        int edadPersona;
        try {
            edadPersona = Integer.parseInt(edad_persona.getText().trim());
        } catch (NumberFormatException e) {
            System.out.println("La edad debe ser un número válido");
            edad_persona.requestFocus();
            return;
        }
        FamiliaBD FamiliaPersona = fam_persona.getValue();
        int IdFamilia = FamiliaPersona.getId();

        int id= FDC.insertarPersona(nombrePersona, IdFamilia, edadPersona);
        if(id != -1){
            System.out.println("Registro exitoso \\nID de asignado:" + id);
            nom_persona.clear();
            edad_persona.clear();
        }else{
            System.out.println("Error en el registro de PERSONA");
        }
    }
}
