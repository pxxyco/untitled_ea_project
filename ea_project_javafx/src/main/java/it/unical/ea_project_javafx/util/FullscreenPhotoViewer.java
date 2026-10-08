package it.unical.ea_project_javafx.util;

import it.unical.ea_project_javafx.controller.attivita.fotoContainer.FullScreenFotoViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class FullscreenPhotoViewer {

    static final private String path = "/it/unical/ea_project_javafx/fxml/";

    public static void mostra(StackPane root, String url) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FullscreenPhotoViewer.class.getResource(path + "attivita/fotoContainer/FullScreenFotoView.fxml"));
            Node overlayNode = loader.load();

            FullScreenFotoViewController controller = loader.getController();
            controller.setFoto(url);
            controller.setOnClose(() -> root.getChildren().remove(overlayNode));

            root.getChildren().add(overlayNode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
