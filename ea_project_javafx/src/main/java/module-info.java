module it.unical.ea_project_javafx {
    requires javafx.controls;
    requires javafx.fxml;

    opens it.unical.ea_project_javafx.controller to javafx.fxml;
    opens it.unical.ea_project_javafx.model to javafx.base;
    opens it.unical.ea_project_javafx to javafx.graphics, javafx.fxml;

    exports it.unical.ea_project_javafx;
}
