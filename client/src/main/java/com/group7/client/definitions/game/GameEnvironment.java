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
    private List<Short> mOpponentScores;
    private Boolean     mIsPisti;
    private Boolean     mIsMoveTurn;
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

    private GameEnvironment(List<Short> handCards, List<Short> middleCards, List<Short> scores, List<Short> opponentScores, Boolean isPisti, Boolean isMoveTurn, MoveType moveType){
        this.mHandCards = handCards;
        this.mMiddleCards = middleCards;
        this.mScores = scores;
        this.mOpponentScores = opponentScores;
        this.mIsPisti = isPisti;
        this.mIsMoveTurn = isMoveTurn;
        this.mMoveType = MoveType.convertMoveTypeToStr(moveType);
    }
}
