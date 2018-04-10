package edu.uab.cvc.huntingwords.presenters;


import edu.uab.cvc.huntingwords.screens.views.LoginView;

/**
 * Created by carlosb on 10/04/18.
 */

public class LoginPresenterImpl implements LoginPresenter {
    private final LoginView view;
    public LoginPresenterImpl(LoginView login) {
        this.view = login;
    }

    @Override
    public boolean login(String username, String passw) {
        this.view.updateLogin(username);
        return false;
    }

    @Override
    public boolean signin(String username, String passw) {
        this.view.updateLogin(username);
        return false;
    }
}
