package it.unical.ea_project_javafx.controller.home;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.unical.ea_project_javafx.dto.home.ActivityHomeDTO;
import it.unical.ea_project_javafx.util.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ExperienceListController {

    @FXML private HBox categoriesBar;
    @FXML private VBox cardsContainer;
    @FXML private Label loadMoreLabel;

    public record ExperienceData(
            String title,
            String location,
            String category,
            String price,
            String rating,
            String imageUrl,
            String dateInfo
    ) {}

    private final List<ExperienceData> items = new ArrayList<>();
    private int currentPage = 0;
    private final int pageSize = 9;
    private boolean loading = false;
    private String activeCategory = "Tutte";

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class,
                    (com.google.gson.JsonDeserializer<LocalDate>) (json, typeOfT, context) -> {
                        String val = json.getAsString();
                        return (val == null || val.isEmpty()) ? null : LocalDate.parse(val.substring(0, 10));
                    })
            .registerTypeAdapter(LocalDateTime.class,
                    (com.google.gson.JsonDeserializer<LocalDateTime>) (json, typeOfT, context) -> {
                        String val = json.getAsString();
                        return (val == null || val.isEmpty()) ? null : LocalDateTime.parse(val);
                    })
            .registerTypeAdapter(LocalTime.class,
                    (com.google.gson.JsonDeserializer<LocalTime>) (json, typeOfT, context) -> {
                        String val = json.getAsString();
                        return (val == null || val.isEmpty()) ? null : LocalTime.parse(val);
                    })
            .create();

    @FXML
    public void initialize() {
        loadActivities();
    }

    private void loadActivities() {
        if (loading) {
            return;
        }
        loading = true;
        int pageToLoad = currentPage;
        String url = String.format(
                "%s/api/activities/top/cards?page=%d&size=%d",
                ApiService.BASE_URL,
                currentPage,
                pageSize
        );
        ApiService.get(
                url,
                resAct -> {
                    if (resAct.statusCode() == 200) {
                        List<ExperienceData> newActivities =
                                parseActivities(resAct.body());
                        Platform.runLater(() -> {
                            items.addAll(newActivities);
                            currentPage++;
                            if (newActivities.isEmpty()
                                    || newActivities.size() < pageSize) {
                                disableLoadMore();
                            }
                            setupCategoryChips();
                            appendCards(newActivities);
                            loading = false;
                        });
                    } else {
                        Platform.runLater(() -> {
                            loading = false;
                        });
                    }
                },
                () -> Platform.runLater(() -> {
                    loading = false;
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
        if (loadMoreLabel != null && loadMoreLabel.isDisabled()) {
            return;
        }
        loadActivities();
    }

    private void disableLoadMore() {
        if (loadMoreLabel != null) {
            loadMoreLabel.setText("Nessun'altra attività da mostrare");
            loadMoreLabel.setDisable(true);
            loadMoreLabel.setStyle("-fx-opacity: 0.6; -fx-cursor: default; -fx-font-weight: normal;");
        }
    }

    private void setupCategoryChips() {
        if (categoriesBar == null) return;
        categoriesBar.getChildren().clear();

        List<String> categories = new ArrayList<>();
        categories.add("Tutte");

        items.stream()
                .map(ExperienceData::category)
                .filter(cat -> cat != null && !cat.isEmpty())
                .distinct()
                .forEach(categories::add);

        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            Button chipBtn = new Button(category);
            chipBtn.getStyleClass().add("category-chip");
            if (i == 0) {
                chipBtn.getStyleClass().add("chip-active");
            }

            chipBtn.setOnAction(e -> {
                categoriesBar.getChildren()
                        .forEach(node -> node.getStyleClass().remove("chip-active"));
                chipBtn.getStyleClass().add("chip-active");
                activeCategory = category;
                filterByCategory(category);
            });

            categoriesBar.getChildren().add(chipBtn);
        }
    }

    private void filterByCategory(String category) {
        if ("Tutte".equalsIgnoreCase(category)) {
            renderCards(items);
        } else {
            List<ExperienceData> filtered = new ArrayList<>();
            for (ExperienceData item : items) {
                if (category.equalsIgnoreCase(item.category())) {
                    filtered.add(item);
                }
            }
            renderCards(filtered);
        }
    }

    private List<ExperienceData> parseActivities(String jsonBody) {
        List<ExperienceData> result = new ArrayList<>();
        try {
            Type listType =
                    new TypeToken<List<ActivityHomeDTO>>() {}.getType();
            List<ActivityHomeDTO> dtos =
                    GSON.fromJson(jsonBody, listType);
            if (dtos != null) {
                for (ActivityHomeDTO dto : dtos) {
                    result.add(new ExperienceData(
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
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private void appendCards(List<ExperienceData> newCards) {
        if (cardsContainer == null || newCards == null || newCards.isEmpty()) {
            return;
        }
        final int CARDS_PER_ROW = 3;
        HBox currentRow = null;
        if (!cardsContainer.getChildren().isEmpty()) {
            javafx.scene.Node lastNode =
                    cardsContainer.getChildren()
                            .get(cardsContainer.getChildren().size() - 1);
            if (lastNode instanceof HBox row
                    && row.getChildren().size() < CARDS_PER_ROW) {
                currentRow = row;
            }
        }
        for (ExperienceData data : newCards) {
            if (!"Tutte".equalsIgnoreCase(activeCategory)
                    && !activeCategory.equalsIgnoreCase(data.category())) {
                continue;
            }
            if (currentRow == null
                    || currentRow.getChildren().size() >= CARDS_PER_ROW) {
                currentRow = new HBox(16);
                currentRow.getStyleClass().add("cards-row");
                currentRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                cardsContainer.getChildren().add(currentRow);
            }

            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource(
                                "/it/unical/ea_project_javafx/fxml/home/experience-card.fxml"
                        )
                );
                VBox cardNode = loader.load();
                ExperienceCardController controller =
                        loader.getController();
                controller.setData(
                        data.title(),
                        data.location(),
                        data.category(),
                        data.price(),
                        data.rating(),
                        data.imageUrl()
                );
                currentRow.getChildren().add(cardNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void renderCards(List<ExperienceData> cardsToDisplay) {

        if (cardsContainer == null) {
            return;
        }
        cardsContainer.getChildren().clear();
        appendCards(cardsToDisplay);
    }
}