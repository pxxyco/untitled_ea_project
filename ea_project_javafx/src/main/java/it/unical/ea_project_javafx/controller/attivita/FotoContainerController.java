package it.unical.ea_project_javafx.controller.attivita;

import it.unical.ea_project_javafx.controller.attivita.fotoContainer.FotoItemController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

import java.io.IOException;

public class FotoContainerController {

    final private String path = "/it/unical/ea_project_javafx/fxml/attivita/fotoContainer";


    @FXML
    private FlowPane imgsContainer;

    @FXML
    public void initialize(){
        loadImages();
    }

    private void loadImages() {
        try {



            FXMLLoader placeholderLoader = new FXMLLoader();
            placeholderLoader.setLocation(getClass().getResource(path + "/FotoPlaceholder.fxml"));
            Node placeholder = placeholderLoader.load();
            imgsContainer.getChildren().add(placeholder);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
