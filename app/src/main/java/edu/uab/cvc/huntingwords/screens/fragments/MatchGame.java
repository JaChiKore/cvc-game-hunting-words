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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Hashtable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.presenters.MatchGamePresenter;
import edu.uab.cvc.huntingwords.presenters.MatchGamePresenterImpl;
import edu.uab.cvc.huntingwords.screens.Utils;
import edu.uab.cvc.huntingwords.screens.dialogs.PlayAgainFragment;
import edu.uab.cvc.huntingwords.screens.views.MatchView;

/**
 * Created by carlosb on 4/15/18.
 */

public class MatchGame  extends Fragment implements MatchView {
    public static final String TABLE_RESULTS = "tableResults";
    public static final int MAX_TIME = 30000;
    public static final int COUNT_DOWN_INTERVAL = 1000;
    @ColorInt int colorPrimary;
    private MatchGamePresenter presenter;

    @BindView(R.id.value_time)
    public TextView time;
    //TODO define
    private int score;

    private   Hashtable correctValues;

    public static MatchGame newInstance(Hashtable correctResults) {
        MatchGame frag = new MatchGame();
        Bundle args = new Bundle();
        args.putSerializable(TABLE_RESULTS, correctResults);
        frag.setArguments(args);
        return frag;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.match_game, container, false);
        ButterKnife.bind(this, view);

        view.setBackgroundColor( Utils.GetBackgroundColour(this.getActivity()));

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
         colorPrimary = typedValue.data;

        correctValues = (Hashtable) getArguments().getSerializable(TABLE_RESULTS);
        presenter = new MatchGamePresenterImpl(this,correctValues);
        this.score = 0;


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        startCountdown();
    }


    public int clickedImage = -1;

    @Override
    public void cleanResult(int idImage, int idButton) {
        if ( this.getActivity() == null ||   this.getActivity().findViewById(idImage) == null) {
            return;
        }
        this.getActivity().findViewById(idImage).setBackgroundColor(getResources().getColor(R.color.white));

        this.clickedImage = -1;
        //TODO remove this!!
        //tryAgainDialog();
        PlayAgainFragment dialogFragment = new PlayAgainFragment();
        Bundle args = new Bundle();
        args.putSerializable(TABLE_RESULTS, correctValues);
        dialogFragment.setArguments(args);
        dialogFragment.show(this.getActivity().getFragmentManager(),"");
    }

    @Override
    public void hideButton(int idImage) {
        this.getActivity().findViewById(idImage).setVisibility(View.INVISIBLE);
    }



    @Optional
    @OnClick({R.id.match_img_0_0, R.id.match_img_0_1, R.id.match_img_0_2, R.id.match_img_1_0, R.id.match_img_1_1, R.id.match_img_1_2, R.id.match_img_2_0, R.id.match_img_2_1, R.id.match_img_2_2, R.id.match_img_3_0, R.id.match_img_3_1, R.id.match_img_3_2})
    public void clickMatchImage(ImageButton button) {
        if (clickedImage!=-1) {
            return;
        }
        clickedImage = button.getId();
        button.setBackgroundColor(colorPrimary);

    }
    @Optional
    @OnClick({ R.id.match_but_0, R.id.match_but_1, R.id.match_but_2, R.id.match_but_3, R.id.match_but_4 })
    public void clickMatchButton(Button button) {
        if (clickedImage==-1) {
            return;
        }

        ImageButton image = (ImageButton)this.getActivity().findViewById(clickedImage);
        String textSolution = button.getText().toString();
        presenter.checkSolution(clickedImage, button.getId(),image.getTag(),textSolution);
        //TODO clean when it eliminate two
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
