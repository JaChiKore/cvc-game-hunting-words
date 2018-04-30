package edu.uab.cvc.huntingwords.presenters;

/**
 * Created by carlosb on 4/16/18.
 */

public interface DifferenceGamePresenter {
    void newGame();

    void updateGame();


    void checkImage(String tag);

    void uploadResult(Integer oldScore, Integer newTotalPoints);

    void checkDifferent();

    void checkSame();

    void restartGame();

    void loadMoreInfo();
}
