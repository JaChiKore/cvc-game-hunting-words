package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.screens.Utils;

/**
 * Created by carlosb on 05/04/18.
 */

public class Languages extends Fragment {
    private static String CATALAN_TAG = "ca";
    private static String SPANISH_TAG = "es";
    private static String ENGLISH_TAG = "en";
    private static String CHINESE_TAG= "zh";


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.language_fragment, container, false);
        ButterKnife.bind(this, view);
        view.setBackgroundColor(Utils.GetBackgroundColour(this.getActivity()));
        return view;
    }

    @OnClick(R.id.but_spanish)
    public void clickSpanish() {
        setLocale(new Locale(SPANISH_TAG));
        updateTextScore();
    }
    @OnClick(R.id.but_english)
    public void clickEnglish() {
        setLocale(new Locale(ENGLISH_TAG));
        updateTextScore();

    }
    @OnClick(R.id.but_catalan)
    public void clickCatalan() {
        setLocale(new Locale(CATALAN_TAG));
        updateTextScore();

    }
    @OnClick(R.id.but_chinese)
    public void clickChinese() {
        setLocale(new Locale(CHINESE_TAG));
        updateTextScore();

    }

    private void updateTextScore() {
        ((TextView)getActivity().findViewById(R.id.text_total_score)).setText(getString(R.string.text_diff_score));

    }





    @SuppressWarnings("deprecation")
    private void setLocale(Locale locale){
        Locale.setDefault(locale);
        Configuration config = getActivity().getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(config,
                getActivity().getBaseContext().getResources().getDisplayMetrics());
    }


}
