package com.group7.server.service.game;

import com.group7.server.definitions.GameTable;
import com.group7.server.definitions.StatusCode;
import com.group7.server.model.ActivePlayer;
import com.group7.server.repository.ActivePlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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

    /** Checks whether given active player is already attached to a game or not.*/
    private boolean isAttachedToGame(ActivePlayer activePlayer){
        return activePlayer.getGameId() >= 0;
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
