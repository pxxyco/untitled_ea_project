package it.unical.ea_project_javafx.controller.attivita.fotoContainer;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import lombok.Setter;

import java.util.function.Consumer;

public class FotoItemController {

    private String url;
    @FXML
    private ImageView image;

    @Setter
    private Consumer<String> onFotoClickedCallBack;

    public void setFoto(String url) {
        this.url = url;
        image.setImage(new Image(url, 150, 150, false, true));
    }

    @FXML
    void handleClick(MouseEvent event) {
        if(onFotoClickedCallBack != null)
            onFotoClickedCallBack.accept(url);
    }

}
