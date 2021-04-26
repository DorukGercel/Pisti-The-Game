package com.group7.server.service.game;

import com.group7.server.definitions.StatusCode;

public interface GameService {
    StatusCode initGame(Long sessionId, Object[] gameId);
}
