module it.unical.ea_project_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.net.http;
    requires static lombok;

    opens it.unical.ea_project_javafx to javafx.fxml, javafx.graphics;
    opens it.unical.ea_project_javafx.dto to javafx.base;

    exports it.unical.ea_project_javafx;
    exports it.unical.ea_project_javafx.dto;
}