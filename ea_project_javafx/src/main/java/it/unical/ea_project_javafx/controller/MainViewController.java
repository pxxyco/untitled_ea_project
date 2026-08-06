package it.unical.ea_project_javafx.controller;

import it.unical.ea_project_javafx.model.MainNavigator;
import it.unical.ea_project_javafx.util.ViewNavigator;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainViewController implements MainNavigator {

    @FXML private ImageView bgImageView;
    @FXML private VBox cardsContainer;

    @FXML private NavbarController navbarController;
    @FXML private SearchBarController searchBarController;
    @FXML private ExperienceListController experienceListController;

    @FXML
    public void initialize() {
        setupBackgroundResize();
        navbarController.setNavigator(this);
    }

    private void setupBackgroundResize() {
        if (bgImageView == null) return;
        bgImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null && bgImageView.getParent() instanceof StackPane parentPane) {
                bgImageView.fitWidthProperty().bind(parentPane.widthProperty());
                bgImageView.fitHeightProperty().bind(parentPane.heightProperty());
            }
        });
    }

    @Override
    public void goToLogin(Node sourceNode) {
        ViewNavigator.switchScene(sourceNode, "/it/unical/ea_project_javafx/fxml/login.fxml");
    }

    @Override
    public void onEsperienzeClicked() {
        // TODO switcha scena verso vista Esperienze
    }

    @Override
    public void onItinerariClicked() {
        // TODO switcha scena verso vista Itinerari Pubblici
    }
}