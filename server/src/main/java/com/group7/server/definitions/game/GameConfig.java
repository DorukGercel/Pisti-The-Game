package com.group7.server.definitions.game;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Configuration class of the prototype scoped game bean*/
@Configuration
public class GameConfig {

    @Bean(name = "Game")
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Game game(final CardTable cardTable){
        return new Game(cardTable);
    }
    
    /** Definition of the card in the game*/
    @Data
    @RequiredArgsConstructor
    public static class Card {

        private final Suit mSuit;
        private final Rank mRank;

        /** Type definition of the suit of the card*/
        public enum Suit {
            CLUBS,
            DIAMONDS,
            HEARTS,
            SPADES
        }

        /** Type definition of the rank of the card*/
        public enum Rank {
            ACE,
            TWO,
            THREE,
            FOUR,
            FIVE,
            SIX,
            SEVEN,
            EIGHT,
            NINE,
            TEN,
            JACK,
            QUEEN,
            KING
        }
    }

    /**
     * Table holds the card number and the card itself as key value pair.
     * Attention: Only created once and shared among games.
     */
    @Component
    public static class CardTable {

        private final Map<Short, Card> mCardMap;

        /** Constructor of the table; called when app starts*/
        public CardTable(){
            mCardMap = new HashMap<>();
            initCards();
        }

        /** Return the card itself with that card number*/
        public Card getCard(short cardNo){
            return mCardMap.get(cardNo);
        }

        /** Return the list of cards with that card number*/
        public List<Card> getAllCards(List<Short> cardList) {
            List<Card> cards = new ArrayList<>();
            for(Short cardNo : cardList) {
                cards.add(getCard(cardNo));
            }
            return cards;
        }

        /** Forms the map, helper function of the constructor*/
        private void initCards(){
            short cardNo = 0;
            for(Card.Suit suit : Card.Suit.values()) {
                for (Card.Rank rank : Card.Rank.values()) {
                    mCardMap.put(cardNo++, new Card(suit, rank));
                }
            }
        }
    }
}
