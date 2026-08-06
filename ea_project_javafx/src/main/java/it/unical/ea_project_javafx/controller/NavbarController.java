package it.unical.ea_project_javafx.controller;

import it.unical.ea_project_javafx.model.MainNavigator;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

public class NavbarController {

    private MainNavigator navigator;

    public void setNavigator(MainNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    void handleLogin(MouseEvent event) {
        navigator.goToLogin((javafx.scene.Node) event.getSource());
    }

    @FXML
    void handleEsperienze(MouseEvent event) {
        navigator.onEsperienzeClicked();
    }

    @FXML
    void handleItinerari(MouseEvent event) {
        navigator.onItinerariClicked();
    }
}