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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {GameServiceImpl.class,
        GameTable.class,
        GameConfig.class,
        GameConfig.CardTable.class,
        LeaderboardRecordServiceImpl.class,
        ActivePlayerRepositoryTestStub.class,
        PlayerRepositoryTestStub.class,
        LeaderboardRecordRepositoryTestStub.class})
public class GameTableTest {
    private GameTable mGameTable;

    @Autowired
    public void setGameTable(GameTable gameTable){
        this.mGameTable = gameTable;
    }

    @Before
    public void setup() {
        // First entry in game table
        mGameTable.addNewGame();
    }

    @After
    public void teardown() {
        // Delete first entry in game table
        mGameTable.deleteGame(1L);
    }

    @Test
    public void testAddAndDeleteGame() {
        // Second game entry in game table, so its gameId = 2L
        assertEquals((Object) 2L, mGameTable.addNewGame());
        // Delete that game
        assertEquals(true, mGameTable.deleteGame(2L));
    }
}
