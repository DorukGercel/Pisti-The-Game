package com.group7.client.controller;

import com.group7.client.controller.common.BaseNetworkController;
import com.group7.client.definitions.screen.ScreenManager;
import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.definitions.player.PlayerManager;
import com.group7.client.dto.authentication.AuthResponse;
import com.group7.client.dto.authentication.LogoutRequest;
import com.group7.client.dto.common.CommonResponse;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Objects;

/** Controller for the user menu*/
@Component
public class UserMenuController extends BaseNetworkController {

    /** Reference to common screen manager*/
    private ScreenManager mScreenManager;
    /** Reference to common network manager*/
    private NetworkManager mNetworkManager;
    /** Reference to common player manager*/
    private PlayerManager mPlayerManager;
    /** Common api address of the back-end for controller requests*/
    @Value("${spring.application.apiAddress.player}") private String apiAddress;

    /** Setter injection method*/
    @Autowired
    public void setManagers(@Lazy ScreenManager screenManager, NetworkManager networkManager, PlayerManager playerManager) {
        this.mScreenManager = screenManager;
        this.mNetworkManager = networkManager;
        this.mPlayerManager = playerManager;
    }

    @FXML
    public void clickStartGameButton() {
    }

    @FXML
    public void clickLeaderboardButton() {
        mScreenManager.activatePane("leaderboard");
    }

    /** Performs logout action*/
    @FXML
    public void clickLogoutButton() {
        // Exchange request and response
        LogoutRequest logoutRequest = new LogoutRequest(mPlayerManager.getSessionId());
        CommonResponse[] commonResponse = new AuthResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
                apiAddress + "/logout",
                HttpMethod.DELETE,
                logoutRequest,
                commonResponse,
                AuthResponse.class);

        // Check if network operation is successful
        if(isNetworkOperationSuccess(commonResponse[0], networkStatusCode)) {
            AuthResponse authResponse = (AuthResponse) commonResponse[0];
            // Check if operation is successful
            if (isOperationSuccess(authResponse)) {
                mScreenManager.activatePane("main_menu");
            } else {
                displayAlert(Alert.AlertType.ERROR,
                        "Error",
                        "Logout Player",
                        Objects.requireNonNull(authResponse).getErrorMessage());
            }
        } else {
            displayAlert(Alert.AlertType.ERROR,
                    "Error",
                    "Logout Player",
                    "Network connection error occurred!");
        }
    }
}
