package com.group7.server.service.authentication;

import com.group7.server.model.Player;
import com.group7.server.security.utility.JwtUtil;
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
            System.out.println("1");
            Authentication user = mAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken);
            System.out.println("2");
            String token = JwtUtil.generateToken(user, mSecretKey, 15);
            System.out.println("3");
            return token;
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
