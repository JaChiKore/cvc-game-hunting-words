package edu.uab.cvc.huntingwords.presenters;



import javax.inject.Inject;

import edu.uab.cvc.huntingwords.application.AppController;
import edu.uab.cvc.huntingwords.models.UserInformation;
import edu.uab.cvc.huntingwords.screens.views.LoginView;
import edu.uab.cvc.huntingwords.tasks.GetRanking;
import edu.uab.cvc.huntingwords.tasks.InsertUser;
import edu.uab.cvc.huntingwords.tasks.Login;

/**
 * Created by carlosb on 10/04/18.
 */

public class ConnectPresenterImpl implements ConnectPresenter, ConnectCallback {

    @Inject
    UserInformation userInfo;

    private final LoginView view;

    public ConnectPresenterImpl(LoginView login){
                /* IT MUST BE FIRST */
        AppController.getComponent().inject(this);
        this.view = login;
    }

    @Override
    public void updateLogin(String username, String password) {
        this.view.setUpLoginParameters(username,password);
        this.view.updateLogin(username);
        userInfo.setUsername(username);
    }

    @Override
    public void updateScore(Integer matchScore, Integer diffScore) {
        userInfo.setScore(matchScore,diffScore);
        this.view.updateScore(matchScore,diffScore);
    }


    @Override
    public boolean login(final String username, String passw) {
        new Login(this).execute(username,passw);
        new GetRanking(this).execute(username);
        return true;
    }


    @Override
    public boolean signin(String username, String passw) {
        new InsertUser(this).execute(username,passw);
        return true;
    }
}
