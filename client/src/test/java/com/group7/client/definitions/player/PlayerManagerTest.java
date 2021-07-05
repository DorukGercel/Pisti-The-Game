package com.group7.client.definitions.player;

import com.group7.client.definitions.common.StatusCode;
import com.group7.client.definitions.network.NetworkManager;
import com.group7.client.dto.common.CommonResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        PlayerManager.class
})
public class PlayerManagerTest {

    private PlayerManager mPlayerManager;

    @Autowired
    public void setManager(PlayerManager playerManager){
        this.mPlayerManager = playerManager;
    }

    @Test
    public void testSetup() {
        mPlayerManager.setGameId(1L);
        mPlayerManager.setSessionId(2L);
        mPlayerManager.setUsername("DODO");
        mPlayerManager.setOpponentUsername("MOMO");

        assertEquals(java.util.Optional.of(1L).get(), mPlayerManager.getGameId());
        assertEquals(java.util.Optional.of(2L).get(), mPlayerManager.getSessionId());
        assertEquals("DODO", mPlayerManager.getUsername());
        assertEquals("MOMO", mPlayerManager.getOpponentUsername());
    }

}
