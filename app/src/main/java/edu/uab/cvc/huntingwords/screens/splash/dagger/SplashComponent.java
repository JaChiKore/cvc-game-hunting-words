package edu.uab.cvc.huntingwords.screens.splash.dagger;

import dagger.Component;
import edu.uab.cvc.huntingwords.application.builder.AppComponent;
import edu.uab.cvc.huntingwords.screens.splash.SplashScreenActivity;

/**
 * Created by yassinegharsallah on 01/04/2017.
 */
@SplashScope
@Component(modules = {SplashContextModule.class, SplashModule.class}, dependencies = {AppComponent.class})
public interface SplashComponent {
    void inject(SplashScreenActivity activity);
}
