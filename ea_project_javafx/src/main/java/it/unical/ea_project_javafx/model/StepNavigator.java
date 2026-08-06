package it.unical.ea_project_javafx.model;

import javafx.event.ActionEvent;

public interface StepNavigator {
    void goToLoginStep();
    void goToRoleStep();
    void goToDetailsStep(String selectedRole);
    void goToHome(ActionEvent event);
    void setLoading(boolean loading);
    void showAlert(javafx.scene.control.Alert.AlertType type, String title, String content);
}