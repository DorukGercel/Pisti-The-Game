package com.group7.server.service.authentication.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

/**
 * Class which is responsible for sending email for reset password
 **/
@Component
public class EmailManager {
    /** Site URL*/
    private final String SITE_URL;
    /** Email address of the application*/
    private final String EMAIL_FROM ;
    /** Name of the company*/
    private final String COMPANY_NAME;
    /** Email sending API*/
    private final JavaMailSender mMailSender;

    @Autowired
    public EmailManager(JavaMailSender mailSender,
                        @Value("${spring.mail.site_url}") String siteUrl,
                        @Value("${spring.mail.username}") String emailAddress,
                        @Value("${spring.mail.company_name}") String companyName) {
        this.SITE_URL = siteUrl;
        this.EMAIL_FROM = emailAddress;
        this.COMPANY_NAME = companyName;
        this.mMailSender = mailSender;
    }

    /** Send email for reset password*/
    public void sendResetPasswordEmail(Long playerId, String playerEmail, String playerName) throws MessagingException, UnsupportedEncodingException {
        String toAddress = playerEmail;
        String fromAddress = EMAIL_FROM;
        String senderName = COMPANY_NAME;
        String subject = "Reset your password!";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to reset your password:<br>"
                + "<h3><a href='[[URL]]' target='_self'>RESET PASSWORD</a></h3>"
                + "Thank you,<br>"
                + COMPANY_NAME + ".";

        MimeMessage message = mMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", playerName);
        String verifyURL = SITE_URL + "/api/player/resetPassword/" + playerId.toString();
        //String verifyURL = "https://www.w3schools.com";

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        System.out.println(content);

        mMailSender.send(message);
    }
}
