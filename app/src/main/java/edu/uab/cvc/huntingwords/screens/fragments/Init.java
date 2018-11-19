package edu.uab.cvc.huntingwords.screens.fragments;

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
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;

import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.presenters.ConnectPresenterImpl;
import edu.uab.cvc.huntingwords.presenters.InitPresenterImpl;
import edu.uab.cvc.huntingwords.presenters.utils.Token;
import edu.uab.cvc.huntingwords.screens.Utils;
import edu.uab.cvc.huntingwords.screens.views.InitView;
import edu.uab.cvc.huntingwords.tasks.LoginAnonymous;

import static edu.uab.cvc.huntingwords.Utils.CURRENT_LEVEL_DIFFERENCE;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_LEVEL_MATCH;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_DIFF;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_MATCH;
import static edu.uab.cvc.huntingwords.Utils.PARAM_TOKEN;
import static edu.uab.cvc.huntingwords.Utils.PARAM_USERNAME;

public class Init extends Fragment implements InitView {
    InitPresenterImpl presenter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.init_fragment, container, false);
        ButterKnife.bind(this, view);
        presenter = new InitPresenterImpl(this);
        view.setBackgroundColor(Utils.GetBackgroundColour(this.getActivity()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        String string = preferences.getString(PARAM_TOKEN, getString(R.string.default_token));
        if (string.equals(getString(R.string.default_token))) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PARAM_USERNAME,getString(R.string.anonym));
            editor.putString(PARAM_TOKEN,getString(R.string.default_token));
            editor.apply();
        }
        String name = getUsername();
        updateUsername(name);
        Token key = Token.getInstance();
        key.setToken(getToken());
        if (!key.getToken().equals(getString(R.string.default_token))) {
            presenter.getScore(name,false);
            Answers.getInstance().logLogin(new LoginEvent()
                    .putMethod("Normal login")
                    .putSuccess(true));
            updateMatchScore(getMatchScore());
            updateDiffScore(getDiffScore());
        } else {
            updateUsername(getString(R.string.anonym));
            updatePreferencesScore(0,0, 1, 1);
            updateMatchScore(0);
            updateDiffScore(0);
        }
    }

    private void updateUsername(String username) {
        TextView textUsername = getActivity().findViewById(R.id.logged_user);
        textUsername.setText(username);
    }

    private void updateMatchScore(int score) {
        TextView valueMatch = getActivity().findViewById(R.id.value_match_score);
        valueMatch.setText(String.valueOf(score));
    }

    private void updateDiffScore(int score) {
        TextView valueDiff = getActivity().findViewById(R.id.value_diff_score);
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
        if (getUsername().equals(getString(R.string.anonym))) {
            new LoginAnonymous().execute();
            Answers.getInstance().logLogin(new LoginEvent()
                    .putMethod("Anonymous login")
                    .putSuccess(true));
            SharedPreferences preferences = getActivity().getSharedPreferences(
                    getString(R.string.preferences_file), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(edu.uab.cvc.huntingwords.Utils.PARAM_USERNAME,"test");
            editor.putString(edu.uab.cvc.huntingwords.Utils.PARAM_TOKEN, Token.getInstance().getToken());
            editor.apply();
            TextView textView = getActivity().findViewById(R.id.logged_user);
            textView.setText("test");
            presenter.getScore("test", true);
        } else {
            initPlay();
        }
    }

    public void initPlay() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_switch, new Play());
        fragmentTransaction.commit();
    }

    private String getUsername() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getString(PARAM_USERNAME,getString(R.string.anonym));
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

    private String getToken() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getString(PARAM_TOKEN,"-1");
    }

    public void updateMatchScore() {
        TextView textView = getActivity().findViewById(R.id.value_match_score);
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        int matchValue = preferences.getInt(edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_MATCH,0);
        ((TextView)getActivity().findViewById(R.id.text_match_score)).setText(getString(R.string.text_match_score));
        textView.setText(String.valueOf(matchValue));
    }

    public void updateDiffScore() {
        TextView textView = getActivity().findViewById(R.id.value_diff_score);
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        int diffValue = preferences.getInt(edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_DIFF,0);
        ((TextView)getActivity().findViewById(R.id.text_diff_score)).setText(getString(R.string.text_diff_score));
        textView.setText(String.valueOf(diffValue));
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
                    () -> updatePreferencesScore(scoreMatch, scoreDiff, matchLevel, diffLevel));
            }
        }.start();
    }

    private void updatePreferencesScore(Integer scoreMatch, Integer scoreDiff, Integer matchLevel, Integer diffLevel) {
        SharedPreferences preferences = getActivity().getSharedPreferences(
            getActivity().getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CURRENT_SCORE_MATCH,scoreMatch);
        editor.putInt(CURRENT_SCORE_DIFF,scoreDiff);
        editor.putInt(CURRENT_LEVEL_MATCH,matchLevel);
        editor.putInt(CURRENT_LEVEL_DIFFERENCE,diffLevel);
        editor.apply();
    }
}