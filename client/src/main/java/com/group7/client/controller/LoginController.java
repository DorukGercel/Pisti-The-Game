package com.group7.client.controller;

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

import java.util.Objects;

/** Controller for the login form*/
@Component
public class LoginController extends BaseNetworkController {

    /** Reference to common screen manager*/
    private ScreenManager mScreenManager;
    /** Reference to common network manager*/
    private NetworkManager mNetworkManager;
    /** Common api address of the back-end for controller requests*/
    @Value("${spring.application.apiAddress.player}") private String apiAddress;

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
    public void clickReturnButton() {
        mScreenManager.activatePane("main_menu");
    }

    /** Sends the fields as a login request*/
    @FXML
    public void clickLoginButton() {
        String username = username_field.getText();
        String email = email_field.getText();
        String password = password_field.getText();
        // TODO: Remove print
        System.out.println(password);

        // Check validity of fields
        if(!areFieldsValid(username, email, password)) {
            displayAlert(Alert.AlertType.ERROR,
                    "Error",
                    "Login Player",
                    "Some fields are missing.");
            return;
        }

        // Exchange request and response
        AuthRequest authRequest = new AuthRequest(username, password, email);
        CommonResponse[] commonResponse = new LoginResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
                apiAddress + "/login",
                HttpMethod.POST,
                authRequest,
                commonResponse,
                LoginResponse.class);

        // Check if network operation is successful
        if(isNetworkOperationSuccess(commonResponse[0], networkStatusCode)) {
            LoginResponse loginResponse = (LoginResponse) commonResponse[0];
            // Check if operation is successful
            if (isOperationSuccess(loginResponse)) {
                mScreenManager.activatePane("user_menu");
            } else {
                displayAlert(Alert.AlertType.ERROR,
                        "Error",
                        "Login Player",
                        Objects.requireNonNull(loginResponse).getErrorMessage());
            }
        } else {
            displayAlert(Alert.AlertType.ERROR,
                    "Error",
                    "Login Player",
                    "Network connection error occurred!");
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
