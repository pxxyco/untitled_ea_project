module it.unical.ea_project_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;
    requires static lombok;

    opens it.unical.ea_project_javafx to javafx.fxml;
    opens it.unical.ea_project_javafx.controller to javafx.fxml, com.google.gson;
    opens it.unical.ea_project_javafx.dto to com.google.gson;

    exports it.unical.ea_project_javafx;
}