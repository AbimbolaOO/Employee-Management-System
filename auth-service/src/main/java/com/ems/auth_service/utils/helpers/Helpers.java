package com.ems.auth_service.utils.helpers;

import java.security.SecureRandom;


public class Helpers {
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "@#$%^&*()-_+=<>?";
    private static final String ALL_CHARS = UPPERCASE + LOWERCASE + DIGITS + SPECIAL_CHARS;
    private static final SecureRandom RANDOM = new SecureRandom();

    private Helpers() {
    }

    public static String generatePassword(int length) {
        if (length < 4) throw new IllegalArgumentException("Password length must be at least 4");

        StringBuilder password = new StringBuilder();
        password.append(UPPERCASE.charAt(RANDOM.nextInt(UPPERCASE.length())));
        password.append(LOWERCASE.charAt(RANDOM.nextInt(LOWERCASE.length())));
        password.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        password.append(SPECIAL_CHARS.charAt(RANDOM.nextInt(SPECIAL_CHARS.length())));

        for (int i = 4; i < length; i++) {
            password.append(ALL_CHARS.charAt(RANDOM.nextInt(ALL_CHARS.length())));
        }

        return shuffleString(password.toString());
    }

    private static String shuffleString(String input) {
        char[] array = input.toCharArray();
        for (int i = array.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
        return new String(array);
    }

    public static void main(String[] args) {
        System.out.println("Generated Password: " + generatePassword(12));
    }

    public static String getRefreshTokenBlackListKey(String id) {
        return "token:refresh:blacklist:" + id;
    }

    public static String getAccessTokenBlackListKey(String id) {
        return "token:access:blacklist:" + id;
    }


}
