package com.group7.server.definitions.game;

import lombok.Data;

import java.util.List;

@Data
public class GameEnvironment {
    private List<Short> mHandCards;
    private Short       mNoHandCards;
    private List<Short> mMiddleCards;
    private List<Short> mScores;
    private Boolean     mIsPisti;
    private String      mMoveType;

    public static GameEnvironment buildPlayerEnvironment(List<Short> handCards, List<Short> middleCards, List<Short> scores, Boolean isPisti, Game.MoveType moveType) {
        return new GameEnvironment(handCards, middleCards, scores, isPisti, moveType);
    }

    public static GameEnvironment buildPcEnvironment(Short noHandCards, List<Short> middleCards, List<Short> scores, Boolean isPisti, Game.MoveType moveType) {
        return new GameEnvironment(noHandCards, middleCards, scores, isPisti, moveType);
    }

    private GameEnvironment(List<Short> handCards, List<Short> middleCards, List<Short> scores, Boolean isPisti, Game.MoveType moveType){
        this.mHandCards = handCards;
        this.mMiddleCards = middleCards;
        this.mScores = scores;
        this.mIsPisti = isPisti;
        this.mMoveType = Game.MoveType.convertMoveTypeToStr(moveType);
    }

    private GameEnvironment(Short noHandCards, List<Short> middleCards, List<Short> scores, Boolean isPisti, Game.MoveType moveType){
        this.mNoHandCards = noHandCards;
        this.mMiddleCards = middleCards;
        this.mScores = scores;
        this.mIsPisti = isPisti;
        this.mMoveType = Game.MoveType.convertMoveTypeToStr(moveType);
    }


}
