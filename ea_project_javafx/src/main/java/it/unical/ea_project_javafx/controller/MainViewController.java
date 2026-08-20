package it.unical.ea_project_javafx.controller;

import it.unical.ea_project_javafx.model.MainNavigator;
import it.unical.ea_project_javafx.model.UserSession;
import it.unical.ea_project_javafx.util.ViewNavigator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
//schermata home (navbar, search bar, cards)
public class MainViewController implements MainNavigator {

    @FXML private ImageView bgImageView;
    @FXML private VBox cardsContainer;
    @FXML private StackPane navbarContainer;

    @FXML private SearchBarController searchBarController;
    @FXML private ExperienceListController experienceListController;

    @FXML
    public void initialize() {
        String savedToken = it.unical.ea_project_javafx.util.TokenStorage.getAccessToken();

        if (savedToken != null && !savedToken.isBlank()) {
            boolean expired = it.unical.ea_project_javafx.util.JwtUtils.isExpired(savedToken);

            if (!expired) {
                String savedRefreshToken = it.unical.ea_project_javafx.util.TokenStorage.getRefreshToken();
                UserSession.getInstance().setTokens(savedToken, savedRefreshToken);
                String email = it.unical.ea_project_javafx.util.JwtUtils.extractEmail(savedToken);
                if (email != null) {
                    it.unical.ea_project_javafx.dto.UserDTO restoredUser = it.unical.ea_project_javafx.dto.UserDTO.builder()
                            .email(email)
                            .username(email.split("@")[0])
                            .build();
                    UserSession.getInstance().setSession(restoredUser);
                }
            } else {
                it.unical.ea_project_javafx.util.TokenStorage.clear();
            }
        }

        setupBackgroundResize();
        loadNavbar();

        if (searchBarController != null && experienceListController != null) {
            searchBarController.setExperienceListController(experienceListController);
        }
    }

    private void loadNavbar() {
        if (navbarContainer == null) return;
        try {
            boolean isLoggedIn = UserSession.getInstance().isLoggedIn();

            String fxmlFile;
            if (isLoggedIn) {
                fxmlFile = "/it/unical/ea_project_javafx/fxml/navbar-logged-in.fxml";
            } else {
                fxmlFile = "/it/unical/ea_project_javafx/fxml/navbar-logged-out.fxml";
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent navbarView = loader.load();

            NavbarController navbarController = loader.getController();
            if (navbarController != null) {
                navbarController.setNavigator(this);
            }

            navbarContainer.getChildren().setAll(navbarView);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public void goToExperiences(Node sourceNode) {
        ViewNavigator.switchScene(sourceNode, "/it/unical/ea_project_javafx/fxml/experiences.fxml");
    }

    @Override
    public void goToItinerari(Node sourceNode) {
        ViewNavigator.switchScene(sourceNode, "/it/unical/ea_project_javafx/fxml/itinerari.fxml");
    }

    @Override
    public void goToProfile(Node sourceNode) {
        ViewNavigator.switchScene(sourceNode, "/it/unical/ea_project_javafx/fxml/profile.fxml");
    }
}