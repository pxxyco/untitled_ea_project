package it.unical.ea_project_javafx.controller.attivita.fotoContainer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class FotoPlaceholderController {

    final private String path = "/it/unical/ea_project_javafx/fxml/attivita";

    @FXML
    void navigateToGallery(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(path + "/FotoGallery.fxml")
        );

        try{
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
