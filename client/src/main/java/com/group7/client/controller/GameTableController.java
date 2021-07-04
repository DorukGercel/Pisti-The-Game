package com.group7.client.controller;

import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.game.*;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.definitions.player.PlayerManager;
import com.group7.client.definitions.screen.ScreenManager;
import com.group7.client.dto.common.CommonResponse;
import com.group7.client.dto.game.InteractRequest;
import com.group7.client.dto.game.InteractResponse;
import javafx.application.Platform;
import javafx.scene.input.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class GameTableController extends GameController {

    /** Setter injection method*/
    @Autowired
    public void setManagers(@Lazy ScreenManager screenManager, NetworkManager networkManager, PlayerManager playerManager, GameManager gameManager) {
        this.mScreenManager = screenManager;
        this.mNetworkManager = networkManager;
        this.mPlayerManager = playerManager;
        this.mGameManager = gameManager;
    }

    /** Start Game Event listener, sets username*/
    @EventListener({UserMenuController.CreateGameEvent.class})
    public void onCreateGameEvent() {
        active_player_label.setText(mPlayerManager.getUsername());
        setCurrentLevel();
        setKeyCombinationListener();
        Executors.newSingleThreadExecutor().execute(() -> mGameManager.run());
        performInteract(MoveType.INITIAL, GameStatusCode.NORMAL, (short) -1);
    }

    /** Cheat Level Up listener*/
    @EventListener({CheatLevelUpEvent.class})
    public void onCheatLevelUpEvent() {
        // TODO: Remove print
        System.out.println("call");
        performInteract(MoveType.INITIAL, GameStatusCode.CHEAT_LEVEL_UP, (short) -1);
    }

    /** Function which is invoked by game manager to simulate card move*/
    @Override
    public void simulateMove(MoveType moveType, GameStatusCode gameStatusCode, short cardNo) {
        Platform.runLater(()-> {
            // Perform back-end interaction
            performInteract(moveType, gameStatusCode, cardNo);
            // Re-init the key combination and drag drop events
            simulatePostMove();
        });
    }

    /** Helper function send the initial interact request*/
    @Override
    protected void performInteract(MoveType moveType, GameStatusCode gameStatusCode, short cardNo) {
        // Exchange request and response
        System.out.println(gameStatusCode);
        InteractRequest interactRequest = new InteractRequest(mPlayerManager.getSessionId(),
                mPlayerManager.getGameId(),
                cardNo,  //Card no doesn't matter for initial move
                moveType,
                gameStatusCode);
        CommonResponse[] commonResponse = new InteractResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
                mApiAddress + "/interactGame",
                HttpMethod.PUT,
                interactRequest,
                commonResponse,
                InteractResponse.class);

        if (isOperationSuccess(commonResponse[0], networkStatusCode, InteractResponse.class, "Interact Game")) {
            // Check if operation was successful
            InteractResponse interactResponse = (InteractResponse) commonResponse[0];
            // TODO: Remove print
            System.out.println(interactResponse.getPlayerEnvironment());
            System.out.println(interactResponse.getPcEnvironment());

            GameStatusCode receivedGameStatusCode = GameStatusCode.convertStrToGameStatusCode(interactResponse.getGameStatusCode());
            if(receivedGameStatusCode.equals(GameStatusCode.NORMAL)) {
                // Normal game operation
                simulateTurn(interactResponse.getPlayerEnvironment(),
                        interactResponse.getPcEnvironment());
            } else if (receivedGameStatusCode.equals(GameStatusCode.WIN)) {
                // Won the level so level up
                performInteract(MoveType.INITIAL, GameStatusCode.LEVEL_UP, (short) -1);
            } else if (receivedGameStatusCode.equals(GameStatusCode.LOST)) {
                // Lost the level so close the game
                displaySuccess("Game Lost", "Sorry for your lost :(");
                simulateGameOver();
                mScreenManager.activatePane("user_menu", null);
            } else if (receivedGameStatusCode.equals(GameStatusCode.GAME_OVER_WIN)) {
                displaySuccess("Game Won", "Congratulations, time for real opponents :)");
                simulateGameOver();
                mScreenManager.activatePane("multiplayer_game_table", new GameTableController.CreateMultiplayerGameEvent());
            } else {
                // Re-initialize the screen after level up
                simulateLevelUp();
                performInteract(MoveType.INITIAL, GameStatusCode.NORMAL, (short) -1);
            }
        }
    }

    /** Helper function to place the cards according to move type*/
    private void simulateTurn(GameEnvironment playerGameEnv, GameEnvironment pcGameEnv) {
        try {
            // Get the move types of both the player and pc
            MoveType playerMoveType = MoveType.convertStrToMoveType(playerGameEnv.getMMoveType());
            MoveType pcMoveType = MoveType.convertStrToMoveType(pcGameEnv.getMMoveType());
            // Decide the conditions according to the move types
            if (playerMoveType.equals(MoveType.INITIAL) || playerMoveType.equals(MoveType.REDEAL)) {
                simulateInitTurn(playerMoveType, playerGameEnv);
            } else if (playerMoveType.equals(MoveType.RESTART)) {
                simulateRestartTurn(playerMoveType, playerGameEnv, pcGameEnv);
            } else {
                simulatePlayerTurn(playerMoveType, MoveTurn.PLAYER, playerGameEnv);
                // TODO: Remove print
                System.out.println("a");
                TimeUnit.SECONDS.sleep(mSleepTime);
                // TODO: Remove print
                System.out.println("b");
                simulatePlayerTurn(pcMoveType, MoveTurn.PC, pcGameEnv);
                // TODO: Remove print
                System.out.println("c");

                // PC Game Env is actually the current state of the game
                simulatePostTurn(pcGameEnv);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /** Helper function to achieve level turn events, after player and pc moves*/
    private void simulatePostTurn(GameEnvironment currentGameEnv) {
        if(mGameManager.getMCurrentLevel() >= 3) {
            // Performs bluff level actions
            handleBluffLevelActions(currentGameEnv);
        }
    }

    /** Helper function to decide the move type of player and simulate player turn*/
    private void simulatePlayerTurn(MoveType moveType, MoveTurn moveTurn, GameEnvironment gameEnvironment) {
        if (MoveType.isBluffLevelMoveType(moveType)) {
            simulateBluffTurn(moveType, moveTurn, gameEnvironment);
        } else {
            placeMiddleCardAndSetScore(moveType, moveTurn, gameEnvironment);
        }
    }

    private void simulateBluffTurn(MoveType moveType, MoveTurn moveTurn, GameEnvironment gameEnvironment) {
        // TODO: Correct the texts
        if(moveType.equals(MoveType.BLUFF) && moveTurn.equals(MoveTurn.PLAYER)) {
            // TODO: Remove print
            System.out.println(moveTurn + " " + moveType);
            // Player made the bluff
            bluff_challenge_text.setVisible(true);
            bluff_challenge_text.setText(mPlayerManager.getUsername() + " Bluffed!");
        } else if(moveType.equals(MoveType.BLUFF) && moveTurn.equals(MoveTurn.PC)) {
            // TODO: Remove print
            System.out.println(moveTurn + " " + moveType);
            // PC made the bluff
            mPcBluffed = true;
            bluff_challenge_text.setVisible(true);
            bluff_challenge_text.setText("PC Bluffed!");
            mMiddleCard = mGameManager.getMiddleCard(gameEnvironment);
            placeMiddleCard(moveTurn, moveType);
            setVisibleChallengeButtons(true);
        } else if (moveTurn.equals(MoveTurn.PLAYER) && MoveType.isChallengeRelatedMove(moveType)) {
            // TODO: Remove print
            System.out.println(moveTurn + " " + moveType);
            // Player challenge is successful or not
            String challengeText = moveType.equals(MoveType.CHALLENGE_SUCCESS) ? " Challenge Success!" : " Challenge Fail!";
            bluff_challenge_text.setVisible(true);
            bluff_challenge_text.setText(mPlayerManager.getUsername() + challengeText);
            placeMiddleCardAndSetScore(moveType, moveTurn, gameEnvironment);
        } else if (moveTurn.equals(MoveTurn.PC) && MoveType.isChallengeRelatedMove(moveType)) {
            // TODO: Remove print
            System.out.println(moveTurn + " " + moveType);
            // PC challenged or didn't challenged
            String challengeText = !moveType.equals(MoveType.NOT_CHALLENGE) ? " Challenged!" : " Didn't Challenge!";
            bluff_challenge_text.setVisible(true);
            bluff_challenge_text.setText("PC" + challengeText);
            placeMiddleCardAndSetScore(moveType, moveTurn, gameEnvironment);
            // Player needs to perform a pass movement
            // TODO: Remove print
            System.out.println("PASS + LOOP");
            performInteract(MoveType.PASS, GameStatusCode.NORMAL, (short) -1);
        }
    }

    /** Helper function to set ctrl 9 event listener*/
    private void setKeyCombinationListener() {
        // TODO: Remove print
        System.out.println("set");
        mScreenManager.getCurrentScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DIGIT9, KeyCombination.CONTROL_DOWN), this::onCheatLevelUpEvent);
    }

    /** Function that turns on cheat buttons*/
    protected void turnOnKeyComb() {
        setKeyCombinationListener();
    }

    /** Function that turns off cheat buttons*/
    protected void turnOffKeyComb() {
        System.out.println("unset");
        mScreenManager.getCurrentScene().getAccelerators().clear();
    }

    /** Event which indicates the multiplayer game level reached*/
    public static class CreateMultiplayerGameEvent extends ApplicationEvent {
        public CreateMultiplayerGameEvent() {
            super(GameTableController.class);
        }
    }
    /** Event which indicates the leaderboard is started*/
    public static class CheatLevelUpEvent extends KeyCombination {
        private static List<KeyCode> sKeyCodeList = new ArrayList<>();
        private final  List<KeyCode> neededCodeList;

        public CheatLevelUpEvent() {
            neededCodeList = Arrays.asList(KeyCode.CONTROL, KeyCode.NUMPAD9);
        }

        @Override
        public boolean match(KeyEvent event) {
            return sKeyCodeList.containsAll(neededCodeList);
        }
    }
}
