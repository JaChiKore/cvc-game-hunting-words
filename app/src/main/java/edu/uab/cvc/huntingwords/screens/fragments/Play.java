package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.presenters.PlayPresenter;
import edu.uab.cvc.huntingwords.presenters.PlayPresenterImpl;
import edu.uab.cvc.huntingwords.screens.Utils;
import edu.uab.cvc.huntingwords.screens.views.PlayView;

/**
 * Created by carlosb on 05/04/18.
 */

public class Play extends Fragment implements PlayView{

    private PlayPresenter presenter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_fragment, container, false);
        ButterKnife.bind(this, view);
        view.setBackgroundColor(Utils.GetBackgroundColour(this.getActivity()));

        this.presenter = new PlayPresenterImpl(this);
        return view;
    }


    @OnClick(R.id.match)
    public void playMatch () {
        startMatchDialog();
    }

    @OnClick(R.id.difference)
    public void playDifference () {
        startDifferenceDialog();

    }

    @OnClick(R.id.ranking_match)
    public void rankingMatch () {
        presenter.updateMatchRanking();
    }

    @OnClick(R.id.ranking_difference)
    public void rankingDifference () {
        presenter.updateDifferenceRanking();
    }


    @OnClick(R.id.how_to_play_match)
    public void helpMatch () {
        Fragment myfragment;
        myfragment = new HelpMatch();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_switch, myfragment);
        fragmentTransaction.commit();
    }
    @OnClick(R.id.how_to_play_difference)
    public void helpDifference () {
        Fragment myfragment;
        myfragment = new HelpDifference();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_switch, myfragment);
        fragmentTransaction.commit();
    }

        public void countDownProgressToStartFragment(Fragment fragment) {
            final ProgressDialog progress = new ProgressDialog(this.getActivity());
            //progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progress.setCancelable(false);
            progress.setIndeterminate(true);

            new CountDownTimer(5000, 1000) {

                public void onTick(long millisUntilFinished) {
                    long seconds = millisUntilFinished / 1000;
                    String countdown = String.format(String.format("%02d", seconds % 60));
                    SpannableString ss2=  new SpannableString(countdown);
                    ss2.setSpan(new RelativeSizeSpan(2f), 0, ss2.length(), 0);
                    ss2.setSpan(new ForegroundColorSpan(Color.RED), 0, ss2.length(), 0);
                    progress.setMessage(ss2);

                }

                public void onFinish() {
                    progress.dismiss();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_switch, fragment);
                    fragmentTransaction.commit();

                }
            }.start();

            progress.show();
            Window window = progress.getWindow();
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);

        }


    private void startMatchDialog()
    {

        ProgressDialog pd = ProgressDialog.show(getActivity(),getString(R.string.title_loading_info),getString(R.string.downloading_text));
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        //start a new thread to process job
        new Thread(() ->  {
            presenter.loadMatchInfo();
            pd.dismiss();
            presenter.runMatchGame();
        }).start();

    }

    private void startDifferenceDialog()
    {

        ProgressDialog pd = ProgressDialog.show(getActivity(),getString(R.string.title_loading_info),getString(R.string.downloading_text));
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        //start a new thread to process job
        new Thread(() ->  {
            presenter.loadDifferenceInfo();
            pd.dismiss();
            presenter.runDifferenceGame();
        }).start();

    }


    @Override
    public void runMatchGame() {
        this.getActivity().runOnUiThread(() -> {
            countDownProgressToStartFragment(MatchGame.newInstance());
        });
    }

    @Override
    public void runDifferenceGame() {
        this.getActivity().runOnUiThread(() -> {
            countDownProgressToStartFragment(DifferenceGame.newInstance());
        });
    }

    @Override
    public void totalRanking(List<Pair<String, String>> scoreList) {
        List<String> joinedList = new ArrayList<>();
        for (Pair score: scoreList) {
            joinedList.add(score.first+", "+score.second);
        }
        final String[] arr = new String [joinedList.size()];
        joinedList.toArray(arr);
        new Thread() {
            public void run() {
                getActivity().runOnUiThread(
                        () -> {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(getString(R.string.ranking))
                                    .setItems( arr, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            builder.create().show();
                        });

            }
        }.start();
    }



}
