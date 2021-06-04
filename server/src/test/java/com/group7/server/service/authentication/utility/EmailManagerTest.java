package com.group7.server.service.authentication.utility;

import com.group7.server.definitions.game.Game;
import com.group7.server.definitions.game.GameConfig;
import com.group7.server.definitions.game.GameEnvironment;
import com.group7.server.definitions.game.GameStrategyLevel1;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        EmailManager.class,
        JavaMailSenderImpl.class
})
public class EmailManagerTest {

    private EmailManager mEmailManager;

    @Autowired
    public void setEmailManager(EmailManager emailManager){
        this.mEmailManager = emailManager;
    }

    @Test
    public void testSendResetPasswordEmail_Fail_NotAMail() {
        boolean flag = false;
        try {
            mEmailManager.sendResetPasswordEmail(1L, "a", "a");
        } catch (Exception e) {
            flag = true;
        } finally {
            assertTrue(flag);
        }
    }

}
