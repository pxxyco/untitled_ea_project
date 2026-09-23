package it.unical.ea_project_javafx.controller.home;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unical.ea_project_javafx.dto.home.ActivityHomeDTO;
import it.unical.ea_project_javafx.util.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ExperienceListController {

    @FXML private VBox cardsContainer;
    @FXML Label loadMoreLabel;
    @FXML private VBox loadingIndicatorBox;
    @FXML private javafx.scene.control.Button resetButton;

    @Getter private final List<ExperienceData> items = new ArrayList<>();

    public record ExperienceData(
            String title,
            String location,
            String category,
            String price,
            String rating,
            String imageUrl,
            String dateInfo
    ) {
    }

    private static final int PAGE_SIZE = 9;
    private int currentPage = 0;
    private boolean loading = false;
    private String currentQuery = "";
    private String currentCategoryFilter = null;
    private static final Gson GSON = new Gson();

    @FXML public void initialize() {
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

    private void loadActivities() {

        if (loading) {
            return;
        }

        loading = true;
        showLoading(true);

        int pageToLoad = currentPage;

        String url = String.format(
                "%s/api/activities/top/cards?page=%d&size=%d",
                ApiService.BASE_URL,
                pageToLoad,
                PAGE_SIZE
        );
        ApiService.get(
                url,
                response -> {
                    if (response.statusCode() == 200) {
                        List<ExperienceData> newActivities = parseActivities(response.body());
                        Platform.runLater(() -> {
                            items.addAll(newActivities);
                            currentPage++;
                            if (newActivities.isEmpty()
                                    || newActivities.size() < PAGE_SIZE) {
                                disableLoadMore();
                            } else {
                                resetLoadMoreLabel();
                            }
                            appendCards(newActivities);
                            loading = false;
                            showLoading(false);
                        });
                    } else {
                        Platform.runLater(() -> {
                            loading = false;
                            showLoading(false);
                            resetLoadMoreLabel();
                        });
                    }
                },
                () -> Platform.runLater(() -> {
                    loading = false;
                    showLoading(false);
                    disableLoadMore();
                }),
                null
        );
    }

    @FXML
    public void handleLoadMore(MouseEvent mouseEvent) {

        if (loading) {
            return;
        }

        if (loadMoreLabel != null
                && loadMoreLabel.isDisabled()) {
            return;
        }
        if (currentQuery.isEmpty()
                && currentCategoryFilter == null) {

            loadActivities();

        } else {

            loadSearchResults();
        }
    }

    private void disableLoadMore() {
        if (loadMoreLabel == null) {
            return;
        }
        loadMoreLabel.setText(
                "Nessun'altra attività da mostrare"
        );
        loadMoreLabel.setDisable(true);
        if (!loadMoreLabel.getStyleClass().contains("label-loading")) {
            loadMoreLabel.getStyleClass().add("label-loading");
        }
    }

    private void resetLoadMoreLabel() {
        if (loadMoreLabel == null) {
            return;
        }
        loadMoreLabel.setText("Carica altro");
        loadMoreLabel.setDisable(false);
        loadMoreLabel.getStyleClass().remove("label-loading");
    }

    private List<ExperienceData> parseActivities(String jsonBody) {
        List<ExperienceData> result = new ArrayList<>();
        try {
            Type listType = new TypeToken<List<ActivityHomeDTO>>() {}.getType();
            List<ActivityHomeDTO> dtos = GSON.fromJson(jsonBody, listType);
            if (dtos != null) {
                for (ActivityHomeDTO dto : dtos) {
                    result.add(
                            new ExperienceData(
                                    dto.getTitle() != null
                                            ? dto.getTitle()
                                            : "Senza Titolo",
                                    dto.getCity() != null
                                            ? dto.getCity()
                                            : "",
                                    dto.getCategory() != null
                                            ? dto.getCategory()
                                            : "Attività",
                                    String.format(
                                            "€%.0f",
                                            dto.getPrice() != null
                                                    ? dto.getPrice()
                                                    : 0.0
                                    ),
                                    String.format(
                                            "%.1f",
                                            dto.getAverageRating() != null
                                                    ? dto.getAverageRating()
                                                    : 5.0
                                    ),
                                    dto.getImageUrl() != null
                                            ? dto.getImageUrl()
                                            : "",
                                    ""
                            )
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void appendCards(List<ExperienceData> newCards)
    {
        if (cardsContainer == null || newCards == null || newCards.isEmpty()) {
            return;
        }
        final int CARDS_PER_ROW = 3;
        HBox currentRow = null;

        if (!cardsContainer.getChildren().isEmpty()) {
            javafx.scene.Node lastNode =
                    cardsContainer.getChildren().get(cardsContainer.getChildren().size() - 1);

            if (lastNode instanceof HBox row
                    && row.getChildren().size() < CARDS_PER_ROW) {

                currentRow = row;
            }
        }

        for (ExperienceData data : newCards) {
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
                controller.setData(data.title(), data.location(), data.category(), data.price(), data.rating(), data.imageUrl());
                currentRow.getChildren().add(cardNode);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void search(String query, String category)
    {
        currentQuery = query == null ? "" : query.trim();
        currentCategoryFilter = category;
        currentPage = 0;
        items.clear();

        if (cardsContainer != null) {
            cardsContainer.getChildren().clear();
        }

        loading = false;
        resetLoadMoreLabel();
        if (resetButtonExists()) {
            showResetButton(true);
        }
        loadSearchResults();
    }

    @FXML
    public void handleReset(javafx.event.ActionEvent event) {
        resetSearch();
    }


    public void resetSearch() {
        currentQuery = "";
        currentCategoryFilter = null;
        currentPage = 0;
        items.clear();
        loading = false;
        resetLoadMoreLabel();
        if (cardsContainer != null) {
            cardsContainer.getChildren().clear();
        }
        if (resetButtonExists()) {
            showResetButton(false);
        }
        loadActivities();
    }

    private void showResetButton(boolean show) {

        if (resetButton == null) {
            return;
        }

        resetButton.setVisible(show);
        resetButton.setManaged(show);
    }

    private void loadSearchResults() {
        if (loading) {return;}
        loading = true;
        showLoading(true);
        int pageToLoad = currentPage;
        StringBuilder url = new StringBuilder(ApiService.BASE_URL + "/api/activities/search" + "?page=" + pageToLoad + "&size=" + PAGE_SIZE);
        if (currentQuery != null && !currentQuery.isEmpty())
        {
            url.append("&query=").append(java.net.URLEncoder.encode(currentQuery, java.nio.charset.StandardCharsets.UTF_8));
        }
        if (currentCategoryFilter != null && !currentCategoryFilter.isEmpty())
        {
            url.append("&category=").append(java.net.URLEncoder.encode(currentCategoryFilter, java.nio.charset.StandardCharsets.UTF_8));
        }

        ApiService.get(url.toString(),

                response -> {
                    if (response.statusCode() == 200) {
                        List<ExperienceData> newActivities =
                                parseActivities(response.body());
                        Platform.runLater(() -> {
                            items.addAll(newActivities);
                            currentPage++;
                            if (newActivities.isEmpty()
                                    || newActivities.size() < PAGE_SIZE) {
                                disableLoadMore();
                            } else {
                                resetLoadMoreLabel();
                            }
                            appendCards(newActivities);
                            loading = false;
                            showLoading(false);
                        });
                    } else {

                        Platform.runLater(() -> {

                            loading = false;
                            showLoading(false);
                            resetLoadMoreLabel();
                        });
                    }
                },
                () -> Platform.runLater(() -> {

                    loading = false;
                    showLoading(false);
                    disableLoadMore();
                }),
                null
        );
    }
}