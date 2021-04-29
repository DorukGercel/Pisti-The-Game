package com.group7.server.definitions;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Table holds the card number and the card itself as key value pair.
 * Attention: Only created once and shared among games.
 */
@Component
public class CardTable {

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
        // TODO: Remove print
        System.out.println(cardNo);
        System.out.println(mCardMap);
    }
}
