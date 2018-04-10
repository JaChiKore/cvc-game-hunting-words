package edu.uab.cvc.huntingwords.screens.splash.core;

import edu.uab.cvc.huntingwords.api.HeroApi;
import edu.uab.cvc.huntingwords.models.Heroes;
import edu.uab.cvc.huntingwords.screens.splash.SplashScreenActivity;
import edu.uab.cvc.huntingwords.utils.NetworkUtils;
import rx.Observable;

/**
 * Created by yassinegharsallah on 01/04/2017.
 */

public class SplashModel {


    private HeroApi api;
    private SplashScreenActivity splashContext;

    public SplashModel(HeroApi api, SplashScreenActivity splashCtx) {
        this.api = api;
        this.splashContext = splashCtx;

    }

    Observable<Heroes> provideListHeroes() {
        return api.getHeroes();
    }

    Observable<Boolean> isNetworkAvailable() {
        return NetworkUtils.isNetworkAvailableObservable(splashContext);
    }


    public void gotoHeroesListActivity() {
        splashContext.showHeroesListActivity();

    }


}
