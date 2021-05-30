package com.group7.server.service.authentication.utility;

import org.springframework.stereotype.Component;

/**
 * Class which is responsible for generating random password
 **/
public class PasswordGenerator {
    /** Length of the password to be generated*/
    private static final int PASSWORD_LENGTH = 10;
    /** Create random password*/
    public static String getRandomPassword() {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder builder = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = (int) (AlphaNumericString.length() * Math.random());
            builder.append(AlphaNumericString.charAt(index));
        }
        return builder.toString();
    }
}
