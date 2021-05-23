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

    public GameEnvironment(List<Short> handCards, List<Short> middleCards, List<Short> scores, Boolean isPisti){
        this.mHandCards = handCards;
        this.mMiddleCards = middleCards;
        this.mScores = scores;
        this.mIsPisti = isPisti;
    }

    public GameEnvironment(Short noHandCards, List<Short> middleCards, List<Short> scores, Boolean isPisti){
        this.mNoHandCards = noHandCards;
        this.mMiddleCards = middleCards;
        this.mScores = scores;
        this.mIsPisti = isPisti;
    }


}
