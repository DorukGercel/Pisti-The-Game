package com.group7.client.definitions.game;

public enum MoveType {
    INITIAL,
    CARD,
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
