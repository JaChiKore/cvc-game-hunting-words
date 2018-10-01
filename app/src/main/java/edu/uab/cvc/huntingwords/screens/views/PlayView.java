package edu.uab.cvc.huntingwords.screens.views;

import android.util.Pair;

import java.util.List;

/**
 * Created by carlosb on 17/04/18.
 */

public interface PlayView {
    void runMatchGame();
    void runDifferenceGame();
    void totalRanking(List<Pair<String, String>> scoreList);
}
