package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.uab.cvc.huntingwords.screens.FragmentActivity;



import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.presenters.InitPresenter;
import edu.uab.cvc.huntingwords.presenters.InitPresenterImpl;
import edu.uab.cvc.huntingwords.screens.Utils;
import edu.uab.cvc.huntingwords.screens.views.InitView;

import static edu.uab.cvc.huntingwords.Utils.CURRENT_LEVEL_DIFFERENCE;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_LEVEL_MATCH;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_DIFF;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_MATCH;

public class Init extends Fragment  implements InitView {

    private InitPresenter presenter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view =inflater.inflate(R.layout.init_fragment, container, false);
                ButterKnife.bind(this, view);
        view.setBackgroundColor(Utils.GetBackgroundColour(this.getActivity()));
        presenter = new InitPresenterImpl(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        String string = preferences.getString(edu.uab.cvc.huntingwords.Utils.PARAM_USERNAME, getString(R.string.anonym));
        if (string.equals("Anònim") || string.equals("Anónimo") || string.equals("Anonymous") || string.equals("无玩家")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(edu.uab.cvc.huntingwords.Utils.PARAM_USERNAME,getString(R.string.anonym));
            editor.commit();
        }
        String name = getUsername();
        updateUsername(name);
        if (!name.equals(getString(R.string.anonym))) {
            updateMatchSocre(getMatchScore());
            updateDiffScore(getDiffScore());
        } else {
            updateUsername(getString(R.string.anonym));
            updatePreferencesScore(0,0, 1, 1);
            updateMatchSocre(0);
            updateDiffScore(0);
        }

    }


    private void updateUsername(String username) {
        TextView textUsername = (TextView)getActivity().findViewById(R.id.logged_user);
        textUsername.setText(username);
    }

    private void updateMatchSocre(int score) {
        TextView valueMatch = (TextView)getActivity().findViewById(R.id.value_match_score);
        valueMatch.setText(String.valueOf(score));
    }

    private void updateDiffScore(int score) {
        TextView valueDiff = (TextView)getActivity().findViewById(R.id.value_diff_score);
        valueDiff.setText(String.valueOf(score));
    }


    @OnClick(R.id.language)
    public void clickLanguage(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_switch, new Languages());
        fragmentTransaction.commit();
    }

    @OnClick(R.id.login)
    public void clickLogin(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_switch, new Connect());
        fragmentTransaction.commit();
    }

    @OnClick(R.id.play)
    public void clickPlay(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_switch,  new Play());
        fragmentTransaction.commit();
    }

    private String getUsername() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getString(edu.uab.cvc.huntingwords.Utils.PARAM_USERNAME,getString(R.string.anonym));
    }

    private int getMatchScore() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getInt(edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_MATCH,0);
    }

    private int getDiffScore() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getInt(edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_DIFF,0);
    }

    @OnClick(R.id.quit)
    public void clickQuit(){
        System.exit(0);
    }


    @Override
    public void updateScore(Integer scoreMatch, Integer scoreDiff, Integer matchLevel, Integer diffLevel) {
                new Thread() {
                    public void run() {
                                getActivity().runOnUiThread(
                                                () -> {
                                                    updatePreferencesScore(scoreMatch, scoreDiff, matchLevel, diffLevel);
                                                    });
                                    }
        }.start();
    }

    private void updatePreferencesScore(Integer scoreMatch, Integer scoreDiff, Integer matchLevel, Integer diffLevel) {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CURRENT_SCORE_MATCH,scoreMatch);
        editor.putInt(CURRENT_SCORE_DIFF,scoreDiff);
        editor.putInt(CURRENT_LEVEL_MATCH,scoreMatch);
        editor.putInt(CURRENT_LEVEL_DIFFERENCE,scoreDiff);
        editor.commit();
    }
}