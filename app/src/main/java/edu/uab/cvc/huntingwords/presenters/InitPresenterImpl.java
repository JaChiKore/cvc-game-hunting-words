package edu.uab.cvc.huntingwords.presenters;

import edu.uab.cvc.huntingwords.presenters.callbacks.ConnectCallback;
import edu.uab.cvc.huntingwords.screens.views.InitView;
import edu.uab.cvc.huntingwords.tasks.GetRanking;

/**
 * Created by carlosb on 23/04/18.
 */

public class InitPresenterImpl implements InitPresenter {

    private final InitView view;
    public InitPresenterImpl(InitView view) {
        this.view = view;

    }

    @Override
    public void getScore(String name) {
               ConnectCallback callback = new ConnectCallback() {
                                @Override
                                public void updateLogin(String username, String password) {

                                                        }

                                        @Override
                                public void updateScore(Integer matchScore, Integer diffScore) {
                                                            view.updateScore(matchScore,diffScore);
                                                }

                                       @Override
                                       public void error() {

                                       }
               };
               new GetRanking(callback).execute(name);
            }
}
