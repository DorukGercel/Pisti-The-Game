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
    private       Boolean               mGameOver;
    /** Sleep time before display of cards*/
    @Value("${spring.application.sleep.short}")
    private       int                   mSleepTime;

    /** Required args constructor*/
    @Autowired
    public GameManager(GameService gameService, GameTableController gameTableController) {
        this.mGameService = gameService;
        this.mGameTableController = gameTableController;
        this.mPlayerCards = new ArrayList<>();
        this.mGameOver = false;
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
                    mGameTableController.turnOnDrag();
                    mGameTableController.turnOnKeyComb();
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
    }

    /** Function which flush player cards after game over and resets level*/
    public void handleGameOver() {
        mCurrentLevel = (short) 1;
        mPlayerCards.clear();
        setMGameOver(true);
    }

    /** Helper function to send the move to backend*/
    private void simulateTurn() {
        mPlayerCards.remove(mMiddleCard);
        mGameTableController.simulateMove(MoveType.CARD, GameStatusCode.NORMAL, mMiddleCard);
        if (mPlayerCards.size() == 0) {
            mGameTableController.simulateMove(MoveType.REDEAL, GameStatusCode.NORMAL, mMiddleCard);
        }
    }
}
