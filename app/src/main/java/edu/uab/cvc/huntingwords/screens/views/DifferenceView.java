package edu.uab.cvc.huntingwords.screens.views;

import java.util.List;

import edu.uab.cvc.huntingwords.screens.fragments.CallbackPostDialog;

/**
 * Created by carlosb on 4/16/18.
 */

public interface DifferenceView {
    void newRoundPlay (List<String> filePaths);

    void messageNotEnoughImages();

    void updateOK(float currentScore);

    void updateFail();

    void runPlayAgainDialog(boolean win, float currentScore, int level, CallbackPostDialog callback);

    void startDialog();

    void setUpNumLives(int numLives);

    void setPause(boolean pause);

    void updateTotalScore(float totalScore);
}
