package com.group7.client.definitions.game;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for dealing with game related functionalities
 */
@Data
@Component
public class GameService {
    /** Reference to common card table*/
    private final CardTable mCardTable;

    /** All args constructor*/
    @Autowired
    public GameService(CardTable cardTable) {
        this.mCardTable = cardTable;
    }

    /** Function which converts the player cards no to card*/
    public List<Card> dealPlayerCards(GameEnvironment playerEnvironment, List<Short> playerCardsNo) {
        List<Card> playerCards = new ArrayList<>();
        for(Short cardNo : playerEnvironment.getMHandCards()) {
            playerCardsNo.add(cardNo);
            playerCards.add(mCardTable.getCard(cardNo));
        }
        return playerCards;
    }

    /** Function which converts the middle card no to card*/
    public Card getMiddleCard(Short cardNo) {
        return mCardTable.getCard(cardNo);
    }
}
