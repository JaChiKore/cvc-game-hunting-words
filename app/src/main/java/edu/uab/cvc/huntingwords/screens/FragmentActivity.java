package edu.uab.cvc.huntingwords.screens;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;

import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.screens.fragments.Init;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Window;

import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.screens.fragments.Init;

public class FragmentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
    }

    @Override
    public void onBackPressed(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_switch, new Init());
        fragmentTransaction.commit();
    }
}