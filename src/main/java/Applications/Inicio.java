package Applications;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.TextField;



public class Inicio extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Interfaces/Colonia.fxml")); ///ABRE LA VISTA DE LA PORTADA DEL PROYECTO

        ///  LAS SIGUIENTES FUNCIONES SON LAS QUE INICIALIZAN LA VISTA DE LA PORTADA Y LA MUESTRAN EN LA PANTALLA
        AnchorPane pane = loader.load();

        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.show();
    }

    static void main(String[] args) {
        launch();
    }
}
