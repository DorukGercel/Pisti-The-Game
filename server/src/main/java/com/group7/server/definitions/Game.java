package com.group7.server.definitions;

import lombok.Data;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.ArrayList;
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
    private final Integer             NO_CARDS = 52;
    /** Predefined number of players*/
    private final Integer             NO_PLAYERS = 2;
    /** Predefined number of players*/
    private final Integer             NO_NON_PLAYER_DECKS = 2;
    /** Predefined number of deal cards*/
    private final Integer             NO_DEAL_CARDS = 4;
    /**
     * Cards in the game. Each inner list is owned by
     * different place/player in the game.
     * Order is as follows -> Main Deck, Middle Cards,
     * Player 1 Cards, Player 2 Cards ....*/
    private final List<List<Short>>   mCards;
    /** Reference to the card table with card numbers and cards*/
    private final CardTable           mCardTable;
    /** Scores of each player*/
    private       List<List<Short>>   mScores;
    /** Current game level of the active game*/
    private       Integer             mLevel;
    /** Indicates whether player plays vs PC or another player*/
    private       Mode                mMode;
    /** Indicates the turn of the player# or pc*/
    private       Side                mTurn;
    /** Indicates who won the last cards*/
    private       Side                mLastWin;

    /** Constructor; called when a new game is created*/
    public Game(CardTable cardTable){
        this.mCardTable = cardTable;
        this.mLevel = 1;
        this.mMode = Mode.SINGLE;
        this.mTurn = Side.PLAYER;
        this.mLastWin = Side.NONE;

        this.mCards = new ArrayList<>(NO_PLAYERS + NO_NON_PLAYER_DECKS);
        initCards();

        this.mScores = new ArrayList<>(NO_PLAYERS);
        for (int i = 0; i < NO_PLAYERS; i++){
            this.mScores.add(new ArrayList<>(2));
        }
    }


    public void interactSinglePlayer(MoveType moveType, Short cardNo){
        if (moveType.equals(MoveType.INITIAL)){

        } else {
            simulateGame(cardNo);
        }
    }

    // TODO: Decide package structure to send to client
    private void simulateGame(Short cardNo) {
        if (mMode.equals(Mode.SINGLE)) {
            simulateMovement(cardNo, mTurn);
        }
    }

    /** The method that evaluates the moves of the player or the pc*/
    private void simulateMovement(Short cardNo, Side side) {
        List<Short> middleDeck = getMiddleDeck();
        // Player's or PC' deck
        List<Short> currentPlayerDeck = getDeck(side);
        // Remove the card from players deck
        currentPlayerDeck.remove(cardNo);
        // Check if middle is empty or not
        if(middleDeck.size() > 0) {
            Card currentPlayerCard = mCardTable.getCard(cardNo);
            if (isMatchedCard(currentPlayerCard)){
                // Increment score of player and set last win
                incrementScore(currentPlayerCard, side);
                setMLastWin(side);
            } else {
                // Add player card to top of middle
                middleDeck.add(cardNo);
            }
        } else {
            // Add player card to top of middle
            middleDeck.add(cardNo);
        }
        // Change turn
        invertTurn();
    }

    private void pcDecideCard() {

    }

    /** Initializes all decks of cards in the game; helper of constructor*/
    private void initCards(){
        for(int deckNo = 0; deckNo < NO_PLAYERS + 2; deckNo++) {
            List<Short> tmpDeck = new ArrayList<>();
            mCards.add(tmpDeck);
        }
        List<Short> mainDeck = getMainDeck();
        for (short cardNo = 0; cardNo < NO_CARDS; cardNo++) {
            mainDeck.add(cardNo);
            // TODO: Remove print
            if (cardNo == 51) {
                System.out.println(cardNo);
            }
        }
        // TODO: remove print statement
        System.out.println(this);

        // Shuffle the deck
        Collections.shuffle(mainDeck);

        // TODO: remove print statement
        System.out.println(this);

        dealCards();
        openCards();
        // TODO: remove print statement
        System.out.println(this);
    }

    /** Helper function to deal the cards to the each player.*/
    private void dealCards() {
        List<Short> mainDeck = getMainDeck();
        //If main deck is empty escape
        if(mainDeck.size() <= 0) {
            return;
        }
        for (int i = 0; i < NO_PLAYERS; i++) {
            for (int j = 0; j < NO_DEAL_CARDS; j++) {
                // Find the top card in the main deck
                Short topCardNo = getTopCardNo(mainDeck);
                // Remove the top card in the main deck
                removeTopCard(mainDeck);
                // Give the card to the player
                mCards.get(NO_NON_PLAYER_DECKS + i).add(topCardNo);
            }
        }
    }

    /** Helper function to open cards to the middle in the beginning.*/
    private void openCards() {
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
            Card faceUpCard = getTopCard(mainDeck);
            // Remove the top card in main deck
            removeTopCard(mainDeck);
            if (!faceUpCard.getMRank().equals(Card.Rank.JACK)) {
                // Found non-Jack card break
                // TODO: Remove print statement
                System.out.println(faceUpCard.getMRank());
                break;
            }
            // If card is Jack place at the bottom
            mainDeck.add(0, faceUpCardNo);
        }
        // Place the other cards to the middle, note: these cards are not visible
        // NO_DEAL_CARD-1 because top card is already opened
        for(int i = 0; i < NO_DEAL_CARDS - 1; i++) {
            Short cardNo = getTopCardNo(mainDeck);
            removeTopCard(mainDeck);
            middleDeck.add(cardNo);
        }
        // Place the face up card
        middleDeck.add(faceUpCardNo);
    }

    /** Helper function to decide if there is a takeover.*/
    private boolean isMatchedCard(Card playerCard) {
        //Extract the face up card
        List<Short> middleDeck = getMiddleDeck();
        Card faceUpCard = getTopCard(middleDeck);

        // True if the player card is jack or the ranks of the cards match
        return (playerCard.getMRank().equals(Card.Rank.JACK) || playerCard.getMRank().equals(faceUpCard.getMRank()));
    }

    /** Helper function to increment the score of the side who achieved takeover.*/
    // TODO: Q: When pisti achieved does user count the values of the cards? No in this implementation.
    private void incrementScore(Card playerCard, Side side) {
        short pointsReceived = 0;
        short cardsReceived = 0;

        List<Short> middleDeck = getMiddleDeck();
        Card faceUpCard = getTopCard(middleDeck);

        // Decide the takeover type
        TakeoverType takeoverType = TakeoverType.getTakeoverType(faceUpCard, middleDeck.size());

        if(takeoverType.equals(TakeoverType.PISTI) || takeoverType.equals(TakeoverType.DOUBLE_PISTI)){
            // Take takeover special points by player card and top middle cards
            short takeoverPoint = SpecialPoint.takeTakeoverPoint(takeoverType);
            removeTopCard(middleDeck);
            pointsReceived = (short) (pointsReceived + takeoverPoint);
            cardsReceived = (short) (cardsReceived + 2);
        } else {
            // Take special points of the player card and top middle cards
            short playerCardPoint = SpecialPoint.takeCardPoint(playerCard);
            short faceUpCardPoint = SpecialPoint.takeCardPoint(faceUpCard);
            removeTopCard(middleDeck);
            pointsReceived = (short) (pointsReceived + playerCardPoint + faceUpCardPoint);
            cardsReceived = (short) (cardsReceived + 2);
        }

        // Count the points of the cards received
        for(Short cardNo : middleDeck) {
            Card middleCard = mCardTable.getCard(cardNo);
            short middleCardPoint = SpecialPoint.takeCardPoint(middleCard);
            pointsReceived = (short) (pointsReceived + middleCardPoint);
            cardsReceived = (short) (cardsReceived + 1);
        }
        // Flush the middle deck
        middleDeck.clear();

        // Set the scores (point, card received) of the player (decide as who made takeover)
        List<Short> scores = getScores(side);
        scores.set(0, (short) (scores.get(0) + pointsReceived));
        scores.set(1, (short) (scores.get(1) + cardsReceived));
    }

    /** Helper function to change turns.*/
    private void invertTurn() {
        if (mTurn.equals(Side.PLAYER)) {
            mTurn = Side.PC;
        } else {
            mTurn = Side.PLAYER;
        }
    }

    /** Helper function to take the top card of given deck.*/
    private Card getTopCard(List<Short> deck) {
        if (deck.size() > 0) {
            Short topCardNo = getTopCardNo(deck);
            Card topCard = mCardTable.getCard(topCardNo);
            return topCard;
        }
        return null;
    }

    /** Helper function to take the top card no of given deck.*/
    private Short getTopCardNo(List<Short> deck) {
        if (deck.size() > 0) {
            Short topCardNo = deck.get(deck.size() - 1);
            return topCardNo;
        }
        return null;
    }

    /** Helper function to remove the top card of given deck.*/
    private void removeTopCard(List<Short> deck) {
        if (deck.size() <= 0) {
            return;
        }
        deck.remove(deck.size() - 1);
    }

    /** Helper function to get main deck.*/
    private List<Short> getMainDeck() {
        return mCards.get(0);
    }

    /** Helper function to get middle deck.*/
    private List<Short> getMiddleDeck() {
        return mCards.get(1);
    }

    /** Helper function to get player deck with given side.*/
    private List<Short> getDeck(Side side) {
        if(side.equals(Side.PLAYER)){
            return mCards.get(NO_NON_PLAYER_DECKS);
        }
        return mCards.get(NO_NON_PLAYER_DECKS + 1);
    }

    /** Helper function to get scores of given side.*/
    private List<Short> getScores(Side side) {
        if (side.equals(Side.PLAYER)){
            return mScores.get(0);
        }
        return mScores.get(1);
    }

    /** Used in game related operations to separate first move and regular card move.*/
    public enum MoveType {
        INITIAL,
        CARD
    }

    /** Type definition of game mode; either vs PC or another player*/
    private enum Mode {
        SINGLE,
        MULTI
    }

    /** Type definition to indicate player or PC*/
    private enum Side {
        PLAYER,
        PC,
        NONE
    }

    /** Type definition of how player took the cards*/
    private enum TakeoverType {
        DOUBLE_PISTI,
        PISTI,
        REGULAR;

        public static TakeoverType getTakeoverType(Card faceUpCard, int noMiddleCards) {
            if (noMiddleCards == 1 && faceUpCard.getMRank().equals(Card.Rank.JACK)) {
                return DOUBLE_PISTI;
            } else if (noMiddleCards == 1) {
                return PISTI;
            } else {
                return REGULAR;
            }
        }
    }

    /** Special points in the game*/
    private enum SpecialPoint {
        DOUBLE_PISTI((short) 20),
        PISTI((short) 10),
        DIAMOND_TEN((short) 3),
        CLUB_TWO((short) 2),
        ACE((short) 1),
        JACK((short) 1);

        private Short point;

        public Short getPoint() {
            return this.point;
        }

        public static Short takeTakeoverPoint(TakeoverType type) {
            if (type.equals(TakeoverType.DOUBLE_PISTI)) {
                return DOUBLE_PISTI.getPoint();
            } else if (type.equals(TakeoverType.PISTI)) {
                return PISTI.getPoint();
            }
            return 0;
        }

        public static Short takeCardPoint(Card card){
            if (card.getMRank().equals(Card.Rank.ACE)) {
                return ACE.getPoint();
            } else if (card.getMRank().equals(Card.Rank.JACK)) {
                return JACK.getPoint();
            } else if (card.getMSuit().equals(Card.Suit.CLUBS) && card.getMRank().equals(Card.Rank.TWO)) {
                return CLUB_TWO.getPoint();
            } else if (card.getMSuit().equals(Card.Suit.DIAMONDS) && card.getMRank().equals(Card.Rank.TEN)) {
                return DIAMOND_TEN.getPoint();
            }
            return 0;
        }

        SpecialPoint(Short point) {
            this.point = point;
        }
    }
}
