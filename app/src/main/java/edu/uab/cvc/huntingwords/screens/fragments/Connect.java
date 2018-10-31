package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.presenters.ConnectPresenter;
import edu.uab.cvc.huntingwords.presenters.ConnectPresenterImpl;
import edu.uab.cvc.huntingwords.presenters.utils.Token;
import edu.uab.cvc.huntingwords.screens.Utils;
import edu.uab.cvc.huntingwords.screens.views.LoginView;

import static edu.uab.cvc.huntingwords.Utils.CURRENT_LEVEL_DIFFERENCE;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_LEVEL_MATCH;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_DIFF;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_MATCH;

/**
 * Created by carlosb on 05/04/18.
 */

public class Connect extends Fragment implements LoginView {
    private ConnectPresenter presenter;

    @BindView(R.id.edit_username)
    EditText username;
    @BindView(R.id.edit_pasword)
    EditText password;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        ButterKnife.bind(this, view);
        presenter = new ConnectPresenterImpl(this);
        view.setBackgroundColor(Utils.GetBackgroundColour(this.getActivity()));
        return view;
    }

    @OnClick(R.id.connect)
    public void login() {
        String user= username.getText().toString();
        String pass= password.getText().toString();
        if (user.length() > 0 && pass.length() > 0) {
            this.presenter.login(user,pass);
        }
    }

    @OnClick(R.id.signin)
    public void signin() {
        String user= username.getText().toString();
        String pass= password.getText().toString();
        if (user.length() > 0 && pass.length() > 0) {
            this.presenter.signin(user, pass);
        }
    }

    @OnClick(R.id.disconnect)
    public void disconnect() {
        setUpAnonymousParameters();
        TextView name = getActivity().findViewById(R.id.logged_user);
        TextView match = getActivity().findViewById(R.id.value_match_score);
        TextView diff = getActivity().findViewById(R.id.value_diff_score);
        name.setText(getString(R.string.anonym));
        match.setText("0");
        diff.setText("0");
        goToInit();
    }

    @Override
    public void updateLogin(String username) {
        TextView textView = getActivity().findViewById(R.id.logged_user);
        new Thread() {
                public void run() {
                    getActivity().runOnUiThread(
                            () -> getActivity().runOnUiThread( () ->  textView.setText(username)));
                }
            }.start();

    }

    @Override
    public void updateMatchScore() {
        TextView textView = getActivity().findViewById(R.id.value_match_score);
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        int matchValue = preferences.getInt(edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_MATCH,0);
        ((TextView)getActivity().findViewById(R.id.text_match_score)).setText(getString(R.string.text_match_score));
        textView.setText(String.valueOf(matchValue));
    }

    @Override
    public void updateDiffScore() {
        TextView textView = getActivity().findViewById(R.id.value_diff_score);
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        int diffValue = preferences.getInt(edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_DIFF,0);
        ((TextView)getActivity().findViewById(R.id.text_diff_score)).setText(getString(R.string.text_diff_score));
        textView.setText(String.valueOf(diffValue));
    }

    @Override
    public void goToInit() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_switch, new Init(), "init");
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void setUpLoginParameters(String username, String password) {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(edu.uab.cvc.huntingwords.Utils.PARAM_USERNAME,username);
        editor.putString(edu.uab.cvc.huntingwords.Utils.PARAM_PASSWORD,password);
        Token key = Token.getInstance();
        editor.putString(edu.uab.cvc.huntingwords.Utils.PARAM_TOKEN, key.getToken());
        editor.apply();
    }
    @Override
    public void setUpScoreParameters(Integer scoreMatch, Integer scoreDiff, Integer levelMatch, Integer levelDiff) {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CURRENT_SCORE_MATCH,scoreMatch);
        editor.putInt(CURRENT_SCORE_DIFF,scoreDiff);
        editor.putInt(CURRENT_LEVEL_MATCH,levelMatch);
        editor.putInt(CURRENT_LEVEL_DIFFERENCE,levelDiff);
        editor.apply();
    }

    public void setUpAnonymousParameters() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(edu.uab.cvc.huntingwords.Utils.PARAM_USERNAME,getString(R.string.anonym));
        editor.apply();
    }

    @Override
    public void errorLogin() {
        new Thread() {
            public void run() {
                getActivity().runOnUiThread(
                        () -> Toast.makeText(getActivity(),getString(R.string.logged_fail),Toast.LENGTH_LONG).show());

            }
        }.start();
    }

    @Override
    public void errorSignin() {
        new Thread() {
            public void run() {
                getActivity().runOnUiThread(
                        () -> Toast.makeText(getActivity(),getString(R.string.signin_fail),Toast.LENGTH_LONG).show());

            }
        }.start();
    }
}
