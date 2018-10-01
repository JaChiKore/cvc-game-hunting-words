package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Fragment;
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

        view.setBackgroundColor(Utils.GetBackgroundColour(this.getActivity()));

        return view;

    }
}
