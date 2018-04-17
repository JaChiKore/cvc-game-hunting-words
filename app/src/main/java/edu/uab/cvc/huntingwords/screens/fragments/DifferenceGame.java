package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Hashtable;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.presenters.DifferenceGamePresenter;
import edu.uab.cvc.huntingwords.presenters.DifferenceGamePresenterImpl;
import edu.uab.cvc.huntingwords.presenters.MatchGamePresenter;
import edu.uab.cvc.huntingwords.presenters.MatchGamePresenterImpl;
import edu.uab.cvc.huntingwords.screens.Utils;
import edu.uab.cvc.huntingwords.screens.views.DifferenceView;
import edu.uab.cvc.huntingwords.screens.views.MatchView;

/**
 * Created by carlosb on 4/15/18.
 */

public class DifferenceGame extends Fragment implements DifferenceView {
    public static final String TABLE_RESULTS = "tableResults";
    public static final int MAX_TIME = 30000;
    public static final int COUNT_DOWN_INTERVAL = 1000;
    @ColorInt
    int colorPrimary;
    private DifferenceGamePresenter presenter;

    @BindView(R.id.value_time)
    public TextView time;
    //TODO define
    private int score;

    private Hashtable correctValues;


    public static DifferenceGame newInstance(Hashtable correctResults) {
        DifferenceGame frag = new DifferenceGame();
        Bundle args = new Bundle();
        args.putSerializable(TABLE_RESULTS, correctResults);
        frag.setArguments(args);
        return frag;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.difference_game, container, false);
        ButterKnife.bind(this, view);

        view.setBackgroundColor(Utils.GetBackgroundColour(this.getActivity()));

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        colorPrimary = typedValue.data;

        correctValues = (Hashtable) getArguments().getSerializable(TABLE_RESULTS);
        presenter = new DifferenceGamePresenterImpl(this, correctValues);
        this.score = 0;


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        startCountdown();
    }


    private void startCountdown()  {
        new CountDownTimer(MAX_TIME, COUNT_DOWN_INTERVAL) {

            public void onTick(long millisUntilFinished) {
                time.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                time.setText("done!");
            }
        }.start();


    }
}
