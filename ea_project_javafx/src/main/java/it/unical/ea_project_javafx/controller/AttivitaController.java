package it.unical.ea_project_javafx.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
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
    @FXML
    private Button btnRecensioni;
    @FXML
    private Spinner spinnerPartecipanti;

    private List<Button> sectionButtons;

    private Node itinerarioNode;
    private Node descrizioneNode;
    private Node fotoNode;

    final String path = "/it/unical/ea_project_javafx/fxml/attivita";

    @FXML
    void initialize(){
        sectionButtons = List.of(btnDescrizione, btnItinerario, btnFoto, btnRecensioni);
        handleDescrizione(null);

        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1);
        spinnerPartecipanti.setValueFactory(valueFactory);


        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(path + "/Itinerario.fxml")
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

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(path + "/Descrizione.fxml")
        );
        try {
             descrizioneNode= loader.load();
             bodyContainer.getChildren().clear();
             bodyContainer.getChildren().add(descrizioneNode);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void handleItinerario(ActionEvent event) {
        bodyContainer.getChildren().clear();
        bodyContainer.getChildren().setAll(itinerarioNode);

        setActiveButton(btnItinerario);

    }

    @FXML
    protected void handleFoto(ActionEvent event) {
        setActiveButton(btnFoto);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(path + "/FotoContainer.fxml")
        );

        try {
            fotoNode= loader.load();
            bodyContainer.getChildren().clear();
            bodyContainer.getChildren().add(fotoNode);

        }catch (IOException e){

        }
    }

    @FXML
    protected void handleRecensioni(ActionEvent event) {
        setActiveButton(btnRecensioni);
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
