package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.presenters.InitPresenter;
import edu.uab.cvc.huntingwords.presenters.InitPresenterImpl;
import edu.uab.cvc.huntingwords.presenters.PlayPresenterImpl;
import edu.uab.cvc.huntingwords.screens.Utils;
import edu.uab.cvc.huntingwords.screens.views.InitView;
import edu.uab.cvc.huntingwords.utils.Constants;

public class Init extends Fragment  implements InitView {


    private InitPresenter presenter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view =inflater.inflate(R.layout.init_fragment, container, false);
                ButterKnife.bind(this, view);
        int color = Utils.GetBackgroundColour(this.getActivity());
        view.setBackgroundColor(color);
        this.presenter = new InitPresenterImpl(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateUsername(getUsername());
    }


    private void updateUsername(String username) {
        TextView textUsername = (TextView)getActivity().findViewById(R.id.logged_user);
        textUsername.setText(username);
    }


    @OnClick(R.id.language)
    public void clickLanguage(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_switch,  new Languages());
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


    @OnClick(R.id.ranking)
    public void clickRanking(){

        presenter.updateRanking();
        /*
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.ranking))
                .setItems(R.array.test_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
        */


    }

    private String getUsername() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getString(Constants.PARAM_USERNAME,getString(R.string.anonym));
    }


    @OnClick(R.id.quit)
    public void clickQuit(){
        System.exit(0);
    }


    @Override
    public void totalRanking(List<Pair<String, String>> scoreMatch, List<Pair<String, String>> scoreDiff) {

        List<String> joinedList = new ArrayList<>();
        joinedList.add(getString(R.string.match_game_score));
        for (Pair score: scoreMatch) {
            joinedList.add(score.first+", "+score.second);
        }
        joinedList.add(getString(R.string.difference_game_score));
        for (Pair score: scoreDiff) {
            joinedList.add(score.first+", "+score.second);
        }
        final String[] arr = new String [joinedList.size()];
        joinedList.toArray(arr);
        new Thread() {
            public void run() {
                getActivity().runOnUiThread(
                        () -> {
                           // String [] arr = {"1","2"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(getString(R.string.ranking))
                                    .setItems( arr, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            builder.create().show();
                        });

            }
        }.start();



    }
}