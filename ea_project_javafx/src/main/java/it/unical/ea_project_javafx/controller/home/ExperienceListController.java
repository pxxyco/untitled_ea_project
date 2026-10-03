package it.unical.ea_project_javafx.controller.home;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unical.ea_project_javafx.dto.home.ActivityHomeDTO;
import it.unical.ea_project_javafx.dto.home.TripHomeDTO;
import it.unical.ea_project_javafx.util.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

public class ExperienceListController {

    public Label label1;
    public Label label2;
    @FXML private VBox cardsContainer;
    @FXML private Label loadMoreLabel;
    @FXML private VBox loadingIndicatorBox;
    @FXML private javafx.scene.control.Button resetButton;

    @Setter private SearchBarController searchBarController;

    private static final int PAGE_SIZE = 9;
    private int currentPage = 0;
    private boolean loading = false;
    private int requestCounter = 0;

    private String currentQuery = "";
    private String currentCategoryFilter = null;
    private LocalDate currentDateFilter = null;
    private static final Gson GSON = new Gson();

    public enum SearchMode { TOP, ACTIVITY, TRIP }
    private SearchMode mode = SearchMode.TOP;

    @FXML
    public void initialize() {
        loadActivities();
    }

    private boolean resetButtonExists() {
        return resetButton != null;
    }

    private void showLoading(boolean show) {
        if (loadingIndicatorBox != null) {
            loadingIndicatorBox.setVisible(show);
            loadingIndicatorBox.setManaged(show);
        }
        if (loadMoreLabel != null && show) {
            loadMoreLabel.setDisable(true);
        }
    }

    private <T> void loadPage(String url, Type type, Consumer<List<T>> onCardsLoaded) {
        if (loading) return;
        loading = true;
        showLoading(true);

        final int currentRequest = requestCounter;

        ApiService.get(url, response -> {
            if (response.statusCode() == 200) {
                List<T> dtos = GSON.fromJson(response.body(), type);
                List<T> safeList = dtos != null ? dtos : List.of();
                Platform.runLater(() -> {
                    if (currentRequest != requestCounter) return;

                    currentPage++;
                    if (safeList.size() < PAGE_SIZE) disableLoadMore();
                    else resetLoadMoreLabel();

                    onCardsLoaded.accept(safeList);

                    loading = false;
                    showLoading(false);
                });
            } else {
                Platform.runLater(() -> {
                    if (currentRequest != requestCounter) return;
                    loading = false;
                    showLoading(false);
                    resetLoadMoreLabel();
                });
            }
        }, () -> Platform.runLater(() -> {
            if (currentRequest != requestCounter) return;
            loading = false;
            showLoading(false);
            disableLoadMore();
        }), null);
    }

    private void loadActivities() {
        String url = String.format("%s/api/activities/top/cards?page=%d&size=%d", ApiService.BASE_URL, currentPage, PAGE_SIZE);
        Type type = new TypeToken<List<ActivityHomeDTO>>() {}.getType();
        loadPage(url, type, this::appendActivityCards);
    }

    private void loadSearchResults() {
        StringBuilder url = new StringBuilder(ApiService.BASE_URL + "/api/activities/search?page=" + currentPage + "&size=" + PAGE_SIZE);
        if (!currentQuery.isEmpty())
            url.append("&query=").append(URLEncoder.encode(currentQuery, StandardCharsets.UTF_8));
        if (currentCategoryFilter != null && !currentCategoryFilter.isEmpty())
            url.append("&category=").append(URLEncoder.encode(currentCategoryFilter, StandardCharsets.UTF_8));
        if (currentDateFilter != null)
            url.append("&date=").append(currentDateFilter);

        Type type = new TypeToken<List<ActivityHomeDTO>>() {}.getType();
        loadPage(url.toString(), type, this::appendActivityCards);
    }

    private void loadTripResults() {
        StringBuilder url = new StringBuilder(ApiService.BASE_URL + "/api/trips/search?page=" + currentPage + "&size=" + PAGE_SIZE);
        if (!currentQuery.isEmpty())
            url.append("&query=").append(URLEncoder.encode(currentQuery, StandardCharsets.UTF_8));
        if (currentDateFilter != null)
            url.append("&date=").append(currentDateFilter);

        Type type = new TypeToken<List<TripHomeDTO>>() {}.getType();
        loadPage(url.toString(), type, this::appendTripCards);
    }

