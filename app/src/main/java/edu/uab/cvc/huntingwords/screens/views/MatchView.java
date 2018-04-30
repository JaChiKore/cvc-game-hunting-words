package edu.uab.cvc.huntingwords.screens.views;

import java.util.List;

import edu.uab.cvc.huntingwords.screens.fragments.CallbackPostDialog;

/**
 * Created by carlosb on 4/15/18.
 */

public interface MatchView {
    void newRoundPlay(List<String> filepaths, List<String> buttons);

    void hideButton(int idImage);

    void messageNotEnoughImages();

    void updateButtons(List<String> nameWords);


    void updateOK(int idImage, float currentScore);

    void updateFail();

    void runPlayAgainDialog(float currentScore, int level, CallbackPostDialog postDialog);

    void startDialog();
}
