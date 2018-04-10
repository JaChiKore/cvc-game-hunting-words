package edu.uab.cvc.huntingwords.screens.details.dagger;

import dagger.Module;
import dagger.Provides;
import edu.uab.cvc.huntingwords.models.Hero;
import edu.uab.cvc.huntingwords.screens.details.HeroDetailsActivity;
import edu.uab.cvc.huntingwords.screens.details.core.HeroDetailsView;

/**
 * Created by yassinegharsallah on 02/04/2017.
 */

@Module
public class HeroDetailsModule  {

    HeroDetailsActivity detailsContext;
    Hero hero;

    public HeroDetailsModule(HeroDetailsActivity context, Hero aHero)
    {
        this.detailsContext = context;
        this.hero = aHero;
    }

    @Provides
    HeroDetailsView provideView()
    {
        return  new HeroDetailsView(detailsContext,hero);
    }
}
