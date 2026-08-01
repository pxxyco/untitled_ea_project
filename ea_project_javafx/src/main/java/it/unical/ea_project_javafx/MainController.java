package it.unical.ea_project_javafx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainController {

    @FXML
    private ImageView bgImageView;

    @FXML
    private VBox cardsContainer;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    public void initialize()
    {
        setupBackgroundResize();
        setupCategoryComboBox();
        setupResponsiveSearchBar();

        // Carichiamo le card dinamiche all'avvio
        loadSampleCards();
    }

    /**
     * Popola il cardsContainer con card generate a codice o da FXML esterno.
     */
    private void loadSampleCards()
    {
        if (cardsContainer == null)
            return;

        cardsContainer.getChildren().clear();

        // Esempio: Ciclo per simulare una lista di esperienze dal DB
        for (int i = 1; i <= 5; i++) {
            HBox card = createExperienceCard(
                "Escursione in Quad Sila #" + i,
                "Camigliatello Silano",
                "4.9 ⭐ (120 recensioni)",
                "Da 45€ / persona");
            cardsContainer.getChildren().add(card);
        }
    }

    /**
     * Crea il layout grafico Glassmorphic responsivo per la singola card
     */
    private HBox createExperienceCard(String titleText, String locationText, String ratingText, String priceText)
    {
        HBox card = new HBox(16);
        card.getStyleClass().add("experience-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(16));

        // Info Testuali
        VBox infoBox = new VBox(6);
        HBox.setHgrow(infoBox, Priority.ALWAYS);

        Label title = new Label(titleText);
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label location = new Label("📍 " + locationText + " • " + ratingText);
        location.setStyle("-fx-font-size: 12px; -fx-text-fill: rgba(255,255,255,0.7);");

        infoBox.getChildren().addAll(title, location);

        // Prezzo e Azione
        VBox actionBox = new VBox(8);
        actionBox.setAlignment(Pos.CENTER_RIGHT);

        Label price = new Label(priceText);
        price.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #38bdf8;");

        Button btnDetails = new Button("Vedi Dettagli");
        btnDetails.getStyleClass().add("btn-primary");

        actionBox.getChildren().addAll(price, btnDetails);

        card.getChildren().addAll(infoBox, actionBox);
        return card;
    }

    private void setupBackgroundResize()
    {
        if (bgImageView != null) {
            bgImageView.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null && bgImageView.getParent() instanceof StackPane) {
                    StackPane parentPane = (StackPane)bgImageView.getParent();
                    bgImageView.fitWidthProperty().bind(parentPane.widthProperty());
                    bgImageView.fitHeightProperty().bind(parentPane.heightProperty());
                }
            });
        }
    }

    private void setupCategoryComboBox()
    {
        if (categoryComboBox != null) {
            ObservableList<String> categories = FXCollections.observableArrayList(
                "Tutte le categorie", "Quad & 4x4", "Degustazioni", "Musei & Cultura", "Trekking & Natura", "Tour in Barca");
            categoryComboBox.setItems(categories);
            categoryComboBox.getSelectionModel().selectFirst();
        }
    }

    private void setupResponsiveSearchBar()
    {
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

    @FXML
    void handleLogin(ActionEvent event)
    {
    }
}
