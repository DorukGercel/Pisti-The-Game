package com.group7.client.controller;

import com.group7.client.definitions.player.PlayerManager;
import com.group7.client.definitions.screen.ScreenManager;
import com.group7.client.controller.common.BaseNetworkController;
import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.dto.authentication.AuthRequest;
import com.group7.client.dto.authentication.LoginResponse;
import com.group7.client.dto.common.CommonResponse;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/** Controller for the login form*/
@Component
public class LoginController extends BaseNetworkController {
    /** Common api address of the back-end for controller requests*/
    @Value("${spring.application.apiAddress.player}") private String mApiAddress;

    /** FXML fields*/
    @FXML private TextField username_field;
    @FXML private TextField email_field;
    @FXML private PasswordField password_field;

    /** Setter injection method*/
    @Autowired
    public void setManagers(@Lazy ScreenManager screenManager, NetworkManager networkManager, @Lazy PlayerManager playerManager) {
        this.mScreenManager = screenManager;
        this.mNetworkManager = networkManager;
        this.mPlayerManager = playerManager;
    }

    /** Returns to the main screen*/
    @FXML
    private void clickReturnButton() {
        mScreenManager.activatePane("main_menu", null);
    }

    /** Sends the fields as a login request*/
    @FXML
    private void clickLoginButton() {
        String username = username_field.getText();
        String email = email_field.getText();
        String password = password_field.getText();
        // TODO: Remove print
        System.out.println(password);

        // Check validity of fields
        if(!areFieldsValid(username, email, password)) {
            displayError("Login Player",
                    "Some fields are missing.");
            return;
        }

        // Exchange request and response
        AuthRequest authRequest = new AuthRequest(username, password, email);
        CommonResponse[] commonResponse = new LoginResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
                mApiAddress + "/login",
                HttpMethod.POST,
                authRequest,
                commonResponse,
                LoginResponse.class);

        // Check if operation is successful
        if(isOperationSuccess(commonResponse[0], networkStatusCode, LoginResponse.class, "Login  Player")) {
            LoginResponse loginResponse = (LoginResponse) commonResponse[0];
            mPlayerManager.setUsername(username);
            mPlayerManager.setSessionId(loginResponse.getSessionId());
            mScreenManager.activatePane("user_menu", null);
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
