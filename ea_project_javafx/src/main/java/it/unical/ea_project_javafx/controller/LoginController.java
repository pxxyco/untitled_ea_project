package it.unical.ea_project_javafx.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {

    @FXML private ImageView bgImageView;
    @FXML private VBox stepLogin, stepRole, stepDetails, loadingOverlay;
    @FXML private Label wizardTitle;
    @FXML private Button btnLogin, btnRegister;

    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;

    @FXML private TextField regFirstNameField;
    @FXML private TextField regLastNameField;
    @FXML private TextField regUsernameField;
    @FXML private TextField regEmailField;
    @FXML private PasswordField regPasswordField;
    @FXML private PasswordField regConfirmPasswordField;

    private String selectedRole = "TRAVELER";
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @FXML
    public void initialize() {
        if (bgImageView != null && bgImageView.getParent() instanceof StackPane parentPane) {
            bgImageView.fitWidthProperty().bind(parentPane.widthProperty());
            bgImageView.fitHeightProperty().bind(parentPane.heightProperty());
        }

        if (btnLogin != null) {
            btnLogin.setDefaultButton(true);
        }

        showStep(stepLogin);
    }

    @FXML void goToLogin(ActionEvent event) { showStep(stepLogin); }
    @FXML void goToRoleSelection(ActionEvent event) { showStep(stepRole); }

    @FXML
    void selectPersonalRole(MouseEvent event) {
        selectedRole = "TRAVELER";
        wizardTitle.setText("Profilo Personal");
        showStep(stepDetails);
    }

    @FXML
    void selectBusinessRole(MouseEvent event) {
        selectedRole = "ORGANIZER";
        wizardTitle.setText("Profilo Business");
        showStep(stepDetails);
    }

    @FXML
    void handleLoginSubmit(ActionEvent event) {
        String identifier = loginUsernameField.getText().trim();
        String password = loginPasswordField.getText().trim();

        if (identifier.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Inserisci email/username e password.");
            return;
        }

        String encodedIdentifier = java.net.URLEncoder.encode(identifier, java.nio.charset.StandardCharsets.UTF_8);
        String encodedPassword = java.net.URLEncoder.encode(password, java.nio.charset.StandardCharsets.UTF_8);

        executeApiCall(
                "http://localhost:8080/api/users/login?email=" + encodedIdentifier + "&password=" + encodedPassword,
                "",
                "POST",
                res -> {
                    if (res.statusCode() == 200) {
                        switchScene(event, "/it/unical/ea_project_javafx/fxml/mainview.fxml");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Errore Autenticazione", "Credenziali non valide.");
                    }
                }
        );
    }

    @FXML
    void handleRegistrationSubmit(ActionEvent event) {
        String firstName = regFirstNameField.getText().trim();
        String lastName = regLastNameField.getText().trim();
        String username = regUsernameField.getText().trim();
        String email = regEmailField.getText().trim();
        String password = regPasswordField.getText().trim();
        String confirmPassword = regConfirmPasswordField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() ||
                email.isEmpty() || password.isEmpty() || !password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Verifica che tutti i campi siano compilati e che le password coincidano.");
            return;
        }
        else if (password.length() < 8) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "La password deve contenere almeno 8 caratteri.");
            return;
        }
        else if (!email.contains("@")) {
            showAlert(Alert.AlertType.WARNING, "Attenzione", "Inserisci un'email valida.");
            return;
        }

        String fullName = firstName + " " + lastName;

        String jsonBody = String.format(
                "{\"username\":\"%s\",\"fullName\":\"%s\",\"email\":\"%s\",\"hashedPassword\":\"%s\",\"role\":\"%s\",\"oauthProvider\":false}",
                escapeJson(username),
                escapeJson(fullName),
                escapeJson(email),
                escapeJson(password),
                selectedRole
        );

        executeApiCall(
                "http://localhost:8080/api/users",
                jsonBody,
                "POST",
                res -> {
                    if (res.statusCode() == 200 || res.statusCode() == 201) {
                        showAlert(Alert.AlertType.INFORMATION, "Successo", "Registrazione completata!");
                        clearRegistrationFields();
                        showStep(stepLogin);
                    } else if (res.statusCode() == 409) {
                        showAlert(Alert.AlertType.ERROR, "Errore Registrazione", "Username o Email già registrati.");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Errore Registrazione", "Impossibile completare la registrazione.");
                    }
                }
        );
    }

    private void clearRegistrationFields() {
        regFirstNameField.clear();
        regLastNameField.clear();
        regUsernameField.clear();
        regEmailField.clear();
        regPasswordField.clear();
        regConfirmPasswordField.clear();
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\").replace("\"", "\\\"");
    }



    private void executeApiCall(String url, String body, String method, java.util.function.Consumer<HttpResponse<String>> onSuccess) {
        setLoading(true);

        Task<HttpResponse<String>> task = new Task<>() {
            @Override
            protected HttpResponse<String> call() throws Exception {
                HttpRequest.Builder builder = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(30));

                if ("POST".equalsIgnoreCase(method)) {
                    builder.header("Content-Type", "application/json");
                    builder.POST(body.isEmpty() ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
                }
                return httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString());
            }
        };

        task.setOnFailed(e -> {
            setLoading(false);
            showAlert(Alert.AlertType.ERROR, "Errore Connessione", "Impossibile contattare il server o richiesta in timeout.");
        });

        task.setOnSucceeded(e -> {
            setLoading(false);
            onSuccess.accept(task.getValue());
        });

        new Thread(task).start();
    }

    @FXML public void goToHome(ActionEvent event) { switchScene(event, "/it/unical/ea_project_javafx/fxml/mainview.fxml"); }

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            Node node = (Node) event.getSource();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) node.getScene().getWindow();
            stage.setScene(new Scene(root, stage.getScene().getWidth(), stage.getScene().getHeight()));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showStep(VBox stepToShow) {
        stepLogin.setVisible(false); stepLogin.setManaged(false);
        stepRole.setVisible(false); stepRole.setManaged(false);
        stepDetails.setVisible(false); stepDetails.setManaged(false);
        stepToShow.setVisible(true); stepToShow.setManaged(true);

        if (btnLogin != null) btnLogin.setDefaultButton(stepToShow == stepLogin);
        if (btnRegister != null) btnRegister.setDefaultButton(stepToShow == stepDetails);
    }

    private void setLoading(boolean isLoading) {
        loadingOverlay.setVisible(isLoading);
        loadingOverlay.setManaged(isLoading);
        btnLogin.setDisable(isLoading);
        btnRegister.setDisable(isLoading);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}