    @FXML
    public void handleLoadMore(MouseEvent mouseEvent) {
        if (loading) return;
        if (loadMoreLabel != null && loadMoreLabel.isDisabled()) return;
        loadNextPage();
    }

    private void loadNextPage() {
        switch (mode) {
            case TOP      -> loadActivities();
            case ACTIVITY -> loadSearchResults();
            case TRIP     -> loadTripResults();
        }
    }

    private void disableLoadMore() {
        if (loadMoreLabel == null) return;
        loadMoreLabel.setText("Nessun'altra attività da mostrare");
        loadMoreLabel.setDisable(true);
        if (!loadMoreLabel.getStyleClass().contains("label-loading")) {
            loadMoreLabel.getStyleClass().add("label-loading");
        }
    }

    private void resetLoadMoreLabel() {
        if (loadMoreLabel == null) return;
        loadMoreLabel.setText("Carica altro");
        loadMoreLabel.setDisable(false);
        loadMoreLabel.getStyleClass().remove("label-loading");
    }

    private void appendActivityCards(List<ActivityHomeDTO> activities) {
        appendCardsGeneric(activities, ExperienceCardController::setData);
    }

    private void appendTripCards(List<TripHomeDTO> trips) {
        appendCardsGeneric(trips, ExperienceCardController::setData);
    }

    private <T> void appendCardsGeneric(List<T> items, Consumer2<ExperienceCardController, T> binder) {
        if (cardsContainer == null || items == null || items.isEmpty()) return;

        final int CARDS_PER_ROW = 3;
        HBox currentRow = null;

        if (!cardsContainer.getChildren().isEmpty()) {
            javafx.scene.Node lastNode = cardsContainer.getChildren().get(cardsContainer.getChildren().size() - 1);
            if (lastNode instanceof HBox row && row.getChildren().size() < CARDS_PER_ROW) {
                currentRow = row;
            }
        }

        for (T item : items) {
            if (currentRow == null || currentRow.getChildren().size() >= CARDS_PER_ROW) {
                currentRow = new HBox(16);
                currentRow.getStyleClass().add("cards-row");
                currentRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                cardsContainer.getChildren().add(currentRow);
            }
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unical/ea_project_javafx/fxml/home/experience-card.fxml"));
                VBox cardNode = loader.load();
                ExperienceCardController controller = loader.getController();
                binder.accept(controller, item);
                currentRow.getChildren().add(cardNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FunctionalInterface
    private interface Consumer2<T, U> {
        void accept(T t, U u);
    }

    public void search(SearchMode mode, String query, String category, LocalDate date) {
        this.mode = mode;
        currentQuery = query == null ? "" : query.trim();
        currentCategoryFilter = category;
        currentDateFilter = date;
        currentPage = 0;
        requestCounter++;

        if (mode == SearchMode.TRIP) {
            label1.setText("Ricerca tra i viaggi");
        } else if (category != null) {
            label1.setText("Ricerca su categoria " + category);
        } else {
            label1.setText("Ricerca su tutte le attività");
        }
        label2.setText(currentQuery);

        if (cardsContainer != null) cardsContainer.getChildren().clear();
        loading = false;
        resetLoadMoreLabel();
        if (resetButtonExists()) showResetButton(true);
        loadNextPage();
    }

    @FXML
    public void handleReset(javafx.event.ActionEvent event) {
        resetSearch();
    }

    public void resetSearch() {
        label1.setText("Esperienze più apprezzate dai viaggiatori");
        label2.setText("Valutate da utenti reali che hanno partecipato alle attività");
        currentQuery = "";
        currentCategoryFilter = null;
        currentDateFilter = null;
        mode = SearchMode.TOP;
        currentPage = 0;
        loading = false;
        requestCounter++;

        resetLoadMoreLabel();
        if (cardsContainer != null) {
            cardsContainer.getChildren().clear();
        }
        if (resetButtonExists()) {
            showResetButton(false);
        }
        if (searchBarController != null) {
            searchBarController.reset();
        }
        loadActivities();
    }

    private void showResetButton(boolean show) {
        if (resetButton == null) return;
        resetButton.setVisible(show);
        resetButton.setManaged(show);
    }
}