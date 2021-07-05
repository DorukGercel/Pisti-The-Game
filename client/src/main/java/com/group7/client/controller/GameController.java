package com.group7.client.controller;

import com.group7.client.controller.common.BaseNetworkController;
import com.group7.client.definitions.game.*;
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
import org.springframework.beans.factory.annotation.Value;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

abstract public class GameController  extends BaseNetworkController {
    /** Reference to common game manager*/
    protected BaseGameManager mGameManager;
    /** Common api address of the back-end for controller requests*/
    @Value("${spring.application.apiAddress.game}") protected String mApiAddress;
    /** Sleep time between display of player and pc cards*/
    @Value("${spring.application.sleep.long}")
    protected int        mSleepTime;
    /** Flag which indicates whether player can bluff or noy*/
    protected boolean mPlayerCanBluff;
    /** Flag which indicates whether bluff mode is open, player chose to bluff*/
    protected boolean mPlayerBluffed;
    /** Flag which indicates that pc bluffed and user shall choose to challenge or not*/
    protected boolean mPcBluffed;
    /** Player cards*/
    protected List<Card> mPlayerCards;
    /** Card in the middle*/
    protected Card       mMiddleCard;

    /** FXML fields*/
    @FXML protected Label     active_player_label;
    @FXML protected Label     active_player_score_label;
    @FXML protected Label     pc_score_label;
    @FXML protected Group     player_area_container;
    @FXML protected Circle    middle_area;
    @FXML protected Rectangle middle_card;
    @FXML protected Label     level_no_label;
    @FXML protected Button    bluff_button;
    @FXML protected Button    challenge_button;
    @FXML protected Button    dont_challenge_button;
    @FXML protected Text      bluff_challenge_text;

