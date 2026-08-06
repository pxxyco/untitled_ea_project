package it.unical.ea_project_javafx.controller;

import it.unical.ea_project_javafx.util.ApiService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class PreMainController {

    @FXML private Label iconLabel;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label statusLabel;
    @FXML private HBox buttonBox;
    @FXML private Button retryButton;
    @FXML private Button exitButton;

    @FXML
    public void initialize() {
        checkBackendConnection();
    }

    private void checkBackendConnection() {
        progressIndicator.setVisible(true);
        progressIndicator.setManaged(true);
        statusLabel.setText("Connessione al server...");
        buttonBox.setVisible(false);
        buttonBox.setManaged(false);

        Task<Boolean> healthCheck = new Task<>() {
            @Override
            protected Boolean call() {
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

        new Thread(healthCheck).start();
    }

    private void showErrorState() {
        Platform.runLater(() -> {
            iconLabel.setText("⚠️");
            progressIndicator.setVisible(false);
            progressIndicator.setManaged(false);
            statusLabel.setText("Impossibile contattare i servizi online.");

            buttonBox.setVisible(true);
            buttonBox.setManaged(true);

            retryButton.setOnAction(e -> checkBackendConnection());
            exitButton.setOnAction(e -> Platform.exit());
        });
    }

    private void loadMainApp() {
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) statusLabel.getScene().getWindow();
                Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/it/unical/ea_project_javafx/fxml/mainview.fxml")));
                Scene scene = new Scene(root, 1280, 720);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/it/unical/ea_project_javafx/css/style.css")).toExternalForm());
                stage.setScene(scene);
                stage.setMaximized(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}