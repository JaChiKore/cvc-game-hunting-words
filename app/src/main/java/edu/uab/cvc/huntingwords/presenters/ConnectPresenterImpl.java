package edu.uab.cvc.huntingwords.presenters;



import javax.inject.Inject;

import edu.uab.cvc.huntingwords.application.AppController;
import edu.uab.cvc.huntingwords.models.UserInformation;
import edu.uab.cvc.huntingwords.presenters.callbacks.ConnectCallback;
import edu.uab.cvc.huntingwords.screens.views.LoginView;
import edu.uab.cvc.huntingwords.tasks.GetRanking;
import edu.uab.cvc.huntingwords.tasks.InsertUser;
import edu.uab.cvc.huntingwords.tasks.Login;

/**
 * Created by carlosb on 10/04/18.
 */

public class ConnectPresenterImpl implements ConnectPresenter {

    @Inject
    UserInformation userInfo;

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
                userInfo.setUsername(username);
            }

            @Override
            public void updateScore(Integer matchScore, Integer diffScore) {
                userInfo.setScore(matchScore,diffScore);
                view.updateScore(matchScore,diffScore);
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
                userInfo.setUsername(username);
            }

            @Override
            public void updateScore(Integer matchScore, Integer diffScore) {
                userInfo.setScore(matchScore,diffScore);
                view.updateScore(matchScore,diffScore);
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
