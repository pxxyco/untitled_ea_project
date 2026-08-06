package it.unical.ea_project_javafx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;

public class SearchBarController {

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker datePicker;

    @FXML
    public void initialize() {
        if (categoryComboBox != null) {
            ObservableList<String> categories = FXCollections.observableArrayList(
                    "Tutte le categorie", "Quad & 4x4", "Degustazioni",
                    "Musei & Cultura", "Trekking & Natura", "Tour in Barca");
            categoryComboBox.setItems(categories);
            categoryComboBox.getSelectionModel().selectFirst();
        }
        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        // todo collegare il backend
    }
}