package it.unical.ea_project_javafx.controller.home;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unical.ea_project_javafx.dto.home.ActivitySuggestionDTO;
import it.unical.ea_project_javafx.util.ApiService;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.util.Duration;
import lombok.Setter;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class SearchBarController {

    public CheckBox SetData;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker datePicker;

    @Setter private ExperienceListController experienceListController;

    private ContextMenu suggestionsPopup;
    private final PauseTransition debounce = new PauseTransition(Duration.millis(300));
    private static final Gson GSON = new Gson();
    private long suggestionRequestId = 0;
    private boolean suppressSuggestions = false;

    private static final Map<String, String> CATEGORY_MAP =
            Map.of(
                    "Escursione", "EXCURSION",
                    "Visita", "VISIT",
                    "Trasporto", "TRANSPORT",
                    "Hotel", "HOTEL",
                    "Ristorazione", "MEAL",
                    "Altro", "OTHER"
            );

    @FXML
    public void initialize() {
        createCombobox();
        SetData.setSelected(false);
        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
            datePicker.disableProperty().bind(SetData.selectedProperty().not());
        }
        if (searchField != null) {
            suggestionsPopup = new ContextMenu();
            debounce.setOnFinished(event -> fetchSuggestions(searchField.getText()));
            searchField.textProperty().addListener((observable, oldValue, newValue) ->
                    {
                        if (suppressSuggestions) return;
                        debounce.stop();
                        if (newValue == null || newValue.isBlank())
                        {
                            suggestionsPopup.hide();
                            return;
                        }
                        debounce.playFromStart();
                    }
            );
        }
    }

    private void createCombobox() {
        ObservableList<String> categories = FXCollections.observableArrayList
                ("Tutto", "Escursione", "Visita", "Trasporto", "Hotel", "Ristorazione", "Altro");
        if (categoryComboBox != null) {
            categoryComboBox.setItems(categories);
            categoryComboBox.getSelectionModel().selectFirst();
        }
    }


    private void fetchSuggestions(String query) {

        if (query == null || query.isBlank()) {
            Platform.runLater(() -> suggestionsPopup.hide());
            return;
        }

        String normalizedQuery = query.trim();
        long requestId = ++suggestionRequestId;
        String url = ApiService.BASE_URL + "/api/activities/suggest?query=" + URLEncoder.encode(normalizedQuery, StandardCharsets.UTF_8) + "&limit=6";
        ApiService.get(url, response ->
                {
                    if (response.statusCode() != 200) {
                        Platform.runLater(() -> {
                            if (requestId == suggestionRequestId) {
                                suggestionsPopup.hide();
                            }
                        });
                        return;
                    }
                    List<ActivitySuggestionDTO> suggestions = parseSuggestions(response.body());
                    Platform.runLater(() ->
                    {
                        if (requestId != suggestionRequestId) {return;}
                        showSuggestions(suggestions);
                    });
                },
                () -> Platform.runLater(() ->
                {
                    if (requestId == suggestionRequestId) {
                        suggestionsPopup.hide();
                    }
                }),
                null);
    }

    private void showSuggestions(List<ActivitySuggestionDTO> suggestions) {
        suggestionsPopup.getItems().clear();
        if (suggestions == null || suggestions.isEmpty()) {
            suggestionsPopup.hide();
            return;
        }

        for (ActivitySuggestionDTO suggestion : suggestions) {
            String city = suggestion.getCity() != null ? suggestion.getCity() : "";
            MenuItem menuItem = new MenuItem(suggestion.getTitle() + " - " + city);
            menuItem.setOnAction(event -> {
                suppressSuggestions = true;
                searchField.setText(suggestion.getTitle());
                suppressSuggestions = false;
                suggestionsPopup.hide();
            });

            suggestionsPopup.getItems().add(menuItem);
        }
        if (!suggestionsPopup.isShowing()) {
            Point2D point = searchField.localToScreen(0, searchField.getHeight());
            if (point == null) return;
            suggestionsPopup.show(searchField, point.getX(), point.getY());
        }
    }

    private List<ActivitySuggestionDTO> parseSuggestions(String jsonBody) {
        try {
            Type listType = new TypeToken<List<ActivitySuggestionDTO>>() {}.getType();
            List<ActivitySuggestionDTO> result = GSON.fromJson(jsonBody, listType);
            return result != null ? result : List.of();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }


    @FXML
    void handleSearch(ActionEvent event) {
        if (experienceListController == null) {
            return;
        }
        String query = searchField != null ? searchField.getText() : "";
        String selectedLabel = categoryComboBox != null ? categoryComboBox.getValue() : null;
        String backendCategory = CATEGORY_MAP.get(selectedLabel);
        LocalDate selectedDate = (SetData.isSelected() && datePicker != null)
                ? datePicker.getValue()
                : null;
        experienceListController.search(query, backendCategory, selectedDate);
    }

    public void reset() {
        debounce.stop();
        suggestionRequestId++;
        if (suggestionsPopup != null) {
            suggestionsPopup.hide();
            suggestionsPopup.getItems().clear();
        }
        if (searchField != null) {
            searchField.clear();
        }
        if (categoryComboBox != null) {
            categoryComboBox.getSelectionModel().selectFirst();
        }
        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
        }
        if (SetData != null) {
            SetData.setSelected(false);
        }
    }
}