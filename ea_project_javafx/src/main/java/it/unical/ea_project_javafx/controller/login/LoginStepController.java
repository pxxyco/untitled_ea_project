package it.unical.ea_project_javafx.controller.login;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import it.unical.ea_project_javafx.dto.UserDTO;
import it.unical.ea_project_javafx.model.StepNavigator;
import it.unical.ea_project_javafx.util.ApiService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoginStepController {

    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Button btnLogin;
    @FXML private Label errorLabel;
    @FXML private CheckBox rememberMeCheckBox;

    private StepNavigator navigator;

    @FXML
    public void initialize() {
        loginUsernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLoginSubmit(null);
            }
        });

        loginPasswordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLoginSubmit(null);
            }
        });
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
            showError("Inserisci email o username e password.");
            return;
        }

        String encodedId = URLEncoder.encode(identifier, StandardCharsets.UTF_8);
        String encodedPwd = URLEncoder.encode(password, StandardCharsets.UTF_8);

        ApiService.post(
                ApiService.BASE_URL + "/api/users/login?email=" + encodedId + "&password=" + encodedPwd,
                null,
                res -> Platform.runLater(() -> {
                    if (res.statusCode() == 200) {
                        try {
                            String authHeader = res.headers().firstValue("Authorization").orElse("");
                            String accessToken = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
                            String refreshToken = res.headers().firstValue("X-Refresh-Token").orElse(null);

                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, typeOfT, context) ->
                                            LocalDateTime.parse(json.getAsString()))
                                    .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                                            LocalDate.parse(json.getAsString()))
                                    .create();

                            UserDTO user = gson.fromJson(res.body(), UserDTO.class);

                            if (user != null) {
                                it.unical.ea_project_javafx.model.UserSession.getInstance().setSession(user);
                                it.unical.ea_project_javafx.model.UserSession.getInstance().setTokens(accessToken, refreshToken);

                                if (rememberMeCheckBox != null && rememberMeCheckBox.isSelected()) {
                                    it.unical.ea_project_javafx.util.TokenStorage.saveTokens(accessToken, refreshToken);
                                } else {
                                    it.unical.ea_project_javafx.util.TokenStorage.clear();
                                }
                                //System.out.println("Access Token salvato: " + UserSession.getInstance().getAccessToken());

                                if (event != null) {
                                    navigator.goToHome(event);
                                } else {
                                    navigator.goToHome(new ActionEvent(btnLogin, null));
                                }
                            } else {
                                showError("Errore il profilo utente è vuoto.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            showError("Errore nella lettura della risposta del server.");
                        }
                    } else {
                        showError("Credenziali non valide.");
                    }
                }),
                () -> Platform.runLater(() -> {
                    showError("Errore di connessione al server.");
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