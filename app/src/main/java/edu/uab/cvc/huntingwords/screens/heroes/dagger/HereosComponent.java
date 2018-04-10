package edu.uab.cvc.huntingwords.screens.heroes.dagger;

import dagger.Component;
import edu.uab.cvc.huntingwords.application.builder.AppComponent;
import edu.uab.cvc.huntingwords.screens.heroes.HeroesListActivity;

/**
 * Created by yassinegharsallah on 01/04/2017.
 */
@HeroesScope
@Component(dependencies = {AppComponent.class} , modules = {HeroesModule.class})
public interface HereosComponent {

    void inject (HeroesListActivity heroesActivity);
}
