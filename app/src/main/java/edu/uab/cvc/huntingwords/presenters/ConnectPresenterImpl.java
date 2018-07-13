package edu.uab.cvc.huntingwords.presenters;



import android.app.FragmentManager;
import android.app.FragmentTransaction;

import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.application.AppController;
import edu.uab.cvc.huntingwords.presenters.callbacks.ConnectCallback;
import edu.uab.cvc.huntingwords.screens.fragments.Connect;
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
    public boolean login(final String username, String passw) {
        ConnectCallback callback = new ConnectCallback() {
            @Override
            public void updateLogin(String username, String password) {
                view.setUpLoginParameters(username,password);
                view.updateLogin(username);
                view.goToInit();
            }

            @Override
            public void updateScore(Integer matchScore, Integer diffScore) {
                view.setUpScoreParameters(matchScore,diffScore);
            }

            @Override
            public void error() {
                view.errorLogin();

            }
        };
        new Login(callback).execute(username,passw);
        new GetRanking(callback).execute(username);
        return true;
    }


    @Override
    public boolean signin(String username, String passw) {
        ConnectCallback callback = new ConnectCallback() {
            @Override
            public void updateLogin(String username, String password) {
                view.setUpLoginParameters(username,password);
                view.updateLogin(username);
            }

            @Override
            public void updateScore(Integer matchScore, Integer diffScore) {
                view.setUpScoreParameters(matchScore,diffScore);
            }

            @Override
            public void error() {
                view.errorLogin();

            }
        };
        new InsertUser(callback).execute(username,passw);
        return true;
    }
}
