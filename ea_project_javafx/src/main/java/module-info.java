module it.unical.ea_project_javafx {
    requires javafx.controls;
    requires javafx.fxml;

    opens it.unical.ea_project_javafx to javafx.fxml;
    exports it.unical.ea_project_javafx;
}
