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

    public static GameEnvironment buildPlayerEnvironment(List<Short> handCards, List<Short> middleCards, List<Short> scores, Boolean isPisti) {
        return new GameEnvironment(handCards, middleCards, scores, isPisti);
    }

    public static GameEnvironment buildPcEnvironment(Short noHandCards, List<Short> middleCards, List<Short> scores, Boolean isPisti) {
        return new GameEnvironment(noHandCards, middleCards, scores, isPisti);
    }

    private GameEnvironment(List<Short> handCards, List<Short> middleCards, List<Short> scores, Boolean isPisti){
        this.mHandCards = handCards;
        this.mMiddleCards = middleCards;
        this.mScores = scores;
        this.mIsPisti = isPisti;
    }

    private GameEnvironment(Short noHandCards, List<Short> middleCards, List<Short> scores, Boolean isPisti){
        this.mNoHandCards = noHandCards;
        this.mMiddleCards = middleCards;
        this.mScores = scores;
        this.mIsPisti = isPisti;
    }


}
