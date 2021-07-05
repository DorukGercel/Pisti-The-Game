package com.group7.client.controller;

import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.game.*;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.definitions.player.PlayerManager;
import com.group7.client.definitions.screen.ScreenManager;
import com.group7.client.dto.common.CommonResponse;
import com.group7.client.dto.game.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class MultiplayerGameTableController extends GameController{
    @FXML private Text match_text;

    /** Setter injection method*/
    @Autowired
    public void setManagers(@Lazy ScreenManager screenManager, NetworkManager networkManager, PlayerManager playerManager, MultiplayerGameManager multiplayerGameManager) {
        this.mScreenManager = screenManager;
        this.mNetworkManager = networkManager;
        this.mPlayerManager = playerManager;
        this.mGameManager = multiplayerGameManager;
    }

    /** Start Game Event listener, sets username*/
    @EventListener({GameTableController.CreateMultiplayerGameEvent.class})
    public void onCreateMultiplayerGameEvent() {
        active_player_label.setText(mPlayerManager.getUsername());
        setCurrentLevel();
        // Start the multiplayer game manager
        Executors.newSingleThreadExecutor().execute(() -> mGameManager.run());
        // TODO: Change text
        match_text.setText("WAITING TO START");
        // Send request to backend to create a game
        Executors.newSingleThreadExecutor().execute(this::performStartMultiplayerGame);
    }

    @Override
    public void simulateMove(MoveType moveType, GameStatusCode gameStatusCode, short cardNo) {
        Platform.runLater(()-> {
            // Perform back-end interaction
            performInteract(moveType, gameStatusCode, cardNo);
            // Re-init the key combination and drag drop events
            simulatePostMove();
        });
    }

    @Override
    protected void simulatePostMove() {
        if (!mPcBluffed){
            // Re-init drag drop events
            turnOnDrag();
        }
        // Re-init the key combination
        turnOnKeyComb();
    }

    @Override
    protected void simulatePostGuiInteract() {
        if (mPlayerCanBluff) {
            // Disable player can bluff
            mPlayerCanBluff = false;
            setVisibleBluffButton(false);
        }
        if(mPlayerBluffed) {
            // Disable player bluff mode and rearrange bluff button
            mPlayerBluffed = false;
            enableClickBluffButton();
            setVisibleBluffButton(false);
        }
        if(mPcBluffed) {
            // Disable pc bluff mode and disable challenge buttons
            mPcBluffed = false;
            setVisibleChallengeButtons(false);
        }
        if(bluff_challenge_text.isVisible()) {
            bluff_challenge_text.setVisible(false);
        }
        turnOffKeyComb();
        turnOffDrag();
        // Notify the game manager to indicate gui interaction is over
        mGameManager.notifyPlayerTurn();

    }

    /** Creates a new multiplayer game and waits response*/
    private void performStartMultiplayerGame() {
        // Exchange request and response
        InitGameRequest initGameRequest = new InitGameRequest(mPlayerManager.getSessionId());
        CommonResponse[] commonResponse = new InitGameResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
                mApiAddress + "/startMultiplayerGame",
                HttpMethod.PUT,
                initGameRequest,
                commonResponse,
                InitGameResponse.class);

        // Check if operation is successful
        if (isOperationSuccess(commonResponse[0], networkStatusCode, InitGameResponse.class, "Start Multiplayer Game")) {
            InitGameResponse initGameResponse = (InitGameResponse) commonResponse[0];
            // Set the new game id
            match_text.setText("TIME TO START");
            mPlayerManager.setGameId(initGameResponse.getGameId());
            mGameManager.notifyPlayerTurn();
        }
    }

    @Override
    protected void performInteract(MoveType moveType, GameStatusCode gameStatusCode, short cardNo) {
        // Exchange request and response
        InteractRequest interactRequest = new InteractRequest(mPlayerManager.getSessionId(),
                mPlayerManager.getGameId(),
                cardNo,  //Card no doesn't matter for initial move
                moveType,
                gameStatusCode);
        CommonResponse[] commonResponse = new MultiplayerInteractResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
                mApiAddress + "/interactMultiplayerGame",
                HttpMethod.PUT,
                interactRequest,
                commonResponse,
                MultiplayerInteractResponse.class);

        if (isOperationSuccess(commonResponse[0], networkStatusCode, MultiplayerInteractResponse.class, "Multiplayer Interact Game")) {
            // Check if operation was successful
            MultiplayerInteractResponse interactResponse = (MultiplayerInteractResponse) commonResponse[0];
            // TODO: Remove print
            System.out.println(interactResponse.getPlayerEnvironment());
            GameStatusCode receivedGameStatusCode = GameStatusCode.convertStrToGameStatusCode(interactResponse.getGameStatusCode());
            if(receivedGameStatusCode.equals(GameStatusCode.NORMAL)) {
                // Normal game operation
                simulateTurn(moveType, interactResponse.getPlayerEnvironment());
            } else if (receivedGameStatusCode.equals(GameStatusCode.LOST)) {
                // Lost the level so close the game
                displaySuccess("Game Lost", "Sorry for your lost :(");
                simulateGameOver();
                mScreenManager.activatePane("user_menu", null);
            } else if (receivedGameStatusCode.equals(GameStatusCode.GAME_OVER_MULTI_WIN)) {
                displaySuccess("Game Won", "Congratulations, it is over :)");
                simulateGameOver();
                mScreenManager.activatePane("user_menu", null);
            } else {
                // Re-initialize the screen after level up
                simulateLevelUp();
                performInteract(MoveType.INITIAL, GameStatusCode.NORMAL, (short) -1);
            }
        }
    }

    /** Helper function to place the cards according to move type*/
    private void simulateTurn(MoveType sentMoveType, GameEnvironment playerGameEnv) {
        try {
            // Get the move types of both the player and pc
            MoveType playerMoveType = MoveType.convertStrToMoveType(playerGameEnv.getMMoveType());
            // Decide the conditions according to the move types
            if (sentMoveType.equals(MoveType.INITIAL) || playerMoveType.equals(MoveType.REDEAL)) {
                simulateInitTurn(sentMoveType, playerGameEnv);
            } else if (playerMoveType.equals(MoveType.RESTART)) {
                simulateRestartTurn(playerMoveType, playerGameEnv);
            } else if (isReadOperation(sentMoveType, playerMoveType)) {
                // Read operation; opponent performed the move
                simulatePlayerTurn(playerMoveType, MoveTurn.PC, playerGameEnv);
                // PC Game Env is actually the current state of the game
                simulatePostTurn(playerGameEnv.getMIsMoveTurn(), playerGameEnv);
            } else {
                simulatePlayerTurn(playerMoveType, MoveTurn.PLAYER, playerGameEnv);
                // PC Game Env is actually the current state of the game
                simulatePostTurn(playerGameEnv.getMIsMoveTurn(), playerGameEnv);
            }
            decideTurn(playerGameEnv);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /** Helper function to achieve level turn events, after player and pc moves*/
    private void simulatePostTurn(boolean isPlayerTurn, GameEnvironment currentGameEnv) {
        if(isPlayerTurn && mGameManager.getMCurrentLevel() >= 3) {
            // Buttons must be displayed only in players turn
            // Performs bluff level actions
            handleBluffLevelActions(currentGameEnv);
        }
    }

    /** Helper function to decide the move type of player and simulate player turn*/
    private void simulatePlayerTurn(MoveType moveType, MoveTurn moveTurn, GameEnvironment gameEnvironment) {
        if (MoveType.isBluffLevelMoveType(moveType)) {
            simulateBluffTurn(moveType, moveTurn, gameEnvironment);
        } else {
            placeMiddleCardAndSetScore(moveType, gameEnvironment);
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
            turnOffDrag();
        } else if (moveTurn.equals(MoveTurn.PLAYER) && MoveType.isChallengeRelatedMove(moveType)) {
            // TODO: Remove print
            System.out.println(moveTurn + " " + moveType);
            // Player challenge is successful or not
            String challengeText = moveType.equals(MoveType.CHALLENGE_SUCCESS) ? " Challenge Success!" : " Challenge Fail!";
            bluff_challenge_text.setVisible(true);
            bluff_challenge_text.setText(mPlayerManager.getUsername() + challengeText);
            placeMiddleCardAndSetScore(moveType, gameEnvironment);
        } else if (moveTurn.equals(MoveTurn.PC) && MoveType.isChallengeRelatedMove(moveType)) {
            // TODO: Remove print
            System.out.println(moveTurn + " " + moveType);
            // PC challenged or didn't challenged
            String challengeText = !moveType.equals(MoveType.NOT_CHALLENGE) ? " Challenged!" : " Didn't Challenge!";
            bluff_challenge_text.setVisible(true);
            bluff_challenge_text.setText("PC" + challengeText);
            placeMiddleCardAndSetScore(moveType, gameEnvironment);
            // Player needs to perform a pass movement
            // TODO: Remove print
            System.out.println("PASS + LOOP");
            //performInteract(MoveType.PASS, GameStatusCode.NORMAL, (short) -1);
            mGameManager.setMPassEnabled(true);
        }
    }

    private void decideTurn(GameEnvironment playerGameEnv) {
        if (playerGameEnv.getMIsMoveTurn().equals(true)) {
            // Player's turn
            if(mGameManager.isRedealRequired() || mGameManager.isPassRequired()) {
                // Doesn't have cards, time to redeal OR pass required after challenge
                mGameManager.notifyPlayerTurn();
                return;
            }
            // Player's turn so drag must stay open; waiting to make a move
            match_text.setText("YOUR TURN");
            return;
        }
        // Not player's turn so drag off; will send read
        turnOffDrag();
        match_text.setText("OPPONENT'S TURN");
        mGameManager.setReadEnabled(true);
        mGameManager.notifyPlayerTurn();
    }

    /** Determines whether move is read related*/
    private boolean isReadOperation(MoveType sentMoveType, MoveType receivedMoveType) {
        return sentMoveType.equals(MoveType.READ) || receivedMoveType.equals(MoveType.READ);
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
