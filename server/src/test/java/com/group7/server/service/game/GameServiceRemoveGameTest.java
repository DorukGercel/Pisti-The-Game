package com.group7.server.service.game;

import com.group7.server.definitions.common.StatusCode;
import com.group7.server.definitions.game.Game;
import com.group7.server.definitions.game.GameConfig;
import com.group7.server.definitions.game.GameEnvironment;
import com.group7.server.definitions.game.GameTable;
import com.group7.server.repository.ActivePlayerRepositoryTestStub;
import com.group7.server.repository.LeaderboardRecordRepositoryTestStub;
import com.group7.server.repository.PlayerRepositoryTestStub;
import com.group7.server.service.leaderboard.LeaderboardRecordServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {GameServiceImpl.class,
        GameTable.class,
        ActivePlayerRepositoryTestStub.class,
        GameConfig.class,
        GameConfig.CardTable.class,
        LeaderboardRecordServiceImpl.class,
        PlayerRepositoryTestStub.class,
        LeaderboardRecordRepositoryTestStub.class})
public class GameServiceRemoveGameTest {

    private GameService mGameService;
    private ActivePlayerRepositoryTestStub mActivePlayerRepository;
    private GameConfig.CardTable mCardTable;
    private Object[] mGameId;
    private static Long mSessionId;

    @Autowired
    public void setGameService(GameService gameService, ActivePlayerRepositoryTestStub activePlayerRepository, GameConfig.CardTable cardTable) {
        this.mGameService = gameService;
        this.mActivePlayerRepository = activePlayerRepository;
        this.mCardTable = cardTable;
    }

    @Before
    public void setup() {
        mGameId = new Object[1];
        StatusCode statusCode = mGameService.initGame(mActivePlayerRepository.NEW_TO_GAME_PLAYER_ID, mGameId);
        assertEquals(statusCode, StatusCode.SUCCESS);
        mSessionId = (Long) mGameId[0];
    }

    @After
    public void teardown() {
        mGameId = null;
    }

    @Test
    public void testRemoveGame_Fail_GameId() {
        // Fail because of null gameId
        StatusCode statusCode = mGameService.removeGame(mActivePlayerRepository.NEW_TO_GAME_PLAYER_ID, null);
        assertEquals(statusCode, StatusCode.FAIL);
    }

    @Test
    public void testRemoveGame_Fail_OfflinePlayer() {
        // Fail because player not online
        StatusCode statusCode = mGameService.removeGame(-1L, (Long) mGameId[0]);
        assertEquals(statusCode, StatusCode.FAIL);
    }

    @Test
    public void testRemoveGame_Success() {
        // Make the card interaction successfully
        // Make the initial move successfully
        Short cardNo = (short) -1;
        List<GameEnvironment> gameEnvironmentList = new ArrayList<>();
        List<Object> gameStatus = new ArrayList<>();
        StatusCode statusCode = mGameService.interactGame(mSessionId,
                (Long) mGameId[0],
                cardNo,
                Game.MoveType.INITIAL,
                Game.GameStatusCode.NORMAL,
                gameEnvironmentList,
                gameStatus);
        assertEquals(statusCode, StatusCode.SUCCESS);
        // Try to remove the game
        statusCode = mGameService.removeGame(mSessionId, (Long) mGameId[0]);
        assertEquals(statusCode, StatusCode.SUCCESS);

        // Also try to remove a game that is already removed
        statusCode = mGameService.removeGame(mSessionId, (Long) mGameId[0]);
        assertEquals(statusCode, StatusCode.FAIL);
    }
}
