package com.group7.client.controller;

import com.group7.client.controller.common.BaseNetworkController;
import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.definitions.screen.ScreenManager;
import com.group7.client.dto.authentication.ResetPasswordRequest;
import com.group7.client.dto.authentication.ResetPasswordResponse;
import com.group7.client.dto.common.CommonResponse;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/** Controller for the reset password form*/
@Component
public class ResetPasswordController extends BaseNetworkController {
    /** Common api address of the back-end for controller requests*/
    @Value("${spring.application.apiAddress.player}") private String mApiAddress;

    @FXML
    private TextField email_field;

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

    /** Sends the email as a reset password request*/
    @FXML
    public void clickResetPasswordButton() {
        String email = email_field.getText();

        // Check validity of fields
        if(!areFieldsValid(email)) {
            displayError("Reset Password",
                    "Some fields are missing.");
            return;
        }

        // Exchange request and response
        ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest(email);
        CommonResponse[] commonResponse = new ResetPasswordResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
                mApiAddress + "/resetPassword",
                HttpMethod.PUT,
                resetPasswordRequest,
                commonResponse,
                ResetPasswordResponse.class);

        // Check if operation is successful
        if(isOperationSuccess(commonResponse[0], networkStatusCode, ResetPasswordResponse.class, "Reset Password")) {
            ResetPasswordResponse resetPasswordResponse = (ResetPasswordResponse) commonResponse[0];
            displaySuccess("New Password", resetPasswordResponse.getNewPassword());
            mScreenManager.activatePane("main_menu", null);
        }
        clearFields();
    }

    /** Clears all the fields*/
    private void clearFields() {
        email_field.clear();
    }

    /** Check whether inputs are valid*/
    private boolean areFieldsValid(String email) {
        return !email.isEmpty();
    }
}
