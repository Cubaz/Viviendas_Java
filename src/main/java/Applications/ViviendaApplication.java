package Applications;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ViviendaApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        ErroresUI.instalar();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Interfaces/MenuPrincipal.fxml"));
        AnchorPane pane = loader.load();
        stage.setScene(new Scene(pane));
        stage.show();
    }
}
