package it.unical.ea_project_javafx.controller.home;

import it.unical.ea_project_javafx.model.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import lombok.Setter;

import java.time.LocalDate;

//logica barra di ricerca
public class SearchBarController {

    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker datePicker;

    @Setter
    private ExperienceListController experienceListController;

    @FXML
    public void initialize() {
        createCombobox();
        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
        }
    }

    void createCombobox()
    {
        boolean isLoggedIn = UserSession.getInstance().isLoggedIn();

        if (categoryComboBox != null && isLoggedIn) {
            ObservableList<String> categories = FXCollections.observableArrayList(
                    "Tutto", "Attività", "Attività Prenotate",
                    "Viaggi", "Viaggi Prenotati");
            categoryComboBox.setItems(categories);
            categoryComboBox.getSelectionModel().selectFirst();
        }
        else if (categoryComboBox != null)
        {
            ObservableList<String> categories = FXCollections.observableArrayList(
                    "Tutto", "Attività",
                    "Viaggi");
            categoryComboBox.setItems(categories);
            categoryComboBox.getSelectionModel().selectFirst();
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {
        String selectedCategory = categoryComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();

    }
}