package it.unical.ea_project_javafx.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;

public class ViewNavigator {

    public static void switchScene(Node sourceNode, String fxmlPath) {
        loadScene(sourceNode, fxmlPath, false);
    }

    public static void loadScene(Node sourceNode, String fxmlPath, boolean maximize) {
        Stage stage = null;

        if (sourceNode != null && sourceNode.getScene() != null) {
            Window window = sourceNode.getScene().getWindow();
            if (window instanceof Stage s) {
                stage = s;
            }
        }

        if (stage == null) {
            for (Window window : Window.getWindows()) {
                if (window instanceof Stage s && s.isShowing()) {
                    stage = s;
                    break;
                }
            }
        }

        if (stage == null) {
            System.err.println("Impossibile trovare uno Stage attivo per caricare la vista: " + fxmlPath);
            return;
        }

        URL fxmlUrl = ViewNavigator.class.getResource(fxmlPath);
        if (fxmlUrl == null) {
            System.err.println("Risorsa FXML non trovata: " + fxmlPath);
            return;
        }

        try {
            Parent root = FXMLLoader.load(fxmlUrl);

            double width = stage.getScene() != null ? stage.getScene().getWidth() : 1280;
            double height = stage.getScene() != null ? stage.getScene().getHeight() : 720;

            Scene scene = new Scene(root, width, height);

            var cssUrl = ViewNavigator.class.getResource("/it/unical/ea_project_javafx/css/style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            stage.setScene(scene);

            if (maximize) {
                stage.setMaximized(true);
            } else {
                stage.setMaximized(false);
                stage.centerOnScreen();
            }

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}