package com.group7.client.definitions.game;

public enum GameStatusCode {
    NORMAL,     // Just regular response
    LEVEL_UP,   // Increase the level (received from client)
    CHEAT_LEVEL_UP, // Increase the level via cheat (received from client)
    WIN,        // Win the level
    LOST,        // Lose the level
    GAME_OVER_WIN; //Game over and won the game

    public static GameStatusCode convertGameStatusCode(String gameStatusCodeStr) {
        return switch (gameStatusCodeStr) {
            case "NORMAL" -> GameStatusCode.NORMAL;
            case "LEVEL_UP" -> GameStatusCode.LEVEL_UP;
            case "CHEAT_LEVEL_UP" -> GameStatusCode.CHEAT_LEVEL_UP;
            case "GAME_OVER_WIN" -> GameStatusCode.GAME_OVER_WIN;
            default -> GameStatusCode.NORMAL;
        };
    }
}
