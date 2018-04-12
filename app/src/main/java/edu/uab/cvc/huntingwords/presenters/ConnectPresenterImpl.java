package edu.uab.cvc.huntingwords.presenters;


import edu.uab.cvc.huntingwords.screens.views.LoginView;
import edu.uab.cvc.huntingwords.tasks.InsertUser2;
import edu.uab.cvc.huntingwords.tasks.Login;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * Created by carlosb on 10/04/18.
 */

public class ConnectPresenterImpl implements ConnectPresenter {
    private final LoginView view;
    public ConnectPresenterImpl(LoginView login) {
        this.view = login;
    }

    @Override
    public boolean login(final String username, String passw) {
        BehaviorSubject<Boolean> subject = BehaviorSubject.create();
        subject.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).filter(logged -> logged == true)
                .subscribe(
                        log -> {
                            this.view.setUpLoginParameters(username,passw);
                            this.view.updateLogin(username);

                        }
                );

        new Login(subject).execute(username,passw);
        return true;
    }


    @Override
    public boolean signin(String username, String passw) {
        BehaviorSubject<Boolean> subject = BehaviorSubject.create();
        subject.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).filter(logged -> logged == true)
                .subscribe(
                        log -> {
                            this.view.setUpLoginParameters(username,passw);
                            this.view.updateLogin(username);

                        }
                );

        new InsertUser2(subject).execute(username,passw);
        return true;
    }
}
