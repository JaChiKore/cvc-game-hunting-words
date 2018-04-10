package edu.uab.cvc.huntingwords.screens.heroes.dagger;

import dagger.Module;
import dagger.Provides;
import edu.uab.cvc.huntingwords.api.HeroApi;
import edu.uab.cvc.huntingwords.screens.heroes.HeroesListActivity;
import edu.uab.cvc.huntingwords.screens.heroes.core.HeroesModel;
import edu.uab.cvc.huntingwords.screens.heroes.core.HeroesPresenter;
import edu.uab.cvc.huntingwords.screens.heroes.core.HeroesView;
import edu.uab.cvc.huntingwords.utils.rx.RxSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by yassinegharsallah on 02/04/2017.
 */
@Module
public class HeroesModule {

    HeroesListActivity heroesListContext;

    public HeroesModule(HeroesListActivity context) {
        this.heroesListContext = context;
    }



    @HeroesScope
    @Provides
    HeroesView provideView() {
        return new HeroesView(heroesListContext);
    }

    @HeroesScope
    @Provides
    HeroesPresenter providePresenter(RxSchedulers schedulers, HeroesView view, HeroesModel model) {
        CompositeSubscription subscriptions = new CompositeSubscription();
        return new HeroesPresenter(schedulers, model, view, subscriptions);
    }



    @HeroesScope
    @Provides
    HeroesListActivity provideContext() {
        return heroesListContext;
    }
    @HeroesScope
    @Provides
    HeroesModel provideModel(HeroApi api) {
        return new HeroesModel(heroesListContext, api);
    }
}
