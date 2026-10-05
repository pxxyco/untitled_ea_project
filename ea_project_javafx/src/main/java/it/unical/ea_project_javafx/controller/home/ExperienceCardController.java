package it.unical.ea_project_javafx.controller.home;

import it.unical.ea_project_javafx.controller.AttivitaController;
import it.unical.ea_project_javafx.dto.home.ActivityHomeDTO;
import it.unical.ea_project_javafx.dto.home.TripHomeDTO;
import it.unical.ea_project_javafx.util.SceneNavigator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import static it.unical.ea_project_javafx.controller.AttivitaController.Type.ACTIVITY;

public class ExperienceCardController {

    @FXML private VBox cardRoot;
    @FXML private StackPane imageContainer;
    @FXML private ImageView imageView;
    @FXML private Label categoryLabel;
    @FXML private Label ratingLabel;
    @FXML private Label titleLabel;
    @FXML private Label locationLabel;
    @FXML private Label dateLabel;
    @FXML private Label priceLabel;
    @FXML private ProgressIndicator imageLoader;

    private static final double IMAGE_WIDTH = 315;
    private static final double IMAGE_HEIGHT = 199;
    private static final int MAX_CACHE_SIZE = 45;

    private static final Map<String, Image> IMAGE_CACHE = new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Image> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };
    private static final DateTimeFormatter CARD_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private AttivitaController.Type detailType;
    private String detailId;

    @FXML
    public void initialize() {
        Rectangle clip = new Rectangle();
        clip.setWidth(IMAGE_WIDTH);
        clip.setArcWidth(24);
        clip.setArcHeight(24);
        clip.heightProperty().bind(imageView.fitHeightProperty());
        imageView.setClip(clip);
    }

    private static synchronized Image getCachedImage(String imageUrl) {
        return IMAGE_CACHE.get(imageUrl);
    }

    private static synchronized Image createAndCacheImage(String imageUrl) {
        Image cached = IMAGE_CACHE.get(imageUrl);
        if (cached != null) return cached;

        Image image = new Image(imageUrl, IMAGE_WIDTH, IMAGE_HEIGHT, false, true, true);
        IMAGE_CACHE.put(imageUrl, image);
        return image;
    }

    static synchronized void removeCachedImage(String imageUrl, Image image) {
        Image cached = IMAGE_CACHE.get(imageUrl);
        if (cached == image) {
            IMAGE_CACHE.remove(imageUrl);
        }
    }

    private void hideImageLoader() {
        if (imageLoader != null) {
            imageLoader.setVisible(false);
            imageLoader.setManaged(false);
        }
    }

    private void showImageLoader() {
        if (imageLoader != null) {
            imageLoader.setVisible(true);
            imageLoader.setManaged(true);
        }
    }

    public void setData(String title, String location, String category, String price, String rating, String imageUrl, String dateInfo)
    {
        titleLabel.setText(title);
        locationLabel.setText(location);
        categoryLabel.setText(category);
        priceLabel.setText("da " + price);
        ratingLabel.setText("⭐ " + rating);

        dateLabel.setText(dateInfo);
        dateLabel.setVisible(dateInfo != null && !dateInfo.isBlank());
        dateLabel.setManaged(dateLabel.isVisible());

        loadImage(imageUrl);
    }

    public void setData(TripHomeDTO dto) {
        if (dto == null) return;

        detailType = AttivitaController.Type.TRIP;
        detailId = dto.getTripId().toString();

        String title = dto.getTitle() != null ? dto.getTitle() : "Senza Titolo";
        String location = dto.getLocation() != null ? dto.getLocation() : "";
        String category = "Viaggio";
        String price = String.format("€%.0f", dto.getTotalPrice() != null ? dto.getTotalPrice().doubleValue() : 0.0);
        String rating = dto.getAverageRating() != null ? String.format("%.1f", dto.getAverageRating().doubleValue()) : "-";
        String imageUrl = dto.getCoverPhotoUrl() != null ? dto.getCoverPhotoUrl() : "";
        String dateInfo = formatDateRange(dto.getStartDate(), dto.getEndDate());

        cardRoot.setOnMouseClicked(event -> handleCardClick(AttivitaController.Type.TRIP, String.valueOf(dto.getTripId())));

        setData(title, location, category, price, rating, imageUrl, dateInfo);

    }



    public void setData(ActivityHomeDTO dto) {
        if (dto == null) return;

        detailType = ACTIVITY;
        detailId = dto.getActivityId().toString();

        String title = dto.getTitle() != null ? dto.getTitle() : "Senza Titolo";
        String location = dto.getCity() != null ? dto.getCity() : "";
        String category = dto.getCategory() != null ? dto.getCategory() : "Attività";
        String price = String.format("€%.0f", dto.getPrice() != null ? dto.getPrice() : 0.0);
        String rating = String.format("%.1f", dto.getAverageRating() != null ? dto.getAverageRating() : 5.0);
        String imageUrl = dto.getImageUrl() != null ? dto.getImageUrl() : "";
        String dateInfo = formatDateRange(dto.getStartDate(), dto.getEndDate());

        cardRoot.setOnMouseClicked(event -> handleCardClick(AttivitaController.Type.ACTIVITY, String.valueOf(dto.getActivityId())));

        setData(title, location, category, price, rating, imageUrl, dateInfo);
    }

    private String formatDateRange(String startDate, String endDate) {
        if (startDate == null || endDate == null || startDate.isBlank() || endDate.isBlank() || startDate.equals("null") || endDate.equals("null")) {
            return "";
        }
        try {
            LocalDate start = LocalDate.parse(startDate.substring(0, 10));
            LocalDate end = LocalDate.parse(endDate.substring(0, 10));
            String formattedStart = start.format(CARD_DATE_FORMAT);
            return start.equals(end) ? formattedStart : formattedStart + " - " + end.format(CARD_DATE_FORMAT);
        } catch (DateTimeParseException | IndexOutOfBoundsException e) {
            return "";
        }
    }

    private void loadImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            imageView.setImage(null);
            hideImageLoader();
            return;
        }

        showImageLoader();

        Image image = getCachedImage(imageUrl);
        if (image == null) {
            image = createAndCacheImage(imageUrl);
        }

        imageView.setImage(image);

        if (image.isError()) {
            removeCachedImage(imageUrl, image);
            hideImageLoader();
            return;
        }

        if (image.getProgress() >= 1.0) {
            hideImageLoader();
            return;
        }

        ImageLoadListener listener = new ImageLoadListener(imageUrl, image, this::hideImageLoader);
        listener.attach();
    }

    private void handleCardClick(AttivitaController.Type type, String s) {
        SceneNavigator.getInstance().loadScene("/it/unical/ea_project_javafx/fxml/Attivita.fxml",
                controller -> {
                    if (controller instanceof AttivitaController attivitaCtrl) {
                        attivitaCtrl.loadData(detailType, detailId);
                    }
                }
        );
    }
}