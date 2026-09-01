package it.unical.ea_project_javafx.controller.attivita;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.io.IOException;

public class RecensioniContainerController {

    @FXML
    private VBox bodyContainer;

    final private String path = "/it/unical/ea_project_javafx/fxml/";

    @Setter
    private Runnable onNewReviewRequested;

    @FXML
    public void initialize(){

    }

    public Object getOnNewReviewRequested() {
        return onNewReviewRequested;
    }

    @FXML
    public void handleNewButton(ActionEvent actionEvent) {
        if(onNewReviewRequested!=null){
            onNewReviewRequested.run();
        }
    }
}
