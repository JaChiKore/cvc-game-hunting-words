package edu.uab.cvc.huntingwords.screens.heroes;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.io.Serializable;

import javax.inject.Inject;

import edu.uab.cvc.huntingwords.application.AppController;
import edu.uab.cvc.huntingwords.models.Hero;
import edu.uab.cvc.huntingwords.screens.details.HeroDetailsActivity;
import edu.uab.cvc.huntingwords.screens.heroes.core.HeroesPresenter;
import edu.uab.cvc.huntingwords.screens.heroes.core.HeroesView;
import edu.uab.cvc.huntingwords.screens.heroes.dagger.DaggerHereosComponent;
import edu.uab.cvc.huntingwords.screens.heroes.dagger.HeroesModule;

/**
 * Created by yassinegharsallah on 01/04/2017.
 */

public class HeroesListActivity extends AppCompatActivity {


    @Inject
    HeroesView view;
    @Inject
    HeroesPresenter presenter;


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DaggerHereosComponent.builder().appComponent(AppController.getNetComponent()).heroesModule(new HeroesModule(this)).build().inject(this);
        setContentView(view.view());
        presenter.onCreate();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }

    public void goToHeroDetailsActivity(Hero hero) {

        Intent in = new Intent(this, HeroDetailsActivity.class);
        in.putExtra("hero", (Serializable) hero);
        startActivity(in);

    }

}
