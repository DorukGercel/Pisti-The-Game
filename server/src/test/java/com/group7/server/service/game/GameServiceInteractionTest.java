package com.group7.server.service.game;

import com.group7.server.definitions.common.StatusCode;
import com.group7.server.definitions.game.Game;
import com.group7.server.definitions.game.GameConfig;
import com.group7.server.definitions.game.GameEnvironment;
import com.group7.server.definitions.game.GameTable;
import com.group7.server.repository.ActivePlayerRepositoryTestStub;
import com.group7.server.repository.LeaderboardRecordRepositoryTestStub;
import com.group7.server.repository.PlayerRepositoryTestStub;
import com.group7.server.service.leaderboard.LeaderboardRecordService;
import com.group7.server.repository.*;
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
import java.util.Optional;

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
        LeaderboardRecordRepositoryTestStub.class,
        LeaderboardRecordServiceImpl.class})

public class GameServiceInteractionTest {

    private GameService mGameService;
    private ActivePlayerRepositoryTestStub mActivePlayerRepository;
    private GameConfig.CardTable mCardTable;
    private static Object[] mGameId;
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
        mGameService.removeGame(mActivePlayerRepository.GAME_ASSIGNED_PLAYER_ID, (Long) mGameId[0]);
    }

    @Test
    public void testInteractGame_Initial_Fail_GameId() {
        // Fail because of wrong game id
        Short cardNo = (short) -1;
        List<GameEnvironment> gameEnvironmentList = new ArrayList<>();
        List<Object> gameStatus = new ArrayList<>();
        StatusCode statusCode = mGameService.interactGame(mSessionId,
                (1L + (Long) mGameId[0]),
                cardNo,
                Game.MoveType.INITIAL,
                Game.GameStatusCode.NORMAL,
                gameEnvironmentList,
                gameStatus);
        assertEquals(statusCode, StatusCode.FAIL);
    }

    @Test
    public void testInteractGame_Initial_Fail_GameEnv() {
        // Fail because of wrong gameEnvironmentList
        Short cardNo = (short) -1;
        List<Object> gameStatus = new ArrayList<>();
        StatusCode statusCode = mGameService.interactGame(mSessionId,
                (Long) mGameId[0],
                cardNo,
                Game.MoveType.INITIAL,
                Game.GameStatusCode.NORMAL,
                null,
                gameStatus);
        assertEquals(statusCode, StatusCode.FAIL);
    }

    @Test
    public void testInteractionGame_Initial_Fail_GameStatus() {
        // Fail because of wrong gameStatus
        Short cardNo = (short) -1;
        List<GameEnvironment> gameEnvironmentList = new ArrayList<>();
        StatusCode statusCode = mGameService.interactGame(mSessionId,
                (Long) mGameId[0],
                cardNo,
                Game.MoveType.INITIAL,
                Game.GameStatusCode.NORMAL,
                gameEnvironmentList,
                null);
        assertEquals(statusCode, StatusCode.FAIL);
    }

    @Test
    public void testInteractGame_Initial_Fail_MoveType() {
        // Fail because of wrong move type
        Short cardNo = (short) -1;
        List<GameEnvironment> gameEnvironmentList = new ArrayList<>();
        List<Object> gameStatus = new ArrayList<>();
        StatusCode statusCode = mGameService.interactGame(mSessionId,
                (Long) mGameId[0],
                cardNo,
                Game.MoveType.CARD,
                Game.GameStatusCode.NORMAL,
                gameEnvironmentList,
                gameStatus);
        assertEquals(statusCode, StatusCode.FAIL);
    }

    @Test
    public void testInteractGame_Initial_Success() {
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

    @Test
    public void testInteractGame_Card_Success() {
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

        GameEnvironment playerEnv = gameEnvironmentList.get(0);

        // Decide a move to make
        List<Short> handDeck = playerEnv.getMHandCards();
        List<Short> middleDeck = playerEnv.getMMiddleCards();
        Short decidedCardNo = decideCard(handDeck, middleDeck);

        /** Below both the movement and card decision by pc are tested*/

        // Make the card move
        List<GameEnvironment> newGameEnvironments = new ArrayList<>();
        statusCode = mGameService.interactGame(mSessionId,
                (Long) mGameId[0],
                decidedCardNo,
                Game.MoveType.CARD,
                Game.GameStatusCode.NORMAL,
                newGameEnvironments,
                gameStatus);

        // Check status code
        assertEquals(statusCode, StatusCode.SUCCESS);

        GameEnvironment newPlayerEnv = newGameEnvironments.get(0);
        GameEnvironment newPcEnv = newGameEnvironments.get(1);

        // Check number of cards of both player and pc
        assertEquals(newPlayerEnv.getMHandCards().size(), 3);
        assertEquals(newPcEnv.getMNoHandCards(), Optional.of((short) 3).get());
        // Check what happened to decided card in the game
        assertTrue(newPlayerEnv.getMMiddleCards().contains(decidedCardNo) || newPlayerEnv.getMMiddleCards().isEmpty());
        // Check player's score incremented or at least the same
        assertTrue(newPlayerEnv.getMScores().get(0) >= playerEnv.getMScores().get(0));
    }

    /** Helper functions are defined below.*/
    private Short decideCard(List<Short> handDeck, List<Short> middleDeck) {
        for (Short cardNo : handDeck) {
            if(isMatchedCard(mCardTable.getCard(cardNo),middleDeck)) {
                return cardNo;
            }
        }
        return handDeck.get(handDeck.size()-1);
    }

    private boolean isMatchedCard(GameConfig.Card playerCard, List<Short> middleDeck) {
        //Extract the face up card
        GameConfig.Card faceUpCard = mCardTable.getCard(middleDeck.get(middleDeck.size() - 1));

        // True if the player card is jack or the ranks of the cards match
        return (playerCard.getMRank().equals(GameConfig.Card.Rank.JACK) || playerCard.getMRank().equals(faceUpCard.getMRank()));
    }
}
