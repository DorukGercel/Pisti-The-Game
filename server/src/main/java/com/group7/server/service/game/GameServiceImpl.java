package com.group7.server.service.game;

import com.group7.server.definitions.game.Game;
import com.group7.server.definitions.game.GameEnvironment;
import com.group7.server.definitions.game.GameTable;
import com.group7.server.definitions.common.StatusCode;
import com.group7.server.model.ActivePlayer;
import com.group7.server.repository.ActivePlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Responsible for providing utilities to the GameController.
 *
 */
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService{

    private final GameTable mGameTable;
    private final ActivePlayerRepository mActivePlayerRepository;

    /** Type definition for different kinds of update operations*/
    private enum UpdateOperationCode{
        INITIALIZE,
        SCORE
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
                // TODO: Remove print
                System.out.println(mGameTable.getGame((Long) gameId[0]));
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
     * @return the status code according to the success of the operation.
     *               If operation is successful, returns success status code.
     *               If operation is not successful, returns fail status code;
     *                  it indicates that some runtime or SQL related exception occurred.
     */
    @Override
    public StatusCode interactGame(Long sessionId, Long gameId, Short cardNo, Game.MoveType moveType, List<GameEnvironment> gameEnvironments) {
        try {
            Optional<ActivePlayer> dbActivePlayer = mActivePlayerRepository.findById(sessionId);
            Game currentGame;
            if(dbActivePlayer.isPresent() &&
                    gameId.equals(dbActivePlayer.get().getGameId()) &&
                    ((currentGame = mGameTable.getGame(gameId)) != null) &&
                    isValidMoveAndCard(cardNo, moveType) && (gameEnvironments != null)){

                List<GameEnvironment> gameEnvironmentList = currentGame.interactSinglePlayer(moveType, cardNo);
                gameEnvironments.addAll(gameEnvironmentList);
                return StatusCode.SUCCESS;
            }
            return StatusCode.FAIL;
        }
        catch (Exception e){
            return StatusCode.FAIL;
        }
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
        return (isValidCardNo(cardNo) || (moveType.equals(Game.MoveType.INITIAL) || moveType.equals(Game.MoveType.REDEAL)));
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
                    activePlayer.setLevel(1);
                    activePlayer.setScore(0);
                    activePlayer.setGameId((Long) updateValue);

                    mActivePlayerRepository.save(activePlayer);

                    return StatusCode.SUCCESS;
                }
                case SCORE -> {
                    return StatusCode.SUCCESS;
                }
                default -> {
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
