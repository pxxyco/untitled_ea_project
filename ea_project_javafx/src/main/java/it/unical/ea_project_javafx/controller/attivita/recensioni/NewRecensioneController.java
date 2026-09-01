package it.unical.ea_project_javafx.controller.attivita.recensioni;

import it.unical.ea_project_javafx.util.ApiService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import lombok.Setter;

import java.util.List;

public class NewRecensioneController {

    @FXML private HBox containerStelle;

    @FXML private Button star1;

    @FXML private Button star2;

    @FXML private Button star3;

    @FXML private Button star4;

    @FXML private Button star5;

    @FXML private TextArea txtCommento;

    @Setter
    private Runnable onClose;


    List<Button> stars ;
    final private String FULL_STAR = "★";
    final private String EMPTY_STAR = "☆";

    @FXML
    private void initialize() {
        stars = List.of(star1, star2, star3, star4, star5);
    }


    @FXML
    void handleCloseButton(ActionEvent event) {
        if (onClose != null) {
            onClose.run();
        }
    }

    @FXML
    void handleSendButton(ActionEvent event) {
        // TO DO
    }

    public void reset(){
        handleStar(0);
        txtCommento.clear();
    }

    @FXML
    void star1Button(ActionEvent event) {
        handleStar(1);
    }

    @FXML
    void starButton2(ActionEvent event) {
        handleStar(2);
    }

    @FXML
    void starButton3(ActionEvent event) {
        handleStar(3);

    }

    @FXML
    void starButton4(ActionEvent event) {
        handleStar(4);
    }

    @FXML
    void starButton5(ActionEvent event) {
        handleStar(5);
    }

    private void handleStar(int value) {

        for(int i = 0; i < stars.size(); i++) {
            if(i+1 <= value){
                stars.get(i).getStyleClass().add("stella-piena");
                stars.get(i).setText(FULL_STAR);

            }
            else {
                stars.get(i).getStyleClass().clear();
                stars.get(i).getStyleClass().add("stella");
                stars.get(i).setText(EMPTY_STAR);
            }
        }

    }
}
