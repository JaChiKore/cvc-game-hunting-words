package edu.uab.cvc.huntingwords.screens.views;

/**
 * Created by carlosb on 10/04/18.
 */

public interface LoginView {

    void updateLogin(String username);
    void updateMatchScore();
    void updateDiffScore();
    void updateJumpScore();
    void goToInit();

    void setUpLoginParameters(String username);

    void setUpScoreParameters(Integer matchScore, Integer diffScore, Integer jumpScore, Integer matchLevel, Integer diffLevel, Integer jumpLevel);

    void errorLogin();

    void errorSignin();
}
