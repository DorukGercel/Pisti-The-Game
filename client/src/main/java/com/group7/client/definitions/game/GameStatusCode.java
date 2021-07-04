package com.group7.client.definitions.game;

public enum GameStatusCode {
    NORMAL,                     // Just regular response
    NORMAL_MULTI,               // Just regular response in multiplayer
    LEVEL_UP,                   // Increase the level (received from client)
    CHEAT_LEVEL_UP,             // Increase the level via cheat (received from client)
    WIN,                        // Win the level
    LOST,                       // Lose the level
    GAME_OVER_WIN,              // Game over in singles and won the game
    GAME_OVER_MULTI_WIN,        // Game over in multiplayer and won the game
    NONE;                       // Empty status code

    public static GameStatusCode convertStrToGameStatusCode(String gameStatusCodeString) {
        return switch (gameStatusCodeString) {
            case "NORMAL" -> GameStatusCode.NORMAL;
            case "NORMAL_MULTI" -> GameStatusCode.NORMAL_MULTI;
            case "LEVEL_UP" -> GameStatusCode.LEVEL_UP;
            case "CHEAT_LEVEL_UP" -> GameStatusCode.CHEAT_LEVEL_UP;
            case "GAME_OVER_WIN" -> GameStatusCode.GAME_OVER_WIN;
            case "GAME_OVER_MULTI_WIN" -> GameStatusCode.GAME_OVER_MULTI_WIN;
            default -> GameStatusCode.NONE;
        };
    }

    public static String convertGameStatusCodeToStr(GameStatusCode gameStatusCode) {
        return switch (gameStatusCode) {
            case NORMAL -> "NORMAL";
            case NORMAL_MULTI -> "NORMAL_MULTI";
            case LEVEL_UP -> "LEVEL_UP";
            case CHEAT_LEVEL_UP -> "CHEAT_LEVEL_UP";
            case WIN -> "WIN";
            case LOST -> "LOST";
            case GAME_OVER_WIN -> "GAME_OVER_WIN";
            case GAME_OVER_MULTI_WIN -> "GAME_OVER_MULTI_WIN";
            default -> "NONE";
        };
    }
}
