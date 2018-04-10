package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.uab.cvc.huntingwords.R;
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


    @OnClick(R.id.match)
    public void playMatch () {
        Intent intent = new Intent(getActivity(), MatchGameActivity.class);
        intent.putExtra(MatchGameActivity.USERNAME,"anonim");
        intent.putExtra(MatchGameActivity.NUM_GAMES,"2");
        intent.putExtra(MatchGameActivity.BD_FILENAME,"matchGameInfo.txt");
        intent.putExtra(MatchGameActivity.BD_FIX_FILENAME,"matchGameFixInfo.txt");
        startActivity(intent);
    }

    @OnClick(R.id.difference)
    public void playDifference () {
        Intent intent = new Intent(getActivity(), DifferenceGameActivity.class);
        intent.putExtra(MatchGameActivity.USERNAME,"anonim");
        intent.putExtra(MatchGameActivity.NUM_GAMES,"2");
        startActivity(intent);

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




}
