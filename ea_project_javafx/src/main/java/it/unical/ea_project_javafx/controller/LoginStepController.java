package it.unical.ea_project_javafx.controller;

import it.unical.ea_project_javafx.model.StepNavigator;
import it.unical.ea_project_javafx.util.ApiService;
import it.unical.ea_project_javafx.util.JsonUtils;
import it.unical.ea_project_javafx.util.ViewNavigator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class LoginStepController {

    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Button btnLogin;

    private StepNavigator navigator;

    public void setNavigator(StepNavigator navigator) {
        this.navigator = navigator;
        btnLogin.setDefaultButton(true);
    }

    @FXML
    void handleLoginSubmit(ActionEvent event) {
        String identifier = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText().trim();

        if (identifier.isEmpty() || password.isEmpty()) {
            navigator.showAlert(Alert.AlertType.WARNING, "Attenzione", "Inserisci email/username e password.");
            return;
        }

        String encodedId = URLEncoder.encode(identifier, StandardCharsets.UTF_8);
        String encodedPwd = URLEncoder.encode(password, StandardCharsets.UTF_8);

        ApiService.call(
                "http://localhost:8080/api/users/login?email=" + encodedId + "&password=" + encodedPwd,
                "", "POST",
                res -> {
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
                        navigator.showAlert(Alert.AlertType.ERROR, "Errore Autenticazione", "Credenziali non valide.");
                    }
                },
                () -> navigator.showAlert(Alert.AlertType.ERROR, "Errore Connessione", "Impossibile contattare il server o richiesta in timeout."),
                navigator::setLoading
        );
    }

    @FXML
    void goToRoleSelection(ActionEvent event) {
        navigator.goToRoleStep();
    }
}