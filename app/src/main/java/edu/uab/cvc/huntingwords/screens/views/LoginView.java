package edu.uab.cvc.huntingwords.screens.views;

/**
 * Created by carlosb on 10/04/18.
 */

public interface LoginView {

    void updateLogin(String username);
    void updateMatchScore();
    void updateDiffScore();
    void goToInit();

    void setUpLoginParameters(String username, String passw);

    void setUpScoreParameters(Integer scoreMatch, Integer scoreDiff);

    void errorLogin();

    void errorSignin();
}
