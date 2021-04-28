package com.group7.server.service.game;

import com.group7.server.definitions.StatusCode;

/**
 * Responsible for providing utilities to the GameController.
 *
 */
public interface GameService {
    StatusCode initGame(Long sessionId, Object[] gameId);
}
