package edu.uab.cvc.huntingwords.application;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import edu.uab.cvc.huntingwords.screens.fragments.JumpGame;

public class JumpGameLauncher extends AndroidApplication {
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        Bundle b = getIntent().getExtras();
        String username = "Anonymous";
        if (b.getString("username") != null) {
            username = b.getString("username");
        }
        initialize(new JumpGame(this, username), config);

    }
}
