package edu.uab.cvc.huntingwords.screens.views;

/**
 * Created by carlosb on 10/04/18.
 */

public interface LoginView {

    void updateLogin(String username);
    void updateMatchScore();
    void updateDiffScore();
    void goToInit();

    void setUpLoginParameters(String username);

    void setUpScoreParameters(Integer scoreMatch, Integer scoreDiff, Integer levelMatch, Integer levelDiff);

    void errorLogin();

    void errorSignin();
}
