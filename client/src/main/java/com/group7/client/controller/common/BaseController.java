package com.group7.client.controller.common;

import com.group7.client.definitions.player.PlayerManager;
import com.group7.client.definitions.screen.ScreenManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

/** Abstract controller which implements common controller methods*/
public abstract class BaseController implements Initializable {
    /** Reference to common screen manager*/
    protected ScreenManager mScreenManager;
    /** Holds active player information*/
    protected PlayerManager mPlayerManager;

    /** Label displayed on screen*/
    @FXML protected Label informationLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        informationLabel = new Label();
    }

    /** Open success dialog*/
    protected void displaySuccess(String headerText, String message) {
        displayAlert(Alert.AlertType.INFORMATION, "Success", headerText, message);
    }

    /** Open error dialog*/
    protected void displayError(String headerText, String message) {
        displayAlert(Alert.AlertType.ERROR, "Error", headerText, message);
    }

    /** Open alert dialog*/
    private void displayAlert(Alert.AlertType alertType, String title, String headerText, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(Objects.requireNonNull(message));
        alert.showAndWait();
        loseFocus();
    }

    private void loseFocus() {
        informationLabel.requestFocus();
    }
}
