package it.unical.ea_project_javafx.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.util.Objects;

public class ViewNavigator {

    public static void switchScene(Node sourceNode, String fxmlPath) {
        loadScene(sourceNode, fxmlPath, false);
    }

    public static void loadScene(Node sourceNode, String fxmlPath, boolean maximize) {
        try {
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            Parent root = FXMLLoader.load(Objects.requireNonNull(ViewNavigator.class.getResource(fxmlPath)));

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