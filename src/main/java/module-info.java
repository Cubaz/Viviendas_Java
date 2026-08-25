module org.example.hospital {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.controlsfx.controls;
    requires java.desktop;

    exports Applications;
    exports Controllers;
    exports ObjetosBD;


    // Permite que JavaFX acceda a los controladores vía reflexión
    opens Interfaces to javafx.fxml;
    opens Controllers to javafx.fxml;
}

