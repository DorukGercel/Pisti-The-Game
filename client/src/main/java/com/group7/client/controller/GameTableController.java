package com.group7.client.controller;

import com.group7.client.controller.common.BaseNetworkController;
import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.game.*;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.definitions.player.PlayerManager;
import com.group7.client.definitions.screen.ScreenManager;
import com.group7.client.dto.common.CommonResponse;
import com.group7.client.dto.game.InteractRequest;
import com.group7.client.dto.game.InteractResponse;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class GameTableController extends BaseNetworkController {
    /** Reference to common game manager*/
    private GameManager mGameManager;
    /** Common api address of the back-end for controller requests*/
    @Value("${spring.application.apiAddress.game}") private String mApiAddress;
    /** Sleep time between display of player and pc cards*/
    @Value("${spring.application.sleep.long}")
    private int        mSleepTime;
    /** Flag which indicates whether player can bluff or noy*/
    private boolean mPlayerCanBluff;
    /** Flag which indicates whether bluff mode is open, player chose to bluff*/
    private boolean mPlayerBluffed;
    /** Flag which indicates that pc bluffed and user shall choose to challenge or not*/
    private boolean mPcBluffed;
    /** Player cards*/
    private List<Card> mPlayerCards;
    /** Card in the middle*/
    private Card       mMiddleCard;

    /** FXML fields*/
    @FXML private Label     active_player_label;
    @FXML private Label     active_player_score_label;
    @FXML private Label     pc_score_label;
    @FXML private Group     player_area_container;
    @FXML private Circle    middle_area;
    @FXML private Rectangle middle_card;
    @FXML private Label     level_no_label;
    @FXML private Button    bluff_button;
    @FXML private Button    challenge_button;
    @FXML private Button    dont_challenge_button;
    @FXML private Text      bluff_challenge_text;

    /** Perform initializations*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        setMiddleAreaDragAndDropListener();
    }

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
    public void simulateMove(MoveType moveType, GameStatusCode gameStatusCode, short cardNo) {
        Platform.runLater(()-> {
            // Perform back-end interaction
            performInteract(moveType, gameStatusCode, cardNo);
            // Re-init the key combination and drag drop events
            simulatePostMove();
        });
    }

    /** Function which is invoked before simulate move started; after ui interaction*/
    private void simulatePostGuiInteract() {
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
            // Disable pc bluff mode and disable challenge buttonss
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

    /** Function which is invoked after simulate move is finished*/
    private void simulatePostMove() {
        if (!mPcBluffed){
            // Re-init drag drop events
            turnOnDrag();
        }
        // Re-init the key combination
        turnOnKeyComb();
    }

    @FXML
    private void clickBluffButton() {
        // Set player bluffed to turn over the card placement in middle
        mPlayerBluffed = true;
        // Disable the button
        disableClickBluffButton();
        // Enable bluff mode in the game manager
        mGameManager.setBluffEnabled(true);
    }

    @FXML
    private void clickChallengeButton() {
        synchronized (mGameManager.getMPlayerTurn()) {
            // Challenge made
            mGameManager.setMChallengeEnabled(true);
            simulatePostGuiInteract();
        }
    }

    @FXML
    private void clickDontChallengeButton() {
        synchronized (mGameManager.getMPlayerTurn()) {
            // Don't challenge made
            mGameManager.setMDontChallengeEnabled(true);
            simulatePostGuiInteract();
        }
    }

    /** Helper function send the initial interact request*/
    private void performInteract(MoveType moveType, GameStatusCode gameStatusCode, short cardNo) {
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
                displaySuccess("Game Won", "Congratulations :)");
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

    /** Helper function to place middle card and set score*/
    private void placeMiddleCardAndSetScore(MoveType moveType, MoveTurn moveTurn, GameEnvironment gameEnvironment) {
        // Place the card in the middle, get the card according to card no
        mMiddleCard = mGameManager.getMiddleCard(gameEnvironment);
        placeMiddleCard(moveTurn, moveType);
        // Set score
        setScore(moveTurn, gameEnvironment.getMScores().get(0));
    }

    /** Helper function to decide the move type of player and simulate player turn*/
    private void simulatePlayerTurn(MoveType moveType, MoveTurn moveTurn, GameEnvironment gameEnvironment) {
        if (MoveType.isBluffLevelMoveType(moveType)) {
            simulateBluffTurn(moveType, moveTurn, gameEnvironment);
        } else {
            placeMiddleCardAndSetScore(moveType, moveTurn, gameEnvironment);
        }
    }

    /** Helper function to place middle card*/
    private void placeMiddleCard(MoveTurn moveTurn, MoveType moveType) {
        if (mMiddleCard == null) {
            // If there is no card in the middle, set color to table color
            middle_card.setFill(Color.BURLYWOOD);
        } else if (moveType.equals(MoveType.INITIAL) || moveType.equals(MoveType.RESTART) || moveType.equals(MoveType.CHALLENGE_SUCCESS) || (moveType.equals(MoveType.CARD) && moveTurn.equals(MoveTurn.PC))) {
            // If new level was restarted, game was initialized, or pc made a card move to be simulated, place the card
            middle_card.setFill(mMiddleCard.getCardGeometry().getFill());
        } else if (moveType.equals(MoveType.BLUFF) && moveTurn.equals(MoveTurn.PC)) {
            // Pc bluff card placement; reversed placement
            middle_card.setFill(Color.SADDLEBROWN);
        }
    }

    /** Helper function to perform initial card placing*/
    private void simulateInitTurn(MoveType moveType, GameEnvironment gameEnvironment) {
        mPlayerCards = mGameManager.dealPlayerCards(gameEnvironment);
        placePlayerCards();
        mMiddleCard = mGameManager.getMiddleCard(gameEnvironment);
        placeMiddleCard(MoveTurn.PLAYER, moveType);
    }

    /** Helper function to perform restart card placing*/
    private void simulateRestartTurn(MoveType moveType, GameEnvironment playerGameEnv, GameEnvironment pcGameEnv) {
        setBothScores(playerGameEnv, pcGameEnv);
        flushContainerAreas();
        simulateInitTurn(moveType, playerGameEnv);
    }

    /** Helper function to perform level up card placing and scores*/
    private void simulateLevelUp() {
        mGameManager.handleLevelChange();
        flushContainerAreas();
        clearScores();
        setCurrentLevel();
    }

    /** Helper function to perform game over card placing and scores*/
    private void simulateGameOver() {
        mGameManager.handleGameOver();
        flushContainerAreas();
        clearScores();
        setCurrentLevel();
    }

    /** Helper function to place player cards*/
    private void placePlayerCards() {
        double angle = 150;
        double angleShift = 20;
        double hShift = 20;
        double vShift;
        int    counter = 0;
        // Place all the cards as they were hold by hand
        for(Card card : mPlayerCards) {
            vShift = (counter == 1 || counter == 2) ? 0:10;
            Rectangle cardRec = card.getCardGeometry();
            cardRec.setRotate(angle + counter * angleShift);
            cardRec.setTranslateX(counter * hShift);
            cardRec.setTranslateY(vShift);
            player_area_container.getChildren().add(cardRec);
            counter += 1;
            // Place the listeners to the card
            setCardDragAndDropListener(card);
        }
    }

    /** Helper function empty player area and middle area*/
    private void flushContainerAreas() {
        player_area_container.getChildren().clear();
    }

    /** Helper function to clear scores*/
    private void clearScores() {
        setScore(MoveTurn.PLAYER, (short) 0);
        setScore(MoveTurn.PC, (short) 0);
    }

    /** Helper function to set viewed level according to the game manager*/
    private void setCurrentLevel() {
        level_no_label.setText(mGameManager.getMCurrentLevel().toString());
    }

    /** Helper function to activate bluff level buttons*/
    private void handleBluffLevelActions(GameEnvironment currentGameEnvironment) {
        if(currentGameEnvironment.getMMiddleCards().size() == 1) {
            // Bluff options is open
            mPlayerCanBluff = true;
            setVisibleBluffButton(true);
        }
    }

    /** Helper function to set visibility of the bluff button*/
    private void setVisibleBluffButton(boolean visible) {
        bluff_button.setVisible(visible);
    }

    /** Helper function to set visibility of the challenge buttons*/
    private void setVisibleChallengeButtons(boolean visible) {
        challenge_button.setVisible(visible);
        dont_challenge_button.setVisible(visible);
    }

    /** Helper function to set scores in the boards for both sides*/
    private void setBothScores(GameEnvironment playerGameEnv, GameEnvironment pcGameEnv) {
        setScore(MoveTurn.PLAYER, playerGameEnv.getMScores().get(0));
        setScore(MoveTurn.PC, pcGameEnv.getMScores().get(0));
    }

    /** Helper function to set score in the board for a side*/
    private void setScore(MoveTurn moveTurn, Short score) {
        if(moveTurn.equals(MoveTurn.PLAYER)) {
            active_player_score_label.setText(score.toString());
            return;
        }
        pc_score_label.setText(score.toString());
    }

    /** Helper function to enable bluff*/
    private void enableClickBluffButton() {
        bluff_button.setDisable(false);
        bluff_button.setText("Bluff");
    }

    /** Helper function to disable bluff button after click*/
    private void disableClickBluffButton() {
        bluff_button.setDisable(true);
        bluff_button.setText("Bluff Mode On");
    }

    /** Helper function to attach drag and drop listener to middle area*/
    private void setMiddleAreaDragAndDropListener() {
        middle_card.setOnDragOver(event -> {
            if (event.getGestureSource() != middle_area) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        middle_card.setOnDragEntered(event -> {
            if (event.getGestureSource() != middle_area &&
                    event.getDragboard().hasImage() &&
                    event.getDragboard().hasString()) {
                middle_area.setFill(Color.CRIMSON);
            }
            event.consume();
        });

        middle_card.setOnDragExited(event -> {
            middle_area.setFill(Color.BURLYWOOD);
            event.consume();
        });

        middle_card.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasImage()) {
                if (!mPlayerBluffed) {
                    // Normal card placement to the middle
                    middle_card.setFill(new ImagePattern(dragboard.getImage()));
                } else {
                    // Bluff card placement; reversed placement
                    middle_card.setFill(Color.SADDLEBROWN);
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    /** Helper function to attach drag and drop listener to card geo*/
    private void setCardDragAndDropListener(Card card) {
        //Get card geometry
        Rectangle cardGeo = card.getCardGeometry();
        // Set on drag listener to card geometry
        cardGeo.setOnDragDetected(event -> {
            // Only allow move transfer option
            Dragboard dragboard = cardGeo.startDragAndDrop(TransferMode.MOVE);
            // Copy image to clipboard content and set dragboard
            ClipboardContent content = new ClipboardContent();
            // Copy background image
            Image cardImage = ((ImagePattern) cardGeo.getFill()).getImage();
            content.putImage(cardImage);
            // Copy card no as string
            content.putString(card.getCardNo().toString());
            dragboard.setContent(content);

            event.consume();
        });
        // Set drag done listener to card geometry
        cardGeo.setOnDragDone(event -> {
            synchronized (mGameManager.getMPlayerTurn()) {
                if (event.getTransferMode() == TransferMode.MOVE) {
                    // Turn off visibility after drag
                    player_area_container.getChildren().remove(cardGeo);
                    mGameManager.setMMiddleCard(card.getCardNo());
                    event.consume();
                    simulatePostGuiInteract();
                } else {
                    event.consume();
                }
            }
        });
    }

    /** Helper function to set ctrl 9 event listener*/
    private void setKeyCombinationListener() {
        // TODO: Remove print
        System.out.println("set");
        mScreenManager.getCurrentScene().getAccelerators().put(new KeyCodeCombination(KeyCode.DIGIT9, KeyCombination.CONTROL_DOWN), this::onCheatLevelUpEvent);
    }

    /** Function that turns on cheat buttons*/
    private void turnOnKeyComb() {
        setKeyCombinationListener();
    }

    /** Function that turns off cheat buttons*/
    private void turnOffKeyComb() {
        System.out.println("unset");
        mScreenManager.getCurrentScene().getAccelerators().clear();
    }

    /** Function which turns on drag mode*/
    private void turnOnDrag() {
        for (Card card : mPlayerCards) {
            setCardDragAndDropListener(card);
        }
    }

    /** Function which turns off drag mode*/
    private void turnOffDrag() {
        for (Card card : mPlayerCards) {
            card.getCardGeometry().setOnDragDetected(null);
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
