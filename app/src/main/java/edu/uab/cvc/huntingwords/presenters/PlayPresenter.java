package edu.uab.cvc.huntingwords.presenters;

import android.content.Context;

/**
 * Created by carlosb on 17/04/18.
 */

public interface PlayPresenter {
    void loadMatchInfo();

    void loadDifferenceInfo();

    void runMatchGame();

    void runDifferenceGame();

    void updateMatchRanking();

    void updateDifferenceRanking();
}
