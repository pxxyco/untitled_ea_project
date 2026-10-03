module it.unical.ea_project_javafx {

    requires transitive javafx.graphics;
    
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.base;
    requires java.prefs;
    requires java.net.http;
    
    requires com.google.gson;
    requires static lombok;
    requires com.gluonhq.maps;

    exports it.unical.ea_project_javafx;
    
    opens it.unical.ea_project_javafx to javafx.fxml;

    opens it.unical.ea_project_javafx.dto to com.google.gson;
    opens it.unical.ea_project_javafx.dto.home to com.google.gson;
    
    opens it.unical.ea_project_javafx.controller to javafx.fxml, com.google.gson;
    opens it.unical.ea_project_javafx.controller.attivita to javafx.fxml;
    opens it.unical.ea_project_javafx.controller.attivita.fotoContainer to javafx.fxml;
    opens it.unical.ea_project_javafx.controller.attivita.recensioni to javafx.fxml;
    opens it.unical.ea_project_javafx.controller.login to com.google.gson, javafx.fxml;
    opens it.unical.ea_project_javafx.controller.home to com.google.gson, javafx.fxml;
    opens it.unical.ea_project_javafx.controller.profile to com.google.gson, javafx.fxml;
    

}