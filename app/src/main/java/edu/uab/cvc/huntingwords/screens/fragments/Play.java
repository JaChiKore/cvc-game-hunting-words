package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.util.Hashtable;

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
        int color = Utils.GetBackgroundColour(this.getActivity());
        view.setBackgroundColor(color);

        this.presenter = new PlayPresenterImpl(this);
        return view;
    }


    //TODO check results
    private Hashtable results = new Hashtable();

    @OnClick(R.id.match)
    public void playMatch () {
        startMatchDialog();
        //countDownProgressToStartFragment ();
        /*
        Intent intent = new Intent(getActivity(), MatchGameActivity.class);
        intent.putExtra(MatchGameActivity.USERNAME,"anonim");
        intent.putExtra(MatchGameActivity.NUM_GAMES,"2");
        intent.putExtra(MatchGameActivity.BD_FILENAME,"matchGameInfo.txt");
        intent.putExtra(MatchGameActivity.BD_FIX_FILENAME,"matchGameFixInfo.txt");
        startActivity(intent);
        */

    }

    @OnClick(R.id.difference)
    public void playDifference () {
        startDifferenceDialog();
        /*
        Intent intent = new Intent(getActivity(), DifferenceGameActivity.class);
        intent.putExtra(MatchGameActivity.USERNAME,"anonim");
        intent.putExtra(MatchGameActivity.NUM_GAMES,"2");
        startActivity(intent);
        */

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
                    String countdown = String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60);
                    progress.setMessage(countdown);

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


}
