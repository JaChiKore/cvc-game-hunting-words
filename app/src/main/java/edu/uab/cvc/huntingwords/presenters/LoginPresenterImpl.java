package edu.uab.cvc.huntingwords.presenters;


import edu.uab.cvc.huntingwords.models.UserInfo;
import edu.uab.cvc.huntingwords.screens.views.LoginView;
import edu.uab.cvc.huntingwords.tasks.Login;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by carlosb on 10/04/18.
 */

public class LoginPresenterImpl implements LoginPresenter {
    private final LoginView view;
    public LoginPresenterImpl(LoginView login) {
        this.view = login;
    }

    @Override
    public boolean login(final String username, String passw) {
        Boolean isLogged = false;
        Observable.just(isLogged)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    logged -> {
                        if (logged) {
                            this.view.updateLogin(username);
                        }
                    }

                );

        new Login(isLogged).execute("test","test");
        return true;
    }

    @Override
    public boolean signin(String username, String passw) {
        this.view.updateLogin(username);
        return false;
    }
}
