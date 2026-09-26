package it.unical.ea_project_javafx.controller.home;

import it.unical.ea_project_javafx.model.MainNavigator;
import it.unical.ea_project_javafx.model.UserSession;
import it.unical.ea_project_javafx.util.WallpaperService;
import it.unical.ea_project_javafx.util.SceneNavigator;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

// Schermata home (Navbar, Search bar, Cards)
public class MainViewController implements MainNavigator {

    @FXML private ImageView bgImageView;
    @FXML private VBox cardsContainer;
    @FXML private StackPane navbarContainer;

    @FXML private SearchBarController searchBarController;
    @FXML private ExperienceListController experienceListController;

    @FXML
    public void initialize() {

        loadBackground();
        
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

    private void loadBackground() {

        if (bgImageView == null) {
            return;
        }

        new Thread(() -> {
            String imageUrl = WallpaperService.getDailyWallpaperUrl();

            // L'aggiornamento dei componenti FXML deve avvenire sempre sul thread JavaFX
            Platform.runLater(() -> {
                Image background = new Image(
                        imageUrl,
                        1920,
                        1080,
                        true,
                        true,
                        true
                );
                bgImageView.setImage(background);
            });
        }).start();

    }

    private void loadNavbar() {

        if (navbarContainer == null) return;
        try {
            boolean isLoggedIn = UserSession.getInstance().isLoggedIn();

            String fxmlFile;
            if (isLoggedIn) {
                fxmlFile = "/it/unical/ea_project_javafx/fxml/home/navbar-logged-in.fxml";
            } else {
                fxmlFile = "/it/unical/ea_project_javafx/fxml/home/navbar-logged-out.fxml";
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
        SceneNavigator.getInstance().loadScene("/it/unical/ea_project_javafx/fxml/login/login.fxml");
    }

    @Override
    public void goToExperiences(Node sourceNode) {
        SceneNavigator.getInstance().loadScene("/it/unical/ea_project_javafx/fxml/experiences.fxml");
    }

    @Override
    public void goToItinerari(Node sourceNode) {
        SceneNavigator.getInstance().loadScene("/it/unical/ea_project_javafx/fxml/itinerari.fxml");
    }

    @Override
    public void goToProfile(Node sourceNode) {
        SceneNavigator.getInstance().loadScene("/it/unical/ea_project_javafx/fxml/profile/profile.fxml");
    }
}