package it.unical.ea_project_javafx.controller.attivita;

import it.unical.ea_project_javafx.controller.attivita.fotoContainer.FotoItemController;
import it.unical.ea_project_javafx.controller.attivita.fotoContainer.FullScreenFotoViewController;
import it.unical.ea_project_javafx.util.FullscreenPhotoViewer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class FotoGalleryController {

    @FXML
    public StackPane rootStack;
    @FXML
    private FlowPane imgsContainer;

    final private String path = "/it/unical/ea_project_javafx/fxml/";

    @FXML
    void initialize() throws IOException {
        loadImages();
    }

    private void loadImages() throws IOException {
        /* TO DO ---  ESEMPIO DI CREAZIONE DI UN IMMAGINE ---
        FXMLLoader fotoLoader = new FXMLLoader();
        fotoLoader.setLocation(getClass().getResource(path + "attivita/fotoContainer/FotoItem.fxml"));
        Node foto = fotoLoader.load();

        FotoItemController controller = fotoLoader.getController();
        controller.setFoto("https://picsum.photos/150/150");

        controller.setOnFotoClickedCallBack(url -> mostraFotoFullscreen(url));

        imgsContainer.getChildren().add(foto);

        */
    }

    private void mostraFotoFullscreen(String url) {
        // TO DO
        // USE util/FullscreenPhotoViewer.java
    }

}
