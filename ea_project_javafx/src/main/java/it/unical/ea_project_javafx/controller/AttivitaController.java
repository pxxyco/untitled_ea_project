package it.unical.ea_project_javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.util.List;

public class AttivitaController {

    @FXML
    private StackPane bodyContainer;
    @FXML
    private Button btnDescrizione;
    @FXML
    private Button btnFoto;
    @FXML
    private Button btnItinerario;

    private List<Button> sectionButtons;

    private Node itinerarioNode;

    @FXML
    void initialize(){
        sectionButtons = List.of(btnDescrizione, btnItinerario, btnFoto);


        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/it/unical/ea_project_javafx/fxml/attivita/Itinerario.fxml")
        );
        try {
            itinerarioNode = loader.load();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    @FXML
    private void handleDescrizione(ActionEvent event) {
        setActiveButton(btnDescrizione);
    }

    @FXML
    private void handleItinerario(ActionEvent event) {

        bodyContainer.getChildren().setAll(itinerarioNode);

        setActiveButton(btnItinerario);

    }

    @FXML
    protected void handleFoto(ActionEvent event) {
        setActiveButton(btnFoto);
    }


    private void setActiveButton(Button active) {
        for (Button b : sectionButtons) {
            b.getStyleClass().remove("section-button-active");
            if (!b.getStyleClass().contains("section-button")) {
                b.getStyleClass().add("section-button");
            }
        }
        active.getStyleClass().remove("section-button");
        active.getStyleClass().add("section-button-active");
    }

}
