package com.group7.client.controller;

import com.group7.client.definitions.game.GameManager;
import com.group7.client.definitions.game.GameStatusCode;
import com.group7.client.definitions.game.MoveType;
import com.group7.client.definitions.game.MultiplayerGameManager;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.definitions.player.PlayerManager;
import com.group7.client.definitions.screen.ScreenManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;

@Component
public class MultiplayerGameTableController extends GameController{
    /** Setter injection method*/
    @Autowired
    public void setManagers(@Lazy ScreenManager screenManager, NetworkManager networkManager, PlayerManager playerManager, MultiplayerGameManager gameManager) {
        this.mScreenManager = screenManager;
        this.mNetworkManager = networkManager;
        this.mPlayerManager = playerManager;
        this.mGameManager = gameManager;
    }

    /** Start Game Event listener, sets username*/
    @EventListener({GameTableController.CreateMultiplayerGameEvent.class})
    public void onCreateMultiplayerGameEvent() {
        active_player_label.setText(mPlayerManager.getUsername());
        setCurrentLevel();
        Executors.newSingleThreadExecutor().execute(() -> mGameManager.run());
        performInteract(MoveType.INITIAL, GameStatusCode.NORMAL, (short) -1);
    }

    @Override
    public void simulateMove(MoveType moveType, GameStatusCode gameStatusCode, short cardNo) {

    }

    @Override
    protected void performInteract(MoveType moveType, GameStatusCode gameStatusCode, short cardNo) {

    }

    /** In multiplayer key combinations are not necessary*/
    @Override
    protected void turnOnKeyComb() {

    }

    /** In multiplayer key combinations are not necessary*/
    @Override
    protected void turnOffKeyComb() {

    }
}
