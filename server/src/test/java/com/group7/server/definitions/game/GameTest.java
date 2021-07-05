package com.group7.server.definitions.game;

import com.group7.server.repository.ActivePlayerRepositoryTestStub;
import com.group7.server.repository.LeaderboardRecordRepositoryTestStub;
import com.group7.server.repository.PlayerRepositoryTestStub;
import com.group7.server.service.game.GameServiceImpl;
import com.group7.server.service.leaderboard.LeaderboardRecordServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {GameServiceImpl.class,
        GameTable.class,
        GameConfig.class,
        GameConfig.CardTable.class,
        LeaderboardRecordServiceImpl.class,
        ActivePlayerRepositoryTestStub.class,
        PlayerRepositoryTestStub.class,
        LeaderboardRecordRepositoryTestStub.class,
        Game.class})

public class GameTest {
    private Game mGame;

    @Autowired
    public void setGame(Game game){
        this.mGame = game;
    }

    @Test
    public void testGameInitialSetup_Success() {
        assertEquals(java.util.Optional.of((short) 151).get(), Game.WIN_SCORE);
        assertEquals(java.util.Optional.of((short) 4).get(), Game.MAX_LEVEL);
        assertEquals(java.util.Optional.of((short) 52).get(), mGame.getNO_CARDS());
        assertEquals(java.util.Optional.of((short) 13).get(), mGame.getNO_RANKS());
        assertEquals(java.util.Optional.of((short) 2).get(), mGame.getNO_PLAYERS());
        assertEquals(java.util.Optional.of((short) 2).get(), mGame.getNO_NON_PLAYER_DECKS());
        assertEquals(java.util.Optional.of((short) 4).get(), mGame.getNO_DEAL_CARDS());
    }

    @Test
    public void testGetOtherSide_Success() {
        assertEquals(Game.Side.PLAYER, mGame.getOtherSide(Game.Side.PC));
        assertEquals(Game.Side.PC, mGame.getOtherSide(Game.Side.PLAYER));
        assertEquals(Game.Side.PLAYER, mGame.getOtherSide(Game.Side.NONE));
    }

    @Test
    public void testGetMainDeck_Success() {
        assertEquals(mGame.getMCards().get(0), mGame.getMainDeck());
    }

    @Test
    public void testGetMiddleDeck_Success() {
        assertEquals(mGame.getMCards().get(1), mGame.getMiddleDeck());
    }

    @Test
    public void testGetPlayerDeck_Success() {
        assertEquals(mGame.getMCards().get(2), mGame.getDeck(Game.Side.PLAYER));
    }

    @Test
    public void testGetPCDeck_Success() {
        assertEquals(mGame.getMCards().get(3), mGame.getDeck(Game.Side.PC));
    }

    @Test
    public void testGetTopCard_Success() {
        List<Short> mainDeck = mGame.getMainDeck();
        assertEquals(mainDeck.get(mainDeck.size() - 1), mGame.getTopCardNo(mainDeck));
    }

    @Test
    public void testGetRankOfCard_Success() {
        assertEquals(java.util.Optional.of(1).get(), mGame.getRankOfCard((short) 1));
        assertEquals(java.util.Optional.of(1).get(), mGame.getRankOfCard((short) 14));
        assertEquals(java.util.Optional.of(1).get(), mGame.getRankOfCard((short) 27));
        assertEquals(java.util.Optional.of(1).get(), mGame.getRankOfCard((short) 40));
    }

    @Test
    public void testGetRankOfCard_Fail() {
        assertNotEquals(java.util.Optional.of(1).get(), mGame.getRankOfCard((short) 2));
        assertNotEquals(java.util.Optional.of(1).get(), mGame.getRankOfCard((short) 15));
        assertNotEquals(java.util.Optional.of(1).get(), mGame.getRankOfCard((short) 28));
        assertNotEquals(java.util.Optional.of(1).get(), mGame.getRankOfCard((short) 41));
    }

    @Test
    public void testIsRankInDeck_Success() {
        assertTrue(mGame.isRankInDeck(mGame.getRankOfCard(mGame.getMainDeck().get(0)), mGame.getMainDeck()));
    }

    @Test
    public void testRemoveTopCard_Success() {
        // Check the top card
        Short cardNo;
        assertEquals(cardNo = (mGame.getMainDeck().get(mGame.getMainDeck().size() - 1)), mGame.getTopCardNo(mGame.getMainDeck()));
        mGame.removeTopCard(mGame.getMainDeck());
        assertNotEquals(cardNo, mGame.getTopCardNo(mGame.getMainDeck()));
    }

    @Test
    public void testGetScores_Success() {
        List<Short> playerScores = mGame.getScores(Game.Side.PLAYER);
        assertTrue(playerScores.get(0) >= 0);
        assertTrue(playerScores.get(1) >= 0);
        List<Short> pcScores = mGame.getScores(Game.Side.PC);
        assertTrue(pcScores.get(0) >= 0);
        assertTrue(pcScores.get(1) >= 0);
    }
}
