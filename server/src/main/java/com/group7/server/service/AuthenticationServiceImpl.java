package com.group7.server.service;

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

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value(value = "${security.secretKey}")
    private String mSecretKey;

    private final DaoAuthenticationProvider mAuthenticationProvider;
    private final UserDetailsManager mUserDetailsManager;

    @Override
    public String register(Player player) {
        mUserDetailsManager.createUser(player);
        return null;
    }

    @Override
    public String authenticate(final Player player) {

        Authentication usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(player.getUsername(), player.getPassword());
        try {
            System.out.println("1");
            Authentication user = mAuthenticationProvider.authenticate(usernamePasswordAuthenticationToken);
            System.out.println("2");
            String token = JwtUtil.generateToken(user, mSecretKey, 15);
            System.out.println("3");
            return token;
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }

        return null;
    }
}
