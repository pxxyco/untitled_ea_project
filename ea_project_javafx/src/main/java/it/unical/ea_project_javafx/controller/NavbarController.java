package it.unical.ea_project_javafx.controller;

import it.unical.ea_project_javafx.model.MainNavigator;
import it.unical.ea_project_javafx.model.UserSession;
import it.unical.ea_project_javafx.util.ViewNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.input.MouseEvent;

public class NavbarController {

    private MainNavigator navigator;

    @FXML private MenuButton userMenuButton;

    @FXML
    public void initialize() {
        if (userMenuButton != null && UserSession.getInstance().isLoggedIn()) {
            userMenuButton.setText(UserSession.getInstance().getUsername());
        }
    }

    public void setNavigator(MainNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    void handleLogin(MouseEvent event) {
        if (navigator != null) {
            navigator.goToLogin((javafx.scene.Node) event.getSource());
        }
    }

    @FXML
    void handleProfilo(ActionEvent event) {
        // TODO inserisci il profilo
    }

    @FXML
    void handleLogout(ActionEvent event) {
        UserSession.getInstance().clear();

        if (userMenuButton != null) {
            ViewNavigator.loadScene(userMenuButton, "/it/unical/ea_project_javafx/fxml/pre-main.fxml", false);
        }

    }

    @FXML
    void handleEsperienze(MouseEvent event) {
        if (navigator != null) {
            navigator.onEsperienzeClicked();
        }
    }

    @FXML
    void handleItinerari(MouseEvent event) {
        if (navigator != null) {
            navigator.onItinerariClicked();
        }
    }
}