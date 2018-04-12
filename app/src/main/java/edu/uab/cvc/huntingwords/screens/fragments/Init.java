package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.screens.Utils;

public class Init extends Fragment {

    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {


        View view =inflater.inflate(R.layout.init_fragment, container, false);
                ButterKnife.bind(this, view);
        int color = Utils.GetBackgroundColour(this.getActivity());
        view.setBackgroundColor(color);



        return view;

    }


    @OnClick(R.id.language)
    public void clickLanguage(){
        Fragment myfragment;
        myfragment = new Languages();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_switch, myfragment);
        fragmentTransaction.commit();

    }
    @OnClick(R.id.login)
    public void clickLogin(){
        Fragment myfragment;
        myfragment = new Connect();


        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_switch, myfragment);
        fragmentTransaction.commit();

    }
    @OnClick(R.id.play)
    public void clickPlay(){
        Fragment myfragment;
        myfragment = new Play();

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_switch, myfragment);
        fragmentTransaction.commit();


    }


    @OnClick(R.id.quit)
    public void clickQuit(){
        System.exit(0);
    }




}