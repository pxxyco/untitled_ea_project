package it.unical.ea_project_javafx.controller;

import it.unical.ea_project_javafx.util.ApiService;
import it.unical.ea_project_javafx.util.ViewNavigator;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
//prima della home il client esegue una connessione controllando se c'è la risposta per il backend questo accade per ogni chiamata
public class PreMainController {

    @FXML private Label iconLabel;
    @FXML private ImageView errorImageView;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label errorTitleLabel;
    @FXML private Label statusLabel;
    @FXML private HBox buttonBox;
    @FXML private Button retryButton;
    @FXML private Button exitButton;

    private static boolean isMonitoringConnection = false;

    @FXML
    public void initialize() {
        ApiService.setOnNetworkFailureGlobal(this::restartAppManually);
        checkBackendConnection();
    }

    private void checkBackendConnection() {
        progressIndicator.setVisible(true);
        progressIndicator.setManaged(true);
        errorTitleLabel.setVisible(false);
        errorTitleLabel.setManaged(false);
        errorImageView.setVisible(false);
        errorImageView.setManaged(false);
        statusLabel.setText("Connessione al server...");
        buttonBox.setVisible(false);
        buttonBox.setManaged(false);

        Task<Boolean> healthCheck = verifyServiceStatus();

        new Thread(healthCheck).start();
    }

    private Task<Boolean> verifyServiceStatus() {
        Task<Boolean> healthCheck = new Task<>() {
            @Override
            protected Boolean call() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return ApiService.isBackendReachable();
            }
        };

        healthCheck.setOnSucceeded(e -> {
            if (Boolean.TRUE.equals(healthCheck.getValue())) {
                loadMainApp();
            } else {
                showErrorState();
            }
        });

        healthCheck.setOnFailed(e -> showErrorState());
        return healthCheck;
    }

    private void showErrorState() {
        Platform.runLater(() -> {
            progressIndicator.setVisible(false);
            progressIndicator.setManaged(false);

            try {
                Image dinoImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/it/unical/ea_project_javafx/images/sad-dino.png")));
                errorImageView.setImage(dinoImage);
                errorImageView.setVisible(true);
                errorImageView.setManaged(true);
            } catch (Exception e)
            {
                e.printStackTrace();
            }

            errorTitleLabel.setVisible(true);
            errorTitleLabel.setManaged(true);

            statusLabel.setText("Impossibile contattare i servizi online.");

            buttonBox.setVisible(true);
            buttonBox.setManaged(true);

            retryButton.setOnAction(e -> checkBackendConnection());
            exitButton.setOnAction(e -> Platform.exit());
        });

        startAutoRecoveryMonitor();
    }

    private void startAutoRecoveryMonitor() {
        if (isMonitoringConnection) return;
        isMonitoringConnection = true;

        Thread recoveryThread = new Thread(() -> {
            boolean recovered = false;
            while (!recovered) {
                try {
                    Thread.sleep(3000);
                    if (ApiService.isBackendReachable()) {
                        recovered = true;
                        isMonitoringConnection = false;
                        Platform.runLater(this::restartAppManually);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        recoveryThread.setDaemon(true);
        recoveryThread.start();
    }

    private void restartAppManually() {
        loadView("/it/unical/ea_project_javafx/fxml/pre-main.fxml", false);
    }

    private void loadMainApp() {
        loadView("/it/unical/ea_project_javafx/fxml/mainview.fxml", true);
    }

    private void loadView(String fxmlPath, boolean maximize) {
        Platform.runLater(() -> {
            if (statusLabel != null
                    && statusLabel.getScene() != null
                    && statusLabel.getScene().getWindow() != null) {
                ViewNavigator.loadScene(statusLabel, fxmlPath, maximize);
            } else {
                javafx.stage.Window.getWindows().stream()
                        .filter(javafx.stage.Window::isShowing)
                        .findFirst()
                        .ifPresent(window -> {
                            if (window instanceof javafx.stage.Stage stage && stage.getScene() != null) {
                                ViewNavigator.loadScene(stage.getScene().getRoot(), fxmlPath, maximize);
                            }
                        });
            }
        });
    }
}