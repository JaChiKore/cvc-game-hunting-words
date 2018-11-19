package edu.uab.cvc.huntingwords.screens.views;

/**
 * Created by carlosb on 19/04/18.
 */

public interface InitView {
    void updateScore(Integer matchScore, Integer diffScore, Integer matchLevel, Integer diffLevel);
    void updateMatchScore();
    void updateDiffScore();
    void initPlay();
}
