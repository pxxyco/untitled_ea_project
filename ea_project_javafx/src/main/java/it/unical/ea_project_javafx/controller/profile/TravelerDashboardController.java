package it.unical.ea_project_javafx.controller.profile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unical.ea_project_javafx.dto.home.ActivityHomeDTO;
import it.unical.ea_project_javafx.model.UserSession;
import it.unical.ea_project_javafx.util.ApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

public class TravelerDashboardController implements ProfileDashboardController {

    @FXML private HBox activityContainer;

    @Override
    public void setUser(UserSession userSession) {
        loadAvailableActivities();
    }

    private void loadAvailableActivities() {
        ApiService.get(
                ApiService.BASE_URL + "/api/activities/top/cards?page=0&size=3",
                response -> {
                    if (response.statusCode() != 200) {
                        return;
                    }
                    Type listType = new TypeToken<List<ActivityHomeDTO>>() { }.getType();
                    List<ActivityHomeDTO> activities = new Gson().fromJson(response.body(), listType);
                    Platform.runLater(() -> renderActivities(activities));
                },
                () -> { },
                null
        );
    }

    private void renderActivities(List<ActivityHomeDTO> activities) {
        activityContainer.getChildren().clear();
        if (activities == null) {
            return;
        }
        activities.stream()
                .filter(activity -> activity != null && activity.getTitle() != null)
                .forEach(activity -> activityContainer.getChildren().add(createActivityCard(activity)));
    }

    private VBox createActivityCard(ActivityHomeDTO activity) {
        Label title = new Label(activity.getTitle());
        title.getStyleClass().add("card-title");

        Label category = new Label(valueOrEmpty(activity.getCategory()).toUpperCase(Locale.ROOT));
        category.getStyleClass().add("card-category");

        HBox titleRow = new HBox(title, category);
        titleRow.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(title, javafx.scene.layout.Priority.ALWAYS);

        Label location = new Label(valueOrEmpty(activity.getCity()));
        location.getStyleClass().add("card-info");

        Label price = new Label(activity.getPrice() == null
                ? ""
                : String.format(Locale.ROOT, "EUR %.2f", activity.getPrice()));
        price.getStyleClass().add("price-tag");

        Button bookButton = new Button("Prenota");
        bookButton.getStyleClass().add("card-action");

        HBox footer = new HBox(price, bookButton);
        footer.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(price, javafx.scene.layout.Priority.ALWAYS);
        footer.getStyleClass().add("card-bottom");

        VBox card = new VBox(titleRow, location, footer);
        card.setSpacing(10.0);
        card.getStyleClass().add("experience-card");
        HBox.setHgrow(card, javafx.scene.layout.Priority.ALWAYS);
        return card;
    }

    private String valueOrEmpty(String value) {
        return value == null ? "" : value;
    }
}