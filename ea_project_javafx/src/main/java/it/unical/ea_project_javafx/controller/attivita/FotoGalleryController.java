package it.unical.ea_project_javafx.controller.attivita;

import it.unical.ea_project_javafx.controller.attivita.fotoContainer.FotoItemController;
import it.unical.ea_project_javafx.dto.ActivityDTO;
import it.unical.ea_project_javafx.dto.ActivityImageDTO;
import it.unical.ea_project_javafx.dto.TripDTO;
import it.unical.ea_project_javafx.util.FullscreenPhotoViewer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import lombok.Setter;

import java.io.IOException;
import java.util.List;

public class FotoGalleryController {

    @FXML public StackPane rootStack;
    @FXML private FlowPane imgsContainer;

    @FXML private Button moreButton;

    private int paging = 0;
    private static final int PAGE_SIZE = 20;
    private List<String> imageUrls;

    @Setter
    private Runnable onBackLink;

    final private String path = "/it/unical/ea_project_javafx/fxml/attivita/fotoContainer";

    @FXML
    void initialize() throws IOException {
        paging = 0;
    }

    public void setImages(List<String> urls) {

        this.imageUrls = urls;

        if (this.imageUrls.size() > PAGE_SIZE) {
            moreButton.setManaged(true);
            moreButton.setVisible(true);
        }
        else {
            moreButton.setManaged(false);
            moreButton.setVisible(false);
        }

        loadNextPage();
    }

    private void loadNextPage() {
        int start = paging * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, imageUrls.size());

        for (int i = start; i < end; i++) {
            loadImage(imageUrls.get(i));
        }
        paging++;
    }

    private void loadImage(String url){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path + "/FotoItem.fxml"));
            Node foto = loader.load();

            FotoItemController controller = loader.getController();
            controller.setFoto(url);
            controller.setOnFotoClickedCallBack( s -> mostraFotoFullscreen(url));

            imgsContainer.getChildren().add(foto);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void backButtonAction(ActionEvent event) throws IOException {
        if(onBackLink != null)
            onBackLink.run();
    }

    @FXML
    public void handleAddMore(ActionEvent actionEvent) {
        loadNextPage();
    }

    private void mostraFotoFullscreen(String url) {
        FullscreenPhotoViewer.mostra(rootStack, url);
    }
}
