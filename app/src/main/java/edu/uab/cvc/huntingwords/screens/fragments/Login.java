package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.presenters.LoginPresenter;
import edu.uab.cvc.huntingwords.presenters.LoginPresenterImpl;
import edu.uab.cvc.huntingwords.screens.Utils;
import edu.uab.cvc.huntingwords.screens.views.LoginView;

/**
 * Created by carlosb on 05/04/18.
 */

public class Login extends Fragment implements LoginView {
    private LoginPresenter presenter;

    @BindView(R.id.edit_username)
    EditText username;
    @BindView(R.id.edit_pasword)
    EditText password;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment, container, false);
        ButterKnife.bind(this, view);
        int color = Utils.GetBackgroundColour(this.getActivity());
        view.setBackgroundColor(color);

        presenter = new LoginPresenterImpl(this);
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

    @Override
    public void updateLogin(String username) {
        TextView textView = (TextView)getActivity().findViewById(R.id.logged_user);
        textView.setText(username);
    }
}
