package edu.uab.cvc.huntingwords.application.builder;

import javax.inject.Singleton;

import dagger.Component;
import edu.uab.cvc.huntingwords.models.UserInformation;
import edu.uab.cvc.huntingwords.presenters.ConnectPresenterImpl;
import edu.uab.cvc.huntingwords.presenters.DifferenceGamePresenterImpl;
import edu.uab.cvc.huntingwords.presenters.MatchGamePresenterImpl;
import edu.uab.cvc.huntingwords.presenters.PlayPresenter;
import edu.uab.cvc.huntingwords.presenters.PlayPresenterImpl;
import edu.uab.cvc.huntingwords.screens.fragments.DifferenceGame;
import edu.uab.cvc.huntingwords.screens.fragments.MatchGame;
import edu.uab.cvc.huntingwords.screens.views.MatchView;
import edu.uab.cvc.huntingwords.utils.rx.RxSchedulers;

/**
 * Created by ygharsallah on 30/03/2017.
 */
@Component(modules = {NetworkModule.class, AppContextModule.class, RxModule.class, GameInfoModule.class, UserInfoModule.class})
@Singleton
public interface AppComponent {
    RxSchedulers rxSchedulers();

    void inject (PlayPresenterImpl presenter);
    void inject(MatchGamePresenterImpl matchGamePresenter);
    void inject(ConnectPresenterImpl connectPresenter);
    void inject(DifferenceGamePresenterImpl differenceGamePresenter);
    void inject(MatchGame matchGame);

    void inject(DifferenceGame frag);
}
