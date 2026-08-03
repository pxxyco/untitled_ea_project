package it.unical.ea_project_javafx;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainController {
    @FXML private ImageView bgImageView;
    @FXML private VBox cardsContainer;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private DatePicker datePicker;
    @FXML private HBox categoriesBar;

    private final List<ExperienceCardBuilder.ExperienceData> sampleData = List.of(
            new ExperienceCardBuilder.ExperienceData("Escursione in Quad tra la Sila", "Camigliatello Silano", "Quad & 4x4", "45€", "4.9"),
            new ExperienceCardBuilder.ExperienceData("Degustazione Vini e Formaggi", "Cirò Marina", "Degustazioni", "30€", "4.8"),
            new ExperienceCardBuilder.ExperienceData("Tour in Barca lungo la Costa degli Dei", "Tropea", "Tour in Barca", "60€", "5.0"),
            new ExperienceCardBuilder.ExperienceData("Trekking alle Cascate del Marmarico", "Bivongi", "Trekking & Natura", "25€", "4.7"),
            new ExperienceCardBuilder.ExperienceData("Visita Guidata ai Bronzi di Riace", "Reggio Calabria", "Musei & Cultura", "15€", "4.9"),
            new ExperienceCardBuilder.ExperienceData("Snorkeling alla Baia di Grotticelle", "Capo Vaticano", "Tour in Barca", "35€", "4.6")
    );
//esempi
    private final List<CategoryChipData> categoryChips = List.of(
            new CategoryChipData("🔥 In Evidenza", "ALL"),
            new CategoryChipData("🏎️ Quad & 4x4", "Quad & 4x4"),
            new CategoryChipData("🍷 Degustazioni", "Degustazioni"),
            new CategoryChipData("🏛️ Musei & Cultura", "Musei & Cultura"),
            new CategoryChipData("🥾 Trekking & Natura", "Trekking & Natura"),
            new CategoryChipData("⛵ Tour in Barca", "Tour in Barca")
    );

    private record CategoryChipData(String label, String categoryKey) {}

    @FXML
    public void initialize() {
        setupBackgroundResize();
        setupCategoryComboBox();
        setupResponsiveSearchBar();
        setupDatePicker();
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

            if (i == 0) {
                chipBtn.getStyleClass().add("chip-active");
            }

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
            if (currentRow != null) {
                currentRow.getChildren().add(card);
            }
        }
    }

    private void setupBackgroundResize() {
        if (bgImageView != null) {
            bgImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null && bgImageView.getParent() instanceof StackPane) {
                    StackPane parentPane = (StackPane) bgImageView.getParent();
                    bgImageView.fitWidthProperty().bind(parentPane.widthProperty());
                    bgImageView.fitHeightProperty().bind(parentPane.heightProperty());
                }
            });
        }
    }

    private void setupCategoryComboBox() {
        if (categoryComboBox != null) {
            ObservableList<String> categories = FXCollections.observableArrayList(
                    "Tutte le categorie", "Quad & 4x4", "Degustazioni", "Musei & Cultura", "Trekking & Natura", "Tour in Barca");
            categoryComboBox.setItems(categories);
            categoryComboBox.getSelectionModel().selectFirst();
        }
    }

    private void setupResponsiveSearchBar() {
        if (bgImageView != null) {
            bgImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    newScene.widthProperty().addListener((observable, oldValue, newValue) -> {
                        if (cardsContainer != null && newValue.doubleValue() < 680) {
                            cardsContainer.setAlignment(Pos.CENTER);
                        } else if (cardsContainer != null) {
                            cardsContainer.setAlignment(Pos.TOP_LEFT);
                        }
                    });
                }
            });
        }
    }

    private void setupDatePicker() {
        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
        }
    }

    @FXML
    void handleLogin(MouseEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/it/unical/ea_project_javafx/fxml/login.fxml"));
            javafx.scene.Parent root = loader.load();
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 1000, 700);
            javafx.stage.Stage stage = (javafx.stage.Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSearch(ActionEvent event) {}

    @FXML
    void handleEsperienze(MouseEvent event) {}

    @FXML
    void handleItinerari(MouseEvent event) {}
}