package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.screens.Utils;

/**
 * Created by carlosb on 09/04/18.
 */

public class HelpMatch extends Fragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view =inflater.inflate(R.layout.help_match_fragment, container, false);
        ButterKnife.bind(this, view);

        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.language_type), Context.MODE_PRIVATE);

        int color = Utils.GetBackgroundColour(this.getActivity());
        view.setBackgroundColor(color);

        return view;

    }

}
