package edu.uab.cvc.huntingwords.presenters.callbacks;

/**
 * Created by carlosb on 18/04/18.
 */

public interface ConnectCallback {
    void updateLogin(String username);

    void updateScore(Integer matchScore, Integer diffScore, Integer matchLevel, Integer diffLevel);

    void setUpScores(Boolean playClicked);

    void error();
}
