package com.group7.client.definitions.game;

public enum MoveType {
    INITIAL,
    CARD,
    BLUFF,
    CHALLENGE,
    NOT_CHALLENGE,
    REDEAL,
    RESTART;

    public static MoveType convertMoveType(String moveString) {
        switch (moveString) {
            case "INITIAL" -> {
                return MoveType.INITIAL;
            }
            case "CARD" -> {
                return MoveType.CARD;
            }
            case "BLUFF" -> {
                return MoveType.BLUFF;
            }
            case "CHALLENGE" -> {
                return MoveType.CHALLENGE;
            }
            case "NOT_CHALLENGE" -> {
                return MoveType.NOT_CHALLENGE;
            }
            case "REDEAL" -> {
                return MoveType.REDEAL;
            }
            case "RESTART" -> {
                return MoveType.RESTART;
            }
        }
        return null;
    }
}
