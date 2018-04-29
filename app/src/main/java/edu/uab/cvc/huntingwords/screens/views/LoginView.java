package edu.uab.cvc.huntingwords.screens.views;

/**
 * Created by carlosb on 10/04/18.
 */

public interface LoginView {

    //TODO add updates
    void updateLogin(String username);

    void updateScore(int matchScore, int diffScore);

    void setUpLoginParameters(String username, String passw);

    void errorLogin();
}
