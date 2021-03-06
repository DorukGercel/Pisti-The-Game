package com.group7.server.service.authentication;

import com.group7.server.definitions.common.StatusCode;
import com.group7.server.model.ActivePlayer;
import com.group7.server.model.Player;
import com.group7.server.repository.ActivePlayerRepository;
import com.group7.server.repository.ActivePlayerRepositoryTestStub;
import com.group7.server.repository.PlayerRepositoryTestStub;
import com.group7.server.security.UserDetailsManagerImpl;
import com.group7.server.security.UserDetailsServiceImpl;
import com.group7.server.security.config.JwtRequestFilter;
import com.group7.server.security.config.PasswordEncoderConfig;
import com.group7.server.security.config.SecurityConfig;
import com.group7.server.service.authentication.utility.EmailManager;
import com.group7.server.service.authentication.utility.PasswordGenerator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
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
        ActivePlayerRepositoryTestStub.class,
        EmailManager.class,
        JavaMailSenderImpl.class,
        PasswordGenerator.class})
@WebAppConfiguration
public class PlayerServiceTest {

    private PlayerService mPlayerService;
    private ActivePlayerRepository mActivePlayerRepository;

    @Autowired
    void setPlayerService(PlayerService playerService, ActivePlayerRepository activePlayerRepository) {
        this.mPlayerService = playerService;
        this.mActivePlayerRepository = activePlayerRepository;
    }

    @Test
    public void testRegister(){
        // Create a user that can be successfully registered
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
        //Logout loged in user
        StatusCode statusCode = mPlayerService.logout(-1L);
        assertEquals(statusCode, StatusCode.SUCCESS);

        // Can't logout as no login performed
        mActivePlayerRepository.save(new ActivePlayer());
        statusCode = mPlayerService.logout(1L);
        assertEquals(statusCode, StatusCode.SUCCESS);
    }
}

