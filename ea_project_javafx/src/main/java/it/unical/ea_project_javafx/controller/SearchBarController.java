package it.unical.ea_project_javafx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;

//logica barra di ricerca
public class SearchBarController {

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker datePicker;

    private ExperienceListController experienceListController;

    public void setExperienceListController(ExperienceListController controller) {
        this.experienceListController = controller;
    }

    @FXML
    public void initialize() {
        if (categoryComboBox != null) {
            ObservableList<String> categories = FXCollections.observableArrayList(
                    "Tutto", "Attività", "Attività Prenotate",
                    "Viaggi", "Viaggi Prenotati");
            categoryComboBox.setItems(categories);
            categoryComboBox.getSelectionModel().selectFirst();
        }
        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String selectedCategory = categoryComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        System.out.println("Ricerca avviata per: " + selectedCategory + " nella data: " + selectedDate);

        if (experienceListController != null) {
            experienceListController.filterBySearchCriteria(selectedCategory, selectedDate);
        }
    }
}