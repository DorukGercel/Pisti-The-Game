package com.group7.server.definitions.game;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Multiplayer Game instance that the player interacts with.
 * Created with a new multiplayer game request.
 * Attention: Be careful dealing with remove method of ArrayList
 * */
@Getter
@Setter
public class MultiplayerGame extends Game {
    /** Lock used for synchronization*/
    private final Lock mLock;
    /** Condition variable used for synchronization of multiplayer game start*/
    private final Condition mGameRun;
    /** Condition variable used for synchronization of multiplayer game move perform*/
    private final Condition mMovePerform;
    /** Condition variable used for synchronization of multiplayer game move read*/
    private final Condition mReadPerform;
    /** Flag which indicated the move is simulated*/
    private final AtomicBoolean mGameStateUpdated;
    /** Flag which indicated the read is simulated*/
    private final AtomicBoolean mReadUpdated;
    /** Flag which indicated the move is simulated*/
    private final AtomicBoolean   mRedealUpdated;
    /** Flag which indicated the move is simulated*/
    private final AtomicBoolean   mRestartUpdated;
    /** Flag which indicated the move is bluff*/
    private final AtomicBoolean   mBluffUpdated;
    /** Flag which indicated the move is challenge related*/
    private final AtomicBoolean   mChallengeUpdated;
    /** Current activity state of multiplayer game*/
    private       MultiplayerGameState mCurrentState;
    /** Id of the first player*/
    private       Long      mPlayerId;
    /** Id of the second player;
     *  naming is as pc to match with the single player mode and prevent typos*/
    private       Long      mPcId;

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
        this.mMovePerform = mLock.newCondition();
        this.mReadPerform = mLock.newCondition();
        this.mGameStateUpdated = new AtomicBoolean(false);
        this.mReadUpdated = new AtomicBoolean(false);
        this.mRedealUpdated = new AtomicBoolean(false);
        this.mRestartUpdated = new AtomicBoolean(false);
        this.mBluffUpdated = new AtomicBoolean(false);
        this.mChallengeUpdated = new AtomicBoolean(false);
        registerStrategy();
    }

    /** The thread that created the game waits for game to run*/
    public void waitToRun(Long activePlayerId) {
        mLock.lock();
        try {
            // Assign the player id of the game (first player)
            this.mPlayerId = activePlayerId;
            while (this.mCurrentState.equals(MultiplayerGameState.WAITING)) {
                // TODO: Remove print
                System.out.println("PLAYER WAITING");
                mGameRun.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            mLock.unlock();
        }
    }

    /** Another thread joins the game and wakes up the other*/
    public void startToRun(Long activePlayerId) {
        mLock.lock();
        try {
            // Assign the pc id of the game (second player)
            this.mPcId = activePlayerId;
            setCurrentState(MultiplayerGameState.RUNNING);
            // TODO: Remove print
            System.out.println("PLAYER RUNNING");
            mGameRun.signal();
        } finally {
            mLock.unlock();
        }
    }

    public List<Object> interactMultiplayer(Long activePlayerId, MoveType moveType, Short cardNo){
        List<Object> gameState;
        mLock.lock();
        Side currentPlayerSide = getSidePlayer(activePlayerId);
        if(currentPlayerSide.equals(getMTurn()) && !mGameStateUpdated.get()) {
            // TODO: Remove print
            System.out.println("ENTER MAKE");
            // The right turn player enters and previous read already exited
            gameState = interactStrategy(moveType, cardNo);
            MoveType gameEnvMoveType = getGameStateMoveType(gameState);
            setRedealRestartFlags(gameEnvMoveType);
            mGameStateUpdated.set(true);
            mMovePerform.signal();
            try {
                while (!mReadUpdated.get()) {
                    mReadPerform.await();
                }
                mReadUpdated.set(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // TODO: Remove print
            System.out.println("EXIT MAKE");
        } else {
            // TODO: Remove print
            System.out.println("ENTER READ");
            gameState = interactRead(currentPlayerSide);
            mGameStateUpdated.set(false);
            mReadUpdated.set(true);
            mReadPerform.signal();
            // TODO: Remove print
            System.out.println("EXIT READ");
        }
        mLock.unlock();
        return gameState;
    }

    /** Player (1st player) id getter*/
    public Long getPlayerId() {
        return this.mPlayerId;
    }

    /** Pc (2nd player) id getter*/
    public Long getPcId() {
        return this.mPcId;
    }

    /** The player interacts with the strategy and makes a move*/
    private List<Object> interactStrategy(MoveType moveType, Short cardNo){
        List<Object> gameState = new ArrayList<>();
        // Add the current game environment list
        gameState.add(getMGameStrategy().interact(moveType, cardNo));
        addGameStatusCode(gameState);
        return gameState;
    }

    /** The player just reads the state*/
    private List<Object> interactRead(Side playerSide) {
        MoveType readerMoveType;
        boolean isMoveTurn;
        List<Object> gameState = new ArrayList<>();
        List<GameEnvironment> gameEnvironmentList = new ArrayList<>();
        while (!mGameStateUpdated.get()) {
            try {
                mMovePerform.await();
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
        // Decide the move type
        readerMoveType = decideReaderMoveType();
        // Decide whether it is move turn; notice that after update value is set; redeal doesn't change turn
        isMoveTurn = playerSide.equals(getMTurn());
        // Create a game environment list
        gameEnvironmentList.add(
                createMultiplayerEnvironment(playerSide, false, isMoveTurn, readerMoveType)
        );
        // Create a game state list
        gameState.add(
                gameEnvironmentList
        );
        addGameStatusCode(gameState);
        return gameState;
    }

    /** Returns whether game is played by two players or one player waiting*/
    public boolean isGameRunning() {
        return this.mCurrentState.equals(MultiplayerGameState.RUNNING);
    }

    /** Sets the current state of the multiplayer game*/
    public void setCurrentState(MultiplayerGameState multiplayerGameState) {
        this.mCurrentState = multiplayerGameState;
    }

    /** Bind game with the appropriate level*/
    private void registerStrategy() {
        // TODO: Remove print
        System.out.println("LEVEL MULTIPLAYER");
        setMGameStrategy(new GameStrategyMultiplayer());
        getMGameStrategy().registerGame(this);
    }

    /** Helper function to obtain the side of the active player; player or pc*/
    private Side getSidePlayer(Long activePlayerId) {
        if(mPlayerId.equals(activePlayerId)) {
            return Side.PLAYER;
        }
        return Side.PC;
    }

    /** Helper function to add game status code*/
    private void addGameStatusCode(List<Object> gameState) {
        // Add the game status code and the level x score if level finished
        gameState.add(getMGameStatusCode());
        if (!getMGameStatusCode().equals(GameStatusCode.NORMAL)) {
            gameState.add(getMLevelXScore());
        }
    }

    /** Get game env list from game state*/
    private MoveType getGameStateMoveType(List<Object> gameState) {
        return MoveType.convertStrToMoveType(
                ((List<GameEnvironment>) gameState.get(0)).get(0).getMMoveType()
        );
    }

    /** Helper function to set restart and redeal flags according to move type*/
    private void setRedealRestartFlags(MoveType moveType) {
        if(moveType.equals(MoveType.REDEAL)) {
            mRedealUpdated.set(true);
        } else if (moveType.equals(MoveType.RESTART)) {
            mRestartUpdated.set(true);
        } else if (moveType.equals(MoveType.BLUFF)) {
            mBluffUpdated.set(true);
        } else if (MoveType.isChallengeRelatedMove(moveType)) {
            mChallengeUpdated.set(true);
        }
    }

    /** Helper function to obtain reader players move type; read, redeal, restart*/
    private MoveType decideReaderMoveType() {
        if (mRedealUpdated.get()) {
            mRedealUpdated.set(false);
            return MoveType.REDEAL;
        } else if (mRestartUpdated.get()) {
            mRestartUpdated.set(false);
            return MoveType.RESTART;
        } else if (mBluffUpdated.get()) {
            mBluffUpdated.set(false);
            return MoveType.BLUFF;
        } else if (mChallengeUpdated.get()) {
            mChallengeUpdated.set(false);
            return MoveType.CHALLENGE;
        }
        return MoveType.READ;
    }

    public enum MultiplayerGameState {
        WAITING,
        RUNNING
    }
}
