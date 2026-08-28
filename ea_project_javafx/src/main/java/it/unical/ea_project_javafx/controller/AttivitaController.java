package it.unical.ea_project_javafx.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import it.unical.ea_project_javafx.controller.attivita.ActivitySectionController;
import it.unical.ea_project_javafx.controller.attivita.TripSectionController;
import it.unical.ea_project_javafx.dto.ActivityDTO;
import it.unical.ea_project_javafx.dto.TripDTO;
import it.unical.ea_project_javafx.util.ApiService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AttivitaController {

    @FXML
    private StackPane bodyContainer;
    
    @FXML
    private Button btnDescrizione;
    @FXML
    private Button btnFoto;
    @FXML
    private Button btnItinerario;
    @FXML
    private Button btnRecensioni;
    
    @FXML
    private Spinner<Integer> spinnerPartecipanti;

    @FXML
    private DatePicker datePicker;
    
    @FXML
    private ImageView heroImage;

    @FXML
    private Label lblCategoria;

    @FXML
    private Label lblLocalita;

    @FXML
    private Label lblPrezzo;

    @FXML
    private Label lblRating;

    @FXML
    private Label lblTitolo;

    @FXML
    private Label lblTotale;

    @FXML
    private LoadingOverlayController loadingCardController;

    @FXML
    private ScrollPane contentScroll;
    @FXML
    private StackPane loadingOverlay;
    @FXML
    private StackPane hero;

    private final List<Object> sectionControllers = new ArrayList<>();

    private List<Button> sectionButtons;

    private Node itinerarioNode;
    private Node descrizioneNode;
    private Node fotoNode;
    private Node recensioniNode;
    private ActivityDTO activity;
    private TripDTO trip;
    private String id;
    private Type myType;

    private final static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
                    LocalDateTime.parse(json.getAsString()))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, type, ctx) ->
                    LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, type, ctx) ->
                    LocalTime.parse(json.getAsString()))
            .create();
    
    public enum Type {
        ACTIVITY, TRIP
    }

    final String path = "/it/unical/ea_project_javafx/fxml/attivita";

    @FXML
    void initialize() {
        
        loadingOverlay.setVisible(true);
        
        btnInitialize();

        // USE FOR DEBUG AND TEST
        //loadData(Type.TRIP, "1");
    }

    public void loadData(Type type, String id) {
        
        myType = type;
        this.id = id;
        
        switch (type) {
            case ACTIVITY -> {
                ApiService.get(
                        ApiService.BASE_URL + "/api/activities/" + id,
                        response ->  {
                            activity = GSON.fromJson(response.body(), ActivityDTO.class);

                            setActivityData();
                            handleDescrizione(null);
                            contentScroll.setManaged(true);
                            contentScroll.setVisible(true);
                            loadingOverlay.setVisible(false);
                        },
                        () -> {
                            loadingOverlay.setVisible(true);
                            loadingCardController.setError("Errore nel caricamento");
                        },
                        loading -> {
                            loadingOverlay.setVisible(true);
                            loadingCardController.setMessage("Caricamento...");
                        }
                );
            }
            case TRIP -> {
                ApiService.get(
                        ApiService.BASE_URL + "/api/trips/" + id,
                        response ->  {
                            trip = GSON.fromJson(response.body(), TripDTO.class);

                            setTripData();
                            handleDescrizione(null);
                            loadingOverlay.setVisible(false);
                            contentScroll.setManaged(true);
                            contentScroll.setVisible(true);
                        },
                        () -> {
                            loadingOverlay.setVisible(true);
                            loadingCardController.setError("Errore nel caricamento");
                        },
                        loading -> {
                            loadingOverlay.setVisible(true);
                            loadingCardController.setMessage("Caricamento...");
                        }
                );
            }
        }
    }

    private void setActivityData() {
        if (activity.getImages().isEmpty()) {
            heroImage.setImage(null);
        } else {
            heroImage.setImage(new Image(ApiService.BASE_URL + activity.getImages().getFirst().getImageUrl()));
        }

        if (activity.getStartDate() != null) {
            datePicker.setValue(LocalDate.from(activity.getStartDate()));
        }
        else {
            datePicker.setValue(LocalDate.now());
        }

        lblLocalita.setText(activity.getCity());
        lblTitolo.setText(activity.getTitle());
        if (activity.getAverageRating() != null) {
            lblRating.setText( "★ " + BigDecimal.valueOf(activity.getAverageRating()));
        }
        else {
            lblRating.setText("★ " + BigDecimal.ZERO);
        }


        BigDecimal prezzo = activity.getPrice() != null ? BigDecimal.valueOf(activity.getPrice()) : null;
        lblPrezzo.setText(setMoneyCurrency(prezzo));
        lblTotale.setText(setMoneyCurrency(prezzo));

    }

    private void setTripData() {
        if (trip.getCoverPhotoUrl() == null) {
            heroImage.setImage(null);
        } else {

            heroImage.setImage(new Image(ApiService.BASE_URL + trip.getCoverPhotoUrl()));
        }

        if (trip.getStartDate() != null) {
            datePicker.setValue(trip.getStartDate());
        }
        else {
            datePicker.setValue(LocalDate.now());
        }

        lblLocalita.setText(trip.getDestinationCity());
        lblTitolo.setText(trip.getTitle());

        if(trip.getAverageRating() != null) {
            lblRating.setText( "★ " + trip.getAverageRating());
        }
        else {
            lblRating.setText( "★ " + BigDecimal.ZERO);
        }

        lblPrezzo.setText(setMoneyCurrency(trip.getTotalPrice()));
        lblTotale.setText(setMoneyCurrency(trip.getTotalPrice()));
    }

    private Node loadSection(String fxmlName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path + "/" + fxmlName));
            Node node = loader.load();
            Object controller = loader.getController();

            if (myType == Type.ACTIVITY && activity != null
                    && controller instanceof ActivitySectionController asc) {
                sectionControllers.add(asc);
                asc.setData(activity);
            } else if (myType == Type.TRIP && trip != null
                    && controller instanceof TripSectionController tsc) {
                tsc.setData(trip);
            }

            return node;
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile caricare la sezione: " + fxmlName, e);
        }
    }

    private void showSection(Node node) {
        bodyContainer.getChildren().setAll(node);
    }


    @FXML
    private void handleDescrizione(ActionEvent event) {
        setActiveButton(btnDescrizione);
        descrizioneNode = loadSection("Descrizione.fxml");
        showSection(descrizioneNode);
    }

    @FXML
    private void handleItinerario(ActionEvent event) {
        setActiveButton(btnItinerario);
        itinerarioNode = loadSection("Itinerario.fxml");
        showSection(itinerarioNode);
    }

    @FXML
    protected void handleFoto(ActionEvent event) {
        setActiveButton(btnFoto);
        fotoNode = loadSection("FotoContainer.fxml");
        showSection(fotoNode);
    }

    @FXML
    protected void handleRecensioni(ActionEvent event) {
        setActiveButton(btnRecensioni);
        recensioniNode = loadSection("RecensioniContainer.fxml");   // bug corretto
        showSection(recensioniNode);
    }

    private void btnInitialize(){
        sectionButtons = List.of(btnDescrizione, btnItinerario, btnFoto, btnRecensioni);

        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1);
        spinnerPartecipanti.setValueFactory(valueFactory);

        spinnerPartecipanti.valueProperty().addListener((obs, oldValue, newValue) -> {
            aggiornaPrezzoTotale(newValue);
        });
    }

    private void aggiornaPrezzoTotale(Integer newValue) {
        Integer partecipanti = spinnerPartecipanti.getValue();
        BigDecimal prezzo = (myType == Type.ACTIVITY)
                ? (activity.getPrice() != null ? BigDecimal.valueOf(activity.getPrice()) : BigDecimal.ZERO)
                : trip.getTotalPrice();

        BigDecimal totale = (prezzo != null) ? (prezzo.multiply(BigDecimal.valueOf(partecipanti))) : BigDecimal.ZERO;

        lblTotale.setText(setMoneyCurrency(totale));
    }

    private String setMoneyCurrency(BigDecimal amount) {
        return amount + "€";
    }

    private void setActiveButton(Button active) {
        for (Button b : sectionButtons) {
            b.getStyleClass().remove("section-button-active");
            if (!b.getStyleClass().contains("section-button")) {
                b.getStyleClass().add("section-button");
            }
        }
        active.getStyleClass().remove("section-button");
        active.getStyleClass().add("section-button-active");
    }

}
