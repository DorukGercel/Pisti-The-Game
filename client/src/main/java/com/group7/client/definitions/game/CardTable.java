package com.group7.client.definitions.game;

import com.group7.client.definitions.image.ImageManager;
import javafx.scene.image.Image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;



/**
 * Table holds the card number and the card itself as key value pair.
 * Attention: Only created once and shared among games.
 */
@Component
public class CardTable {
    private final Map<Short, Card> mCardMap;
    private final ImageManager     mImageManager;

    /** Constructor of the table; called when app starts*/
    @Autowired
    public CardTable(ImageManager imageManager,
                     @Value("${spring.application.screen.card.url}") String cardUrl) {
        this.mImageManager = imageManager;
        mCardMap = new HashMap<>();
        initCards(cardUrl);
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
    private void initCards(String cardUrl){
        for(int i = 0; i < Card.Suit.values().length; i++) {
            for (int j = 0; j < Card.Rank.values().length; j++) {
                short cardNo = (short) (i*Card.Rank.values().length + j);
                Image cardImage = mImageManager.getCardImage(cardNo);
                mCardMap.put(cardNo, new Card(cardNo, cardUrl, cardImage));
            }
        }
    }

}

