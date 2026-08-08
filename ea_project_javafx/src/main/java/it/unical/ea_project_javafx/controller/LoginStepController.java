package it.unical.ea_project_javafx.controller;

import it.unical.ea_project_javafx.model.StepNavigator;
import it.unical.ea_project_javafx.util.ApiService;
import it.unical.ea_project_javafx.util.JsonUtils;
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
                        String body = res.body();
                        String username = JsonUtils.extractField(body, "username");
                        String email = JsonUtils.extractField(body, "email");
                        String role = JsonUtils.extractField(body, "role");

                        it.unical.ea_project_javafx.model.UserSession.getInstance().setSession(
                                username.isEmpty() ? identifier : username,
                                email.isEmpty() ? identifier : email,
                                role.isEmpty() ? "TRAVELER" : role
                        );

                        navigator.goToHome(event);
                    } else {
                        showError("Credenziali non valide.");
                    }
                }),
                () -> Platform.runLater(() -> showError("Impossibile contattare il server o richiesta in timeout.")),
                navigator::setLoading
        );
    }

    @FXML
    void goToRoleSelection(ActionEvent event) {
        clearError();
        navigator.goToRoleStep();
    }
}