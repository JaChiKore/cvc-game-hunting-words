package edu.uab.cvc.huntingwords.screens.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Hashtable;

import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.screens.fragments.Init;
import edu.uab.cvc.huntingwords.screens.fragments.MatchGame;

/**
 * Created by carlosb on 4/15/18.
 */

public class PlayAgainFragment extends DialogFragment
{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View view = getActivity().getLayoutInflater().inflate(R.layout.play_again, new LinearLayout(getActivity()), false);
        // Retrieve layout elements
        Button ok = (Button) view.findViewById(R.id.play_again_button_ok);
        Button cancel = (Button) view.findViewById(R.id.play_again_button_cancel);

        ok.setOnClickListener(v -> {
            dismiss();
            Fragment myfragment = MatchGame.newInstance();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_switch, myfragment);
            fragmentTransaction.commit();
        });
        cancel.setOnClickListener(v -> {
            dismiss();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_switch, new Init());
            fragmentTransaction.commit();
        });
        // Build dialog
        Dialog builder = new Dialog(getActivity());

        builder.setContentView(view);
        return builder;
    }
}