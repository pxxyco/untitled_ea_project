package it.unical.ea_project_javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ExperienceCardBuilder {

    public record ExperienceData(
            String title,
            String location,
            String category,
            String price,
            String rating
    ) {}

    public static VBox createCard(ExperienceData data) {
        VBox card = new VBox(8);
        card.getStyleClass().add("experience-card");
        card.setPadding(new Insets(16));

        HBox.setHgrow(card, Priority.ALWAYS);
        card.setMaxWidth(Double.MAX_VALUE);

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        Label categoryLabel = new Label(data.category());
        categoryLabel.getStyleClass().add("card-category");

        Label ratingLabel = new Label("⭐ " + data.rating());
        ratingLabel.getStyleClass().add("card-rating");

        VBox spacer = new VBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(categoryLabel, spacer, ratingLabel);

        Label titleLabel = new Label(data.title());
        titleLabel.getStyleClass().add("card-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        Label locationLabel = new Label(data.location());
        locationLabel.getStyleClass().add("card-location");

        VBox verticalSpacer = new VBox();
        VBox.setVgrow(verticalSpacer, Priority.ALWAYS);

        HBox footer = new HBox();
        footer.setAlignment(Pos.BOTTOM_RIGHT);

        Label priceLabel = new Label("da " + data.price());
        priceLabel.getStyleClass().add("card-price");

        footer.getChildren().add(priceLabel);

        card.getChildren().addAll(header, titleLabel, locationLabel, verticalSpacer, footer);
        return card;
    }
}