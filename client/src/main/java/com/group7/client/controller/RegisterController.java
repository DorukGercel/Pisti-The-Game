package com.group7.client.controller;

import com.group7.client.definitions.screen.ScreenManager;
import com.group7.client.controller.common.BaseNetworkController;
import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.dto.authentication.AuthRequest;
import com.group7.client.dto.authentication.AuthResponse;
import com.group7.client.dto.common.CommonResponse;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.*;
import org.springframework.stereotype.Component;

/** Controller for the register form*/
@Component
public class RegisterController extends BaseNetworkController {
    /** Common api address of the back-end for controller requests*/
    @Value("${spring.application.apiAddress.player}") private String mApiAddress;

    /** FXML fields*/
    @FXML private TextField username_field;
    @FXML private TextField email_field;
    @FXML private PasswordField password_field;

    /** Setter injection method*/
    @Autowired
    public void setManagers(@Lazy ScreenManager screenManager, NetworkManager networkManager) {
        this.mScreenManager = screenManager;
        this.mNetworkManager = networkManager;
    }

    /** Returns to the main screen*/
    @FXML
    private void clickReturnButton() {
        mScreenManager.activatePane("main_menu", null);
    }

    /** Sends the fields as a register request*/
    @FXML
    private void clickRegisterButton() {
        String username = username_field.getText();
        String email = email_field.getText();
        String password = password_field.getText();

        // Check validity of fields
        if(!areFieldsValid(username, email, password)) {
            displayError("Register Player",
                    "Some fields are missing!");
            return;
        }

        // Exchange request and response
        AuthRequest authRequest = new AuthRequest(username, password, email);
        CommonResponse[] commonResponse = new AuthResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
                mApiAddress + "/register",
                HttpMethod.POST,
                authRequest,
                commonResponse,
                AuthResponse.class);

        // Check if operation is successful
        if (isOperationSuccess(commonResponse[0], networkStatusCode, AuthResponse.class, "Register Player")) {
            mScreenManager.activatePane("main_menu", null);
        }
        clearFields();
    }

    /** Clears all the fields*/
    private void clearFields() {
        username_field.clear();
        email_field.clear();
        password_field.clear();
    }

    /** Check whether inputs are valid*/
    private boolean areFieldsValid(String username, String email, String password) {
        return !username.isEmpty() && !email.isEmpty() && !password.isEmpty();
    }
}
