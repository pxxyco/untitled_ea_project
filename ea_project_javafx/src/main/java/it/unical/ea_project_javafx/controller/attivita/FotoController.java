package it.unical.ea_project_javafx.controller.attivita;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

import java.io.IOException;

public class FotoController {

    final private String path =  "/it/unical/ea_project_javafx/fxml/attivitaDir/fotoDir";


    @FXML
    private FlowPane imgsContainer;

    @FXML
    public void initialize(){
        loadImages();
    }

    private void loadImages() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(path + "/FotoPlaceholder.fxml"));

        Node placeholder = null;

        try {
            placeholder = loader.load();
            imgsContainer.getChildren().add(placeholder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
