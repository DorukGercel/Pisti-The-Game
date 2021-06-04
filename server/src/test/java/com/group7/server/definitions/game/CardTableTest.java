package com.group7.server.definitions.game;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        GameConfig.CardTable.class
        })
public class CardTableTest {
    private GameConfig.CardTable mCardTable;

    @Autowired
    public void setGameTable(GameConfig.CardTable cardTable){
        this.mCardTable = cardTable;
    }

    @Test
    public void testGetCard_Fail_InvalidCard() {
        GameConfig.Card card = mCardTable.getCard((short) -1);
        assertNull(card);
    }

    @Test
    public void testGetCard_Fail_OutOfBoundCard() {
        GameConfig.Card card = mCardTable.getCard((short) 55);
        assertNull(card);
    }

    @Test
    public void testGetCard_Success() {
        GameConfig.Card card = mCardTable.getCard((short) 21);
        assertNotNull(card);
    }
}
