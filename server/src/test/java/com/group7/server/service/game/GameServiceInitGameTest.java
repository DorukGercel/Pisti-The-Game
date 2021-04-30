package com.group7.server.service.game;

import com.group7.server.definitions.common.StatusCode;
import com.group7.server.definitions.game.GameConfig;
import com.group7.server.definitions.game.GameTable;
import com.group7.server.repository.ActivePlayerRepositoryTestStub;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {GameServiceImpl.class,
        GameTable.class,
        ActivePlayerRepositoryTestStub.class,
        GameConfig.class,
        GameConfig.CardTable.class})
public class GameServiceInitGameTest {

    private GameService mGameService;
    private ActivePlayerRepositoryTestStub mActivePlayerRepository;
    private Object[] mGameId;

    @Autowired
    public void setGameService(GameService gameService, ActivePlayerRepositoryTestStub activePlayerRepository, GameConfig.CardTable cardTable) {
        this.mGameService = gameService;
        this.mActivePlayerRepository = activePlayerRepository;
    }

    @Before
    public void setup() {
        mGameId = new Object[1];
    }

    @After
    public void teardown() {
        mGameId = null;
    }

    @Test
    public void testInitGame_Fail_GameId() {
        // Fail because of null gameId
        StatusCode statusCode = mGameService.initGame(mActivePlayerRepository.NEW_TO_GAME_PLAYER_ID, null);
        assertEquals(statusCode, StatusCode.FAIL);
    }

    @Test
    public void testInitGame_Fail_OfflinePlayer() {
        // Fail because player not online
        StatusCode statusCode = mGameService.initGame(-1L, mGameId);
        assertEquals(statusCode, StatusCode.FAIL);
    }

    @Test
    public void testInitGame_Fail_AttachedPlayer() {
        // Fail because of player already attached to another game
        StatusCode statusCode = mGameService.initGame(mActivePlayerRepository.GAME_ASSIGNED_PLAYER_ID, mGameId);
        assertEquals(statusCode, StatusCode.FAIL);
    }

    @Test
    public void testInitGame_Success() {
        // Success; check status codes and game id
        StatusCode statusCode = mGameService.initGame(mActivePlayerRepository.NEW_TO_GAME_PLAYER_ID, mGameId);
        assertEquals(statusCode, StatusCode.SUCCESS);
        assertTrue((Long) mGameId[0] >= 0L);
    }
}
