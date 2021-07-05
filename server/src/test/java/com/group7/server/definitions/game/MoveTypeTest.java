package com.group7.server.definitions.game;

import com.group7.server.repository.ActivePlayerRepositoryTestStub;
import com.group7.server.repository.LeaderboardRecordRepositoryTestStub;
import com.group7.server.repository.PlayerRepositoryTestStub;
import com.group7.server.service.game.GameServiceImpl;
import com.group7.server.service.leaderboard.LeaderboardRecordServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

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
        Game.class}
)

public class MoveTypeTest {
    @Test
    public void testConvertStrToMoveType_Success() {
        assertEquals(Game.MoveType.INITIAL, Game.MoveType.convertStrToMoveType("INITIAL"));
        assertEquals(Game.MoveType.REDEAL, Game.MoveType.convertStrToMoveType("REDEAL"));
        assertEquals(Game.MoveType.PASS, Game.MoveType.convertStrToMoveType("PASS"));
        assertEquals(Game.MoveType.NONE, Game.MoveType.convertStrToMoveType("PENALTY"));
    }

    @Test
    public void testConvertMoveTypeToStr_Success() {
        assertEquals("INITIAL", Game.MoveType.convertMoveTypeToStr(Game.MoveType.INITIAL));
        assertEquals("REDEAL", Game.MoveType.convertMoveTypeToStr(Game.MoveType.REDEAL));
        assertEquals("PASS", Game.MoveType.convertMoveTypeToStr(Game.MoveType.PASS));
        assertEquals("NONE", Game.MoveType.convertMoveTypeToStr(Game.MoveType.NONE));
    }

    @Test
    public void testIsPassMove_Success() {
        assertTrue(Game.MoveType.isPassMove(Game.MoveType.PASS));
        assertFalse(Game.MoveType.isPassMove(Game.MoveType.RESTART));
    }

    @Test
    public void isChallengeRelatedMove_Success() {
        assertTrue(Game.MoveType.isChallengeRelatedMove(Game.MoveType.CHALLENGE));
        assertTrue(Game.MoveType.isChallengeRelatedMove(Game.MoveType.NOT_CHALLENGE));
        assertTrue(Game.MoveType.isChallengeRelatedMove(Game.MoveType.CHALLENGE_SUCCESS));
        assertTrue(Game.MoveType.isChallengeRelatedMove(Game.MoveType.CHALLENGE_FAIL));
    }

    @Test
    public void isChallengeRelatedMove_Fail() {
        assertFalse(Game.MoveType.isChallengeRelatedMove(Game.MoveType.RESTART));
        assertFalse(Game.MoveType.isChallengeRelatedMove(Game.MoveType.READ));
        assertFalse(Game.MoveType.isChallengeRelatedMove(Game.MoveType.CARD));
        assertFalse(Game.MoveType.isChallengeRelatedMove(Game.MoveType.NONE));
    }

    @Test
    public void isIsSimulateMoveType_Success() {
        assertTrue(Game.MoveType.isSimulateMoveType(Game.MoveType.CARD));
        assertTrue(Game.MoveType.isSimulateMoveType(Game.MoveType.NOT_CHALLENGE));
        assertTrue(Game.MoveType.isSimulateMoveType(Game.MoveType.BLUFF));
        assertTrue(Game.MoveType.isSimulateMoveType(Game.MoveType.READ));
        assertTrue(Game.MoveType.isSimulateMoveType(Game.MoveType.PASS));
    }

    @Test
    public void isIsSimulateMoveType_Fail() {
        assertFalse(Game.MoveType.isSimulateMoveType(Game.MoveType.CHALLENGE_FAIL));
        assertFalse(Game.MoveType.isSimulateMoveType(Game.MoveType.CHALLENGE_SUCCESS));
        assertFalse(Game.MoveType.isSimulateMoveType(Game.MoveType.NONE));
    }

    @Test
    public void isMoveWithoutCard_Success() {
        assertTrue(Game.MoveType.isMoveWithoutCard(Game.MoveType.INITIAL));
        assertTrue(Game.MoveType.isMoveWithoutCard(Game.MoveType.REDEAL));
        assertTrue(Game.MoveType.isMoveWithoutCard(Game.MoveType.CHALLENGE));
        assertTrue(Game.MoveType.isSimulateMoveType(Game.MoveType.NOT_CHALLENGE));
        assertTrue(Game.MoveType.isSimulateMoveType(Game.MoveType.PASS));
    }

    @Test
    public void isMoveWithoutCard_Fail() {
        assertFalse(Game.MoveType.isMoveWithoutCard(Game.MoveType.CARD));
        assertFalse(Game.MoveType.isMoveWithoutCard(Game.MoveType.BLUFF));
    }
}
