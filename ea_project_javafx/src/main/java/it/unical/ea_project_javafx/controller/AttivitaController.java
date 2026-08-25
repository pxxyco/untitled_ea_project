package it.unical.ea_project_javafx.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import it.unical.ea_project_javafx.dto.ActivityDTO;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private Label loadingLabel;

    @FXML
    private ScrollPane contentScroll;
    @FXML
    private StackPane loadingOverlay;
    @FXML
    private StackPane hero;



    private List<Button> sectionButtons;

    private Node itinerarioNode;
    private Node descrizioneNode;
    private Node fotoNode;
    private Node recensioniNode;
    private ActivityDTO activity;


    private String id = "1";
    private final static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (json, type, ctx) ->
                    LocalDateTime.parse(json.getAsString()))
            .create();

    final String path = "/it/unical/ea_project_javafx/fxml/attivita";

    @FXML
    void initialize() {
        btnInitialize();
        itinerarioNode = loadSection("Itinerario.fxml");

        loadData();
    }

    private void loadData() {

        ApiService.get(
                ApiService.BASE_URL + "/api/activities/" + id,
                response ->  {
                    activity = GSON.fromJson(response.body(), ActivityDTO.class);

                    setData();
                    contentScroll.setManaged(true);
                    contentScroll.setVisible(true);
                    loadingOverlay.setVisible(false);
                },
                () -> {
                    loadingOverlay.setVisible(false);
                    loadingLabel.getStyleClass().clear();
                    loadingLabel.getStyleClass().add("loading-error");
                    loadingLabel.setText("Errore nel caricamento");
                },
                loading -> {
                    loadingOverlay.setVisible(true);
                    loadingLabel.getStyleClass().clear();
                    loadingLabel.getStyleClass().add("loading-text");
                }
        );

    }

    private void setData() {

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
        lblRating.setText( "★ " + activity.getAverageRating());
        lblPrezzo.setText(setMoneyCurrency(activity.getPrice()));
        lblTotale.setText(setMoneyCurrency(activity.getPrice()));


    }

    private Node loadSection(String fxmlName) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(path + "/" + fxmlName));
        try {
            return loader.load();
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
        handleDescrizione(null);

        SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1);
        spinnerPartecipanti.setValueFactory(valueFactory);

        spinnerPartecipanti.valueProperty().addListener((obs, oldValue, newValue) -> {
            aggiornaPrezzoTotale(newValue);
        });
    }

    private void aggiornaPrezzoTotale(Integer newValue) {
        Integer partecipanti = spinnerPartecipanti.getValue();
        Double prezzo = activity.getPrice();
        Double totale = (prezzo != null) ? (prezzo * partecipanti) : 0.0;

        lblTotale.setText(setMoneyCurrency(totale));
    }

    private String setMoneyCurrency(Double amount) {
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
