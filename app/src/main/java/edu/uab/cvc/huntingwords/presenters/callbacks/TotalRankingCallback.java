package edu.uab.cvc.huntingwords.presenters.callbacks;

import android.util.Pair;

import java.util.List;

/**
 * Created by carlosb on 19/04/18.
 */


public interface TotalRankingCallback {
    void updateTotalRanking(List<Pair<String, String>> rankingMatch, List<Pair<String,String>> rankingDifference);
}
