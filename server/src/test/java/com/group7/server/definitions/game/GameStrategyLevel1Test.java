package com.group7.server.definitions.game;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        Game.class,
        GameConfig.CardTable.class,
})
public class GameStrategyLevel1Test {
    private Game mGame;
    private GameStrategyLevel1 mGameStrategyLevel1;

    @Autowired
    public void setGame(Game game){
        this.mGame = game;
        this.mGameStrategyLevel1 = new GameStrategyLevel1();
    }

    @Before
    public void setup() {
        // First entry in game table
        mGame.setMGameStrategy(mGameStrategyLevel1);
        mGameStrategyLevel1.registerGame(mGame);
    }

    @Test
    public void testInitialInteractPlayer_Success() {
        List<GameEnvironment> gameEnvironmentList = mGameStrategyLevel1.interact(Game.MoveType.INITIAL, (short) -1);
        GameEnvironment playerEnv = gameEnvironmentList.get(0);
        assertEquals(2, (short) playerEnv.getMScores().size());
        assertFalse(playerEnv.getMIsPisti());

    }

    @Test
    public void testInitialInteractPc_Success() {
        List<GameEnvironment> gameEnvironmentList = mGameStrategyLevel1.interact(Game.MoveType.INITIAL, (short) -1);
        GameEnvironment pcEnv = gameEnvironmentList.get(1);
        assertEquals(2, (short) pcEnv.getMScores().size());
        assertFalse(pcEnv.getMIsPisti());
    }

    @Test
    public void testCardInteractPlayer_Fail_InvalidCard() {
        boolean flag = false;
        try {
            mGameStrategyLevel1.interact(Game.MoveType.CARD, (short) -1);
        } catch (Exception e) {
            flag = true;
        } finally {
            assertTrue(flag);
        }
    }

    @Test
    public void testCardInteractPlayer_Success() {
        List<GameEnvironment> gameEnvironmentList = mGameStrategyLevel1.interact(Game.MoveType.INITIAL, (short) -1);
        GameEnvironment playerEnv = gameEnvironmentList.get(0);
        // Make a card move
        List<GameEnvironment> newGameEnvironmentList = mGameStrategyLevel1.interact(Game.MoveType.CARD, (short) playerEnv.getMHandCards().get(0));
        assertNotNull(newGameEnvironmentList);
    }
}
