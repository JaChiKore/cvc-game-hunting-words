package edu.uab.cvc.huntingwords.presenters;

import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.presenters.callbacks.ConnectCallback;
import edu.uab.cvc.huntingwords.screens.views.InitView;
import edu.uab.cvc.huntingwords.tasks.GetRanking;

/**
 * Created by carlosb on 23/04/18.
 */

public class InitPresenterImpl implements InitPresenter {

    private final InitView view;
    public InitPresenterImpl(InitView view) {
        this.view = view;

    }

    @Override
    public void getScore(String name, Boolean playClicked) {
        ConnectCallback callback = new ConnectCallback() {
            @Override
            public void updateLogin(String username) {}

            @Override
            public void updateScore(Integer matchScore, Integer diffScore, Integer matchLevel, Integer diffLevel) {
                view.updateScore(matchScore,diffScore, matchLevel, diffLevel);
            }

            @Override
            public void setUpScores(Boolean playClicked) {
                view.updateMatchScore();
                view.updateDiffScore();
                if (playClicked) {
                    view.initPlay();
                }
            }

            @Override
            public void error() {}
        };
        new GetRanking(callback, playClicked).execute(name);
    }
}
