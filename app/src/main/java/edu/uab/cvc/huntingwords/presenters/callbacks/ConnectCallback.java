package edu.uab.cvc.huntingwords.presenters.callbacks;

/**
 * Created by carlosb on 18/04/18.
 */

public interface ConnectCallback {
    void updateLogin(String username, String password);


    void updateScore(Integer matchScore, Integer diffScore);

    void error();
}
