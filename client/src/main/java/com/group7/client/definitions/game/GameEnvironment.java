package com.group7.client.definitions.game;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class GameEnvironment {
    private List<Short> mHandCards;
    private Short       mNoHandCards;
    private List<Short> mMiddleCards;
    private List<Short> mScores;
    private Boolean     mIsPisti;
    private String      mMoveType;

    private GameEnvironment(List<Short> handCards, List<Short> middleCards, List<Short> scores, Boolean isPisti, MoveType moveType){
        this.mHandCards = handCards;
        this.mMiddleCards = middleCards;
        this.mScores = scores;
        this.mIsPisti = isPisti;

        switch (moveType) {
            case INITIAL -> this.mMoveType = "INITIAL";
            case CARD -> this.mMoveType = "CARD";
            case BLUFF -> this.mMoveType = "BLUFF";
            case CHALLENGE -> this.mMoveType = "CHALLENGE";
            case NOT_CHALLENGE -> this.mMoveType = "NOT_CHALLENGE";
            case REDEAL -> this.mMoveType = "REDEAL";
            case RESTART -> this.mMoveType = "RESTART";
        }
    }

    private GameEnvironment(Short noHandCards, List<Short> middleCards, List<Short> scores, Boolean isPisti, MoveType moveType) {
        this.mNoHandCards = noHandCards;
        this.mMiddleCards = middleCards;
        this.mScores = scores;
        this.mIsPisti = isPisti;

        switch (moveType) {
            case INITIAL -> this.mMoveType = "INITIAL";
            case CARD -> this.mMoveType = "CARD";
            case BLUFF -> this.mMoveType = "BLUFF";
            case CHALLENGE -> this.mMoveType = "CHALLENGE";
            case NOT_CHALLENGE -> this.mMoveType = "NOT_CHALLENGE";
            case REDEAL -> this.mMoveType = "REDEAL";
            case RESTART -> this.mMoveType = "RESTART";
        }
    }
}
