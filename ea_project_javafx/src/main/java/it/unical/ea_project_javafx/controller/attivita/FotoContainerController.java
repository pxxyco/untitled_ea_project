package it.unical.ea_project_javafx.controller.attivita;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.unical.ea_project_javafx.controller.attivita.fotoContainer.FotoItemController;
import it.unical.ea_project_javafx.controller.attivita.fotoContainer.FotoPlaceholderController;
import it.unical.ea_project_javafx.dto.ActivityDTO;
import it.unical.ea_project_javafx.dto.ActivityImageDTO;
import it.unical.ea_project_javafx.dto.TripDTO;
import it.unical.ea_project_javafx.dto.TripImageDTO;
import it.unical.ea_project_javafx.util.ApiService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import lombok.Setter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class FotoContainerController implements ActivitySectionController, TripSectionController {

    private static final Gson GSON_TRIP = new Gson() ;
    private static final Type TRIP_IMAGE_LIST_TYPE = new TypeToken<List<TripImageDTO>>(){}.getType();
    final private String path = "/it/unical/ea_project_javafx/fxml/attivita/fotoContainer";

    @Setter
    private Runnable onGalleryRequested;

    @FXML
    private FlowPane imgsContainer;

    @FXML
    public void initialize(){

    }

    public void setData(ActivityDTO activity){
        loadActivityImages(activity.getImages());
    }

    public void setData(TripDTO trip){

        ApiService.get(
                ApiService.BASE_URL + "/api/trip-images/trip/" + trip.getTripId(),
                response -> {
                    List<TripImageDTO> images = GSON_TRIP.fromJson(response.body(), TRIP_IMAGE_LIST_TYPE);
                    loadTripImages(images);
                },
                () -> {

                },
                loading -> {}
        );
    }

    private void loadActivityImages(List<ActivityImageDTO> activities){

        int max = Math.min(activities.size(), 5);
        for(int i = 0; i < max; i++){
            String url = ApiService.BASE_URL + activities.get(i).getImageUrl();
            loadImage(url);
        }

        if(activities.size() > 5) {
            addPlaceholder();
        }
    }


    private void loadTripImages(List<TripImageDTO> images) {

        int max = Math.min(images.size(), 5);
        for(int i = 0; i < max; i++){
            String url = ApiService.BASE_URL + images.get(i).getImageUrl();
            loadImage(url);
        }

        if(images.size() > 5) {
            addPlaceholder();
        }
    }

    private void loadImage(String url){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path + "/FotoItem.fxml"));
            Node foto = loader.load();

            FotoItemController controller = loader.getController();
            controller.setFoto(url);

            imgsContainer.getChildren().add(foto);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void addPlaceholder(){
        try {
            FXMLLoader placeholderLoader = new FXMLLoader();
            placeholderLoader.setLocation(getClass().getResource(path + "/FotoPlaceholder.fxml"));
            Node placeholder = placeholderLoader.load();               // load() PRIMA di getController()

            FotoPlaceholderController controller = placeholderLoader.getController();
            controller.setOnPlaceholderClickedCallBack(() -> {
                if (onGalleryRequested != null) onGalleryRequested.run();
            });

            imgsContainer.getChildren().add(placeholder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
