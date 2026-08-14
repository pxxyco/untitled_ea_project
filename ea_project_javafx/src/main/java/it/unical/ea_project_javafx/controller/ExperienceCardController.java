package it.unical.ea_project_javafx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
//Controller della card di ogni esperienza
public class ExperienceCardController {

    @FXML private VBox cardRoot;
    @FXML private StackPane imageContainer;
    @FXML private ImageView imageView;
    @FXML private Label categoryLabel;
    @FXML private Label ratingLabel;
    @FXML private Label titleLabel;
    @FXML private Label locationLabel;
    @FXML private Label priceLabel;
    @FXML private ProgressIndicator imageLoader;

    @FXML
    public void initialize() {
        Rectangle clip = new Rectangle();
        clip.setWidth(315);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        clip.heightProperty().bind(imageView.fitHeightProperty());
        imageView.setClip(clip);
    }

    public void setData(String title, String location, String category, String price, String rating, String imageUrl) {
        titleLabel.setText(title);
        locationLabel.setText(location);
        categoryLabel.setText(category);
        priceLabel.setText("da " + price);
        ratingLabel.setText("⭐ " + rating);

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Image image = new Image(imageUrl, true);
            imageView.setImage(image);

            image.progressProperty().addListener((obs, old, newVal) -> {
                if (newVal.doubleValue() >= 1.0) {
                    imageLoader.setVisible(false);
                    imageLoader.setManaged(false);
                }
            });
        } else {
            imageLoader.setVisible(false);
            imageLoader.setManaged(false);
        }

        cardRoot.setOnMouseClicked(event -> handleCardClick());
    }

    private void handleCardClick() {
        // TODO Logica di navigazione al click sulla card
    }
}