package edu.uab.cvc.huntingwords.presenters;

import android.content.Context;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import edu.uab.cvc.huntingwords.application.AppController;
import edu.uab.cvc.huntingwords.models.DifferenceFixGameInformation;
import edu.uab.cvc.huntingwords.models.DifferenceGameInformation;
import edu.uab.cvc.huntingwords.models.MatchFixGameInformation;
import edu.uab.cvc.huntingwords.models.MatchGameInformation;
import edu.uab.cvc.huntingwords.presenters.callbacks.TotalRankingCallback;
import edu.uab.cvc.huntingwords.screens.views.PlayView;
import edu.uab.cvc.huntingwords.tasks.GetTotalRanking;
import edu.uab.cvc.huntingwords.tasks.loaders.LoaderDifferenceGameInformation;
import edu.uab.cvc.huntingwords.tasks.loaders.LoaderMatchGameInformation;
import edu.uab.cvc.huntingwords.tasks.loaders.UpdateDifferenceGame;
import edu.uab.cvc.huntingwords.tasks.loaders.UpdateJumpGame;
import edu.uab.cvc.huntingwords.tasks.loaders.UpdateMatchGame;
import timber.log.Timber;

import static edu.uab.cvc.huntingwords.tasks.GetTotalRanking.DIFFERENCE;
import static edu.uab.cvc.huntingwords.tasks.GetTotalRanking.JUMP;
import static edu.uab.cvc.huntingwords.tasks.GetTotalRanking.MATCH;

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
    public void loadMatchInfo(String username) {
        try {
            new UpdateMatchGame().update(appContext, username);
            new LoaderMatchGameInformation().load(appContext,matchInfo, matchFixInfo);

        } catch (FileNotFoundException e) {
            Timber.e(e);
        }

    }

    @Override
    public void loadDifferenceInfo(String username) {
        try {
            new UpdateDifferenceGame().update(appContext, username);
            new LoaderDifferenceGameInformation().load(appContext,differenceInfo, differenceFixInfo);
        } catch (FileNotFoundException e) {
            Timber.e(e);
        }
    }

    @Override
    public void loadJumpInfo(String username) {
        try {
            new UpdateJumpGame().update(appContext, username);
        } catch (Exception e) {
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

    @Override
    public void runJumpGame() {
        this.view.runJumpGame();
    }

    @Override
    public void updateMatchRanking() {
        TotalRankingCallback callback = (ranking)-> view.totalRanking(ranking);
        new GetTotalRanking(callback).execute(MATCH);

    }
    @Override
    public void updateDifferenceRanking() {
        TotalRankingCallback callback = (ranking)-> view.totalRanking(ranking);
        new GetTotalRanking(callback).execute(DIFFERENCE);
    }

    @Override
    public void updateJumpRanking() {
        TotalRankingCallback callback = (ranking)-> view.totalRanking(ranking);
        new GetTotalRanking(callback).execute(JUMP);
    }
}
