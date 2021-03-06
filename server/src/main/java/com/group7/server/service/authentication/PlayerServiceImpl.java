package com.group7.server.service.authentication;

import com.group7.server.definitions.common.StatusCode;
import com.group7.server.model.ActivePlayer;
import com.group7.server.model.Player;
import com.group7.server.repository.ActivePlayerRepository;
import com.group7.server.repository.PlayerRepository;
import com.group7.server.service.authentication.utility.EmailManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

/**
 * Responsible for providing utilities to the PlayerController.
 *
 */
@RequiredArgsConstructor
@Service
public class PlayerServiceImpl implements PlayerService {

    private final AuthenticationService  mAuthenticationService;
    private final PlayerRepository mPlayerRepository;
    private final ActivePlayerRepository mActivePlayerRepository;
    private final EmailManager mEmailManager;

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
                    ((sessionId = initializeActivePlayer(player)) != null) &&
                    (credentials != null) && (credentials.length == 2)) {
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

    @Override
    public StatusCode handleForgotPassword(Player player) {
        try {
            Optional<Player> dbPlayer = mPlayerRepository.findByEmail(player.getEmail());
            if (dbPlayer.isPresent()) {
                Player registeredPlayer = dbPlayer.get();
                mEmailManager.sendResetPasswordEmail(registeredPlayer.getId(),
                        registeredPlayer.getEmail(),
                        registeredPlayer.getUsername());
                return StatusCode.SUCCESS;
            }
            return StatusCode.FAIL;
        } catch (Exception e) {
            return StatusCode.FAIL;
        }
    }

    /**
     * Handles the password reset of the player if he forgot his/her password.
     *
     * @param player who sent forgot password request, id of the player must be available
     * @param credentials stores the new password of the player who sent forgot password request.
     *                    Initially it's values are empty and they are set in this method.

     * @return the status code according to the success of the operation.
     *              If operation is successful, returns success status code.
     *              If operation is not successful, returns fail status code;
     *                  it indicates that some runtime or SQL related exception occurred.
     */
    @Override
    public StatusCode resetPassword(Player player, Object[] credentials) {
        try {
            Optional<Player> dbPlayer = mPlayerRepository.findById(player.getId());
            if (dbPlayer.isPresent()) {
                String newPassword = mAuthenticationService.resetPassword(dbPlayer.get());
                if (newPassword != null) {
                    credentials[0] = newPassword;
                    return StatusCode.SUCCESS;
                }
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
    //TODO: Delete game with logout
    @Override
    public StatusCode logout(Long sessionId) {
        try {
            Optional<ActivePlayer> dbActivePlayer = mActivePlayerRepository.findById(sessionId);
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
            ActivePlayer activePlayer = mActivePlayerRepository.save(new ActivePlayer(dbPlayer.get()));
            return activePlayer.getId();
        }
        return null;
    }
}
