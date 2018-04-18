package edu.uab.cvc.huntingwords.screens.dialogs;

import android.annotation.SuppressLint;
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
import edu.uab.cvc.huntingwords.presenters.MatchGamePresenter;
import edu.uab.cvc.huntingwords.screens.fragments.Init;
import edu.uab.cvc.huntingwords.screens.fragments.MatchGame;
import edu.uab.cvc.huntingwords.screens.views.MatchView;

/**
 * Created by carlosb on 4/15/18.
 */

@SuppressLint("ValidFragment")
public class PlayAgainFragment extends DialogFragment
{

    private final float currentScore;
    private  final MatchGamePresenter presenter;
    @SuppressLint("ValidFragment")
    public PlayAgainFragment(MatchGamePresenter presenter, float currentScore) {
        this.presenter = presenter;
        this.currentScore = currentScore;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View view = getActivity().getLayoutInflater().inflate(R.layout.play_again, new LinearLayout(getActivity()), false);
        // Retrieve layout elements
        Button ok = (Button) view.findViewById(R.id.play_again_button_ok);
        Button cancel = (Button) view.findViewById(R.id.play_again_button_cancel);
        TextView text = (TextView) view.findViewById(R.id.value_result_score);
        text.setText(String.valueOf(currentScore));

        ok.setOnClickListener(v -> {
            dismiss();
            this.presenter.restartGame();
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
        builder.getWindow().setLayout(500,500);
        builder.setContentView(view);
        return builder;
    }
}