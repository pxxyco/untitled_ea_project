package it.unical.ea_project_javafx;

import it.unical.ea_project_javafx.dto.TripDTO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

public class OrganizerDashboardController {

    @FXML private StackPane rootStackPane;
    @FXML private ImageView bgImageView;

    @FXML private Label publishedCountLabel;
    @FXML private Label totalSubscribersLabel;
    @FXML private Label totalRevenueLabel;

    @FXML private ListView<TripDTO> activitiesListView;

    @FXML private TableView<SubscriberRow> subscribersTable;
    @FXML private TableColumn<SubscriberRow, String> participantColumn;
    @FXML private TableColumn<SubscriberRow, String> emailColumn;
    @FXML private TableColumn<SubscriberRow, String> activityColumn;
    @FXML private TableColumn<SubscriberRow, String> dateColumn;

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
        setupResponsiveBackground();
        setupTableColumns();
        setupActivityCellFactory();
        loadMockData();
        adjustTableHeight();
    }

    private void setupResponsiveBackground() {
        if (bgImageView != null && rootStackPane != null) {
            bgImageView.fitWidthProperty().bind(rootStackPane.widthProperty());
            bgImageView.fitHeightProperty().bind(rootStackPane.heightProperty());
        }
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

                    int sold = trip.getSoldSeats();
                    int total = trip.getMaxSeats() != null ? trip.getMaxSeats() : 0;
                    Label seatsLabel = new Label("Posti Venduti: " + sold + " / " + total);
                    seatsLabel.getStyleClass().add("activity-card-seats");

                    HBox bottomBox = new HBox(8);
                    bottomBox.setAlignment(Pos.CENTER_LEFT);

                    double price = trip.getPriceAsDouble();
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

    private void loadMockData() {
        ObservableList<TripDTO> trips = FXCollections.observableArrayList(
                new TripDTO(1L, "Escursione Sila in Quad", "Giro in quad nella Sila", "Camigliatello",
                        new BigDecimal("45.00"), 20, 8, "PUBLISHED"),
                new TripDTO(2L, "Trekking Cascate del Marmarico", "Trekking alle cascate", "Bivongi",
                        new BigDecimal("25.00"), 25, 0, "PUBLISHED")
        );
        activitiesListView.setItems(trips);

        publishedCountLabel.setText("6");
        totalSubscribersLabel.setText("142");
        totalRevenueLabel.setText("€ 3.250");

        ObservableList<SubscriberRow> subscribers = FXCollections.observableArrayList(
                new SubscriberRow("Luigi Bianchi", "luigi@example.com", "Escursione Sila in Quad", "Oggi, 14:20"),
                new SubscriberRow("Anna Verdi", "anna@example.com", "Trekking Cascate del Marmarico", "Ieri, 18:05")
        );
        subscribersTable.setItems(subscribers);
    }

    @FXML
    private void handleNewActivity(ActionEvent event) {}

    private void handleEditTrip(TripDTO trip) {}

    private void handleDeleteTrip(TripDTO trip) {}
}