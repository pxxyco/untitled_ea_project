package it.unical.ea_project_javafx.controller.home;

import it.unical.ea_project_javafx.dto.ActivityDTO;
import it.unical.ea_project_javafx.dto.ActivityImageDTO;
import it.unical.ea_project_javafx.dto.TripDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;

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


    private static final double IMAGE_WIDTH = 315;
    private static final double IMAGE_HEIGHT = 199;
    private static final Map<String, Image> IMAGE_CACHE =
            new ConcurrentHashMap<>();
    @FXML
    public void initialize() {
        Rectangle clip = new Rectangle();
        clip.setWidth(315);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        clip.heightProperty().bind(imageView.fitHeightProperty());
        imageView.setClip(clip);
    }

    private void hideImageLoader() {
        if (imageLoader != null) {
            imageLoader.setVisible(false);
            imageLoader.setManaged(false);
        }
    }

    public void setData(
            String title,
            String location,
            String category,
            String price,
            String rating,
            String imageUrl
    ) {

        titleLabel.setText(title);
        locationLabel.setText(location);
        categoryLabel.setText(category);
        priceLabel.setText("da " + price);
        ratingLabel.setText("⭐ " + rating);

        loadImage(imageUrl);

        cardRoot.setOnMouseClicked(event -> handleCardClick());
    }

    private void loadImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            imageView.setImage(null);
            hideImageLoader();
            return;
        }
        imageLoader.setVisible(true);
        imageLoader.setManaged(true);
        try {
            Image image = IMAGE_CACHE.get(imageUrl);
            if (image == null) {
                image = new Image(
                        imageUrl,
                        IMAGE_WIDTH,
                        IMAGE_HEIGHT,
                        false,
                        true,
                        true
                );
                IMAGE_CACHE.put(imageUrl, image);
            }
            imageView.setImage(image);
            if (image.getProgress() >= 1.0) {
                hideImageLoader();
            }
            image.progressProperty().addListener(
                    (obs, oldValue, newValue) -> {

                        if (newValue.doubleValue() >= 1.0) {
                            hideImageLoader();
                        }
                    }
            );
            image.errorProperty().addListener(
                    (obs, oldValue, hasError) -> {

                        if (hasError) {
                            IMAGE_CACHE.remove(imageUrl);
                            hideImageLoader();
                        }
                    }
            );
        } catch (Exception e) {

            hideImageLoader();
        }
    }

    private String firstImageUrl(List<ActivityImageDTO> images) {
        if (images == null || images.isEmpty()) {
            return null;
        }
        return images.stream()
                .filter(img -> img.getImageUrl() != null)
                .min(java.util.Comparator.comparing(img ->
                        img.getOrderIndex() != null ? img.getOrderIndex() : Integer.MAX_VALUE))
                .map(it.unical.ea_project_javafx.dto.ActivityImageDTO::getImageUrl)
                .orElse(null);
    }

    /*
    public void setData(TripDTO dto) {
        String location = "";
        if (dto.getDestinationCity() != null) location += dto.getDestinationCity();
        if (dto.getDestinationCountry() != null) {
            location += (location.isEmpty() ? "" : ", ") + dto.getDestinationCountry();
        }
        setData(
                dto.getTitle() != null ? dto.getTitle() : "Senza Titolo",
                location,
                "Viaggio",
                String.format("€%.0f", dto.getTotalPrice() != null ? dto.getTotalPrice().doubleValue() : 0.0),
                String.format("%.1f", dto.getAverageRating() != null ? dto.getAverageRating().doubleValue() : 5.0),
                dto.getCoverPhotoUrl()
        );
    }*/

    private void handleCardClick() {
        // TODO Logica di navigazione al click sulla card
    }
}
