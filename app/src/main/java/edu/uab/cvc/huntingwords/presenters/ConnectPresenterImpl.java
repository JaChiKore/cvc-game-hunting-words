package edu.uab.cvc.huntingwords.presenters;

import edu.uab.cvc.huntingwords.application.AppController;
import edu.uab.cvc.huntingwords.presenters.callbacks.ConnectCallback;
import edu.uab.cvc.huntingwords.screens.views.LoginView;
import edu.uab.cvc.huntingwords.tasks.GetRanking;
import edu.uab.cvc.huntingwords.tasks.InsertUser;
import edu.uab.cvc.huntingwords.tasks.Login;

/**
 * Created by carlosb on 10/04/18.
 */

public class ConnectPresenterImpl implements ConnectPresenter {


    private final LoginView view;

    public ConnectPresenterImpl(LoginView login){
                /* IT MUST BE FIRST */
        AppController.getComponent().inject(this);
        this.view = login;
    }




    @Override
    public boolean login(final String username, String password) {
        ConnectCallback callback = new ConnectCallback() {
            @Override
            public void updateLogin(String username) {
                view.setUpLoginParameters(username);
                view.updateLogin(username);
            }

            @Override
            public void updateScore(Integer matchScore, Integer diffScore, Integer jumpScore, Integer matchLevel, Integer diffLevel, Integer jumpLevel) {
                view.setUpScoreParameters(matchScore, diffScore, jumpScore, matchLevel, diffLevel, jumpLevel);
            }

            @Override
            public void setUpScores(Boolean playClicked) {
                view.updateMatchScore();
                view.updateDiffScore();
            }

            @Override
            public void error() {
                view.errorLogin();

            }
        };
        new Login(callback).execute(username, password);
        new GetRanking(callback,false).execute(username);
        view.goToInit();
        return true;
    }

    @Override
    public boolean signin(String username, String password) {
        ConnectCallback callback = new ConnectCallback() {
            @Override
            public void updateLogin(String username) {
                view.setUpLoginParameters(username);
                view.updateLogin(username);
                view.goToInit();
            }

            @Override
            public void updateScore(Integer matchScore, Integer diffScore, Integer jumpScore, Integer matchLevel, Integer diffLevel, Integer jumpLevel) {
                view.setUpScoreParameters(matchScore, diffScore, jumpScore, matchLevel, diffLevel, jumpLevel);
            }

            @Override
            public void setUpScores(Boolean playClicked) {
                view.updateMatchScore();
                view.updateDiffScore();
            }

            @Override
            public void error() {
                view.errorSignin();

            }
        };
        new InsertUser(callback).execute(username, password);
        return true;
    }
}
