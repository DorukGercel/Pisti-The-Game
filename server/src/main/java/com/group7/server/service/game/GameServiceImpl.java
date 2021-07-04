package com.group7.server.service.game;

import com.group7.server.definitions.game.Game;
import com.group7.server.definitions.game.GameEnvironment;
import com.group7.server.definitions.game.GameTable;
import com.group7.server.definitions.common.StatusCode;
import com.group7.server.model.ActivePlayer;
import com.group7.server.repository.ActivePlayerRepository;
import com.group7.server.service.leaderboard.LeaderboardRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Responsible for providing utilities to the GameController.
 *
 */
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final GameTable                 mGameTable;
    private final ActivePlayerRepository    mActivePlayerRepository;
    private final LeaderboardRecordService  mLeaderboardRecordService;

    /** Type definition for different kinds of update operations*/
    private enum UpdateOperationCode{
        INITIALIZE,
        INITIALIZE_MULTI,
        SCORE,
        LEVEL,
        GAME_OVER,
        GAME_OVER_MULTI
    }

    /**
     * Responsible for initializing and starting a new game.
     *
     * @param sessionId the id of the active player who wants to start a new game.
     * @param gameId the id of the game created; initially it's values are empty and they are set in this method.
     * @return the status code according to the success of the operation.
     *               If operation is successful, returns success status code.
     *               If operation is not successful, returns fail status code;
     *                  it indicates that some runtime or SQL related exception occurred.
     */
    @Override
    public StatusCode initGame(Long sessionId, Object[] gameId){
        try {
            Optional<ActivePlayer> dbActivePlayer = mActivePlayerRepository.findById(sessionId);
            // Check if the player logged in and is not attached to another game
            if(dbActivePlayer.isPresent() && !isAttachedToGame(dbActivePlayer.get())){
                gameId[0] = mGameTable.addNewGame();
                // Set the active player's level and attach to the newly created game
                return updateActivePlayer(UpdateOperationCode.INITIALIZE, dbActivePlayer.get(), gameId[0]);
            }
            return StatusCode.FAIL;
        } catch (Exception e){
            e.printStackTrace();
            return StatusCode.FAIL;
        }
    }

    /**
     * Responsible for initializing and starting a new multiplayer game.
     *
     * @param sessionId the id of the active player who wants to start a new game.
     * @param gameId the id of the game created; initially it's values are empty and they are set in this method.
     * @return the status code according to the success of the operation.
     *               If operation is successful, returns success status code.
     *               If operation is not successful, returns fail status code;
     *                  it indicates that some runtime or SQL related exception occurred.
     */
    @Override
    public StatusCode initMultiplayerGame(Long sessionId, Object[] gameId){
        try {
            Optional<ActivePlayer> dbActivePlayer = mActivePlayerRepository.findById(sessionId);
            // Check if the player logged in and is not attached to another game
            if(dbActivePlayer.isPresent() && !isAttachedToGame(dbActivePlayer.get())){
                gameId[0] = mGameTable.addNewMultiplayerGame();
                // Set the active player's level and attach to the newly created game
                return updateActivePlayer(UpdateOperationCode.INITIALIZE_MULTI, dbActivePlayer.get(), gameId[0]);
            }
            return StatusCode.FAIL;
        } catch (Exception e){
            e.printStackTrace();
            return StatusCode.FAIL;
        }
    }



    /**
     * Responsible for removing a game entry from the game table.
     *
     * @param sessionId the id of the active player who wants to exit the game.
     * @param gameId gameId the id of the game to be exited.
     * @return the status code according to the success of the operation.
     *               If operation is successful, returns success status code.
     *               If operation is not successful, returns fail status code;
     *                  it indicates that some runtime or SQL related exception occurred.
     */
    public StatusCode removeGame(Long sessionId, Long gameId) {
        try {
            Optional<ActivePlayer> dbActivePlayer = mActivePlayerRepository.findById(sessionId);
            if (dbActivePlayer.isPresent() &&
                    gameId.equals(dbActivePlayer.get().getGameId()) &&
                    (mGameTable.getGame(gameId) != null) && mGameTable.deleteGame(gameId)) {
                return StatusCode.SUCCESS;
            }
            return StatusCode.FAIL;
        }
        catch (Exception e) {
            return StatusCode.FAIL;
        }
    }

    /**
     * Responsible for interacting the game and providing communication between game and player.
     *
     * @param sessionId the id of the active player who wants to start a new game.
     * @param gameId the id of the game to be interacted with.
     * @param cardNo the no of the card that is played by the player.
     * @param moveType the type of the move that user made; can be initial, card or re-deal move.
     * @param gameEnvironments the current state of the game; initially it's values are empty and they are set in this method.
     * @param gameStatus the status of the game; the status code and level x score in the end; initially it's values are empty and they are set in this method.
     * @return the status code according to the success of the operation.
     *               If operation is successful, returns success status code.
     *               If operation is not successful, returns fail status code;
     *                  it indicates that some runtime or SQL related exception occurred.
     */
    @Override
    public StatusCode interactGame(Long sessionId, Long gameId, Short cardNo, Game.MoveType moveType, Game.GameStatusCode receivedGameStatusCode, List<GameEnvironment> gameEnvironments, List<Object> gameStatus) {
        try {
            Optional<ActivePlayer> dbActivePlayer = mActivePlayerRepository.findById(sessionId);
            Game currentGame;
            if(dbActivePlayer.isPresent() &&
                    gameId.equals(dbActivePlayer.get().getGameId()) &&
                    ((currentGame = mGameTable.getGame(gameId)) != null) &&
                    isValidMoveAndCard(cardNo, moveType) && (gameEnvironments != null) && (gameStatus != null)) {

                // Eligible to perform operations
                if(receivedGameStatusCode.equals(Game.GameStatusCode.NORMAL)) {
                    // Game status is only a regular move
                    return handleNormalGameOperations(currentGame, dbActivePlayer.get(), cardNo, moveType, gameEnvironments, gameStatus);
                } else {
                    // Start new level or game over
                    return handleSwitchLevelGameOperations(currentGame, dbActivePlayer.get(), receivedGameStatusCode, gameStatus);
                }
            }
            return StatusCode.FAIL;
        }
        catch (Exception e){
            return StatusCode.FAIL;
        }
    }

    /** Helper function to deal with normal game operations; NORMAL, WIN, LOST*/
    private StatusCode handleNormalGameOperations(Game currentGame,
                                                  ActivePlayer activePlayer,
                                                  Short cardNo,
                                                  Game.MoveType moveType,
                                                  List<GameEnvironment> gameEnvironments,
                                                  List<Object> gameStatus) {
        List<Object> gameState = currentGame.interact(moveType, cardNo);
        // Add game environments
        gameEnvironments.addAll(getGameEnvList(gameState));
        // Add game status code from the game
        gameStatus.add(getGameStatusCode(gameState));
        if (!getGameStatusCode(gameState).equals(Game.GameStatusCode.NORMAL)) {
            if(getGameStatusCode(gameState).equals(Game.GameStatusCode.LOST)) {
                // Remove the game from the games table if lost
                addLeaderboardRecord(activePlayer);
                removeGame(activePlayer.getId(), activePlayer.getGameId());
            }
            // Add level x score as level is finished (Add to the old level values)
            gameStatus.add(getLevelXScore(gameState));
            // Set the active player's level x score
            return updateActivePlayer(UpdateOperationCode.SCORE, activePlayer, gameStatus.get(1));
        }
        return StatusCode.SUCCESS;
    }

    /** Helper function to deal with level switching game operations; LEVEL_UP, CHEAT_LEVEL_UP, GAME_OVER*/
    private StatusCode handleSwitchLevelGameOperations(Game currentGame,
                                                       ActivePlayer activePlayer,
                                                       Game.GameStatusCode receivedGameStatusCode,
                                                       List<Object> gameStatus) {
        if (activePlayer.getLevel() < Game.SINGLE_MAX_LEVEL) {
            // Max level not reached, level up
            currentGame.initLevelUp();
            if (receivedGameStatusCode.equals(Game.GameStatusCode.CHEAT_LEVEL_UP)) {
                // Add the cheat score
                updateActivePlayer(UpdateOperationCode.SCORE, activePlayer, Game.WIN_SCORE);
            }
            // Set the new level of the player
            updateActivePlayer(UpdateOperationCode.LEVEL, activePlayer, (short) (activePlayer.getLevel() + (short) 1));
            gameStatus.add(receivedGameStatusCode);
        } else if (activePlayer.getLevel() < Game.MAX_LEVEL) {
            if (receivedGameStatusCode.equals(Game.GameStatusCode.CHEAT_LEVEL_UP)) {
                // Add the cheat score
                updateActivePlayer(UpdateOperationCode.SCORE, activePlayer, Game.WIN_SCORE);
            }
            // Set the new level of the player
            updateActivePlayer(UpdateOperationCode.LEVEL, activePlayer, (short) (activePlayer.getLevel() + (short) 1));
            // Remove single player game id
            updateActivePlayer(UpdateOperationCode.GAME_OVER, activePlayer, null);
            // Remove the game from the games table
            removeGame(activePlayer.getId(), activePlayer.getGameId());
            gameStatus.add(Game.GameStatusCode.GAME_OVER_WIN);
        } else {
            // TODO: Adapt to multiplayer
            // Max level reached, game over
            // Add to leaderboard
            addLeaderboardRecord(activePlayer);
            // Re-init active player
            updateActivePlayer(UpdateOperationCode.GAME_OVER_MULTI, activePlayer, null);
            // Remove the game from the games table
            removeGame(activePlayer.getId(), activePlayer.getGameId());
            gameStatus.add(Game.GameStatusCode.GAME_OVER_MULTI_WIN);
        }
        return StatusCode.SUCCESS;
    }

    /** Checks whether given active player is already attached to a game or not.*/
    private boolean isAttachedToGame(ActivePlayer activePlayer){
        return activePlayer.getGameId() >= 0;
    }

    /** Checks if the given card no is valid.*/
    private boolean isValidCardNo(Short cardNo){
        return cardNo >= 0 && cardNo <= 51;
    }

    /** Checks if the given card no and move is valid.*/
    private boolean isValidMoveAndCard(Short cardNo, Game.MoveType moveType){
        return isValidCardNo(cardNo) ||
                isMoveWithoutCard(moveType);
    }

    private boolean isMoveWithoutCard(Game.MoveType moveType) {
        return moveType.equals(Game.MoveType.INITIAL) ||
                moveType.equals(Game.MoveType.REDEAL) ||
                moveType.equals(Game.MoveType.CHALLENGE) ||
                moveType.equals(Game.MoveType.NOT_CHALLENGE) ||
                moveType.equals(Game.MoveType.PASS);
    }

    /** Get game env list from game state*/
    private List<GameEnvironment> getGameEnvList(List<Object> gameState) {
        return (List<GameEnvironment>) gameState.get(0);
    }

    /** Get game status code from game state*/
    private Game.GameStatusCode getGameStatusCode(List<Object> gameState) {
        return (Game.GameStatusCode) gameState.get(1);
    }

    /** Get level x score from game state*/
    private Short getLevelXScore(List<Object> gameState) {
        return (Short) gameState.get(2);
    }

    /** Helper function to add leaderboard record in the end*/
    private void addLeaderboardRecord(ActivePlayer activePlayer) {
        List<Long> recordId = new ArrayList<>();
        if(mLeaderboardRecordService.recordExists(recordId, activePlayer.getPlayer()).equals(StatusCode.FAIL)) {
            //TODO: Remove print
            System.out.println("CREATE");
            mLeaderboardRecordService.createRecord(activePlayer.getPlayer().getId(), new Date(), activePlayer.getScore());
        } else{
            if(mLeaderboardRecordService.updateRecordRequired(recordId.get(0), activePlayer.getScore()).equals(StatusCode.SUCCESS)) {
                //TODO: Remove print
                System.out.println("UPDATE");
                mLeaderboardRecordService.updateRecord(recordId.get(0), activePlayer.getPlayer().getId(), new Date(), activePlayer.getScore());
            }
        }
    }

    /**
     * Responsible for updating any given active player in the db table.
     *
     * @param updateOperationCode the code of the required update operation.
     * @param activePlayer the active player that needs to be updated in the table.
     * @param updateValue the values of the fields to be updated.
     * @return the status code according to the success of the operation.
     *              If operation is successful, returns success status code.
     *              If operation is not successful, returns fail status code;
     *                  it indicates that some runtime or SQL related exception occurred.
     */
    private StatusCode updateActivePlayer(UpdateOperationCode updateOperationCode, ActivePlayer activePlayer, Object updateValue){
        try {
            switch (updateOperationCode) {
                case INITIALIZE -> {
                    activePlayer.setId(activePlayer.getId());
                    activePlayer.setPlayer(activePlayer.getPlayer());
                    activePlayer.setLevel((short) 1);
                    activePlayer.setScore((short) 0);
                    activePlayer.setGameId((Long) updateValue);

                    mActivePlayerRepository.save(activePlayer);

                    return StatusCode.SUCCESS;
                } case INITIALIZE_MULTI -> {
                    activePlayer.setId(activePlayer.getId());
                    activePlayer.setPlayer(activePlayer.getPlayer());
                    activePlayer.setLevel(activePlayer.getLevel());
                    activePlayer.setScore(activePlayer.getScore());
                    activePlayer.setGameId((Long) updateValue);

                    mActivePlayerRepository.save(activePlayer);

                    return StatusCode.SUCCESS;

                } case SCORE -> {
                    // TODO: Do proper calculation according to levels
                    // Set Level x score and update level
                    activePlayer.setId(activePlayer.getId());
                    activePlayer.setPlayer(activePlayer.getPlayer());
                    activePlayer.setLevel(activePlayer.getLevel());
                    activePlayer.setScore((short) ((short) updateValue + activePlayer.getLevel() * activePlayer.getScore()));
                    activePlayer.setGameId(activePlayer.getGameId());

                    mActivePlayerRepository.save(activePlayer);

                    return StatusCode.SUCCESS;

                } case LEVEL -> {
                    // Set Level x score and update level
                    activePlayer.setId(activePlayer.getId());
                    activePlayer.setPlayer(activePlayer.getPlayer());
                    activePlayer.setLevel((short) updateValue);
                    activePlayer.setScore(activePlayer.getScore());
                    activePlayer.setGameId(activePlayer.getGameId());

                    mActivePlayerRepository.save(activePlayer);

                    return StatusCode.SUCCESS;

                } case GAME_OVER -> {
                    // Clear active player's only game id field
                    activePlayer.setId(activePlayer.getId());
                    activePlayer.setPlayer(activePlayer.getPlayer());
                    activePlayer.setLevel(activePlayer.getLevel());
                    activePlayer.setScore(activePlayer.getScore());
                    activePlayer.setGameId(-1);

                    mActivePlayerRepository.save(activePlayer);

                    return StatusCode.SUCCESS;

                } case GAME_OVER_MULTI -> {
                    // Clear active player's game fields
                    activePlayer.setId(activePlayer.getId());
                    activePlayer.setPlayer(activePlayer.getPlayer());
                    activePlayer.setLevel((short) 0);
                    activePlayer.setScore((short) 0);
                    activePlayer.setGameId(-1);

                    mActivePlayerRepository.save(activePlayer);

                    return StatusCode.SUCCESS;

                } default -> {
                    return StatusCode.FAIL;
                }

            }
        }
        catch (Exception e){
            e.printStackTrace();
            return StatusCode.FAIL;
        }
    }
}
