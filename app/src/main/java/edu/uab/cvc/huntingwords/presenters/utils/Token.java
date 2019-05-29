package edu.uab.cvc.huntingwords.presenters.utils;

public class Token {
    private static Token instance = null;
    private String token = "-1";

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

    public void setToken(String token) {
        this.token = token;
    }
}
