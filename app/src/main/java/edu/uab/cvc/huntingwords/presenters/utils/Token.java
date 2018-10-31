package edu.uab.cvc.huntingwords.presenters.utils;

import java.util.Random;
import static edu.uab.cvc.huntingwords.Utils.TOKEN_LENGTH;

public class Token {
    private static Token instance = null;
    private String token;
    private static final Random random = new Random();
    private static final String CHARS = "abcdefghijkmnopqrstuvwxyzABCDEFGHJKLMNOPQRSTUVWXYZ234567890!$%&@#?¿";

    private Token() {}

    public static Token getInstance() {
        if (instance == null) {
            instance = new Token();
        }
        return (instance);
    }

    public String getToken() {
        return token;
    }

    public void generateToken() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        this.token = token.toString();
    }

    public void setToken(String token) {
        this.token = token;
    }
}
