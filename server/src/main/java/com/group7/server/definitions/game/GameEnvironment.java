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
    /** Check whether level is over*/
    private Boolean     mGameFinished;
    /** Check whether game is over*/
    private Game.GameStatus mGameStatus;
    private String      mMoveType;

    public static GameEnvironment buildPlayerEnvironment(List<Short> handCards, List<Short> middleCards, List<Short> scores, Boolean isPisti, Boolean gameFinished, Game.GameStatus gameStatus, Game.MoveType moveType) {
        return new GameEnvironment(handCards, middleCards, scores, isPisti, gameFinished, gameStatus ,moveType);
    }

    public static GameEnvironment buildPcEnvironment(Short noHandCards, List<Short> middleCards, List<Short> scores, Boolean isPisti, Boolean gameFinished, Game.GameStatus gameStatus, Game.MoveType moveType) {
        return new GameEnvironment(noHandCards, middleCards, scores, isPisti, gameFinished, gameStatus, moveType);
    }

    private GameEnvironment(List<Short> handCards, List<Short> middleCards, List<Short> scores, Boolean isPisti, Boolean gameFinished, Game.GameStatus gameStatus, Game.MoveType moveType){
        this.mHandCards = handCards;
        this.mMiddleCards = middleCards;
        this.mScores = scores;
        this.mIsPisti = isPisti;
        this.mGameFinished = gameFinished;
        this.mGameStatus = gameStatus;
        switch (moveType) {
            case INITIAL -> this.mMoveType = "INITIAL";
            case CARD -> this.mMoveType = "CARD";
            case REDEAL -> this.mMoveType = "REDEAL";
            case RESTART -> this.mMoveType = "RESTART";
        }
    }

    private GameEnvironment(Short noHandCards, List<Short> middleCards, List<Short> scores, Boolean isPisti, Boolean gameFinished, Game.GameStatus gameStatus, Game.MoveType moveType){
        this.mNoHandCards = noHandCards;
        this.mMiddleCards = middleCards;
        this.mScores = scores;
        this.mIsPisti = isPisti;
        this.mGameFinished = gameFinished;
        this.mGameStatus = gameStatus;
        switch (moveType) {
            case INITIAL -> this.mMoveType = "INITIAL";
            case CARD -> this.mMoveType = "CARD";
            case REDEAL -> this.mMoveType = "REDEAL";
            case RESTART -> this.mMoveType = "RESTART";
        }
    }


}
