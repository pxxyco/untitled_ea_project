module it.unical.ea_project_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    opens it.unical.ea_project_javafx to javafx.fxml;
    opens it.unical.ea_project_javafx.controller to javafx.fxml;

    exports it.unical.ea_project_javafx;
}