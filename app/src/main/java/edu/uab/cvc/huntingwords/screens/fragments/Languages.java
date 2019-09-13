package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.screens.Utils;

/**
 * Created by carlosb on 05/04/18.
 */

public class Languages extends Fragment {
    public static final String CATALAN_TAG = "ca";
    public static final String SPANISH_TAG = "es";
    public static final String ENGLISH_TAG = "en";
    public static final String CHINESE_TAG = "zh";


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.language_fragment, container, false);
        ButterKnife.bind(this, view);
        view.setBackgroundColor(Utils.GetBackgroundColour(this.getActivity()));
        return view;
    }

    @OnClick(R.id.but_spanish)
    public void clickSpanish() {
        Answers.getInstance().logCustom(new CustomEvent("Language")
                .putCustomAttribute("Language", "Spanish"));
        setLocale(new Locale(SPANISH_TAG));
        updateTextScore();
    }
    @OnClick(R.id.but_english)
    public void clickEnglish() {
        Answers.getInstance().logCustom(new CustomEvent("Language")
                .putCustomAttribute("Language", "English"));
        setLocale(new Locale(ENGLISH_TAG));
        updateTextScore();
    }
    @OnClick(R.id.but_catalan)
    public void clickCatalan() {
        Answers.getInstance().logCustom(new CustomEvent("Language")
                .putCustomAttribute("Language", "Catalan"));
        setLocale(new Locale(CATALAN_TAG));
        updateTextScore();

    }
    @OnClick(R.id.but_chinese)
    public void clickChinese() {
        Answers.getInstance().logCustom(new CustomEvent("Language")
                .putCustomAttribute("Language", "Chinese"));
        setLocale(new Locale(CHINESE_TAG));
        updateTextScore();

    }

    private void updateTextScore() {
        ((TextView)getActivity().findViewById(R.id.text_match_score)).setText(getString(R.string.text_match_score));
        ((TextView)getActivity().findViewById(R.id.text_diff_score)).setText(getString(R.string.text_diff_score));
        ((TextView)getActivity().findViewById(R.id.text_jump_score)).setText(getString(R.string.text_jump_score));
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        String string = preferences.getString(edu.uab.cvc.huntingwords.Utils.PARAM_USERNAME, getString(R.string.anonym));
        if (string.equals("Anònim") || string.equals("Anónimo") || string.equals("Anonymous") || string.equals("无玩家")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(edu.uab.cvc.huntingwords.Utils.PARAM_USERNAME,getString(R.string.anonym));
            editor.apply();
        }
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_switch, new Init(), "init");
        ft.addToBackStack(null);
        ft.commit();
    }

    private void setLocale(Locale locale) {
        Locale.setDefault(locale);
        System.out.println("changed to new locale: "+Locale.getDefault().toString());
        Configuration config = new Configuration(getActivity().getBaseContext().getResources().getConfiguration());
        config.locale = locale;
        getActivity().getBaseContext().getResources().updateConfiguration(config,
                getActivity().getBaseContext().getResources().getDisplayMetrics());
        config = new Configuration(getActivity().getApplicationContext().getResources().getConfiguration());
        config.locale = locale;
        getActivity().getApplicationContext().getResources().updateConfiguration(config,
                getActivity().getApplicationContext().getResources().getDisplayMetrics());
    }
}
