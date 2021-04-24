package com.group7.server.service;

import com.group7.server.definitions.StatusCode;
import com.group7.server.model.ActivePlayer;
import com.group7.server.model.Player;
import com.group7.server.repository.ActivePlayerRepository;
import com.group7.server.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Responsible for providing utilities to the PlayerController.
 *
 */
@RequiredArgsConstructor
@Service
public class PlayerServiceImpl implements PlayerService {

    private final AuthenticationService mAuthenticationService;
    private final PlayerRepository mPlayerRepository;
    private final ActivePlayerRepository mActivePlayerRepository;

    /**
     * Handles player's register operations. Utilizes  AuthenticationService's method.
     *
     * @param player the entity that is sent to the api as an request
     *               and must be registered to the system.
     * @return the status code according to the success of the operation.
     *              If operation is successful, returns success status code.
     *              If operation is not successful, returns fail status code;
     *                  it indicates that some runtime or SQL related exception occurred.
     */
    @Override
    public StatusCode register(Player player) {
        try {
            mAuthenticationService.register(player);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.FAIL;
        }
    }

    /**
     * Handles player's login operations. Utilizes  AuthenticationService's method
     *                                              and PlayerService's private method.
     *
     * @param player the entity that is sent to the api as an request
     *               and must be added to the active player's table.
     * @param credentials stores the token and the session id of the player who sent login request.
     *                    Initially it's values are empty and they are set in this method.
     * @return the status code according to the success of the operation.
     *              If operation is successful, returns success status code.
     *              If operation is not successful, returns fail status code;
     *                  it indicates that some runtime or SQL related exception occurred.
     */
    @Override
    public StatusCode login(Player player, Object[] credentials) {
        try {
            String token;
            Long sessionId;
            if(((token = mAuthenticationService.authenticate(player)) != null) &&
                    ((sessionId = initializeActivePlayer(player)) != null)) {
                /* Assigns credentials if the operations are successful*/
                credentials[0] = token;
                credentials[1] = sessionId;
                return StatusCode.SUCCESS;
            }
            return StatusCode.FAIL;
        } catch (Exception e) {
            return StatusCode.FAIL;
        }
    }

    /**
     * Handles player's logout operations. Utilizes active player repository methods.
     *
     * @param sessionId the id of the player in the active player's table.
     * @return the status code according to the success of the operation.
     *              If operation is successful, returns success status code.
     *              If operation is not successful, returns fail status code;
     *                  it indicates that some runtime or SQL related exception occurred.
     */
    @Override
    public StatusCode logout(Long sessionId) {
        try {
            Optional<ActivePlayer> dbActivePlayer = mActivePlayerRepository.findById(sessionId);
            System.out.println(dbActivePlayer.get());
            mActivePlayerRepository.deleteById(sessionId);
            return StatusCode.SUCCESS;
        } catch (Exception e) {
            return StatusCode.FAIL;
        }

    }

    /**
     * Creates an entry in the active player's table, for the given player.
     *
     * @param player the player who needs to be added to the active player's table.
     * @return the session id of the player in the active player's table.
     *              If given player wasn't registered; returns null.
     */
    private Long initializeActivePlayer(Player player) {
        Optional<Player> dbPlayer = mPlayerRepository.findByUsername(player.getUsername());
        if (dbPlayer.isPresent()) {
            /* Given player was registered; then add to the active player's table.*/
            System.out.println(dbPlayer.get());
            ActivePlayer activePlayer = mActivePlayerRepository.save(new ActivePlayer(dbPlayer.get()));
            return activePlayer.getId();
        }
        return null;
    }
}
