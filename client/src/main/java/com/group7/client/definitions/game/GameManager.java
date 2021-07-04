package com.group7.client.definitions.game;

import com.group7.client.controller.GameTableController;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
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
            synchronized (mPlayerTurn) {
                mLock.lock();
                while (!mGameOver) {
                    mPlayerTurn.wait();
                    TimeUnit.SECONDS.sleep(mSleepTime);
                    simulateTurn();
                }
                mLock.unlock();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Helper function to return move type which is decided by player via controller*/
    private MoveType getPlayerMoveType() {
        MoveType moveType = MoveType.CARD;
        if (mBluffLevel) {
            // Decide move type
            if (mBluffEnabled) {
                moveType = MoveType.BLUFF;
                // Disable bluff mode
                mBluffEnabled = false;
            } else if (mChallengeEnabled) {
                moveType = MoveType.CHALLENGE;
                // Disable challenge mode
                mChallengeEnabled = false;
            } else if (mDontChallengeEnabled) {
                moveType = MoveType.NOT_CHALLENGE;
                // Disable don't challenge mode
                mDontChallengeEnabled = false;
            }
        }
        return moveType;
    }

    /** Helper function to remove the middle card from player deck according to move type*/
    private void removeMiddleCardFromPlayerDeck(MoveType moveType) {
        if (!(moveType.equals(MoveType.CHALLENGE) || moveType.equals(MoveType.NOT_CHALLENGE))) {
            // No card to remove in challenge and don't challenge move type
            // Remove the current allocated middle card
            mPlayerCards.remove(mMiddleCard);
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
