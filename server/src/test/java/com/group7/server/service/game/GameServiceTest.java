package com.group7.server.service.game;

import com.group7.server.definitions.*;
import com.group7.server.model.ActivePlayer;
import com.group7.server.repository.ActivePlayerRepository;
import com.group7.server.repository.ActivePlayerRepositoryTestStub;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {GameServiceImpl.class,
        GameTable.class,
        ActivePlayerRepositoryTestStub.class,
        GameConfig.class,
        CardTable.class})
public class GameServiceTest {

    private GameService mGameService;
    private ActivePlayerRepositoryTestStub mActivePlayerRepository;

    @Autowired
    public void setGameService(GameService gameService, ActivePlayerRepositoryTestStub activePlayerRepository) {
        this.mGameService = gameService;
        this.mActivePlayerRepository = activePlayerRepository;
    }

    @Test
    public void testInitGame() {
        // Fail because of null gameId
        Object[] gameId = null;
        StatusCode statusCode = mGameService.initGame(mActivePlayerRepository.NEW_TO_GAME_PLAYER_ID, gameId);
        assertEquals(statusCode, StatusCode.FAIL);

        // Fail because player not online
        gameId = new Object[1];
        statusCode = mGameService.initGame(-1L, gameId);
        assertEquals(statusCode, StatusCode.FAIL);

        // Fail because of player already attached to another game
        statusCode = mGameService.initGame(mActivePlayerRepository.GAME_ASSIGNED_PLAYER_ID, gameId);
        assertEquals(statusCode, StatusCode.FAIL);

        // Success; check status code and game id
        statusCode = mGameService.initGame(mActivePlayerRepository.NEW_TO_GAME_PLAYER_ID, gameId);
        assertEquals(statusCode, StatusCode.SUCCESS);
        assertTrue((Long) gameId[0] >= 0L);
    }

    @Test
    public void testInteractGame() {
        // Successfully create a game
        Object[] gameId = new Object[1];
        StatusCode statusCode = mGameService.initGame(mActivePlayerRepository.NEW_TO_GAME_PLAYER_ID, gameId);
        assertEquals(statusCode, StatusCode.SUCCESS);
        assertTrue((Long) gameId[0] >= 0L);

        // Fail because of wrong game id
        Short cardNo = (short) -1;
        List<GameEnvironment> gameEnvironmentList = new ArrayList<>();
        statusCode = mGameService.interactGame(mActivePlayerRepository.NEW_TO_GAME_PLAYER_ID,
                (1L + (Long)gameId[0]),
                cardNo,
                gameEnvironmentList);
        assertEquals(statusCode, StatusCode.FAIL);

        // Fail because of wrong card no
        statusCode = mGameService.interactGame(mActivePlayerRepository.GAME_ASSIGNED_PLAYER_ID,
                (Long)gameId[0],
                (short) 52,
                gameEnvironmentList);
        assertEquals(statusCode, StatusCode.FAIL);

        // Fail because of wrong gameEnvironmentList
        statusCode = mGameService.interactGame(mActivePlayerRepository.GAME_ASSIGNED_PLAYER_ID,
                (Long)gameId[0],
                cardNo,
                null);
        assertEquals(statusCode, StatusCode.FAIL);

        // Make the initial move successfully
        statusCode = mGameService.interactGame(mActivePlayerRepository.GAME_ASSIGNED_PLAYER_ID,
                (Long)gameId[0],
                cardNo,
                gameEnvironmentList);
        assertEquals(statusCode, StatusCode.SUCCESS);

        GameEnvironment playerEnv = gameEnvironmentList.get(0);
        GameEnvironment pcEnv = gameEnvironmentList.get(1);

        assertEquals(playerEnv.getMHandCards().size(), 4);
        assertEquals(pcEnv.getMNoHandCards(), Optional.of((short) 4).get());
        assertEquals(playerEnv.getMMiddleCards().size(), pcEnv.getMMiddleCards().size());
        assertEquals(playerEnv.getMScores().get(0), Short.valueOf((short) 0));
        assertEquals(pcEnv.getMScores().get(0), Short.valueOf((short) 0));
        assertEquals(playerEnv.getMScores().get(1), Short.valueOf((short) 0));
        assertEquals(pcEnv.getMScores().get(1), Short.valueOf((short) 0));
        assertEquals(playerEnv.getMIsPisti(), false);
        assertEquals(pcEnv.getMIsPisti(), false);
    }

}
