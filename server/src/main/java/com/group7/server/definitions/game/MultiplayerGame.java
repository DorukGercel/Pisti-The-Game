package com.group7.server.definitions.game;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Multiplayer Game instance that the player interacts with.
 * Created with a new multiplayer game request.
 * Attention: Be careful dealing with remove method of ArrayList
 * */
public class MultiplayerGame extends Game{
    /** Lock used for synchronization*/
    private final Lock mLock;
    /** Condition variable used for synchronization*/
    private final Condition mGameRun;
    /** Current activity state of multiplayer game*/
    private MultiplayerGameState mCurrentState;
    /**
     * Constructor; called when a new game is created
     *
     * @param cardTable
     */
    public MultiplayerGame(GameConfig.CardTable cardTable) {
        super(cardTable);
        super.setMMode(Game.Mode.MULTI);
        this.mCurrentState = MultiplayerGameState.WAITING;
        this.mLock = new ReentrantLock();
        this.mGameRun = mLock.newCondition();
    }

    /** The thread that created the game waits for game to run*/
    public void waitToRun() {
        mLock.lock();
        try {
            while (this.mCurrentState.equals(MultiplayerGameState.WAITING)) {
                mGameRun.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mLock.unlock();
        }
    }

    /** Another thread joins the game and wakes up the other*/
    public void startToRun() {
        mLock.lock();
        try {
            setCurrentState(MultiplayerGameState.RUNNING);
            mGameRun.notify();
        } finally {
            mLock.unlock();
        }
    }

    /** Returns whether game is played by two players or one player waiting*/
    public boolean isGameRunning() {
        return this.mCurrentState.equals(MultiplayerGameState.RUNNING);
    }

    /** Sets the current state of the multiplayer game*/
    public void setCurrentState(MultiplayerGameState multiplayerGameState) {
        this.mCurrentState = multiplayerGameState;
    }

    public enum MultiplayerGameState {
        WAITING,
        RUNNING
    }
}
