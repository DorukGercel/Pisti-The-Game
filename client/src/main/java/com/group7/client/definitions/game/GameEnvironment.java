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
        this.mMoveType = MoveType.convertMoveTypeToStr(moveType);
    }

    private GameEnvironment(Short noHandCards, List<Short> middleCards, List<Short> scores, Boolean isPisti, MoveType moveType) {
        this.mNoHandCards = noHandCards;
        this.mMiddleCards = middleCards;
        this.mScores = scores;
        this.mIsPisti = isPisti;
        this.mMoveType = MoveType.convertMoveTypeToStr(moveType);
    }
}
