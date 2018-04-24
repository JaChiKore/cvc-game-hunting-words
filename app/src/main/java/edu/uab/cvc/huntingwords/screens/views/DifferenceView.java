package edu.uab.cvc.huntingwords.screens.views;

import java.util.List;

/**
 * Created by carlosb on 4/16/18.
 */

public interface DifferenceView {
    void newRoundPlay (List<String> filepaths);

    void messageNotEnoughImages();

    void updateOK(float currentScore);

    void updateFail();

    void runPlayAgainDialog(float currentScore);

    void startCountdown();
}
