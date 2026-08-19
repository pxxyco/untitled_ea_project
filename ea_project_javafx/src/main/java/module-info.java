module it.unical.ea_project_javafx {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.net.http;
    requires javafx.graphics;

    opens it.unical.ea_project_javafx to javafx.fxml;
    opens it.unical.ea_project_javafx.controller to javafx.fxml;
    opens it.unical.ea_project_javafx.controller.attivita to javafx.fxml;
    opens it.unical.ea_project_javafx.controller.attivita.fotoContainer to javafx.fxml;

    exports it.unical.ea_project_javafx;
    exports it.unical.ea_project_javafx.controller;
    exports it.unical.ea_project_javafx.controller.attivita;
}