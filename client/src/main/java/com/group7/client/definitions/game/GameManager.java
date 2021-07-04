package com.group7.client.definitions.game;

import com.group7.client.controller.GameTableController;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Responsible for managing the current game
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Component
public class GameManager extends BaseGameManager{
    /** Required args constructor*/
    @Autowired
    public GameManager(GameService gameService, GameTableController gameTableController) {
        this.mGameService = gameService;
        this.mGameController = gameTableController;
        this.mPlayerCards = new ArrayList<>();
        this.mLock = new ReentrantLock();
        this.mPlayerTurn = mLock.newCondition();
        this.mCurrentLevel = 1;
    }

    /** Thread function where game instance runs*/
    public void run() {
        try {
            //TODO: Remove print
            System.out.println("SINGLE TIME");
            mLock.lock();
            while (!mGameOver) {
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


    /** Helper function to send the move to backend*/
    private void simulateTurn() {
        // Decide move type
        MoveType moveType = getPlayerMoveType();
        // Remove the current allocated middle card
        removeMiddleCardFromPlayerDeck(moveType);
        // Simulate move with network; send to backend
        mGameController.simulateMove(moveType, GameStatusCode.NORMAL, mMiddleCard);
        if (mPlayerCards.size() == 0) {
            mGameController.simulateMove(MoveType.REDEAL, GameStatusCode.NORMAL, mMiddleCard);
        }
    }
}
