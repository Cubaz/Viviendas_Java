package Controllers;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import ObjetosBD.JDFamilia;


public class FamiliaController {

    private JDFamilia FDB;

    /// ELEMENTOS DEL FORMULARIO
    @FXML private TextField ap_m;
    @FXML private TextField ap_p;

    /// FUNCIONES
    @FXML void initialize(){FDB = new JDFamilia();} /// Inicializar una nueva instancia de tipo FAMILIA

    @FXML void registrarFamilia(ActionEvent event){validar();} ///Realiza la acción de REGISTRO a la FAMILIA al PRESIONAR el BOTÓN

    private void validar(){
        String ap_pat, ap_mat, apellidos;
        if(ap_p.getText().isBlank()){
            System.out.println("El apellido parterno es necesario");
            ap_p.requestFocus();
            return;
        }
        if(ap_m.getText().isBlank()){
            System.out.println("El apellido materno es necesario");
            ap_m.requestFocus();
            return;
        }

        ap_pat = ap_p.getText();
        ap_mat = ap_m.getText();
        apellidos = ap_pat +" "+ ap_mat;

        int id= FDB.insertarFamilia(apellidos);
        if(id != -1){
            System.out.println("Registro exitoso \\nID de usuario:" + id);

        }else{
            System.out.println("Error en el registro de FAMILIA");
        }
    }

}
