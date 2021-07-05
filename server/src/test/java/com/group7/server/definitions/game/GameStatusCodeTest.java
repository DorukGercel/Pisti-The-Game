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
import static org.junit.Assert.assertFalse;

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
public class GameStatusCodeTest {

    @Test
    public void testConvertStrToGameStatusCode_Success() {
        assertEquals(Game.GameStatusCode.CHEAT_LEVEL_UP, Game.GameStatusCode.convertStrToGameStatusCode("CHEAT_LEVEL_UP"));
        assertEquals(Game.GameStatusCode.NORMAL, Game.GameStatusCode.convertStrToGameStatusCode("NORMAL"));
        assertEquals(Game.GameStatusCode.NORMAL_MULTI, Game.GameStatusCode.convertStrToGameStatusCode("NORMAL_MULTI"));
        assertEquals(Game.GameStatusCode.GAME_OVER_WIN, Game.GameStatusCode.convertStrToGameStatusCode("GAME_OVER_WIN"));
    }

    @Test
    public void testConvertGameStatusCodeToStr_Success() {
        assertEquals("CHEAT_LEVEL_UP", Game.GameStatusCode.convertGameStatusCodeToStr(Game.GameStatusCode.CHEAT_LEVEL_UP));
        assertEquals("NORMAL", Game.GameStatusCode.convertGameStatusCodeToStr(Game.GameStatusCode.NORMAL));
        assertEquals("NORMAL_MULTI", Game.GameStatusCode.convertGameStatusCodeToStr(Game.GameStatusCode.NORMAL_MULTI));
        assertEquals("GAME_OVER_WIN", Game.GameStatusCode.convertGameStatusCodeToStr(Game.GameStatusCode.GAME_OVER_WIN));
    }

    @Test
    public void testIsGameLevelSwitching_Success() {
        assertTrue(Game.GameStatusCode.isGameLevelSwitching(Game.GameStatusCode.LEVEL_UP));
        assertTrue(Game.GameStatusCode.isGameLevelSwitching(Game.GameStatusCode.CHEAT_LEVEL_UP));
        assertTrue(Game.GameStatusCode.isGameLevelSwitching(Game.GameStatusCode.GAME_OVER_WIN));
    }

    @Test
    public void testIsGameLevelSwitching_Fail() {
        assertFalse(Game.GameStatusCode.isGameLevelSwitching(Game.GameStatusCode.NORMAL));
        assertFalse(Game.GameStatusCode.isGameLevelSwitching(Game.GameStatusCode.NORMAL_MULTI));
        assertFalse(Game.GameStatusCode.isGameLevelSwitching(Game.GameStatusCode.LOST));
    }

}

