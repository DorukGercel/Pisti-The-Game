package com.group7.client.controller;

import com.group7.client.controller.common.BaseNetworkController;
import com.group7.client.definitions.screen.ScreenManager;
import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.definitions.player.PlayerManager;
import com.group7.client.dto.authentication.AuthResponse;
import com.group7.client.dto.authentication.LogoutRequest;
import com.group7.client.dto.common.CommonResponse;
import com.group7.client.dto.game.InitGameRequest;
import com.group7.client.dto.game.InitGameResponse;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/** Controller for the user menu*/
@Component
public class UserMenuController extends BaseNetworkController {
    /** Common api addresses of the back-end for controller requests*/
    @Value("${spring.application.apiAddress.player}") private String mPlayerApiAddress;
    @Value("${spring.application.apiAddress.game}") private String mGameApiAddress;

    /** Setter injection method*/
    @Autowired
    public void setManagers(@Lazy ScreenManager screenManager, NetworkManager networkManager, PlayerManager playerManager) {
        this.mScreenManager = screenManager;
        this.mNetworkManager = networkManager;
        this.mPlayerManager = playerManager;
    }

    /** Creates a new game and switches to the game table*/
    @FXML
    private void clickStartGameButton() {
        // Exchange request and response
        InitGameRequest initGameRequest = new InitGameRequest(mPlayerManager.getSessionId());
        CommonResponse[] commonResponse = new InitGameResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
                mGameApiAddress + "/startGame",
                HttpMethod.PUT,
                initGameRequest,
                commonResponse,
                InitGameResponse.class);

        // Check if operation is successful
        if (isOperationSuccess(commonResponse[0], networkStatusCode, InitGameResponse.class, "Start Game")) {
            InitGameResponse initGameResponse = (InitGameResponse) commonResponse[0];
            mPlayerManager.setGameId(initGameResponse.getGameId());
            mScreenManager.activatePane("game_table", new CreateGameEvent());
        }
    }

    /** Switches to leaderboard screen*/
    @FXML
    private void clickLeaderboardButton() {
        mScreenManager.activatePane("leaderboard", null);
        mScreenManager.activatePane("leaderboard", new InitLeaderboardEvent());
    }

    /** Performs logout action*/
    @FXML
    private void clickLogoutButton() {
        // Exchange request and response
        LogoutRequest logoutRequest = new LogoutRequest(mPlayerManager.getSessionId());
        CommonResponse[] commonResponse = new AuthResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
                mPlayerApiAddress + "/logout",
                HttpMethod.DELETE,
                logoutRequest,
                commonResponse,
                AuthResponse.class);

        // Check if operation is successful
        if (isOperationSuccess(commonResponse[0], networkStatusCode, AuthResponse.class, "Logout Player")) {
            AuthResponse authResponse = (AuthResponse) commonResponse[0];
            mPlayerManager.setUsername("");
            mPlayerManager.setSessionId(-1L);
            mScreenManager.activatePane("main_menu", null);
        }
    }

    /** Event which indicates the game is started*/
    public static class CreateGameEvent extends ApplicationEvent {
        public CreateGameEvent() {
            super(UserMenuController.class);
        }
    }

    /** Event which indicates the game is started*/
    public static class InitLeaderboardEvent extends ApplicationEvent {
        public InitLeaderboardEvent() {
            super(UserMenuController.class);
        }
    }
}
