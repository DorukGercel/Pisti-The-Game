package com.group7.server.definitions.game;

import com.group7.server.repository.ActivePlayerRepositoryTestStub;
import com.group7.server.repository.LeaderboardRecordRepositoryTestStub;
import com.group7.server.repository.PlayerRepositoryTestStub;
import com.group7.server.service.game.GameServiceImpl;
import com.group7.server.service.leaderboard.LeaderboardRecordServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {GameServiceImpl.class,
        GameTable.class,
        GameConfig.class,
        GameConfig.CardTable.class,
        LeaderboardRecordServiceImpl.class,
        ActivePlayerRepositoryTestStub.class,
        PlayerRepositoryTestStub.class,
        LeaderboardRecordRepositoryTestStub.class,
        Game.class,
        MultiplayerGame.class
})

public class MultiplayerGameTest {

    private MultiplayerGame mMultiplayerGame;

    @Autowired
    public void setMultiplayerGame(MultiplayerGame multiplayerGame){
        this.mMultiplayerGame = multiplayerGame;
    }

    @Test
    public void testMultiplayerGameInitialSetup_Success() {
        assertEquals(java.util.Optional.of((short) 151).get(), Game.WIN_SCORE);
        assertEquals(java.util.Optional.of((short) 3).get(), Game.SINGLE_MAX_LEVEL);
        assertEquals(java.util.Optional.of((short) 4).get(), Game.MAX_LEVEL);
        assertEquals(java.util.Optional.of((short) 52).get(), mMultiplayerGame.getNO_CARDS());
        assertEquals(java.util.Optional.of((short) 13).get(), mMultiplayerGame.getNO_RANKS());
        assertEquals(java.util.Optional.of((short) 2).get(), mMultiplayerGame.getNO_PLAYERS());
        assertEquals(java.util.Optional.of((short) 2).get(), mMultiplayerGame.getNO_NON_PLAYER_DECKS());
        assertEquals(java.util.Optional.of((short) 4).get(), mMultiplayerGame.getNO_DEAL_CARDS());
    }

    @Test
    public void testWaitToRun_Success() {
        Long firstPlayerId = 1L;
        mMultiplayerGame.setCurrentState(MultiplayerGame.MultiplayerGameState.RUNNING);
        mMultiplayerGame.waitToRun(firstPlayerId);
        assertEquals(java.util.Optional.of(firstPlayerId).get(), mMultiplayerGame.getPlayerId());
    }

    @Test
    public void testStartToRun_Success() {
        Long secondPlayerId = 2L;
        mMultiplayerGame.startToRun(secondPlayerId);
        assertEquals(java.util.Optional.of(secondPlayerId).get(), mMultiplayerGame.getPcId());
    }

    @Test
    public void testIsGameRunning_Success() {
        Long secondPlayerId = 2L;
        mMultiplayerGame.startToRun(secondPlayerId);
        assertTrue(mMultiplayerGame.isGameRunning());
    }

    @Test
    public void testGetPlayerId_PcId_Success() {
        Long firstPlayerId = 1L;
        mMultiplayerGame.setCurrentState(MultiplayerGame.MultiplayerGameState.RUNNING);
        mMultiplayerGame.waitToRun(firstPlayerId);
        assertEquals(java.util.Optional.of(firstPlayerId).get(), mMultiplayerGame.getPlayerId());
        Long secondPlayerId = 2L;
        mMultiplayerGame.startToRun(secondPlayerId);
        assertEquals(java.util.Optional.of(secondPlayerId).get(), mMultiplayerGame.getPcId());
    }

    @Test
    public void testGetPlayerId_PcId_Fail() {
        Long firstPlayerId = 1L;
        Long secondPlayerId = 2L;
        mMultiplayerGame.setCurrentState(MultiplayerGame.MultiplayerGameState.RUNNING);
        mMultiplayerGame.waitToRun(firstPlayerId);
        mMultiplayerGame.startToRun(secondPlayerId);
        assertNotEquals(java.util.Optional.of(secondPlayerId).get(), mMultiplayerGame.getPlayerId());
        assertNotEquals(java.util.Optional.of(firstPlayerId).get(), mMultiplayerGame.getPcId());
    }

}