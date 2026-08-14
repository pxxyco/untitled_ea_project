package it.unical.ea_project_javafx.controller;

import it.unical.ea_project_javafx.model.StepNavigator;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
//selezione ruolo nel login
public class RoleSelectionStepController {

    private StepNavigator navigator;

    public void setNavigator(StepNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    void goToLogin() {
        navigator.goToLoginStep();
    }

    @FXML
    void selectPersonalRole(MouseEvent event) {
        navigator.goToDetailsStep("TRAVELER");
    }

    @FXML
    void selectBusinessRole(MouseEvent event) {
        navigator.goToDetailsStep("ORGANIZER");
    }
}