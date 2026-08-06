package it.unical.ea_project_javafx.controller;

import it.unical.ea_project_javafx.model.StepNavigator;
import it.unical.ea_project_javafx.util.ApiService;
import it.unical.ea_project_javafx.util.JsonUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegistrationStepController {

    @FXML private Label wizardTitle;
    @FXML private TextField regFirstNameField, regLastNameField, regUsernameField, regEmailField;
    @FXML private PasswordField regPasswordField, regConfirmPasswordField;
    @FXML private Button btnRegister;

    private StepNavigator navigator;
    private String selectedRole = "TRAVELER";

    public void setNavigator(StepNavigator navigator) {
        this.navigator = navigator;
    }

    public void setSelectedRole(String role) {
        this.selectedRole = role;
        wizardTitle.setText(role.equals("ORGANIZER") ? "Profilo Business" : "Profilo Personal");
        btnRegister.setDefaultButton(true);
    }

    @FXML
    void backToRoleSelection() {
        navigator.goToRoleStep();
    }

    @FXML
    void handleRegistrationSubmit(ActionEvent event) {
        String firstName = regFirstNameField.getText().trim();
        String lastName = regLastNameField.getText().trim();
        String username = regUsernameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = regPasswordField.getText().trim();
        String confirmPassword = regConfirmPasswordField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()
                || email.isEmpty() || password.isEmpty() || !password.equals(confirmPassword)) {
            navigator.showAlert(Alert.AlertType.WARNING, "Attenzione", "Verifica che tutti i campi siano compilati e che le password coincidano.");
            return;
        }
        if (password.length() < 8) {
            navigator.showAlert(Alert.AlertType.WARNING, "Attenzione", "La password deve contenere almeno 8 caratteri.");
            return;
        }
        if (!email.contains("@")) {
            navigator.showAlert(Alert.AlertType.WARNING, "Attenzione", "Inserisci un'email valida.");
            return;
        }

        String jsonBody = String.format(
                "{\"username\":\"%s\",\"fullName\":\"%s\",\"email\":\"%s\",\"hashedPassword\":\"%s\",\"role\":\"%s\",\"oauthProvider\":false}",
                JsonUtils.escape(username),
                JsonUtils.escape(firstName + " " + lastName),
                JsonUtils.escape(email),
                JsonUtils.escape(password),
                selectedRole
        );

        ApiService.call(
                "http://localhost:8080/api/users",
                jsonBody, "POST",
                res -> {
                    if (res.statusCode() == 200 || res.statusCode() == 201) {
                        navigator.showAlert(Alert.AlertType.INFORMATION, "Successo", "Registrazione completata!");
                        clearFields();
                        navigator.goToLoginStep();
                    } else if (res.statusCode() == 409) {
                        navigator.showAlert(Alert.AlertType.ERROR, "Errore Registrazione", "Username o Email già registrati.");
                    } else {
                        navigator.showAlert(Alert.AlertType.ERROR, "Errore Registrazione", "Impossibile completare la registrazione.");
                    }
                },
                () -> navigator.showAlert(Alert.AlertType.ERROR, "Errore Connessione", "Impossibile contattare il server."),
                navigator::setLoading
        );
    }

    private void clearFields() {
        regFirstNameField.clear();
        regLastNameField.clear();
        regUsernameField.clear();
        regEmailField.clear();
        regPasswordField.clear();
        regConfirmPasswordField.clear();
    }
}