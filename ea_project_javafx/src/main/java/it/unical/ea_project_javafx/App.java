package it.unical.ea_project_javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX App Main Entry Point
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) {
        try {
            Parent root = loadFXML("mainview");
            scene = new Scene(root, 1280, 720);
            stage.setTitle("EA Project");
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

    private static Parent loadFXML(String fxml) throws IOException {
        String resourcePath = "fxml/" + fxml + ".fxml";
        URL fxmlUrl = App.class.getResource(resourcePath);

        if (fxmlUrl == null) {
            throw new IOException("Error (2-main): FXML file not found: " + resourcePath);
        }

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch(args);
    }
}