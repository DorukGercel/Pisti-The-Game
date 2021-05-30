package com.group7.server.definitions.game;

import java.util.List;

/**
 * Current strategy of the game according to level
 * Responsible for controlling states of the game
 */
public interface GameStrategy {
    void registerGame(Game game);
    List<GameEnvironment> interact(Game.MoveType moveType, Short cardNo);
}
