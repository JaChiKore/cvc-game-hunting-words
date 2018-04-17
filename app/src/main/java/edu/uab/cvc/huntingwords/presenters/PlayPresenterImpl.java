package edu.uab.cvc.huntingwords.presenters;

import android.content.Context;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import edu.uab.cvc.huntingwords.application.AppController;
import edu.uab.cvc.huntingwords.models.DifferenceFixGameInformation;
import edu.uab.cvc.huntingwords.models.DifferenceGameInformation;
import edu.uab.cvc.huntingwords.models.MatchFixGameInformation;
import edu.uab.cvc.huntingwords.models.MatchGameInformation;
import edu.uab.cvc.huntingwords.screens.views.PlayView;
import edu.uab.cvc.huntingwords.tasks.loaders.LoaderDifferenceGameInformation;
import edu.uab.cvc.huntingwords.tasks.loaders.LoaderMatchGameInformation;
import edu.uab.cvc.huntingwords.tasks.loaders.UpdateDifferenceGame;
import edu.uab.cvc.huntingwords.tasks.loaders.UpdateMatchGame;
import timber.log.Timber;

/**
 * Created by carlosb on 17/04/18.
 */

public class PlayPresenterImpl implements PlayPresenter {
        @Inject
        DifferenceGameInformation differenceInfo;

        @Inject
        MatchGameInformation matchInfo;

       @Inject
       DifferenceFixGameInformation differenceFixInfo;

        @Inject
        MatchFixGameInformation matchFixInfo;

        @Inject
        Context appContext;


    private final PlayView view;


    public PlayPresenterImpl(PlayView view) {
        AppController.getComponent().inject(this);
        this.view = view;
    }


    @Override
    public void loadMatchInfo() {
        try {
            new UpdateMatchGame().update(appContext);
            new LoaderMatchGameInformation().load(appContext,matchInfo);
            new LoaderMatchGameInformation().loadFix(appContext,matchFixInfo);

        } catch (FileNotFoundException e) {
            Timber.e(e);
        }

    }

    @Override
    public void loadDifferenceInfo() {
        try {
            new UpdateDifferenceGame().update(appContext);
            new LoaderDifferenceGameInformation().load(appContext,differenceInfo);
            new LoaderDifferenceGameInformation().loadFix(appContext,differenceFixInfo);

        } catch (FileNotFoundException e) {
            Timber.e(e);
        }


    }

    @Override
    public void runMatchGame() {
        this.view.runMatchGame();

    }

    @Override
    public void runDifferenceGame() {
        this.view.runDifferenceGame();
    }

}
