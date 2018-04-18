package edu.uab.cvc.huntingwords.models;

import android.util.Pair;

/**
 * Created by carlosb on 18/04/18.
 */

public class UserInformation {
    private String username;
    private int gameScore;
    private int differenceScore;

    public UserInformation() {
        this.username = new String();
        this.gameScore = 0;
        this.differenceScore = 0;
    }

    public int getDifferenceScore() {
        return differenceScore;
    }

    public void setDifferenceScore(int differenceScore) {
        this.differenceScore = differenceScore;
    }

    public int getGameScore() {
        return gameScore;
    }

    public void setGameScore(int gameScore) {
        this.gameScore = gameScore;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(int matchScore, int difScore) {
        this.gameScore = matchScore;
        this.differenceScore = difScore;
    }
}
