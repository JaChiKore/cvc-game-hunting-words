package edu.uab.cvc.huntingwords.screens.views;

/**
 * Created by carlosb on 10/04/18.
 */

public interface LoginView {

    //TODO add updates
    void updateLogin(String username);


    void setUpLoginParameters(String username, String passw);

    void setUpScoreParameters(Integer scoreMatch, Integer scoreDiff);

    void errorLogin();
}
