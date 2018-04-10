package edu.uab.cvc.huntingwords.screens.games;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class DifferenceGameActivity extends Activity {
    public static final String USERNAME = "username";
    public static final String NUM_GAMES = "num_games";
    DifferenceGameView differentGameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Bundle b = getIntent().getExtras();
        String username = null;
        int num_games = -1;
        if (b != null) {
            username = b.getString(USERNAME);
            num_games = b.getInt(NUM_GAMES);
        }

        differentGameView = new DifferenceGameView(this, size.x, size.y, username, num_games);
        setContentView(differentGameView);
    }



    protected void onResume() {
        super.onResume();

        differentGameView.resume();
    }

    protected void onPause() {
        super.onPause();

        differentGameView.pause();
    }
}
