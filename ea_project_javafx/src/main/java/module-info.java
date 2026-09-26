module it.unical.ea_project_javafx {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    
    requires java.net.http;
    requires java.prefs;
    
    requires com.google.gson;
    requires static lombok;

    exports it.unical.ea_project_javafx;
    
    opens it.unical.ea_project_javafx to javafx.fxml;
    
    opens it.unical.ea_project_javafx.controller to javafx.fxml, com.google.gson;
    opens it.unical.ea_project_javafx.controller.login to com.google.gson, javafx.fxml;
    opens it.unical.ea_project_javafx.controller.home to com.google.gson, javafx.fxml;
    
    opens it.unical.ea_project_javafx.dto to com.google.gson;
    opens it.unical.ea_project_javafx.dto.home to com.google.gson;
}