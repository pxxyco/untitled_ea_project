package it.unical.ea_project_javafx.controller.home;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.scene.image.Image;

public class ImageLoadListener implements InvalidationListener {

    private final String imageUrl;
    private final Image image;
    private final Runnable onFinish;
    private boolean active = true;

    public ImageLoadListener(String imageUrl, Image image, Runnable onFinish) {
        this.imageUrl = imageUrl;
        this.image = image;
        this.onFinish = onFinish;
    }

    public void attach() {
        image.progressProperty().addListener(this);
        image.errorProperty().addListener(this);
    }

    @Override
    public void invalidated(Observable observable) {
        if (!active) return;

        if (image.isError()) {
            ExperienceCardController.removeCachedImage(imageUrl, image);
            finish();
            return;
        }

        if (image.getProgress() >= 1.0) {
            finish();
        }
    }

    private void finish() {
        if (!active) return;
        active = false;

        image.progressProperty().removeListener(this);
        image.errorProperty().removeListener(this);

        if (onFinish != null) {
            onFinish.run();
        }
    }
}