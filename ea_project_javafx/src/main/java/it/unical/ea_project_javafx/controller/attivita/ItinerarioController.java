package it.unical.ea_project_javafx.controller.attivita;

import it.unical.ea_project_javafx.dto.ActivityDTO;
import it.unical.ea_project_javafx.util.ApiService;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;

public class ItinerarioController implements ActivitySectionController {


    @FXML
    private VBox itinerarioContainer;

    @FXML
    private WebView mapView;

    @Override
    public void setData(ActivityDTO activity) {

        ApiService.get(
            ApiService.BASE_URL,
            response -> {},
            () -> {},
            loading -> {}
        );

    }
}
