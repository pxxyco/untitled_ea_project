package it.unical.ea_project_javafx.controller;

import it.unical.ea_project_javafx.model.BookingModel;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class UserHomeController {

    @FXML private ImageView bgImageView;
    @FXML private HBox userProfileBox;
    @FXML private Label lblAvatar;
    @FXML private Label lblUserName;
    @FXML private Label lblUserEmail;
    @FXML private TextField txtSearch;
    @FXML private FlowPane cardsGrid;
    
    @FXML private TableView<BookingModel> bookingsTable;
    @FXML private TableColumn<BookingModel, String> colActivity;
    @FXML private TableColumn<BookingModel, String> colDateTime;
    @FXML private TableColumn<BookingModel, String> colLocation;
    @FXML private TableColumn<BookingModel, String> colStatus;

    private final ContextMenu profileMenu = new ContextMenu();

    @FXML
    public void initialize() {
        // Setup Resize dinamico per sfondo
        bgImageView.fitWidthProperty().bind(bgImageView.sceneProperty().flatMap(s -> s.widthProperty()));
        bgImageView.fitHeightProperty().bind(bgImageView.sceneProperty().flatMap(s -> s.heightProperty()));

        // Setup Dropdown Profilo
        MenuItem itemAccount = new MenuItem("👤 Informazioni account");
        MenuItem itemLogout = new MenuItem("🚪 Esci");
        profileMenu.getItems().addAll(itemAccount, itemLogout);

        // Binding Tabella
        colActivity.setCellValueFactory(d -> d.getValue().activityProperty());
        colDateTime.setCellValueFactory(d -> d.getValue().dateTimeProperty());
        colLocation.setCellValueFactory(d -> d.getValue().locationProperty());
        colStatus.setCellValueFactory(d -> d.getValue().statusProperty());

        // Popola dati di prova
        loadMockData();
    }

    @FXML
    private void handleProfileClick(MouseEvent event) {
        profileMenu.show(userProfileBox, event.getScreenX(), event.getScreenY());
    }

    @FXML
    private void handleSearch() {
        String filter = txtSearch.getText();
        // Logica di filtraggio card
    }

    private void loadMockData() {
        // Popola Card dinamiche
        cardsGrid.getChildren().addAll(
            createCard("Escursione Sila in Quad", "EXCURSION", "Cosenza • Durata: 4 ore", "Sila Adventure", "€ 45.00", "8 posti disponibili", false),
            createCard("Visita MAB & Centro Storico", "VISIT", "Rende • Durata: 2 ore", "Guide Cosenza", "€ 12.00", "15 posti disponibili", false),
            createCard("Degustazione Vini & Prodotti", "MEAL", "Saracena • Durata: 3 ore", "Cantine di Calabria", "€ 30.00", "Completo (0 posti)", true)
        );

        // Popola Tabella
        bookingsTable.getItems().add(new BookingModel("Escursione Sila in Quad", "12 Giugno 2026 - 09:30", "Camigliatello Silano", "CONFERMATA"));
    }

    private VBox createCard(String title, String category, String info, String organizer, String price, String seats, boolean soldOut) {
        VBox card = new VBox(10);
        card.getStyleClass().add("card-item");

        HBox top = new HBox();
        Label cardTitle = new Label(title);
        cardTitle.getStyleClass().add("card-title");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label catLabel = new Label(category);
        catLabel.getStyleClass().add("card-category");
        top.getChildren().addAll(cardTitle, sp, catLabel);

        Label infoLbl = new Label(info);
        infoLbl.setStyle("-fx-text-fill: rgba(255,255,255,0.7); -fx-font-size: 12px;");

        Label orgLbl = new Label("Organizzato da: " + organizer);
        orgLbl.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 11px; -fx-font-style: italic;");

        HBox bottom = new HBox();
        bottom.setAlignment(Pos.CENTER_LEFT);
        bottom.setPadding(new Insets(8, 0, 0, 0));
        bottom.setStyle("-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1 0 0 0;");

        Label priceLbl = new Label(price);
        priceLbl.getStyleClass().add("card-price");

        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);

        Label seatsLbl = new Label(seats);
        seatsLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: rgba(255,255,255,0.8);");

        Button btnAction = new Button(soldOut ? "Esaurito" : "Prenota");
        btnAction.setDisable(soldOut);
        btnAction.getStyleClass().add("btn-primary");

        HBox rightBox = new HBox(10, seatsLbl, btnAction);
        rightBox.setAlignment(Pos.CENTER);

        bottom.getChildren().addAll(priceLbl, sp2, rightBox);
        card.getChildren().addAll(top, infoLbl, orgLbl, bottom);
        return card;
    }
}