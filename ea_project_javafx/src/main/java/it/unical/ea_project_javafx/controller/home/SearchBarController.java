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
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import lombok.Setter;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class SearchBarController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private DatePicker datePicker;

    @Setter
    private ExperienceListController experienceListController;

    private ContextMenu suggestionsPopup;

    private final PauseTransition debounce =
            new PauseTransition(Duration.millis(300));

    private static final Gson GSON = new Gson();

    /*
     * Identifica la versione più recente della richiesta.
     *
     * Serve a ignorare risposte vecchie arrivate in ritardo.
     */
    private long suggestionRequestId = 0;

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

        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
        }

        if (searchField != null) {

            suggestionsPopup = new ContextMenu();

            /*
             * Configuriamo il debounce UNA SOLA VOLTA.
             */
            debounce.setOnFinished(
                    event -> fetchSuggestions(
                            searchField.getText()
                    )
            );

            searchField.textProperty().addListener(
                    (observable, oldValue, newValue) -> {

                        debounce.stop();

                        /*
                         * Se il campo è vuoto,
                         * chiudiamo subito i suggerimenti.
                         */
                        if (newValue == null
                                || newValue.isBlank()) {

                            suggestionsPopup.hide();
                            return;
                        }

                        debounce.playFromStart();
                    }
            );
        }
    }

    /**
     * Crea le categorie visualizzate nella ComboBox.
     */
    private void createCombobox() {

        ObservableList<String> categories =
                FXCollections.observableArrayList(
                        "Tutto",
                        "Escursione",
                        "Visita",
                        "Trasporto",
                        "Hotel",
                        "Ristorazione",
                        "Altro"
                );

        if (categoryComboBox != null) {

            categoryComboBox.setItems(categories);

            categoryComboBox
                    .getSelectionModel()
                    .selectFirst();
        }
    }

    /**
     * Recupera i suggerimenti dal backend.
     */
    private void fetchSuggestions(String query) {

        if (query == null || query.isBlank()) {

            Platform.runLater(
                    () -> suggestionsPopup.hide()
            );

            return;
        }

        String normalizedQuery = query.trim();

        /*
         * Ogni richiesta riceve un ID progressivo.
         */
        long requestId = ++suggestionRequestId;

        String url =
                ApiService.BASE_URL
                        + "/api/activities/suggest?query="
                        + URLEncoder.encode(
                        normalizedQuery,
                        StandardCharsets.UTF_8
                )
                        + "&limit=6";

        ApiService.get(
                url,

                response -> {

                    if (response.statusCode() != 200) {
                        return;
                    }

                    List<ActivitySuggestionDTO> suggestions =
                            parseSuggestions(response.body());

                    Platform.runLater(() -> {

                        /*
                         * Se nel frattempo è partita
                         * una richiesta più recente,
                         * ignoriamo questa risposta.
                         */
                        if (requestId != suggestionRequestId) {
                            return;
                        }

                        showSuggestions(suggestions);
                    });
                },

                () -> Platform.runLater(() -> {

                    if (requestId == suggestionRequestId) {
                        suggestionsPopup.hide();
                    }
                }),

                null
        );
    }

    /**
     * Mostra i suggerimenti sotto il campo di ricerca.
     */
    private void showSuggestions(
            List<ActivitySuggestionDTO> suggestions
    ) {

        suggestionsPopup.getItems().clear();

        if (suggestions == null
                || suggestions.isEmpty()) {

            suggestionsPopup.hide();
            return;
        }

        for (ActivitySuggestionDTO suggestion : suggestions) {

            String city =
                    suggestion.getCity() != null
                            ? suggestion.getCity()
                            : "";

            MenuItem menuItem =
                    new MenuItem(
                            suggestion.getTitle()
                                    + " - "
                                    + city
                    );

            menuItem.setOnAction(event -> {

                searchField.setText(
                        suggestion.getTitle()
                );

                suggestionsPopup.hide();
            });

            suggestionsPopup
                    .getItems()
                    .add(menuItem);
        }

        if (!suggestionsPopup.isShowing()) {

            Point2D point =
                    searchField.localToScreen(
                            0,
                            searchField.getHeight()
                    );

            suggestionsPopup.show(
                    searchField,
                    point.getX(),
                    point.getY()
            );
        }
    }

    /**
     * Converte il JSON dei suggerimenti in DTO.
     */
    private List<ActivitySuggestionDTO> parseSuggestions(
            String jsonBody
    ) {

        try {

            Type listType =
                    new TypeToken<List<ActivitySuggestionDTO>>() {
                    }.getType();

            List<ActivitySuggestionDTO> result =
                    GSON.fromJson(
                            jsonBody,
                            listType
                    );

            return result != null
                    ? result
                    : List.of();

        } catch (Exception e) {

            e.printStackTrace();

            return List.of();
        }
    }

    /**
     * Avvia la ricerca vera e propria.
     */
    @FXML
    void handleSearch(ActionEvent event) {

        if (experienceListController == null) {
            return;
        }

        String query =
                searchField != null
                        ? searchField.getText()
                        : "";

        String selectedLabel =
                categoryComboBox != null
                        ? categoryComboBox.getValue()
                        : null;

        /*
         * "Tutto" non è presente nella mappa,
         * quindi restituisce null.
         */
        String backendCategory =
                CATEGORY_MAP.get(selectedLabel);

        experienceListController.search(
                query,
                backendCategory
        );
    }
}