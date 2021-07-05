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
    /** Variable to indicate if it is time for player to pass*/
    protected       boolean               mPassEnabled;
    /** Variable to indicate if it is time for player to read*/
    protected       boolean               mReadEnabled;
    /** Sleep time before display of cards*/
    @Value("${spring.application.sleep.short}")
    protected       int                   mSleepTime;

    /** Thread function where game instance runs*/
    abstract public void run();

    /** Function which signals change of turn*/
    public void notifyPlayerTurn() {
        mLock.lock();
        mPlayerTurn.signal();
        mLock.unlock();
    }

    /** Function which enables the bluff mode when button pressed*/
    public void setBluffEnabled(boolean enabled) {
        this.mBluffEnabled = enabled;
    }

    /** Function which enables the read mode when it is not player's turn*/
    public void setReadEnabled(boolean enabled) {
        this.mReadEnabled = enabled;
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

    /** Function to determine player needs to send redeal request*/
    public boolean isRedealRequired() {
        // No cards and no need to pass
        return (mPlayerCards.size() == 0) && !isPassRequired();
    }

    /** Function to determine player needs to send pass request*/
    public boolean isPassRequired() {
        return mPassEnabled;
    }

    /** Helper function to return move type which is decided by player via controller*/
    protected MoveType getPlayerMoveType() {
        MoveType moveType = MoveType.CARD;
        if(isRedealRequired()) {
            // Check if no card available
            moveType = MoveType.REDEAL;
        }
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
            } else if (mReadEnabled) {
                moveType = MoveType.READ;
                // Disable don't challenge mode
                mReadEnabled = false;
            } else if (mPassEnabled) {
                moveType = MoveType.PASS;
                // Disable don't challenge mode
                mPassEnabled = false;
            }
        }
        return moveType;
    }

    /** Helper function to remove the middle card from player deck according to move type*/
    protected void removeMiddleCardFromPlayerDeck(MoveType moveType) {
        if (!(moveType.equals(MoveType.CHALLENGE) || moveType.equals(MoveType.NOT_CHALLENGE))) {
            // No card to remove in challenge and don't challenge move type
            // Remove the current allocated middle card
            mPlayerCards.remove(mMiddleCard);
        }
    }
}
