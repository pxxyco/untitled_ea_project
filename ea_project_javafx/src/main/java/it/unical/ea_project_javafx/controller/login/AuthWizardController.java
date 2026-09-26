package it.unical.ea_project_javafx.controller.login;

import it.unical.ea_project_javafx.model.StepNavigator;
import it.unical.ea_project_javafx.util.SceneNavigator;
import it.unical.ea_project_javafx.util.WallpaperService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

//Controller del login (LoginStepController, RoleSelectionStepController, RegistrationStepController)
public class AuthWizardController implements StepNavigator {

    @FXML private ImageView bgImageView;
    @FXML private VBox stepLogin, stepForgotPassword, stepRole, stepDetails, loadingOverlay;

    @FXML private LoginStepController stepLoginController;
    @FXML private ForgotPasswordController stepForgotPasswordController;
    @FXML private RoleSelectionStepController stepRoleController;
    @FXML private RegistrationStepController stepDetailsController;


    @FXML
    public void initialize() {
        bgImageView.setEffect(new GaussianBlur(18));
        loadBackground();

        if (bgImageView != null && bgImageView.getParent() instanceof StackPane parentPane) {
            bgImageView.fitWidthProperty().bind(parentPane.widthProperty());
            bgImageView.fitHeightProperty().bind(parentPane.heightProperty());
        }

        stepLoginController.setNavigator(this);
        stepForgotPasswordController.setNavigator(this);
        stepRoleController.setNavigator(this);
        stepDetailsController.setNavigator(this);

        goToLoginStep();
    }

    private void loadBackground() {
        if (bgImageView == null) {
            return;
        }

        new Thread(() -> {
            String imageUrl = WallpaperService.getDailyWallpaperUrl();
            Platform.runLater(() -> bgImageView.setImage(new Image(
                    imageUrl,
                    1920,
                    1080,
                    true,
                    true,
                    true
            )));
        }).start();
    }

    @Override public void goToLoginStep() {
        stepLoginController.clearError();
        showStep(stepLogin);
    }

    @Override public void goToForgotPasswordStep() {
        stepForgotPasswordController.clearMessage();
        showStep(stepForgotPassword);
    }

    @Override public void goToRoleStep() {
        showStep(stepRole);
    }

    @Override
    public void goToDetailsStep(String selectedRole) {
        stepDetailsController.clearError();
        stepDetailsController.setSelectedRole(selectedRole);
        showStep(stepDetails);
    }

    @Override
    public void goToHome(ActionEvent event) {
        //ViewNavigator.switchScene((Node) event.getSource(), "/it/unical/ea_project_javafx/fxml/home/mainview.fxml");
        SceneNavigator.getInstance().loadScene("/it/unical/ea_project_javafx/fxml/home/mainview.fxml");
    }

    @FXML
    void goToHomeFromButton(ActionEvent event) {
        goToHome(event);
    }


    @Override
    public void setLoading(boolean loading) {
        loadingOverlay.setVisible(loading);
        loadingOverlay.setManaged(loading);
    }

    @Override
    public void showErrorMessage(String message) {
        if (stepLogin.isVisible()) {
            stepLoginController.showError(message);
        } else if (stepForgotPassword.isVisible()) {
            stepForgotPasswordController.showError(message);
        } else if (stepDetails.isVisible()) {
            stepDetailsController.showError(message);
        }
    }

    private void showStep(VBox stepToShow) {
        stepLogin.setVisible(false);   stepLogin.setManaged(false);
        stepForgotPassword.setVisible(false); stepForgotPassword.setManaged(false);
        stepRole.setVisible(false);    stepRole.setManaged(false);
        stepDetails.setVisible(false); stepDetails.setManaged(false);
        stepToShow.setVisible(true);   stepToShow.setManaged(true);
    }

}