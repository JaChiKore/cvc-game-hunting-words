package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Hashtable;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.screens.FragmentActivity;
import edu.uab.cvc.huntingwords.screens.Utils;
import edu.uab.cvc.huntingwords.screens.games.DifferenceGameActivity;
import edu.uab.cvc.huntingwords.screens.games.MatchGameActivity;

/**
 * Created by carlosb on 05/04/18.
 */

public class Play extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.play_fragment, container, false);
        ButterKnife.bind(this, view);
        int color = Utils.GetBackgroundColour(this.getActivity());
        view.setBackgroundColor(color);

        return view;
    }


    //TODO check results
    private Hashtable results = new Hashtable();

    @OnClick(R.id.match)
    public void playMatch () {
        startProgressDialog ();
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
        Fragment myfragment;
        myfragment = DifferenceGame.newInstance(results);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_switch, myfragment);
        fragmentTransaction.commit();
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

    public void startProgressDialog() {
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
                Fragment myfragment;
                myfragment = MatchGame.newInstance(results);

                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_switch, myfragment);
                fragmentTransaction.commit();

            }
        }.start();

        progress.show();

    }






}
