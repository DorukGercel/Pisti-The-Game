package com.group7.server.service.authentication;

import com.group7.server.model.Player;
import com.group7.server.security.utility.JwtUtil;
import com.group7.server.service.authentication.utility.PasswordGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

/**
 * Responsible for providing authentication related utilities.
 *
 */
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value(value = "${security.secretKey}")
    private       String                    mSecretKey;
    private final DaoAuthenticationProvider mAuthenticationProvider;
    private final UserDetailsManager        mUserDetailsManager;

    /**
     * Add the player to the player table; with hashed password.
     * Utilizes UserDetailsManager's method for security measures.
     *
     * @param player the entity to be added to the player's table.
     */
    @Override
    public void register(Player player) {
        mUserDetailsManager.createUser(player);
    }

    /**
     * Checks whether player with that email is registered
     *
     * @param player to check
     * @return true if user with that email exists
     */
    @Override
    public boolean userExistsByEmail(Player player) {
        return mUserDetailsManager.userExists(player.getEmail());
    }

    /**
     * Reset the password of player if he forgot his/her password.
     *
     * @param player who sent forgot password request, must have email
     * @return newly generated random password
     */
    @Override
    public String resetPassword(Player player) {
        try {
            if(mUserDetailsManager.userExists(player.getEmail())) {
                String randomPassword = PasswordGenerator.getRandomPassword();
                Player newPlayer = player;
                player.setPassword(randomPassword);
                mUserDetailsManager.updateUser(newPlayer);

                return randomPassword;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Handles the creation of JWT Token and authenticate the player.
     * Utilizes AuthenticationProvider's method.
     *
     * @param player the entity to be authenticated
     * @return the generated JWT token after authentication if operation is successful.
     *              Else; returns null.
     */
    @Override
    public String authenticate(final Player player) {
        try {
            Authentication usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(player.getUsername(), player.getPassword());
            Authentication user = mAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken);
            String token = JwtUtil.generateToken(user, mSecretKey, 15);
            return token;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
