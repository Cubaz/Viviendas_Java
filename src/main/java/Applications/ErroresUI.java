package Applications;

import ObjetosBD.DataAccessException;
import javafx.application.Platform;
import javafx.scene.control.Alert;

/** Los handlers FXML envuelven excepciones: buscar la causa antes de informar. */
public final class ErroresUI {
    private ErroresUI() {}

    public static String mensaje(Throwable error) {
        for (Throwable causa = error; causa != null; causa = causa.getCause()) {
            if (causa instanceof DataAccessException || causa instanceof IllegalArgumentException)
                return causa.getMessage();
        }
        return "No se pudo completar la operación. Revise los datos e inténtelo de nuevo.";
    }

    public static void instalar() {
        Thread.currentThread().setUncaughtExceptionHandler((thread, error) -> mostrar(error));
    }

    private static void mostrar(Throwable error) {
        error.printStackTrace();
        Runnable informar = () -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Revise la operación");
            alert.setHeaderText("La operación no se completó");
            alert.setContentText(mensaje(error));
            alert.show();
        };
        if (Platform.isFxApplicationThread()) informar.run(); else Platform.runLater(informar);
    }
}
