package it.unical.ea_project_javafx.controller.profile;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unical.ea_project_javafx.dto.TripDTO;
import it.unical.ea_project_javafx.model.UserSession;
import it.unical.ea_project_javafx.util.ApiService;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Effect;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

public class OrganizerDashboardController implements ProfileDashboardController {

    @FXML private javafx.scene.layout.StackPane rootStackPane;

    @FXML private Label publishedCountLabel;
    @FXML private Label totalSubscribersLabel;
    @FXML private Label totalRevenueLabel;

    @FXML private ListView<TripDTO> activitiesListView;

    @FXML private TableView<SubscriberRow> subscribersTable;
    @FXML private TableColumn<SubscriberRow, String> participantColumn;
    @FXML private TableColumn<SubscriberRow, String> emailColumn;
    @FXML private TableColumn<SubscriberRow, String> activityColumn;
    @FXML private TableColumn<SubscriberRow, String> dateColumn;

    @FXML
    private void handleOpenCreateOffer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/it/unical/ea_project_javafx/fxml/profile/create_offer.fxml"));
            Parent popup = loader.load();

            Stage owner = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Stage modal = new Stage(StageStyle.TRANSPARENT);
            modal.initOwner(owner);
            modal.initModality(Modality.APPLICATION_MODAL);

            Scene scene = new Scene(popup, 920, 720);
            scene.setFill(Color.TRANSPARENT);
            modal.setScene(scene);
            modal.setOnShown(ignored -> {
                modal.setX(owner.getX() + (owner.getWidth() - modal.getWidth()) / 2.0);
                modal.setY(owner.getY() + (owner.getHeight() - modal.getHeight()) / 2.0);
            });

            Effect previousEffect = owner.getScene().getRoot().getEffect();
            owner.getScene().getRoot().setEffect(new GaussianBlur(8));
            modal.setResizable(false);
            modal.setOnHidden(ignored -> owner.getScene().getRoot().setEffect(previousEffect));
            modal.showAndWait();
        } catch (IOException | RuntimeException exception) {
            ownerSceneRoot(event).setEffect(null);
            exception.printStackTrace();
        }
    }

    private javafx.scene.Parent ownerSceneRoot(ActionEvent event) {
        return ((Node) event.getSource()).getScene().getRoot();
    }

    public static class SubscriberRow {
        private final SimpleStringProperty participant;
        private final SimpleStringProperty email;
        private final SimpleStringProperty activity;
        private final SimpleStringProperty date;

        public SubscriberRow(String participant, String email, String activity, String date) {
            this.participant = new SimpleStringProperty(participant);
            this.email = new SimpleStringProperty(email);
            this.activity = new SimpleStringProperty(activity);
            this.date = new SimpleStringProperty(date);
        }

        public SimpleStringProperty participantProperty() { return participant; }
        public SimpleStringProperty emailProperty() { return email; }
        public SimpleStringProperty activityProperty() { return activity; }
        public SimpleStringProperty dateProperty() { return date; }
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupActivityCellFactory();
        activitiesListView.setItems(FXCollections.observableArrayList());
        subscribersTable.setItems(FXCollections.observableArrayList());
        publishedCountLabel.setText("0");
        totalSubscribersLabel.setText("0");
        totalRevenueLabel.setText("EUR 0.00");
        adjustTableHeight();
    }

    @Override
    public void setUser(UserSession userSession) {
        if (userSession == null || userSession.getId() == null) {
            return;
        }

        ApiService.get(
                ApiService.BASE_URL + "/api/trips/organizer/" + userSession.getId(),
                response -> {
                    if (response.statusCode() != 200) {
                        return;
                    }
                    Type listType = new TypeToken<List<TripDTO>>() { }.getType();
                    List<TripDTO> trips = new Gson().fromJson(response.body(), listType);
                    Platform.runLater(() -> updateDashboard(trips));
                },
                () -> Platform.runLater(() -> updateDashboard(List.of())),
                null
        );
    }

    private void updateDashboard(List<TripDTO> trips) {
        List<TripDTO> safeTrips = trips == null ? List.of() : trips;
        activitiesListView.setItems(FXCollections.observableArrayList(safeTrips));
        publishedCountLabel.setText(String.valueOf(safeTrips.stream()
                .filter(trip -> "PUBLISHED".equalsIgnoreCase(trip.getStatus()))
                .count()));

        BigDecimal totalValue = safeTrips.stream()
                .map(TripDTO::getTotalPrice)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        totalRevenueLabel.setText(String.format("EUR %.2f", totalValue));
        totalSubscribersLabel.setText("0");
    }

    private void setupTableColumns() {
        participantColumn.setCellValueFactory(cell -> cell.getValue().participantProperty());
        emailColumn.setCellValueFactory(cell -> cell.getValue().emailProperty());
        activityColumn.setCellValueFactory(cell -> cell.getValue().activityProperty());
        dateColumn.setCellValueFactory(cell -> cell.getValue().dateProperty());
    }

    private void setupActivityCellFactory() {
        activitiesListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TripDTO trip, boolean empty) {
                super.updateItem(trip, empty);

                if (empty || trip == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox card = new VBox(8);
                    card.getStyleClass().add("activity-card");

                    HBox topBox = new HBox(10);
                    topBox.setAlignment(Pos.CENTER_LEFT);

                    Label titleLabel = new Label(trip.getTitle());
                    titleLabel.getStyleClass().add("activity-card-title");

                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    Label statusBadge = new Label(trip.getStatus() != null ? trip.getStatus() : "PUBLISHED");
                    statusBadge.getStyleClass().add("badge-published");

                    topBox.getChildren().addAll(titleLabel, spacer, statusBadge);

                    int total = trip.getMaxSeats() != null ? trip.getMaxSeats() : 0;
                    int available = trip.getAvailableSeats() != null ? trip.getAvailableSeats() : total;
                    int sold = Math.max(0, total - available);
                    Label seatsLabel = new Label("Posti Venduti: " + sold + " / " + total);
                    seatsLabel.getStyleClass().add("activity-card-seats");

                    HBox bottomBox = new HBox(8);
                    bottomBox.setAlignment(Pos.CENTER_LEFT);

                        double price = trip.getTotalPrice() != null
                            ? trip.getTotalPrice().doubleValue()
                            : 0.0;
                    Label priceLabel = new Label(String.format("€ %.2f", price));
                    priceLabel.getStyleClass().add("activity-card-price");

                    Region bottomSpacer = new Region();
                    HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

                    Button editBtn = new Button("Modifica");
                    editBtn.getStyleClass().add("btn-card-action");
                    editBtn.setOnAction(e -> handleEditTrip(trip));

                    Button deleteBtn = new Button("Cancella");
                    deleteBtn.getStyleClass().add("btn-card-delete");
                    deleteBtn.setOnAction(e -> handleDeleteTrip(trip));

                    bottomBox.getChildren().addAll(priceLabel, bottomSpacer, editBtn, deleteBtn);

                    card.getChildren().addAll(topBox, seatsLabel, bottomBox);
                    setGraphic(card);
                }
            }
        });
    }

    private void adjustTableHeight() {
        subscribersTable.fixedCellSizeProperty().set(40);
        subscribersTable.prefHeightProperty().bind(
                Bindings.size(subscribersTable.getItems())
                        .multiply(subscribersTable.fixedCellSizeProperty())
                        .add(45) // offset header
        );
    }

    @FXML
    private void handleNewActivity(ActionEvent event) {
        handleOpenCreateOffer(event);
    }

    private void handleEditTrip(TripDTO trip) {}

    private void handleDeleteTrip(TripDTO trip) {}
}