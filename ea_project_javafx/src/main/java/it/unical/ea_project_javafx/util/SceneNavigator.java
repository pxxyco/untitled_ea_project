package it.unical.ea_project_javafx.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

public class SceneNavigator {

    private static SceneNavigator instance;
    private Stage mainStage;

    private final Deque<String> history = new ArrayDeque<>();

    private SceneNavigator() {}

    /**
     * Restituisce l'istanza unica del navigator.
     */
    public static synchronized SceneNavigator getInstance() {
        if (instance == null) {
            instance = new SceneNavigator();
        }
        return instance;
    }

    /**
     * Inizializza il navigator con lo Stage principale.
     * Da chiamare nel metodo start() della classe Main application.
     */
    public void init(Stage stage) {
        this.mainStage = stage;
    }

    /**
     * Carica una nuova scena e aggiunge la schermata corrente allo storico.
     * 
     * @param fxmlPath Il percorso del file FXML (es. "/views/HomeView.fxml")
     */
    public void loadScene(String fxmlPath) {
        if (mainStage == null) {
            throw new IllegalStateException("SceneNavigator non inizializzato. Chiamare il metodo init(Stage) prima");
        }

        try {
            // Se c'è già una vista visualizzata, la salva nello storico
            if (mainStage.getScene() != null && mainStage.getScene().getUserData() != null) {
                String currentView = (String) mainStage.getScene().getUserData();
                history.push(currentView);
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Scene scene;
            if (mainStage.getScene() != null) {
                scene = new Scene(root, mainStage.getScene().getWidth(), mainStage.getScene().getHeight());
            } else {
                scene = new Scene(root, AppConfig.WINDOW_WIDTH, AppConfig.WINDOW_HEIGHT);
            }
            // Salva il percorso FXML nei metadati della scena
            scene.setUserData(fxmlPath);

            mainStage.setScene(scene);
            mainStage.show();

        } catch (IOException e) {
            System.err.println("Impossibile caricare la vista: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Torna alla vista precedente nello storico.
     */
    public void goBack() {
        if (hasHistory()) {
            String previousView = history.pop();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(previousView));
                Parent root = loader.load();

                Scene scene = new Scene(root);
                scene.setUserData(previousView);

                mainStage.setScene(scene);
                mainStage.show();
            } catch (IOException e) {
                System.err.println("Impossibile tornare indietro a: " + previousView);
                e.printStackTrace();
            }
        }
    }

    /**
     * Svuota lo storico e carica direttamente la schermata iniziale.
     * Utile per pulsanti tipo "Home" o dopo il Logout.
     */
    public void goToHome(String homeFxmlPath) {
        history.clear();
        loadScene(homeFxmlPath);
    }

    /**
     * Verifica se c'è almeno una schermata precedente nello storico.
     */
    public boolean hasHistory() {
        return !history.isEmpty();
    }
}