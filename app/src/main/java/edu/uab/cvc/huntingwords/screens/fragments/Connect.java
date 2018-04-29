package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import edu.uab.cvc.huntingwords.screens.Utils;
import edu.uab.cvc.huntingwords.screens.views.LoginView;
import edu.uab.cvc.huntingwords.utils.Constants;

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
    public void login () {
        String user= username.getText().toString();
        String pass= password.getText().toString();
        this.presenter.login(user,pass);


    }

    @OnClick(R.id.signin)
    public void signin() {
        String user= username.getText().toString();
        String pass= password.getText().toString();
        this.presenter.signin(user,pass);
    }

    @OnClick(R.id.disconnect)
    public void disconnect() {
        setUpAnonymousParameters();
        TextView valueMatchScore = (TextView)this.getActivity().findViewById(R.id.value_match_total_score);
        TextView valueDiffScore = (TextView)this.getActivity().findViewById(R.id.value_diff_total_score);
        TextView textView = (TextView)getActivity().findViewById(R.id.logged_user);
        valueMatchScore.setText("0");
        valueDiffScore.setText("0");
        getActivity().runOnUiThread( () ->  textView.setText(getString(R.string.anonym)));

    }

    @Override
    public void updateLogin(String username) {
        TextView textView = (TextView)getActivity().findViewById(R.id.logged_user);
        new Thread() {
                public void run() {
                    getActivity().runOnUiThread(
                            () -> {
                                getActivity().runOnUiThread( () ->  textView.setText(username));
                            });

                }
            }.start();

    }

    @Override
    public void updateScore(int matchScore, int diffScore) {
        TextView valueMatchScore = (TextView)this.getActivity().findViewById(R.id.value_match_total_score);
        TextView valueDiffScore = (TextView)this.getActivity().findViewById(R.id.value_diff_total_score);
        new Thread() {
            public void run() {
                getActivity().runOnUiThread(
                        () -> {
                            valueMatchScore.setText(String.valueOf(matchScore));
                            valueDiffScore.setText(String.valueOf(diffScore));
                        });

            }
        }.start();
    }

    @Override
    public void setUpLoginParameters(String username, String passw) {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.PARAM_USERNAME,username);
        editor.putString(Constants.PARAM_PASSWORD,passw);
        editor.commit();
    }

    public void setUpAnonymousParameters() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constants.PARAM_USERNAME,getString(R.string.anonym));
        editor.commit();
    }

    @Override
    public void errorLogin() {
        new Thread() {
            public void run() {
                getActivity().runOnUiThread(
                        () -> {
                            Toast.makeText(getActivity(),getString(R.string.logged_fail),Toast.LENGTH_LONG).show();
                        });

            }
        }.start();
    }
}
