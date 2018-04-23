package edu.uab.cvc.huntingwords.screens.views;

import android.util.Pair;

import java.util.List;

/**
 * Created by carlosb on 19/04/18.
 */

public interface InitView {
    void totalRanking(List<Pair<String,String>> scoreMatch, List<Pair<String,String>> scoreDiff);

    void updateScore(Integer matchScore, Integer diffScore);
}
