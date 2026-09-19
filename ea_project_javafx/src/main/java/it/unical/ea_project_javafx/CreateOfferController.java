package it.unical.ea_project_javafx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class CreateOfferController {

    @FXML private StackPane rootPane;
    @FXML private Label headerTitleLabel;
    @FXML private VBox stepChoose;
    @FXML private VBox stepTrip;
    @FXML private VBox stepActivity;
    @FXML private VBox stagesContainer;

    @FXML private TextField tripTitleField;
    @FXML private TextField tripCountryField;
    @FXML private TextField tripCityField;
    @FXML private DatePicker tripStartDatePicker;
    @FXML private DatePicker tripEndDatePicker;
    @FXML private TextField tripPriceField;
    @FXML private TextField tripMaxSeatsField;
    @FXML private TextField tripCoverUrlField;
    @FXML private TextArea tripDescriptionArea;

    @FXML private TextField actTitleField;
    @FXML private ComboBox<String> actCategoryCombo;
    @FXML private TextField actPlaceField;
    @FXML private TextField actCityField;
    @FXML private TextField actDurationField;
    @FXML private TextField actPriceField;
    @FXML private TextField actMaxSeatsField;
    @FXML private DatePicker actStartDatePicker;
    @FXML private DatePicker actEndDatePicker;
    @FXML private TextArea actDescriptionArea;
    @FXML private TextArea actNotesArea;

    @FXML
    public void initialize() {
        if (actCategoryCombo != null) {
            actCategoryCombo.getItems().setAll("Excursion", "Tasting", "Guided Tour", "Sport", "Workshop", "Relax");
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleSelectTrip() {
        stepChoose.setVisible(false);
        stepChoose.setManaged(false);
        stepTrip.setVisible(true);
        stepTrip.setManaged(true);
        headerTitleLabel.setText("Nuovo Viaggio (Itinerario)");
    }

    @FXML
    private void handleSelectActivity() {
        stepChoose.setVisible(false);
        stepChoose.setManaged(false);
        stepActivity.setVisible(true);
        stepActivity.setManaged(true);
        headerTitleLabel.setText("Nuova Attività Singola");
    }

    @FXML
    private void handleBackToChoose() {
        stepTrip.setVisible(false);
        stepTrip.setManaged(false);
        stepActivity.setVisible(false);
        stepActivity.setManaged(false);
        stepChoose.setVisible(true);
        stepChoose.setManaged(true);
        headerTitleLabel.setText("Crea Nuova Offerta");
    }

    @FXML
    private void handleAddStageRow() {
    }

    @FXML
    private void handleSaveTrip() {
    }

    @FXML
    private void handleSaveActivity() {
    }
}