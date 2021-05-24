package com.group7.client.controller;

import com.group7.client.controller.common.BaseNetworkController;
import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.game.Card;
import com.group7.client.definitions.game.GameManager;
import com.group7.client.definitions.game.MoveType;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.definitions.player.PlayerManager;
import com.group7.client.definitions.screen.ScreenManager;
import com.group7.client.dto.common.CommonResponse;
import com.group7.client.dto.game.InteractRequest;
import com.group7.client.dto.game.InteractResponse;
import javafx.event.EventHandler;
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
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class GameTableController extends BaseNetworkController
        implements ApplicationListener<UserMenuController.StartGameEvent> {

    /** Reference to common game manager*/
    private GameManager mGameManager;
    /** Common api address of the back-end for controller requests*/
    @Value("${spring.application.apiAddress.game}") private String mApiAddress;

    private List<Card> mPlayerCards;
    private Card       mMiddleCard;
    /** FXML fields*/
    @FXML private Label     active_player_label;
    @FXML private Label     active_player_score_label;
    @FXML private Label     pc_score_label;
    @FXML private Group     player_area_container;
    @FXML private Group     middle_area_container;
    @FXML private Circle    middle_area;
    @FXML private Rectangle middle_card;
    @FXML private Group     pc_area_container;

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
    @Override
    public void onApplicationEvent(UserMenuController.StartGameEvent startGameEvent) {
        active_player_label.setText(mPlayerManager.getUsername());
        performInitialInteract();
        // TODO: Delete this
        active_player_score_label.setText(mPlayerManager.getGameId().toString());
    }

    /** Helper function send the initial interact request*/
    private void performInitialInteract() {
        // Exchange request and response
        InteractRequest interactRequest = new InteractRequest(mPlayerManager.getSessionId(),
                mPlayerManager.getGameId(),
                (short)-1,  //Card no doesn't matter for initial move
                MoveType.INITIAL);
        CommonResponse[] commonResponse = new InteractResponse[1];

        StatusCode networkStatusCode = mNetworkManager.exchange(
                mApiAddress + "/interactGame",
                HttpMethod.PUT,
                interactRequest,
                commonResponse,
                InteractResponse.class);

        if (isOperationSuccess(commonResponse[0], networkStatusCode, InteractResponse.class, "Interact Game - Initial")) {
            InteractResponse interactResponse = (InteractResponse) commonResponse[0];
            // TODO: Remove print
            System.out.println(interactResponse.getPlayerEnvironment());
            System.out.println(interactResponse.getPcEnvironment());

            mPlayerCards = mGameManager.dealPlayerCards(interactResponse.getPlayerEnvironment());
            mMiddleCard = mGameManager.getMiddleCard(interactResponse.getPlayerEnvironment());
            placePlayerCards();
        }
    }

    /** Helper function to place player cards*/
    private void placePlayerCards() {
        double angle = 150;
        double angleShift = 20;
        double hShift = 20;
        double vShift;
        int    counter = 0;
        for(Card card : mPlayerCards) {
            vShift = (counter == 1 || counter == 2) ? 0:10;
            Rectangle cardRec = card.getCardGeometry();
            cardRec.setRotate(angle + counter * angleShift);
            cardRec.setTranslateX(counter * hShift);
            cardRec.setTranslateY(vShift);
            player_area_container.getChildren().add(cardRec);
            counter += 1;
            setCardDragAndDropListener(card);
        }
    }

    private void setMiddleAreaDragAndDropListener() {
        middle_card.setOnDragOver(event -> {
            //TODO: Remove print
            System.out.println("over");

            if (event.getGestureSource() != middle_area &&
                    event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        middle_card.setOnDragEntered(event -> {
            //TODO: Remove print
            System.out.println("enter");

            /* the drag-and-drop gesture entered the target */
            /* show to the user that it is an actual gesture target */
            if (event.getGestureSource() != middle_area &&
                    event.getDragboard().hasImage()) {
                middle_area.setFill(Color.GREEN);
            }
            event.consume();
        });

        middle_card.setOnDragExited(event -> {
            //TODO: Remove print
            System.out.println("exit");
            /* mouse moved away, remove the graphical cues */
            middle_area.setFill(Color.BLACK);
            event.consume();
        });

        middle_card.setOnDragDropped(event -> {
            //TODO: Remove print
            System.out.println("drop");

            Dragboard dragboard = event.getDragboard();
            boolean success = false;
            if (dragboard.hasImage()) {
                //TODO: Remove print
                System.out.println("drop success");

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
            //TODO: Remove print
            System.out.println("detect");
            // Only allow move transfer option
            Dragboard dragboard = cardGeo.startDragAndDrop(TransferMode.MOVE);
            // Copy image to clipboard and dragboard
            ClipboardContent content = new ClipboardContent();
            Image cardImage = ((ImagePattern) cardGeo.getFill()).getImage();
            content.putImage(cardImage);
            dragboard.setContent(content);

            event.consume();
        });
        // Set drag done listener to card geometry
        cardGeo.setOnDragDone(event -> {
            //TODO: Remove print
            System.out.println("done");
            if (event.getTransferMode() == TransferMode.MOVE) {
                //TODO: Remove print
                System.out.println("done success");
                // Turn off visibility after drag
                cardGeo.setVisible(false);
            }
            event.consume();
        });
    }



}
