package com.group7.server.service;

import com.group7.server.model.Player;

/**
 * Responsible for providing authentication related utilities.
 *
 */
public interface AuthenticationService {
    void register(final Player player);
    String authenticate(final Player player);
}
