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
//registrazione
public class RegistrationStepController {

    @FXML private Label wizardTitle;
    @FXML private Label errorLabel;
    @FXML private TextField regFirstNameField, regLastNameField, regUsernameField, regEmailField;
    @FXML private PasswordField regPasswordField, regConfirmPasswordField;
    @FXML private Button btnRegister;

    private StepNavigator navigator;
    private String selectedRole = "TRAVELER";

    private static class RegistrationRequestDto {
        String username;
        String fullName;
        String email;
        String hashedPassword;
        String role;
        boolean oauthProvider;

        public RegistrationRequestDto(String username, String fullName, String email, String hashedPassword, String role) {
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.hashedPassword = hashedPassword;
            this.role = role;
            this.oauthProvider = false;
        }
    }

    public void setNavigator(StepNavigator navigator) {
        this.navigator = navigator;
    }

    public void setSelectedRole(String role) {
        this.selectedRole = role;
        wizardTitle.setText(role.equals("ORGANIZER") ? "Profilo Business" : "Profilo Personal");
        btnRegister.setDefaultButton(true);
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
    void backToRoleSelection() {
        clearError();
        navigator.goToRoleStep();
    }

    @FXML
    void handleRegistrationSubmit(ActionEvent event) {
        clearError();
        String firstName = regFirstNameField.getText().trim();
        String lastName = regLastNameField.getText().trim();
        String username = regUsernameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = regPasswordField.getText().trim();
        String confirmPassword = regConfirmPasswordField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty()
                || email.isEmpty() || password.isEmpty() || !password.equals(confirmPassword)) {
            showError("Verifica che tutti i campi siano compilati e che le password coincidano.");
            return;
        }
        if (password.length() < 8) {
            showError("La password deve contenere almeno 8 caratteri.");
            return;
        }
        if (!email.contains("@")) {
            showError("Inserisci un'email valida.");
            return;
        }

        RegistrationRequestDto requestDto = new RegistrationRequestDto(
                username,
                firstName + " " + lastName,
                email,
                password,
                selectedRole
        );

        Gson gson = new Gson();
        String jsonBody = gson.toJson(requestDto);

        ApiService.call(
                ApiService.BASE_URL + "/api/users",
                jsonBody, "POST",
                res -> Platform.runLater(() -> {
                    if (res.statusCode() == 200 || res.statusCode() == 201) {
                        clearFields();
                        navigator.goToLoginStep();
                    } else if (res.statusCode() == 409) {
                        showError("Username o Email già registrati.");
                    } else {
                        showError("Impossibile completare la registrazione.");
                    }
                }),
                () -> Platform.runLater(() -> {
                    it.unical.ea_project_javafx.util.ViewNavigator.loadScene(btnRegister, "/it/unical/ea_project_javafx/fxml/pre-main.fxml", false);
                }),
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