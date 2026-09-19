package it.unical.ea_project_javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) {
        try {
            Parent root = loadFXML("organizer_dashboard");
            scene = new Scene(root, 1280, 720);
            stage.setTitle("EA Explorer");
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            System.err.println("Error (1-main)");
            e.printStackTrace();
        }
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        String cleanName = fxml.endsWith(".fxml") ? fxml.substring(0, fxml.length() - 5) : fxml;
        String absolutePath = "/it/unical/ea_project_javafx/fxml/" + cleanName + ".fxml";
        String relativePath = "it/unical/ea_project_javafx/fxml/" + cleanName + ".fxml";

        URL fxmlUrl = App.class.getResource(absolutePath);

        if (fxmlUrl == null) {
            fxmlUrl = App.class.getClassLoader().getResource(relativePath);
        }

        if (fxmlUrl == null) {
            fxmlUrl = Thread.currentThread().getContextClassLoader().getResource(relativePath);
        }

        if (fxmlUrl == null) {
            throw new IOException("FXML non trovato nel percorso: " + absolutePath);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}