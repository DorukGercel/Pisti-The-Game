package com.group7.server.service.authentication;

import com.group7.server.definitions.StatusCode;
import com.group7.server.model.Player;
import com.group7.server.repository.ActivePlayerRepositoryTestStub;
import com.group7.server.repository.PlayerRepositoryTestStub;
import com.group7.server.security.UserDetailsManagerImpl;
import com.group7.server.security.UserDetailsServiceImpl;
import com.group7.server.security.config.JwtRequestFilter;
import com.group7.server.security.config.PasswordEncoderConfig;
import com.group7.server.security.config.SecurityConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {PlayerServiceImpl.class,
        AuthenticationServiceImpl.class,
        UserDetailsServiceImpl.class,
        UserDetailsManagerImpl.class,
        PasswordEncoderConfig.class,
        SecurityConfig.class,
        JwtRequestFilter.class,
        PlayerRepositoryTestStub.class,
        ActivePlayerRepositoryTestStub.class})
@WebAppConfiguration

public class PlayerServiceTest {

    private PlayerService mPlayerService;

    @Autowired
    void setPlayerService(PlayerService playerService) {
        this.mPlayerService = playerService;
    }

    @Test
    public void testRegister(){
        // Creat a user that can be successfully registered
        Player testPlayer = new Player("Doruk", "lolValley", "d@g.com");
        StatusCode statusCode = mPlayerService.register(testPlayer);
        assertEquals(statusCode, StatusCode.SUCCESS);

        // Create a user with null values
        testPlayer = new Player();
        statusCode = mPlayerService.register(testPlayer);
        assertEquals(statusCode, StatusCode.FAIL);
    }

    @Test
    public void testLogin() {
        // Can't login because of empty credentials
        Player testPlayer = new Player("Doruk", "lolValley", "d@g.com");
        Object[] credentials = null;
        StatusCode statusCode = mPlayerService.login(testPlayer, credentials);
        assertEquals(statusCode, StatusCode.FAIL);

        // Can't login because of no db value
        credentials = new Object[2];
        statusCode = mPlayerService.login(testPlayer, credentials);
        assertEquals(statusCode, StatusCode.FAIL);
    }

    @Test
    public void testLogout() {
        // Can't logout as no login performed
        StatusCode statusCode = mPlayerService.logout(1L);
        assertEquals(statusCode, StatusCode.FAIL);

    }
}

