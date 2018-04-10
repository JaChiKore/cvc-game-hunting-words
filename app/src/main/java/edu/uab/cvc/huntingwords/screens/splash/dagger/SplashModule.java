package edu.uab.cvc.huntingwords.screens.splash.dagger;

import dagger.Module;
import dagger.Provides;
import edu.uab.cvc.huntingwords.api.HeroApi;
import edu.uab.cvc.huntingwords.screens.splash.SplashScreenActivity;
import edu.uab.cvc.huntingwords.screens.splash.core.SplashModel;
import edu.uab.cvc.huntingwords.screens.splash.core.SplashPresenter;
import edu.uab.cvc.huntingwords.screens.splash.core.SplashView;
import edu.uab.cvc.huntingwords.utils.rx.RxSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by yassinegharsallah on 01/04/2017.
 */


@Module
public class SplashModule {


    @SplashScope
    @Provides
    SplashPresenter providePresenter(RxSchedulers schedulers, SplashModel model) {
        CompositeSubscription compositeSubscription = new CompositeSubscription();
        return new SplashPresenter(model, schedulers, compositeSubscription);
    }


    @SplashScope
    @Provides
    SplashView provideSplashView(SplashScreenActivity context) {
        return new SplashView(context);
    }


    @SplashScope
    @Provides
    SplashModel provideSplashModel(HeroApi api, SplashScreenActivity ctx) {
        return new SplashModel(api, ctx);
    }

}

