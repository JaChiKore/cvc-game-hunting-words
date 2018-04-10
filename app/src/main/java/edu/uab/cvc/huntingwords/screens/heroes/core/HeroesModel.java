package edu.uab.cvc.huntingwords.screens.heroes.core;

import edu.uab.cvc.huntingwords.api.HeroApi;
import edu.uab.cvc.huntingwords.models.Hero;
import edu.uab.cvc.huntingwords.models.Heroes;
import edu.uab.cvc.huntingwords.screens.heroes.HeroesListActivity;
import edu.uab.cvc.huntingwords.utils.NetworkUtils;
import rx.Observable;

/**
 * Created by yassinegharsallah on 02/04/2017.
 */

public class HeroesModel {

    HeroesListActivity context;
    HeroApi api;

    public HeroesModel(HeroesListActivity context, HeroApi api) {
        this.api = api;
        this.context = context;
    }


    Observable<Heroes> provideListHeroes() {
        return api.getHeroes();
    }

    Observable<Boolean> isNetworkAvailable() {
        return NetworkUtils.isNetworkAvailableObservable(context);
    }



    public void gotoHeroDetailsActivity(Hero hero) {
        context.goToHeroDetailsActivity(hero);
    }


}
