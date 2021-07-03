package com.group7.client.definitions.game;

import com.group7.client.controller.GameTableController;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Responsible for managing the current game
 */
@Data
@Component
public class GameManager {
    /** Lock used for synchronization*/
    private final Lock                  mLock;
    /** Condition variable used for synchronization*/
    private final Condition             mPlayerTurn;
    /** Reference to common game service*/
    private final GameService           mGameService;
    /** Reference to common card table*/
    private final GameTableController   mGameTableController;
    /** Player cards*/
    private final List<Short>           mPlayerCards;
    /** Card in the middle that is updated by player (after move)*/
    private       Short                 mMiddleCard;
    /** Current level*/
    private       Short                 mCurrentLevel;
    /** Variable to indicate if game is over*/
    private       boolean               mGameOver;
    /** Variable to indicate if bluff level*/
    private       boolean               mBluffLevel;
    /** Variable to indicate if bluff is enabled*/
    private       boolean               mBluffEnabled;
    /** Variable to indicate if challenge is enabled*/
    private       boolean               mChallengeEnabled;
    /** Variable to indicate if don't challenge is enabled*/
    private       boolean               mDontChallengeEnabled;
    /** Sleep time before display of cards*/
    @Value("${spring.application.sleep.short}")
    private       int                   mSleepTime;

    /** Required args constructor*/
    @Autowired
    public GameManager(GameService gameService, GameTableController gameTableController) {
        this.mGameService = gameService;
        this.mGameTableController = gameTableController;
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

    /** Function which signals change of turn*/
    public void notifyPlayerTurn() {
        mPlayerTurn.notify();
    }

    /** Function which enables the bluff mode when button pressed*/
    public void setBluffEnabled(boolean enabled) {
        this.mBluffEnabled = enabled;
    }

    /** Function which converts the player cards no to card*/
    public List<Card> dealPlayerCards(GameEnvironment playerEnvironment) {
        return mGameService.dealPlayerCards(playerEnvironment, mPlayerCards);
    }

    /** Function which converts the middle card no to card*/
    public Card getMiddleCard(GameEnvironment playerEnvironment) {
        List<Short> middleCardsNo = playerEnvironment.getMMiddleCards();
        if (middleCardsNo.size() == 0) {
            return null;
        }
        return mGameService.getMiddleCard(middleCardsNo.get(middleCardsNo.size() - 1));
    }

    /** Function which flush player cards after level up and sets new level*/
    public void handleLevelChange() {
        mCurrentLevel = (short) (mCurrentLevel + 1);
        mPlayerCards.clear();
        if(mCurrentLevel >= 3) {
            mBluffLevel = true;
        }
    }

    /** Function which flush player cards after game over and resets level*/
    public void handleGameOver() {
        mCurrentLevel = (short) 1;
        mPlayerCards.clear();
        setMGameOver(true);
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
        mGameTableController.simulateMove(moveType, GameStatusCode.NORMAL, mMiddleCard);
        if (mPlayerCards.size() == 0) {
            mGameTableController.simulateMove(MoveType.REDEAL, GameStatusCode.NORMAL, mMiddleCard);
        }
    }
}
