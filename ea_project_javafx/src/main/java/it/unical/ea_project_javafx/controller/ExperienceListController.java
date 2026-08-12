package it.unical.ea_project_javafx.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unical.ea_project_javafx.dto.ActivityDto;
import it.unical.ea_project_javafx.util.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ExperienceListController {

    @FXML private HBox categoriesBar;
    @FXML private VBox cardsContainer;

    public record ExperienceData(
            String title,
            String location,
            String category,
            String price,
            String rating,
            String imageUrl
    ) {}

    private List<ExperienceData> allActivities = new ArrayList<>();
    private List<CategoryChipData> categoryChips = new ArrayList<>();

    private record CategoryChipData(String label, String categoryKey) {}

    @FXML
    public void initialize() {
        loadActivitiesFromBackend();
    }

    private void loadActivitiesFromBackend() {
        ApiService.call(
                ApiService.BASE_URL + "/api/activities",
                "", "GET",
                res -> Platform.runLater(() -> {
                    if (res.statusCode() == 200) {
                        allActivities = parseActivities(res.body());
                        setupCategoryChips();
                        renderCards(allActivities);
                    }
                }),
                () -> Platform.runLater(() -> {
                    if (cardsContainer != null && cardsContainer.getScene() != null) {
                        it.unical.ea_project_javafx.util.ViewNavigator.loadScene(cardsContainer, "/it/unical/ea_project_javafx/fxml/pre-main.fxml", false);
                    }
                }),
                null
        );
    }

    private List<ExperienceData> parseActivities(String jsonBody) {
        List<ExperienceData> result = new ArrayList<>();
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ActivityDto>>() {}.getType();
            List<ActivityDto> dtos = gson.fromJson(jsonBody, listType);

            if (dtos != null) {
                for (ActivityDto dto : dtos) {
                    result.add(new ExperienceData(
                            dto.getTitle() != null ? dto.getTitle() : "Senza Titolo",
                            dto.getCity() != null ? dto.getCity() : "",
                            dto.getCategory() != null ? dto.getCategory() : "OTHER",
                            String.format("€%.0f", dto.getPrice() != null ? dto.getPrice() : 0.0),
                            String.format("%.1f", dto.getAverageRating() != null ? dto.getAverageRating() : 5.0),
                            dto.getImageUrl() != null ? dto.getImageUrl() : ""
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void setupCategoryChips() {
        if (categoriesBar == null) return;
        categoriesBar.getChildren().clear();

        List<String> uniqueCategories = allActivities.stream()
                .map(ExperienceData::category)
                .distinct()
                .toList();

        categoryChips = new ArrayList<>();
        categoryChips.add(new CategoryChipData("In Evidenza", "ALL"));
        uniqueCategories.forEach(cat -> categoryChips.add(new CategoryChipData(cat, cat)));

        for (int i = 0; i < categoryChips.size(); i++) {
            createCategoryButton(categoryChips.get(i), i == 0);
        }
    }

    private void createCategoryButton(CategoryChipData chipData, boolean isActive) {
        Button chipBtn = new Button(chipData.label());
        chipBtn.getStyleClass().add("category-chip");
        if (isActive) chipBtn.getStyleClass().add("chip-active");

        chipBtn.setOnAction(e -> {
            categoriesBar.getChildren().forEach(node -> node.getStyleClass().remove("chip-active"));
            chipBtn.getStyleClass().add("chip-active");
            filterCardsByCategory(chipData.categoryKey());
        });

        categoriesBar.getChildren().add(chipBtn);
    }

    private void filterCardsByCategory(String categoryKey) {
        List<ExperienceData> filtered = "ALL".equals(categoryKey)
                ? allActivities
                : allActivities.stream()
                .filter(card -> card.category().equalsIgnoreCase(categoryKey))
                .toList();

        renderCards(filtered);
    }

    private void renderCards(List<ExperienceData> cardsToDisplay) {
        if (cardsContainer == null) return;
        cardsContainer.getChildren().clear();

        final int CARDS_PER_ROW = 3;
        HBox currentRow = null;

        for (int i = 0; i < cardsToDisplay.size(); i++) {
            if (i % CARDS_PER_ROW == 0) {
                currentRow = new HBox(16);
                currentRow.getStyleClass().add("cards-row");
                currentRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                cardsContainer.getChildren().add(currentRow);
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/unical/ea_project_javafx/fxml/experience-card.fxml"));
                VBox cardNode = loader.load();

                ExperienceCardController controller = loader.getController();
                ExperienceData data = cardsToDisplay.get(i);
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
}