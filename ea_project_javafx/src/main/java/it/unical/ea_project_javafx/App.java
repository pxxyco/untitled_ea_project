package it.unical.ea_project_javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("EA Project");

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("fxml/pre-main.fxml")));
        Scene splashScene = new Scene(root, 1280, 720);
        splashScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("css/style.css")).toExternalForm());

        stage.setScene(splashScene);
        stage.setMinWidth(1280);
        stage.setMinHeight(720);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(App.class.getResource("fxml/" + fxml + ".fxml")));
        scene.setRoot(root);
    }

    public static void main(String[] args) {
        launch(args);
    }
}