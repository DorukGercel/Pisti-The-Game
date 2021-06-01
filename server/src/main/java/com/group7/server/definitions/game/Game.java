package com.group7.server.definitions.game;

import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Game instance that the player interacts with.
 * Created with a new game request.
 * Attention: Be careful dealing with remove method of ArrayList
 * */
@Data
public class Game {

    /** Predefined number of cards*/
    private final Short             NO_CARDS = 52;
    /** Predefined number of ranks*/
    private final Short             NO_RANKS = 13;
    /** Predefined number of players*/
    private final Short             NO_PLAYERS = 2;
    /** Predefined number of players*/
    private final Short             NO_NON_PLAYER_DECKS = 2;
    /** Predefined number of deal cards*/
    private final Short             NO_DEAL_CARDS = 4;
    /**
     * Cards in the game. Each inner list is owned by
     * different place/player in the game.
     * Order is as follows -> Main Deck, Middle Cards,
     * Player 1 Cards, Player 2 Cards ....*/
    private final List<List<Short>>   mCards;
    /** Reference to the card table with card numbers and cards*/
    private final GameConfig.CardTable mCardTable;
    /** Reference to the current game strategy*/
    private       GameStrategy         mGameStrategy;
    /** Scores of each player*/
    private       List<List<Short>>   mScores;
    /** Current game level of the active game*/
    private       Short               mLevel;
    /** Indicates whether player plays vs PC or another player*/
    private       Mode                mMode;
    /** Indicates the turn of the player# or pc*/
    private       Side                mTurn;
    /** Indicates who won the last cards*/
    private       Side                mLastWin;
    /** Played cards by both players
     * 13 length counter list of each rank */
    private       List<Integer>         mPlayedCards;

    /** Constructor; called when a new game is created*/
    public Game(GameConfig.CardTable cardTable){
        this.mCardTable = cardTable;
        this.mLevel = 1;
        this.mMode = Mode.SINGLE;
        this.mTurn = Side.PLAYER;
        this.mLastWin = Side.NONE;
        registerStrategy();

        this.mPlayedCards = new ArrayList<>(this.NO_RANKS);
        for(int i = 0; i < this.NO_CARDS; i++) {
            this.mPlayedCards.add(0);
        }
        this.mCards = new ArrayList<>(NO_PLAYERS + NO_NON_PLAYER_DECKS);
        for(int deckNo = 0; deckNo < NO_PLAYERS + 2; deckNo++) {
            List<Short> tmpDeck = new ArrayList<>();
            mCards.add(tmpDeck);
        }
        initCards();

        this.mScores = new ArrayList<>(NO_PLAYERS);
        for (int i = 0; i < NO_PLAYERS; i++){
            List<Short> scoreList = Arrays.asList((short) 0,(short) 0);
            this.mScores.add(scoreList);
        }
    }

    public List<GameEnvironment> interact(MoveType moveType, Short cardNo){
        return mGameStrategy.interact(moveType, cardNo);
    }

    public List<GameEnvironment> createEnvironment(GameEnvironment playerEnv, GameEnvironment pcEnv) {
        List<GameEnvironment> environment = new ArrayList<>();
        environment.add(playerEnv);
        environment.add(pcEnv);

        return environment;
    }

    public GameEnvironment createPlayerEnvironment(boolean isPisti, boolean gameFinished, GameStatus gameStatus, Game.MoveType moveType) {
        List<Short> handCards = new ArrayList<>(getDeck(Game.Side.PLAYER));
        List<Short> middleCards = new ArrayList<>(getMiddleDeck());
        List<Short> scores = new ArrayList<>(getScores(Game.Side.PLAYER));

        return GameEnvironment.buildPlayerEnvironment(handCards, middleCards, scores, isPisti, gameFinished, gameStatus, moveType);
    }

    public GameEnvironment createPcEnvironment(boolean isPisti, boolean gameFinished, GameStatus gameStatus, Game.MoveType moveType) {
        Short noHandCards = (short) getDeck(Game.Side.PC).size();
        List<Short> middleCards = new ArrayList<>(getMiddleDeck());
        List<Short> scores = new ArrayList<>(getScores(Game.Side.PC));

        return GameEnvironment.buildPcEnvironment(noHandCards, middleCards, scores, isPisti, gameFinished, gameStatus, moveType);
    }

    /** Helper function to change turns.*/
    public void invertTurn() {
        if (getMTurn().equals(Game.Side.PLAYER)) {
            setMTurn(Game.Side.PC);
        } else {
            setMTurn(Game.Side.PLAYER);
        }
    }

    /** Helper function to take the top card of given deck.*/
    public GameConfig.Card getTopCard(List<Short> deck) {
        if (deck.size() > 0) {
            Short topCardNo = getTopCardNo(deck);
            GameConfig.Card topCard = getMCardTable().getCard(topCardNo);
            return topCard;
        }
        return null;
    }

    /** Helper function to take the top card no of given deck.*/
    public Short getTopCardNo(List<Short> deck) {
        if (deck.size() > 0) {
            Short topCardNo = deck.get(deck.size() - 1);
            return topCardNo;
        }
        return null;
    }

