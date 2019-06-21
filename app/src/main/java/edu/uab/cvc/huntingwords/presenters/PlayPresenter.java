package edu.uab.cvc.huntingwords.presenters;


/**
 * Created by carlosb on 17/04/18.
 */

public interface PlayPresenter {
    void loadMatchInfo(String username);

    void loadDifferenceInfo(String username);

    void runMatchGame();

    void runDifferenceGame();

    void runJumpGame();

    void updateMatchRanking();

    void updateDifferenceRanking();
}
