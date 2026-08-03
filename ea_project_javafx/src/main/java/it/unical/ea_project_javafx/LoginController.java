package it.unical.ea_project_javafx;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController {

    @FXML private ImageView bgImageView;
    @FXML private VBox stepLogin;
    @FXML private VBox stepRole;
    @FXML private VBox stepDetails;
    @FXML private Label wizardTitle;
    @FXML private ComboBox<String> nationalityCombo;

    @FXML
    public void initialize() {
        if (bgImageView != null) {
            bgImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null && bgImageView.getParent() instanceof StackPane) {
                    StackPane parentPane = (StackPane) bgImageView.getParent();
                    bgImageView.fitWidthProperty().bind(parentPane.widthProperty());
                    bgImageView.fitHeightProperty().bind(parentPane.heightProperty());
                }
            });
        }

        if (nationalityCombo != null) {
            nationalityCombo.setItems(FXCollections.observableArrayList(
                    "Italy", "France", "Germany", "United States"
            ));
        }

        showStep(stepLogin);
    }

    @FXML
    void goToLogin(ActionEvent event) {
        showStep(stepLogin);
    }

    @FXML
    void goToRoleSelection(ActionEvent event) {
        showStep(stepRole);
    }

    @FXML
    void selectPersonalRole(MouseEvent event) {
        wizardTitle.setText("Profilo Personal");
        showStep(stepDetails);
    }

    @FXML
    void selectBusinessRole(MouseEvent event) {
        wizardTitle.setText("Profilo Business");
        showStep(stepDetails);
    }

    @FXML
    void handleLoginSubmit(ActionEvent event) {
        System.out.println("Esecuzione Login...");
        // TODO: Aggiungi qui la chiamata al Backend / Database

        // Se l'autenticazione ha successo, portalo alla Home:
        switchScene(event, "/it/unical/ea_project_javafx/fxml/mainview.fxml");
    }

    @FXML
    void handleRegistrationSubmit(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successo");
        alert.setHeaderText(null);
        alert.setContentText("Registrazione completata con successo!");
        alert.showAndWait();

        // Torna al login dopo la registrazione
        showStep(stepLogin);
    }


    @FXML
    public void goToHome(ActionEvent actionEvent) {
        switchScene(actionEvent, "/it/unical/ea_project_javafx/fxml/mainview.fxml");
    }

    @FXML
    public void goToHomeFromMouse(MouseEvent mouseEvent) {
        switchSceneFromNode((Node) mouseEvent.getSource(), "/it/unical/ea_project_javafx/fxml/mainview.fxml");
    }

    private void switchScene(ActionEvent event, String fxmlPath) {
        Node sourceNode = (Node) event.getSource();
        switchSceneFromNode(sourceNode, fxmlPath);
    }

    private void switchSceneFromNode(Node node, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Stage stage = (Stage) node.getScene().getWindow();

            double width = stage.getScene() != null ? stage.getScene().getWidth() : 1000;
            double height = stage.getScene() != null ? stage.getScene().getHeight() : 700;

            stage.setScene(new Scene(root, width, height));
            stage.show();
        } catch (IOException e) {
            System.err.println("Errore nel caricamento del file FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    private void showStep(VBox stepToShow) {
        stepLogin.setVisible(false);
        stepLogin.setManaged(false);
        stepRole.setVisible(false);
        stepRole.setManaged(false);
        stepDetails.setVisible(false);
        stepDetails.setManaged(false);
        stepToShow.setVisible(true);
        stepToShow.setManaged(true);
    }
}