    /** Helper function to remove the top card of given deck.*/
    public void removeTopCard(List<Short> deck) {
        if (deck.size() <= 0) {
            return;
        }
        deck.remove(deck.size() - 1);
    }

    /** Helper function to get main deck.*/
    public List<Short> getMainDeck() {
        return getMCards().get(0);
    }

    /** Helper function to get middle deck.*/
    public List<Short> getMiddleDeck() {
        return getMCards().get(1);
    }

    /** Helper function to get player deck with given side.*/
    public List<Short> getDeck(Game.Side side) {
        if(side.equals(Game.Side.PLAYER)){
            return getMCards().get(getNO_NON_PLAYER_DECKS());
        }
        return getMCards().get(getNO_NON_PLAYER_DECKS() + 1);
    }

    /** Helper function to get scores of given side.*/
    public List<Short> getScores(Game.Side side) {
        if (side.equals(Game.Side.PLAYER)){
            return getMScores().get(0);
        }
        return getMScores().get(1);
    }

    /** Helper function to increment the count of rank in the played cards.*/
    public void addCardToPlayedCards(Short cardNo) {
        Integer rank = getRankOfCard(cardNo);
        this.mPlayedCards.set(rank, mPlayedCards.get(rank) + 1);
    }

    /** Helper function to find out whether a rank exists in a deck*/
    public boolean isRankInDeck(int rank, List<Short> deck) {
        for(int i = 0; i < deck.size(); i++) {
            if(rank == getRankOfCard(deck.get(i))) {
                return true;
            }
        }
        return false;
    }

    /** Find rank of the given card. There is 13 ranks in each rank */
    public Integer getRankOfCard(Short cardNo) {
        return cardNo % 13;
    }

    /** Initializes all decks of cards in the game; helper of constructor*/
    public void initCards(){
        List<Short> mainDeck = getMainDeck();
        for (short cardNo = 0; cardNo < getNO_CARDS(); cardNo++) {
            mainDeck.add(cardNo);
        }
        // Shuffle the deck
        Collections.shuffle(mainDeck);
        dealCards();
        openCards();
    }

    /** Helper function to deal the cards to the each player.*/
    public void dealCards() {
        List<Short> mainDeck = getMainDeck();
        //If main deck is empty escape
        if(mainDeck.size() <= 0) {
            return;
        }
        for (int i = 0; i < getNO_PLAYERS(); i++) {
            for (int j = 0; j < getNO_DEAL_CARDS(); j++) {
                // Find the top card in the main deck
                Short topCardNo = getTopCardNo(mainDeck);
                // Remove the top card in the main deck
                removeTopCard(mainDeck);
                // Give the card to the player
                getMCards().get(getNO_NON_PLAYER_DECKS() + i).add(topCardNo);
            }
        }
    }

    /** Helper function to open cards to the middle in the beginning.*/
    public void openCards() {
        List<Short> mainDeck = getMainDeck();
        List<Short> middleDeck = getMiddleDeck();
        // If main deck is empty escape
        if(mainDeck.size() <= 0) {
            return;
        }
        // Find the card to be top in the middle
        Short faceUpCardNo;
        // Loop until placing non-Jack card
        while (true) {
            // Find top card and card no
            faceUpCardNo = getTopCardNo(mainDeck);
            GameConfig.Card faceUpCard = getTopCard(mainDeck);
            // Remove the top card in main deck
            removeTopCard(mainDeck);
            if (!faceUpCard.getMRank().equals(GameConfig.Card.Rank.JACK)) {
                // Found non-Jack card break
                break;
            }
            // If card is Jack place at the bottom
            mainDeck.add(0, faceUpCardNo);
        }
        // Place the other cards to the middle, note: these cards are not visible
        // NO_DEAL_CARD-1 because top card is already opened
        for(int i = 0; i < getNO_DEAL_CARDS() - 1; i++) {
            Short cardNo = getTopCardNo(mainDeck);
            removeTopCard(mainDeck);
            middleDeck.add(cardNo);
        }
        // Place the face up card
        middleDeck.add(faceUpCardNo);
    }

    /** Bind game with the appropriate level*/
    private void registerStrategy() {
        switch (getMLevel()) {
            case 1 -> {
                // TODO: Remove print
                System.out.println("LEVEL 1");
                mGameStrategy = new GameStrategyLevel1();
                mGameStrategy.registerGame(this);
            }
            case 2 -> {
                // TODO: Remove print
                System.out.println("LEVEL 2");
                mGameStrategy = new GameStrategyLevel2();
                mGameStrategy.registerGame(this);
            }
        }
    }

    /** Type definition to indicate player or PC*/
    public enum Side {
        PLAYER,
        PC,
        NONE
    }

    /** Used in game related operations to separate first move and regular card move.*/
    public enum MoveType {
        INITIAL,
        CARD,
        REDEAL,
        RESTART
    }

    public enum GameStatus {
        NORMAL,
        LEVEL_UP,
        LOST,
        WIN
    }

    /** Type definition of game mode; either vs PC or another player*/
    private enum Mode {
        SINGLE,
        MULTI
    }

}
