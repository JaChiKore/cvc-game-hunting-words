package edu.uab.cvc.huntingwords.screens.views;

import java.util.List;

/**
 * Created by carlosb on 4/15/18.
 */

public interface MatchView {
    void newRoundPlay(List<String> filepaths, List<String> buttons);

    void cleanResult(int idImage, int idButton);

    void hideButton(int idImage);
}
