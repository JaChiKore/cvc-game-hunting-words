package edu.uab.cvc.huntingwords.screens.games;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

public class MatchGameActivity extends Activity {
    public static final String BD_FILENAME = "BDFilename";
    public static final String BD_FIX_FILENAME = "BDFixFilename";
    public static final String USERNAME = "username";
    public static final String NUM_GAMES = "num_games";
    MatchGameView matchGameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Bundle b = getIntent().getExtras();
        String BDFilename = null;
        String BDFixFilename = null;
        String username = null;
        int num_games = -1;
        BDFilename = b.getString(BD_FILENAME);
        BDFixFilename = b.getString(BD_FIX_FILENAME);
        username = b.getString(USERNAME);
        num_games = b.getInt(NUM_GAMES);

        matchGameView = new MatchGameView(this, size.x, size.y, BDFilename, BDFixFilename, username, num_games);
        setContentView(matchGameView);
    }



    protected void onResume() {
        super.onResume();

        matchGameView.resume();
    }

    protected void onPause() {
        super.onPause();

        matchGameView.pause();
    }
}
