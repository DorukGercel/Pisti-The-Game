package com.group7.client.definitions.game;

import com.group7.client.controller.GameController;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

@Data
abstract public class BaseGameManager {
    /** Lock used for synchronization*/
    protected        Lock                  mLock;
    /** Condition variable used for synchronization*/
    protected       Condition              mPlayerTurn;
    /** Reference to common game service*/
    protected       GameService           mGameService;
    /** Reference to common game gui controller*/
    protected       GameController        mGameController;
    /** Player cards*/
    protected       List<Short>           mPlayerCards;
    /** Card in the middle that is updated by player (after move)*/
    protected       Short                 mMiddleCard;
    /** Current level*/
    protected       Short                 mCurrentLevel;
    /** Variable to indicate if game is over*/
    protected       boolean               mGameOver;
    /** Variable to indicate if bluff level*/
    protected       boolean               mBluffLevel;
    /** Variable to indicate if bluff is enabled*/
    protected       boolean               mBluffEnabled;
    /** Variable to indicate if challenge is enabled*/
    protected       boolean               mChallengeEnabled;
    /** Variable to indicate if don't challenge is enabled*/
    protected       boolean               mDontChallengeEnabled;
    /** Sleep time before display of cards*/
    @Value("${spring.application.sleep.short}")
    protected       int                   mSleepTime;

    /** Thread function where game instance runs*/
    abstract public void run();

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
}
