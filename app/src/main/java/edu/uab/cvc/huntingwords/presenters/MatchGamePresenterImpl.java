package edu.uab.cvc.huntingwords.presenters;

import android.content.Context;

import java.util.Hashtable;

import javax.inject.Inject;

import edu.uab.cvc.huntingwords.application.AppController;
import edu.uab.cvc.huntingwords.models.DifferenceFixGameInformation;
import edu.uab.cvc.huntingwords.models.MatchFixGameInformation;
import edu.uab.cvc.huntingwords.models.MatchGameInformation;
import edu.uab.cvc.huntingwords.screens.views.MatchView;

/**
 * Created by carlosb on 4/15/18.
 */

public class MatchGamePresenterImpl implements MatchGamePresenter {
    @Inject
    MatchGameInformation matchInfo;

    @Inject
    MatchFixGameInformation matchFixInfo;

    @Inject
    Context appContext;

    private final MatchView view;
    public MatchGamePresenterImpl(MatchView view) {
        AppController.getComponent().inject(this);
        this.view = view;
    }

    @Override
    public void checkSolution(final int clickedImage,final int idButton, Object tag, String textSolution) {
        Runnable task = () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.view.cleanResult(clickedImage,idButton);
        };

        task.run();
        Thread thread = new Thread(task);
        thread.start();
    }
}
