package edu.uab.cvc.huntingwords.presenters;

import java.util.Hashtable;

import edu.uab.cvc.huntingwords.screens.views.MatchView;

/**
 * Created by carlosb on 4/15/18.
 */

public class MatchGamePresenterImpl implements MatchGamePresenter {
    private final MatchView view;
    private final Hashtable correctValues;
    public MatchGamePresenterImpl(MatchView view, Hashtable correctValues) {
        this.view = view;
        this.correctValues = correctValues;
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
