package edu.uab.cvc.huntingwords.screens;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Fragment;

import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.screens.fragments.Init;

import edu.uab.cvc.huntingwords.screens.fragments.Play;

public class FragmentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
    }

    @Override
    public void onBackPressed() {

        Fragment match = getFragmentManager().findFragmentByTag("how_to_play_match");
        Fragment diff = getFragmentManager().findFragmentByTag("how_to_play_difference");
        if ((match != null && match.isVisible()) || (diff != null && diff.isVisible())) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_switch, new Play(), "play");
            ft.commit();
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_switch, new Init(), "init");
            ft.commit();
        }
    }
}