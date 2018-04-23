package edu.uab.cvc.huntingwords.presenters;

/**
 * Created by carlosb on 4/15/18.
 */

public interface MatchGamePresenter {
    void checkSolution(int idImage, int idButton, String tag, String textSolution);

    void newGame();

    void updateButtonsByImage(String tag);

    void finishRound();

    void restartGame();

    void uploadResult(Integer oldScore, Integer newTotalPoints);
}
