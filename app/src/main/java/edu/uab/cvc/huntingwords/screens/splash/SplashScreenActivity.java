package edu.uab.cvc.huntingwords.screens.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import javax.inject.Inject;

import edu.uab.cvc.huntingwords.application.AppController;
import edu.uab.cvc.huntingwords.screens.heroes.HeroesListActivity;
import edu.uab.cvc.huntingwords.screens.splash.core.SplashPresenter;
import edu.uab.cvc.huntingwords.screens.splash.core.SplashView;
import edu.uab.cvc.huntingwords.screens.splash.dagger.DaggerSplashComponent;
import edu.uab.cvc.huntingwords.screens.splash.dagger.SplashContextModule;

/**
 * Created by yassinegharsallah on 01/04/2017.
 */

public class SplashScreenActivity extends AppCompatActivity {


    @Inject
    SplashView view;
    @Inject
    SplashPresenter splashPresenter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerSplashComponent.builder().
                appComponent(AppController.getNetComponent()).
                splashContextModule(new SplashContextModule(this)).
                build().inject(this);

        setContentView(view.constructView());
        splashPresenter.onCreate();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        splashPresenter.onDestroy();
    }

    public void showHeroesListActivity() {
        Log.d("loaded", "ok showed");
        Intent i = new Intent(this, HeroesListActivity.class);
        startActivity(i);
    }

}
