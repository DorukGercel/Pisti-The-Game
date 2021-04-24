package com.group7.server.service;

import com.group7.server.definitions.StatusCode;
import com.group7.server.model.Player;

/**
 * Responsible for providing utilities to the PlayerController.
 *
 */
public interface PlayerService {
    StatusCode register(Player player);
    StatusCode login(Player player, Object[] credentials);
    StatusCode logout(Long sessionId);
}
