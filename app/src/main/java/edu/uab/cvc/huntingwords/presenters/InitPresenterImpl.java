package edu.uab.cvc.huntingwords.presenters;

import edu.uab.cvc.huntingwords.presenters.callbacks.TotalRankingCallback;
import edu.uab.cvc.huntingwords.screens.fragments.Init;
import edu.uab.cvc.huntingwords.screens.views.InitView;
import edu.uab.cvc.huntingwords.tasks.GetTotalRanking;
import timber.log.Timber;

/**
 * Created by carlosb on 19/04/18.
 */

public class InitPresenterImpl implements InitPresenter {

    private final InitView view;
    public InitPresenterImpl(InitView view) {
        this.view = view;
    }

    @Override
    public void updateRanking() {
        TotalRankingCallback callback = (scoreMatch,scoreDiff)-> {
            view.totalRanking(scoreMatch,scoreDiff);

        };
        new GetTotalRanking(callback).execute();

    }
}
