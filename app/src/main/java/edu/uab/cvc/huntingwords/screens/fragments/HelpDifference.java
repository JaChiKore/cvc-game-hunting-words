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

public class HelpDifference extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view =inflater.inflate(R.layout.help_difference_fragment, container, false);
        ButterKnife.bind(this, view);


        Context context = getActivity();
        SharedPreferences sharedPref = context.getSharedPreferences(
                getString(R.string.language_type), Context.MODE_PRIVATE);

        view.setBackgroundColor(Utils.GetBackgroundColour(this.getActivity()));
        return view;

    }

}