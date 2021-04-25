package com.group7.server.security;

import com.group7.server.model.Player;
import com.group7.server.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

/** Performs the repository related operations by using security services*/
@Service
@RequiredArgsConstructor
public class UserDetailsManagerImpl implements UserDetailsManager {

    private final PlayerRepository mPlayerRepository;
    private final PasswordEncoder mPasswordEncoder;

    /** Saves the given user to the repository with password encryption*/
    @Override
    public void createUser(final UserDetails user) {
        Player player = (Player) user;
        player.setUsername(user.getUsername());
        player.setPassword(mPasswordEncoder.encode(player.getPassword()));
        mPlayerRepository.save(player);
    }

    /** Updates the given user in the repository*/
    @Override
    public void updateUser(final UserDetails user) {
        Player oldUser = (Player) loadUserByUsername(user.getUsername());
        Player newUser = (Player) user;
        newUser.setId(oldUser.getId());
        mPlayerRepository.save(newUser);
    }

    /** Deletes the given user from the repository*/
    @Override
    public void deleteUser(final String username) {
        mPlayerRepository.deleteByUsername(username);
    }

    /** Performs the password reset operation*/
    @Override
    public void changePassword(final String oldPassword, final String newPassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) authentication.getPrincipal();
        Player user = (Player) loadUserByUsername(username);
        if(mPasswordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(mPasswordEncoder.encode(newPassword));
            mPlayerRepository.save(user);
        } else {
            throw new BadCredentialsException("Wrong old password is given!");
        }
    }

    /** Checks whether user exists with the given username*/
    @Override
    public boolean userExists(final String username) {
        return mPlayerRepository.existsByUsername(username);
    }

    /** Returns the user from the repository with the given username*/
    @Override
    public UserDetails loadUserByUsername(final String username) {
        return mPlayerRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
