package it.unical.ea_project_javafx.controller;

import it.unical.ea_project_javafx.util.ExperienceCardBuilder;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.stream.Collectors;

public class ExperienceListController {

    @FXML private HBox categoriesBar;
    @FXML private VBox cardsContainer;

    private final List<ExperienceCardBuilder.ExperienceData> sampleData = List.of(
            new ExperienceCardBuilder.ExperienceData("Concerto psyche", "Camigliatello Silano", "Psyche", "45€", "4.9"),
            new ExperienceCardBuilder.ExperienceData("concerto zephyr con apertura laura pausini cantante degli spiritbox", "Cosenza", "Laura Pausini", "30€", "4.8"),
            new ExperienceCardBuilder.ExperienceData("Serata cinghiale", "San Marco Argentano", "Trekking & Natura", "25€", "4.7"),
            new ExperienceCardBuilder.ExperienceData("Filo rosso con deBonis", "Cosenza", "Musei & Cultura", "15€", "4.9")
    );

    private final List<CategoryChipData> categoryChips = List.of(
            new CategoryChipData("In Evidenza", "ALL"),
            new CategoryChipData("Concerto Psyche", "Psyche"),
            new CategoryChipData("Laura Pausini", "Laura Pausini"),
            new CategoryChipData("Musei & Cultura", "Musei & Cultura"),
            new CategoryChipData("Trekking & Natura", "Trekking & Natura")
    );

    private record CategoryChipData(String label, String categoryKey) {}

    @FXML
    public void initialize() {
        setupCategoryChips();
        renderCards(sampleData);
    }

    private void setupCategoryChips() {
        if (categoriesBar == null) return;
        categoriesBar.getChildren().clear();

        for (int i = 0; i < categoryChips.size(); i++) {
            CategoryChipData chipData = categoryChips.get(i);
            Button chipBtn = new Button(chipData.label());
            chipBtn.getStyleClass().add("category-chip");

            if (i == 0) chipBtn.getStyleClass().add("chip-active");

            chipBtn.setOnAction(e -> {
                categoriesBar.getChildren().forEach(node -> node.getStyleClass().remove("chip-active"));
                chipBtn.getStyleClass().add("chip-active");
                filterCardsByCategory(chipData.categoryKey());
            });

            categoriesBar.getChildren().add(chipBtn);
        }
    }

    private void filterCardsByCategory(String categoryKey) {
        if ("ALL".equals(categoryKey)) {
            renderCards(sampleData);
        } else {
            List<ExperienceCardBuilder.ExperienceData> filtered = sampleData.stream()
                    .filter(card -> card.category().equalsIgnoreCase(categoryKey))
                    .collect(Collectors.toList());
            renderCards(filtered);
        }
    }

    private void renderCards(List<ExperienceCardBuilder.ExperienceData> cardsToDisplay) {
        if (cardsContainer == null) return;
        cardsContainer.getChildren().clear();

        int cardsPerRow = 3;
        HBox currentRow = null;

        for (int i = 0; i < cardsToDisplay.size(); i++) {
            if (i % cardsPerRow == 0) {
                currentRow = new HBox(16);
                currentRow.getStyleClass().add("cards-row");
                currentRow.setMaxWidth(Double.MAX_VALUE);
                VBox.setVgrow(currentRow, Priority.NEVER);
                cardsContainer.getChildren().add(currentRow);
            }
            VBox card = ExperienceCardBuilder.createCard(cardsToDisplay.get(i));
            if (currentRow != null) currentRow.getChildren().add(card);
        }
    }
}