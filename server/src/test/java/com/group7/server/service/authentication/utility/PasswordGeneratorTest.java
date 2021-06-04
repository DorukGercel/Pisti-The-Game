package com.group7.server.service.authentication.utility;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        PasswordGenerator.class
})
public class PasswordGeneratorTest {
    @Test
    public void testGetRandomPassword_Success() {
        assertEquals(10, PasswordGenerator.getRandomPassword().length());
    }
}