    /** Perform initializations*/
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        setMiddleAreaDragAndDropListener();
    }

    /** Function which is invoked by game manager to simulate card move*/
    abstract public    void simulateMove(MoveType moveType, GameStatusCode gameStatusCode, short cardNo);

    abstract protected void turnOnKeyComb();

    abstract protected void turnOffKeyComb();

    abstract protected void performInteract(MoveType moveType, GameStatusCode gameStatusCode, short cardNo);

    /** Function which is invoked after simulate move is finished*/
    abstract protected void simulatePostMove();

    /** Function which is invoked before simulate move started; after ui interaction*/
    abstract protected void simulatePostGuiInteract();

    @FXML
    protected void clickBluffButton() {
        // Set player bluffed to turn over the card placement in middle
        mPlayerBluffed = true;
        // Disable the button
        disableClickBluffButton();
        // Enable bluff mode in the game manager
        mGameManager.setBluffEnabled(true);
    }

    @FXML
    protected void clickChallengeButton() {
        // Challenge made
        mGameManager.setMChallengeEnabled(true);
        simulatePostGuiInteract();
    }

    @FXML
    protected void clickDontChallengeButton() {
        // Don't challenge made
        mGameManager.setMDontChallengeEnabled(true);
        simulatePostGuiInteract();

    }

    /** Helper function to place middle card and set score; for multiplayer mode*/
    protected void placeMiddleCardAndSetScore(MoveType moveType, GameEnvironment gameEnvironment) {
        MoveTurn moveTurn = (moveType.equals(MoveType.READ) || moveType.equals(MoveType.PASS)) ? MoveTurn.PC : MoveTurn.PLAYER;
        // Place the card in the middle, get the card according to card no
        mMiddleCard = mGameManager.getMiddleCard(gameEnvironment);
        placeMiddleCard(moveTurn, moveType);
        // Set score
        if(moveTurn.equals(MoveTurn.PLAYER)) {
            setScore(moveTurn, gameEnvironment.getMScores().get(0));
            return;
        }
        setScore(moveTurn, gameEnvironment.getMOpponentScores().get(0));
    }

    /** Helper function to place middle card and set score*/
    protected void placeMiddleCardAndSetScore(MoveType moveType, MoveTurn moveTurn, GameEnvironment gameEnvironment) {
        // Place the card in the middle, get the card according to card no
        mMiddleCard = mGameManager.getMiddleCard(gameEnvironment);
        placeMiddleCard(moveTurn, moveType);
        // Set score
        setScore(moveTurn, gameEnvironment.getMScores().get(0));
    }

    /** Helper function to place middle card*/
    protected void placeMiddleCard(MoveTurn moveTurn, MoveType moveType) {
        if (mMiddleCard == null) {
            // If there is no card in the middle, set color to table color
            middle_card.setFill(Color.BURLYWOOD);
        } else if (moveType.equals(MoveType.INITIAL) || moveType.equals(MoveType.READ) || moveType.equals(MoveType.PASS) || moveType.equals(MoveType.RESTART) || moveType.equals(MoveType.CHALLENGE_SUCCESS) || (moveType.equals(MoveType.CARD) && moveTurn.equals(MoveTurn.PC))) {
            // If new level was restarted, game was initialized, or pc made a card move to be simulated, place the card
            middle_card.setFill(mMiddleCard.getCardGeometry().getFill());
        } else if (moveType.equals(MoveType.BLUFF) && moveTurn.equals(MoveTurn.PC)) {
            // Pc bluff card placement; reversed placement
            middle_card.setFill(Color.SADDLEBROWN);
        }
    }

    /** Helper function to perform initial card placing*/
    protected void simulateInitTurn(MoveType moveType, GameEnvironment gameEnvironment) {
        mPlayerCards = mGameManager.dealPlayerCards(gameEnvironment);
        placePlayerCards();
        mMiddleCard = mGameManager.getMiddleCard(gameEnvironment);
        placeMiddleCard(MoveTurn.PLAYER, moveType);
    }

    /** Helper function to perform restart card placing*/
    protected void simulateRestartTurn(MoveType moveType, GameEnvironment playerGameEnv, GameEnvironment pcGameEnv) {
        setBothScores(playerGameEnv, pcGameEnv);
        flushContainerAreas();
        simulateInitTurn(moveType, playerGameEnv);
    }

    /** Helper function to perform restart card placing*/
    protected void simulateRestartTurn(MoveType moveType, GameEnvironment playerGameEnv) {
        setBothScores(playerGameEnv);
        flushContainerAreas();
        simulateInitTurn(moveType, playerGameEnv);
    }

    /** Helper function to perform level up card placing and scores*/
    protected void simulateLevelUp() {
        mGameManager.handleLevelChange();
        flushContainerAreas();
        clearScores();
        setCurrentLevel();
    }

    /** Helper function to perform game over card placing and scores*/
    protected void simulateGameOver() {
        mGameManager.handleGameOver();
        flushContainerAreas();
        clearScores();
        setCurrentLevel();
    }

    /** Helper function to place player cards*/
    protected void placePlayerCards() {
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
    protected void flushContainerAreas() {
        player_area_container.getChildren().clear();
    }

    /** Helper function to clear scores*/
    protected void clearScores() {
        setScore(MoveTurn.PLAYER, (short) 0);
        setScore(MoveTurn.PC, (short) 0);
    }

    /** Helper function to set viewed level according to the game manager*/
    protected void setCurrentLevel() {
        level_no_label.setText(mGameManager.getMCurrentLevel().toString());
    }

    /** Helper function to activate bluff level buttons*/
    protected void handleBluffLevelActions(GameEnvironment currentGameEnvironment) {
        if(currentGameEnvironment.getMMiddleCards().size() == 1) {
            // Bluff options is open
            mPlayerCanBluff = true;
            setVisibleBluffButton(true);
        }
    }

    /** Helper function to set visibility of the bluff button*/
    protected void setVisibleBluffButton(boolean visible) {
        bluff_button.setVisible(visible);
    }

    /** Helper function to set visibility of the challenge buttons*/
    protected void setVisibleChallengeButtons(boolean visible) {
        challenge_button.setVisible(visible);
        dont_challenge_button.setVisible(visible);
    }

    /** Helper function to set scores in the boards for both sides*/
    protected void setBothScores(GameEnvironment playerGameEnv, GameEnvironment pcGameEnv) {
        setScore(MoveTurn.PLAYER, playerGameEnv.getMScores().get(0));
        setScore(MoveTurn.PC, pcGameEnv.getMScores().get(0));
    }

    /** Helper function to set scores in the boards for both sides*/
    protected void setBothScores(GameEnvironment playerGameEnv) {
        setScore(MoveTurn.PLAYER, playerGameEnv.getMScores().get(0));
        setScore(MoveTurn.PC, playerGameEnv.getMOpponentScores().get(0));
    }

    /** Helper function to set score in the board for a side*/
    protected void setScore(MoveTurn moveTurn, Short score) {
        if(moveTurn.equals(MoveTurn.PLAYER)) {
            active_player_score_label.setText(score.toString());
            return;
        }
        pc_score_label.setText(score.toString());
    }

    /** Helper function to enable bluff*/
    protected void enableClickBluffButton() {
        bluff_button.setDisable(false);
        bluff_button.setText("Bluff");
    }

    /** Helper function to disable bluff button after click*/
    protected void disableClickBluffButton() {
        bluff_button.setDisable(true);
        bluff_button.setText("Bluff Mode On");
    }

    /** Helper function to attach drag and drop listener to middle area*/
    protected void setMiddleAreaDragAndDropListener() {
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
    protected void setCardDragAndDropListener(Card card) {
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
            if (event.getTransferMode() == TransferMode.MOVE) {
                // Turn off visibility after drag
                player_area_container.getChildren().remove(cardGeo);
                mGameManager.setMMiddleCard(card.getCardNo());
                event.consume();
                simulatePostGuiInteract();
            } else {
                event.consume();
            }

        });
    }

    /** Function which turns on drag mode*/
    protected void turnOnDrag() {
        for (Card card : mPlayerCards) {
            setCardDragAndDropListener(card);
        }
    }

    /** Function which turns off drag mode*/
    protected void turnOffDrag() {
        for (Card card : mPlayerCards) {
            card.getCardGeometry().setOnDragDetected(null);
        }
    }
}

