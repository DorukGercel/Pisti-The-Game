package com.group7.server.definitions;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Game instance that the player interacts with.
 * Created with a new game request.
 * */
@Data
public class Game {

    /** Predefined number of cards*/
    private final Integer             NO_CARDS = 52;
    /** Predefined number of players*/
    private final Integer             NO_PLAYERS = 2;
    /**
     * Cards in the game. Each inner list is owned by
     * different place/player in the game.
     * Order is as follows -> Main Deck, Middle Cards,
     * Player 1 Cards, Player 2 Cards ....*/
    private final List<List<Integer>> mCards;
    /** Reference to the card table with card numbers and cards*/
    private final CardTable           mCardTable;
    /** Scores of each player*/
    private       List<Integer>       mScores;
    /** Current game level of the active game*/
    private       Integer             mLevel;
    /** Indicates whether player plays vs PC or another player*/
    private       Mode                mMode;

    /** Constructor; called when a new game is created*/
    public Game(CardTable cardTable){
        this.mCardTable = cardTable;
        this.mLevel = 1;
        this.mMode = Mode.SINGLE;
        this.mScores = new ArrayList<>(2);
        this.mCards = new ArrayList<>(4);
        initCards();
    }

    /** Initializes all decks of cards in the game; helper of constructor*/
    private void initCards(){
        for(int deckNo = 0; deckNo < NO_PLAYERS + 2; deckNo++){
            List<Integer> tmpDeck = new ArrayList<>();
            mCards.add(tmpDeck);
        }
        for(int cardNo = 0; cardNo < NO_CARDS; cardNo++){
            mCards.get(0).add(cardNo);
            // TODO: Remove print
            if (cardNo == 51){
                System.out.println(cardNo);
            }
        }
    }

    /** Type definition of game mode; either vs PC or another player*/
    private enum Mode{
        SINGLE,
        MULTI
    }
}
