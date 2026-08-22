package it.unical.ea_project_javafx.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import it.unical.ea_project_javafx.dto.ActivityDTO;
import it.unical.ea_project_javafx.dto.ActivityImageDTO;
import it.unical.ea_project_javafx.dto.TripDTO;
import it.unical.ea_project_javafx.util.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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
            String imageUrl,
            String dateInfo
    ) {}

    private final List<ExperienceData> allItems = new ArrayList<>();
    private List<ExperienceData> allActivities = new ArrayList<>();
    private List<ExperienceData> allTrips = new ArrayList<>();
    private List<CategoryChipData> categoryChips = new ArrayList<>();

    private boolean activitiesLoaded = false;
    private boolean tripsLoaded = false;

    private record CategoryChipData(String label, String categoryKey) {}

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

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        loadDataFromBackend();
    }

    private void loadDataFromBackend() {
        activitiesLoaded = false;
        tripsLoaded = false;
        allActivities.clear();
        allTrips.clear();

        ApiService.get(
                ApiService.BASE_URL + "/api/activities",
                resAct -> {
                    if (resAct.statusCode() == 200) {
                        allActivities = parseActivities(resAct.body());
                    }
                    markActivitiesLoaded();
                },
                this::markActivitiesLoaded,
                null
        );

        ApiService.get(
                ApiService.BASE_URL + "/api/trips/published",
                resTrip -> {
                    if (resTrip.statusCode() == 200) {
                        allTrips = parseTrips(resTrip.body());
                    }
                    markTripsLoaded();
                },
                this::markTripsLoaded,
                null
        );
    }

    private synchronized void markActivitiesLoaded() {
        activitiesLoaded = true;
        checkAndRenderAll();
    }

    private synchronized void markTripsLoaded() {
        tripsLoaded = true;
        checkAndRenderAll();
    }

    private void checkAndRenderAll() {
        if (activitiesLoaded && tripsLoaded) {
            Platform.runLater(() -> {
                allItems.clear();
                allItems.addAll(allActivities);
                allItems.addAll(allTrips);

                setupCategoryChips();
                renderCards(allItems);
            });
        }
    }

    private List<ExperienceData> parseActivities(String jsonBody) {
        List<ExperienceData> result = new ArrayList<>();
        try {
            Type listType = new TypeToken<List<ActivityDTO>>() {}.getType();
            List<ActivityDTO> dtos = GSON.fromJson(jsonBody, listType);

            if (dtos != null) {
                for (ActivityDTO dto : dtos) {
                    result.add(new ExperienceData(
                            dto.getTitle() != null ? dto.getTitle() : "Senza Titolo",
                            dto.getCity() != null ? dto.getCity() : "",
                            dto.getCategory() != null ? dto.getCategory() : "Attività",
                            String.format("€%.0f", dto.getPrice() != null ? dto.getPrice() : 0.0),
                            String.format("%.1f", dto.getAverageRating() != null ? dto.getAverageRating() : 5.0),
                            firstImageUrl(dto.getImages()),
                            ""
                    ));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String firstImageUrl(List<ActivityImageDTO> images) {
        if (images == null || images.isEmpty()) {
            return "";
        }
        return images.stream()
                .filter(img -> img.getImageUrl() != null)
                .min(Comparator.comparing(img ->
                        img.getOrderIndex() != null ? img.getOrderIndex() : Integer.MAX_VALUE))
                .map(ActivityImageDTO::getImageUrl)
                .orElse("");
    }

    private List<ExperienceData> parseTrips(String jsonBody) {
        List<ExperienceData> result = new ArrayList<>();
        try {
            Type listType = new TypeToken<List<TripDTO>>() {}.getType();
            List<TripDTO> dtos = GSON.fromJson(jsonBody, listType);

            if (dtos != null) {
                for (TripDTO dto : dtos) {
                    String location = "";
                    if (dto.getDestinationCity() != null) location += dto.getDestinationCity();
                    if (dto.getDestinationCountry() != null) {
                        location += (location.isEmpty() ? "" : ", ") + dto.getDestinationCountry();
                    }

                    String dateInfo = "";
                    if (dto.getStartDate() != null && dto.getEndDate() != null) {
                        dateInfo = dto.getStartDate().format(DATE_FMT) + " ➔ " + dto.getEndDate().format(DATE_FMT);
                    }

                    result.add(new ExperienceData(
                            dto.getTitle() != null ? dto.getTitle() : "Senza Titolo",
                            location,
                            "Viaggi",
                            String.format("€%.0f", dto.getTotalPrice() != null ? dto.getTotalPrice().doubleValue() : 0.0),
                            String.format("%.1f", dto.getAverageRating() != null ? dto.getAverageRating().doubleValue() : 5.0),
                            dto.getCoverPhotoUrl() != null ? dto.getCoverPhotoUrl() : "",
                            dateInfo
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

        categoryChips = new ArrayList<>();
        categoryChips.add(new CategoryChipData("In Evidenza", "ALL"));

        List<String> uniqueCategories = allItems.stream()
                .map(ExperienceData::category)
                .distinct()
                .toList();

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
        List<ExperienceData> filtered;
        if ("ALL".equalsIgnoreCase(categoryKey)) {
            filtered = allItems;
        } else {
            filtered = new ArrayList<>();
            for (ExperienceData card : allItems) {
                if (card.category() != null && card.category().equalsIgnoreCase(categoryKey)) {
                    filtered.add(card);
                }
            }
        }
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

    public void filterBySearchCriteria(String categoryType, LocalDate date) {
        List<ExperienceData> filtered;

        if ("Viaggi".equalsIgnoreCase(categoryType)) {
            filtered = allTrips;
        } else if ("Attività".equalsIgnoreCase(categoryType)) {
            filtered = allActivities;
        } else if ("Tutto".equalsIgnoreCase(categoryType)) {
            filtered = allItems;
        } else if ("Viaggi Prenotati".equalsIgnoreCase(categoryType)) {
            loadBookedTripsFromServer();
            return;
        } else if ("Attività Prenotate".equalsIgnoreCase(categoryType)) {
            return;
        } else {
            filtered = allItems;
        }

        renderCards(filtered);
    }

    private void loadBookedTripsFromServer() {
        Long userId = it.unical.ea_project_javafx.model.UserSession.getInstance().getId();

        if (userId == null) {
            return;
        }

        ApiService.get(
                ApiService.BASE_URL + "/api/bookings/user/" + userId + "/trips",
                res -> {
                    if (res.statusCode() == 200) {
                        List<ExperienceData> bookedTripsCards = parseTrips(res.body());
                        renderCards(bookedTripsCards);
                    }
                },
                () -> {}, null
        );
    }
}