package com.group7.client.definitions.game;

import com.group7.client.controller.MultiplayerGameTableController;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Responsible for managing the current multiplayer game
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Component
public class MultiplayerGameManager extends BaseGameManager{
    /** Required args constructor*/
    @Autowired
    public MultiplayerGameManager(GameService gameService, MultiplayerGameTableController multiplayerGameTableController) {
        this.mGameService = gameService;
        this.mGameController = multiplayerGameTableController;
        this.mPlayerCards = new ArrayList<>();
        this.mLock = new ReentrantLock();
        this.mPlayerTurn = mLock.newCondition();
        this.mMiddleCard = -1;      // Card move is initially considered null
        this.mCurrentLevel = 4;     // Start level is 4 for multiplayer
        this.mBluffLevel = true;
    }

    /** Thread function where game instance runs*/
    public void run() {
        try {
            mLock.lock();
            // TODO: Remove print
            System.out.println("MULTI TIME");
            System.out.println("Enter await");
            mPlayerTurn.await();
            //TODO: Remove print
            System.out.println("Exit await");
            TimeUnit.SECONDS.sleep(mSleepTime);
            simulateInitial();
            while (!mGameOver) {
                // TODO: Remove print
                System.out.println("Enter await");
                mPlayerTurn.await();
                //TODO: Remove print
                System.out.println("Exit await");
                TimeUnit.SECONDS.sleep(mSleepTime);
                simulateTurn();
            }
            mLock.unlock();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void simulateInitial() {
        mGameController.simulateMove(MoveType.INITIAL, GameStatusCode.NORMAL, (short) -1);
    }

    /** Helper function to send the move to backend*/
    private void simulateTurn() {
        // Decide move type
        MoveType moveType = getPlayerMoveType();
        // Remove the current allocated middle card
        removeMiddleCardFromPlayerDeck(moveType);
        // Simulate move with network; send to backend
        mGameController.simulateMove(moveType, GameStatusCode.NORMAL, mMiddleCard);
    }
}
