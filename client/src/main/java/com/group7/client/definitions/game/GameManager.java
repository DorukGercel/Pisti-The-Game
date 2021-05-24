package com.group7.client.definitions.game;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for dealing with game related functionalities
 */
@Component
public class GameManager {

    private final CardTable mCardTable;

    @Autowired
    public GameManager(CardTable cardTable) {
        this.mCardTable = cardTable;
    }

    public List<Card> dealPlayerCards(GameEnvironment playerEnvironment) {
        List<Card> playerCards = new ArrayList<>();
        for(Short cardNo : playerEnvironment.getMHandCards()) {
            playerCards.add(mCardTable.getCard(cardNo));
        }
        return playerCards;
    }

    public Card getMiddleCard(GameEnvironment playerEnvironment) {
        List<Short> middleCardsNo = playerEnvironment.getMMiddleCards();
        if (middleCardsNo.size() == 0) {
            return null;
        }
        return mCardTable.getCard(middleCardsNo.get(middleCardsNo.size() - 1));
    }
}
