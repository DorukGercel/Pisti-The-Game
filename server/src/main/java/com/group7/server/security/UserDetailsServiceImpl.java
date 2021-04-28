package com.group7.server.security;

import com.group7.server.repository.ActivePlayerRepository;
import com.group7.server.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/** Performs the repository related operations by implementing security related service methods*/
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final PlayerRepository mPlayerRepository;

    /** Returns the user from the repository with the given username*/
    @Override
    public UserDetails loadUserByUsername(final String username)
            throws UsernameNotFoundException {
        return mPlayerRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username)
        );
    }
}
