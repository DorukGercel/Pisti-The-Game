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
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
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
    private int mSleepTime;
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
            performInteract(moveType, gameStatusCode, cardNo);
            // Re-init the key combination and drag drop events
            turnOnDrag();
            turnOnKeyComb();
        });
    }

    public void turnOnKeyComb() {
        setKeyCombinationListener();
    }

    public void turnOffKeyComb() {
        System.out.println("unset");
        mScreenManager.getCurrentScene().getAccelerators().clear();
    }

    /** Function which turns on drag mode*/
    public void turnOnDrag() {
        for (Card card : mPlayerCards) {
            setCardDragAndDropListener(card);
        }
    }

    /** Function which turns off drag mode*/
    public void turnOffDrag() {
        for (Card card : mPlayerCards) {
            card.getCardGeometry().setOnDragDetected(null);
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

            GameStatusCode receivedGameStatusCode = GameStatusCode.convertGameStatusCode(interactResponse.getGameStatusCode());
            if(receivedGameStatusCode.equals(GameStatusCode.NORMAL)) {
                // Normal game operation
                simulateTurn(MoveType.convertMoveType(interactResponse.getPlayerEnvironment().getMMoveType()),
                        interactResponse.getPlayerEnvironment(),
                        interactResponse.getPcEnvironment());
            } else if (receivedGameStatusCode.equals(GameStatusCode.WIN)) {
                // Won the level so level up
                performInteract(MoveType.INITIAL, GameStatusCode.LEVEL_UP, (short) -1);
            } else if (receivedGameStatusCode.equals(GameStatusCode.LOST)) {
                // Lost the level so close the game
                displaySuccess("Game Lost", "Sorry for your lost :)");
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
    private void simulateTurn(MoveType moveType, GameEnvironment playerGameEnv, GameEnvironment pcGameEnv) {
        try {
            if (moveType.equals(MoveType.INITIAL) || moveType.equals(MoveType.REDEAL)) {
                simulateInitTurn(moveType, playerGameEnv);
            } else if (moveType.equals(MoveType.BLUFF)) {
                if((playerGameEnv.getMMiddleCards().size() == 1) && (pcGameEnv.getMMiddleCards().size() == 1)) {
                    // if there is just one face-up card on the table, the player or pc can bluff
                    simulateBluffTurn(moveType);
                } else {
                    displayError("Bluffing Move", "Bluffing cannot be made! Bluffing is possible only when there is just one card on the table");
                }
            } else if (moveType.equals(MoveType.RESTART)) {
                simulateRestartTurn(moveType, playerGameEnv, pcGameEnv);
            } else {
                simulatePlayerTurn(moveType, MoveTurn.PLAYER, playerGameEnv);
                // TODO: Remove print
                System.out.println("a");

                TimeUnit.SECONDS.sleep(mSleepTime);
                // TODO: Remove print
                System.out.println("b");
                simulatePlayerTurn(moveType, MoveTurn.PC, pcGameEnv);
                // TODO: Remove print
                System.out.println("c");
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    /** Helper function to perform bluffing*/
    private void simulateBluffTurn(MoveType moveType) {
        //TODO: bluff edilen kart ters cevrilip ortaya konacak
    }

    /** Helper function to place middle card and set score*/
    private void simulatePlayerTurn(MoveType moveType, MoveTurn moveTurn,GameEnvironment gameEnvironment) {
        // Place the card in the middle
        mMiddleCard = mGameManager.getMiddleCard(gameEnvironment);
        placeMiddleCard(moveTurn, moveType);
        // Set score
        setScore(moveTurn, gameEnvironment.getMScores().get(0));
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

    /** Helper function to place middle card*/
    private void placeMiddleCard(MoveTurn moveTurn, MoveType moveType) {
        if (mMiddleCard == null) {
            middle_card.setFill(Color.BURLYWOOD);
        } else if (moveType.equals(MoveType.INITIAL) || moveType.equals(MoveType.RESTART) || moveTurn.equals(MoveTurn.PC)) {
            middle_card.setFill(mMiddleCard.getCardGeometry().getFill());
        }
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
                middle_card.setFill(new ImagePattern(dragboard.getImage()));
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
                    turnOffKeyComb();
                    turnOffDrag();
                    mGameManager.notifyPlayerTurn();

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
