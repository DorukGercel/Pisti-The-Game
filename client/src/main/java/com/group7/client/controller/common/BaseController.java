package com.group7.client.controller.common;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.util.Objects;

/** Abstract controller which implements common controller methods*/
public abstract class BaseController {

    /** Label displayed on screen*/
    @FXML protected Label informationLabel;

    /** Open alert dialog*/
    protected void displayAlert(Alert.AlertType alertType, String title, String headerText, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(Objects.requireNonNull(message));
        alert.showAndWait();
        loseFocus();
    }

    protected void loseFocus() {
        informationLabel.requestFocus();
    }
}
