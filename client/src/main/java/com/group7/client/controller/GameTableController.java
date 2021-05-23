package com.group7.client.controller;

import com.group7.client.controller.common.BaseNetworkController;
import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.game.MoveType;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.definitions.player.PlayerManager;
import com.group7.client.definitions.screen.ScreenManager;
import com.group7.client.dto.common.CommonResponse;
import com.group7.client.dto.game.InteractRequest;
import com.group7.client.dto.game.InteractResponse;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GameTableController extends BaseNetworkController
        implements ApplicationListener<UserMenuController.StartGameEvent> {
    /** Common api address of the back-end for controller requests*/
    @Value("${spring.application.apiAddress.game}") private String apiAddress;

    /** FXML fields*/
    @FXML private Label active_player_label;
    @FXML private Label active_player_score_label;
    @FXML private Label pc_score_label;
    @FXML private Group player_area_container;
    @FXML private Group middle_area_container;
    @FXML private Group pc_area_container;

    /** Setter injection method*/
    @Autowired
    public void setManagers(@Lazy ScreenManager screenManager, NetworkManager networkManager, PlayerManager playerManager) {
        this.mScreenManager = screenManager;
        this.mNetworkManager = networkManager;
        this.mPlayerManager = playerManager;
    }

    /** Start Game Event listener, sets username*/
    @Override
    public void onApplicationEvent(UserMenuController.StartGameEvent startGameEvent) {
        active_player_label.setText(mPlayerManager.getUsername());
        performInitialInteract();
        // TODO: Delete this
        active_player_score_label.setText(mPlayerManager.getGameId().toString());
    }

    private void performInitialInteract() {
        // Exchange request and response
        InteractRequest interactRequest = new InteractRequest(mPlayerManager.getSessionId(),
                mPlayerManager.getGameId(),
                (short)-1,  //Card no doesn't matter
                MoveType.INITIAL);
        CommonResponse[] commonResponse = new InteractResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
                apiAddress + "/interactGame",
                HttpMethod.PUT,
                interactRequest,
                commonResponse,
                InteractResponse.class);

        if (isOperationSuccess(commonResponse[0], networkStatusCode, InteractResponse.class, "Interact Game - Initial")) {
            InteractResponse interactResponse = (InteractResponse) commonResponse[0];
            System.out.println(interactResponse.getPlayerEnvironment());
            System.out.println(interactResponse.getPcEnvironment());
        }

    }

}
