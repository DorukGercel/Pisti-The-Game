package com.group7.client.definitions.game;

public enum MoveType {
    INITIAL,
    CARD,
    BLUFF,
    CHALLENGE,
    NOT_CHALLENGE,
    CHALLENGE_SUCCESS,
    CHALLENGE_FAIL,
    REDEAL,
    RESTART,
    READ,
    PASS,
    NONE;

    public static MoveType convertStrToMoveType(String moveString) {
        return switch (moveString) {
            case "INITIAL" -> MoveType.INITIAL;
            case "CARD" -> MoveType.CARD;
            case "BLUFF" -> MoveType.BLUFF;
            case "CHALLENGE" -> MoveType.CHALLENGE;
            case "NOT_CHALLENGE" -> MoveType.NOT_CHALLENGE;
            case "CHALLENGE_SUCCESS" -> MoveType.CHALLENGE_SUCCESS;
            case "CHALLENGE_FAIL" -> MoveType.CHALLENGE_FAIL;
            case "REDEAL" -> MoveType.REDEAL;
            case "RESTART" -> MoveType.RESTART;
            case "READ" -> MoveType.READ;
            case "PASS" -> PASS;
            default -> MoveType.NONE;
        };
    }

    public static String convertMoveTypeToStr(MoveType moveType) {
        return switch (moveType) {
            case INITIAL -> "INITIAL";
            case CARD -> "CARD";
            case BLUFF -> "BLUFF";
            case CHALLENGE -> "CHALLENGE";
            case NOT_CHALLENGE -> "NOT_CHALLENGE";
            case CHALLENGE_SUCCESS -> "CHALLENGE_SUCCESS";
            case CHALLENGE_FAIL -> "CHALLENGE_FAIL";
            case REDEAL -> "REDEAL";
            case RESTART -> "RESTART";
            case READ -> "READ";
            case PASS -> "PASS";
            default -> "NONE";
        };
    }

    public static boolean isPassMove(MoveType moveType) {
        return moveType.equals(PASS);
    }

    public static boolean isChallengeRelatedMove(MoveType moveType) {
        return moveType.equals(CHALLENGE) || moveType.equals(NOT_CHALLENGE) || moveType.equals(CHALLENGE_SUCCESS) || moveType.equals(CHALLENGE_FAIL);
    }

    public static boolean isBluffLevelMoveType(MoveType moveType) {
        return moveType.equals(BLUFF) || isChallengeRelatedMove(moveType);
    }
}
