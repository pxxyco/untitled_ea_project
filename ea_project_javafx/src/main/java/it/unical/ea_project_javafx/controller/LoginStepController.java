package it.unical.ea_project_javafx.controller;

import com.google.gson.Gson;
import it.unical.ea_project_javafx.model.StepNavigator;
import it.unical.ea_project_javafx.util.ApiService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class LoginStepController {

    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Button btnLogin;
    @FXML private Label errorLabel;

    private StepNavigator navigator;

    private static class LoginResponseDto {
        String username;
        String email;
        String role;
    }

    public void setNavigator(StepNavigator navigator) {
        this.navigator = navigator;
        btnLogin.setDefaultButton(true);
    }

    public void showError(String message) {
        Platform.runLater(() -> {
            if (errorLabel != null) {
                errorLabel.setText(message);
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
            }
        });
    }

    public void clearError() {
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        }
    }

    @FXML
    void handleLoginSubmit(ActionEvent event) {
        clearError();
        String identifier = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText().trim();

        if (identifier.isEmpty() || password.isEmpty()) {
            showError("Inserisci email/username e password.");
            return;
        }

        String encodedId = URLEncoder.encode(identifier, StandardCharsets.UTF_8);
        String encodedPwd = URLEncoder.encode(password, StandardCharsets.UTF_8);

        ApiService.call(
                ApiService.BASE_URL + "/api/users/login?email=" + encodedId + "&password=" + encodedPwd,
                "", "POST",
                res -> Platform.runLater(() -> {
                    if (res.statusCode() == 200) {
                        try {
                            Gson gson = new Gson();
                            LoginResponseDto user = gson.fromJson(res.body(), LoginResponseDto.class);

                            it.unical.ea_project_javafx.model.UserSession.getInstance().setSession(
                                    user != null && user.username != null && !user.username.isEmpty() ? user.username : identifier,
                                    user != null && user.email != null && !user.email.isEmpty() ? user.email : identifier,
                                    user != null && user.role != null && !user.role.isEmpty() ? user.role : "TRAVELER"
                            );

                            navigator.goToHome(event);
                        } catch (Exception e) {
                            showError("Errore nella lettura della risposta del server.");
                        }
                    } else {
                        showError("Credenziali non valide.");
                    }
                }),
                () -> Platform.runLater(() -> {
                    it.unical.ea_project_javafx.util.ViewNavigator.loadScene(btnLogin, "/it/unical/ea_project_javafx/fxml/pre-main.fxml", false);
                }),
                navigator::setLoading
        );
    }

    @FXML
    void goToRoleSelection(ActionEvent event) {
        clearError();
        navigator.goToRoleStep();
    }
}