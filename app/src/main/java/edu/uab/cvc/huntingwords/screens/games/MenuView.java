package edu.uab.cvc.huntingwords.screens.games;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.tasks.checks.CheckDifferenceGameImages;
import edu.uab.cvc.huntingwords.tasks.checks.CheckMatchGameImages;
import edu.uab.cvc.huntingwords.tasks.checks.CheckNewDifferenceGameFixClusters;
import edu.uab.cvc.huntingwords.tasks.checks.CheckNewMatchGameFixImages;
import edu.uab.cvc.huntingwords.tasks.checks.CheckNumGame_Images;
import edu.uab.cvc.huntingwords.tasks.difference.GetDifferenceGameFixInfo;
import edu.uab.cvc.huntingwords.tasks.difference.GetDifferenceGameInfo;
import edu.uab.cvc.huntingwords.tasks.difference.GetImagesDifferenceGame;
import edu.uab.cvc.huntingwords.tasks.match.GetImagesMatchGame;
import edu.uab.cvc.huntingwords.tasks.match.GetMatchGameFixInfo;
import edu.uab.cvc.huntingwords.tasks.match.GetMatchGameInfo;
import edu.uab.cvc.huntingwords.tasks.updates.UpdatePlayTimes;


@SuppressLint("ViewConstructor")
public class MenuView extends SurfaceView implements Runnable {

    private Context context;

    private Thread menuThread = null;

    private volatile boolean menu_ing;
    //private volatile int[] downloadedFiles;
    private boolean logged;
    private int log_sign_text_status;
    private int login_status;

    private boolean change_language;

    private boolean new_user;

    private final int NOT_PRESSED = -1;
    private final int LOGIN = 1;
    private final int LOGGED = 2;

    private final int NUM_OF_MINIGAMES = 2;
    private final int MAX_USERNAME_LENGTH = 20;

    private final int DISABLED = -1;
    private final int LOGGED_SUCCESS = 1;
    private final int SIGNIN_SUCCESS = 2;
    private final int LOGGED_FAIL = 3;
    private final int SIGNIN_FAIL = 4;

    final static int SMALL = 20;
    final static int NORMAL = 35;
    final static int BIG = 50;
    final static int HUGE = 70;
    final static int MEGA = 90;

    private String username;

    private long lastTextTimeForUser;

    private boolean selectMinigame;
    private boolean gameSelected = true;

    private boolean seeRanking;

    private SurfaceHolder ourHolder;

    private Canvas canvas;
    private Paint paint;

    private int screenX;
    private int screenY;

    private Button play_button;
    private Button status_button;
    private Button login_button;
    private Button signin_button;
    private Button ranking_button;
    private Button connect_button;
    private Button exit_button;
    private Button return_button;
    private Button match_game_button;
    private Button difference_game_button;
    private Button background_button;
    private Button login_background_button;

    private Button language_button;
    private Button ca_language_button;
    private Button en_language_button;
    private Button es_language_button;
    private Button zh_language_button;

    private Button cantPlayMatchGame_button;
    private Button cantPlayDifferenceGame_button;

    private Button seeMatchGameScore;
    private Button seeDifferenceGameScore;

    private Button howToPlayMatchGame;
    private Button howToPlayDifferenceGame;

    private int witchExplanation;

    private final int MATCH_GAME = 0;
    private final int DIFFERENCE_GAME = 1;
    private final int NO_GAME = -1;

    private boolean matchGameOrDifferenceGame;

    private RectF rankingWindow;
    private String[] rankingScoresMatchGame;
    private String[] rankingScoresDifferenceGame;

    private Button user_textBox;
    private Button pass_textBox;

    private String userText;
    private String passText;

    private Bitmap[] match_game;
    private Bitmap howToMatchGame;
    private Bitmap[] difference_game;
    private Bitmap howToDifferenceGame;

    private String matchGameInfoFilename = "matchGameInfo.txt";
    private String matchGameFixInfoFilename = "matchGameFixInfo.txt";
    private String downImages = "-2";
    private boolean down;
    private boolean downMatchGameFix;
    private int numImagesMatchGameInBBDD;

    private boolean cantPlayMathGame;
    private boolean cantPlayDifferenceGame;

    private String differenceGameInfoFilename = "differenceGameInfo.txt";
    private String differenceGameFixInfoFilename = "differenceGameFixInfo.txt";
    private boolean downDifferenceGameFix;
    private int numClusterDifferenceGameBBDD;

    private long lastTextTimeForDown;

    private String downloaded = "-2";
    private boolean needDownload = false;
    private boolean needDownloadFix = false;

    private int match_game_frame_count;
    private int difference_game_frame_count;
    private long lastLapseTime;

    private boolean isOnline = false;

    private int[] maxScores = null;

    InputMethodManager imm;

    public MenuView(Context context, int x, int y) {
        super(context);

        this.context = context;

        isOnline = isOnline();

        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        ourHolder = getHolder();
        paint = new Paint();

        logged = false;
        login_status = NOT_PRESSED;

        log_sign_text_status = DISABLED;

        change_language = false;

        selectMinigame = false;
        gameSelected = false;

        seeRanking = false;

        matchGameOrDifferenceGame = false;

        username = "anonim";

        witchExplanation = NO_GAME;

        screenX = x;
        screenY = y;

        cantPlayMathGame = false;
        cantPlayDifferenceGame = false;

        match_game = new Bitmap[7];
        difference_game = new Bitmap[6];

        rankingScoresMatchGame = new String[10];
        for (int i = 0; i < 10; i++) {
            rankingScoresMatchGame[i] = "ERROR,999999";
        }

        rankingScoresDifferenceGame = new String[10];
        for (int i = 0; i < 10; i++) {
            rankingScoresDifferenceGame[i] = "ERROR,999999";
        }

        rankingWindow = new RectF(screenX / 2 - (screenX / 4),
                screenY / 6,
                screenX * 3 / 4,
                screenY / 6 + screenY * 2 / 3);

        try {
            for (int i = 0; i < 7; i++) {
                InputStream is = context.getAssets().open("sprites/minigames/match game/match_game_0" + (i + 1) + ".png");

                match_game[i] = BitmapFactory.decodeStream(is);

                match_game[i] = Bitmap.createScaledBitmap(
                        match_game[i],
                        screenX / 3,
                        screenY / 3,
                        false);
            }
        } catch (IOException e) {
            Log.e("Error:","Failed loading match game frames.");
        }

        try {
            for (int i = 0; i < 6; i++) {
                InputStream is = context.getAssets().open("sprites/minigames/difference game/difference_game_0" + (i + 1) + ".png");

                difference_game[i] = BitmapFactory.decodeStream(is);

                difference_game[i] = Bitmap.createScaledBitmap(
                        difference_game[i],
                        screenX / 3,
                        screenY / 3,
                        false);
            }
        } catch (IOException e) {
            Log.e("Error:","Failed loading difference game frames.");
        }

        Locale current = getResources().getConfiguration().locale;
        String language = current.getLanguage();

        try {
            InputStream is = null;
            switch (language) {
                case "ca":
                    is = context.getAssets().open("sprites/minigames/match game/how_to_play_match_ca.png");
                    break;
                case "zh":
                    is = context.getAssets().open("sprites/minigames/match game/how_to_play_match_zh.png");
                    break;
                case "es":
                    is = context.getAssets().open("sprites/minigames/match game/how_to_play_match_es.png");
                    break;
                case "en":
                    is = context.getAssets().open("sprites/minigames/match game/how_to_play_match_en.png");
                    break;
            }

            howToMatchGame = BitmapFactory.decodeStream(is);

            howToMatchGame = Bitmap.createScaledBitmap(
                    howToMatchGame,
                    screenX * 18 / 20,
                    screenY * 18 / 20,
                    false);
        } catch (IOException e) {
            Log.e("Error:","Failed loading how to play match game.");
        }

        try {
            InputStream is = null;
            switch (language) {
                case "ca":
                    is = context.getAssets().open("sprites/minigames/difference game/how_to_play_different_ca.png");
                    break;
                case "zh":
                    is = context.getAssets().open("sprites/minigames/difference game/how_to_play_different_zh.png");
                    break;
                case "es":
                    is = context.getAssets().open("sprites/minigames/difference game/how_to_play_different_es.png");
                    break;
                case "en":
                    is = context.getAssets().open("sprites/minigames/difference game/how_to_play_different_en.png");
                    break;
            }
            howToDifferenceGame = BitmapFactory.decodeStream(is);

            howToDifferenceGame = Bitmap.createScaledBitmap(
                    howToDifferenceGame,
                    screenX * 18 / 20,
                    screenY * 18 / 20,
                    false);
        } catch (IOException e) {
            Log.e("Error:","Failed loading how to play difference game.");
        }

        match_game_frame_count = 0;
        difference_game_frame_count = 0;

        int padding = 25;

        int button_width = screenX / 3;
        int button_height = screenY * 2/10;
        int button_x = screenX / 2 - button_width / 2;

        play_button = new Button(button_x,
                screenY * 5/10,
                button_width,
                button_height,
                context.getString(R.string.play));
        exit_button = new Button(button_x,
                screenY * 7/10 + padding,
                button_width,
                button_height,
                context.getString(R.string.exit));

        status_button = new Button(screenX - padding - screenX / 8,
                                    padding,
                                    screenX / 8,
                                    screenX / 13,
                                    "");

        button_height = screenY * 2 / 14;
        login_button = new Button(screenX / 2 - (screenX / 4),
                screenY / 7 + screenY * 2 / 3,
                screenX / 4,
                button_height,
                context.getString(R.string.login));
        signin_button = new Button(screenX / 2 - (screenX / 4) + screenX / 4,
                screenY / 7 + screenY * 2 / 3,
                screenX / 4,
                button_height,
                context.getString(R.string.signin));

        button_x = screenX * 3 / 4 - padding - screenX / 5;
        ranking_button = new Button(screenX / 2 - (screenX / 4) + padding,
                screenY / 7 + screenY * 2 / 3 - screenY * 2 / 13 - padding,
                screenX / 5,
                screenY * 2 / 13,
                context.getString(R.string.ranking));
        connect_button = new Button(button_x,
                screenY / 7 + screenY * 2 / 3 - screenY * 2 / 13 - padding,
                screenX / 5,
                screenY * 2 / 13,
                "");

        user_textBox = new Button(button_x - screenX / 7,
                screenY / 4,
                screenX / 4,
                button_height,
                "");
        pass_textBox = new Button(button_x - screenX / 7,
                screenY / 4 + button_height + padding,
                screenX / 4,
                button_height,
                "");

        return_button = new Button(padding,
                padding,
                screenX / 13,
                screenX / 13,
                context.getString(R.string.return_text));
        match_game_button = new Button(screenX / 10,
                screenY / 3,
                (screenX / 3) + padding,
                (screenY / 3) + padding,
                context.getString(R.string.match_game));
        difference_game_button = new Button(screenX - (screenX / 10) - (screenX / 3),
                screenY / 3,
                (screenX / 3) + padding,
                (screenY / 3) + padding,
                context.getString(R.string.difference_game));

        howToPlayMatchGame = new Button(match_game_button.getRect().left + match_game_button.getRect().width() / 2 - screenX / 10,
                match_game_button.getRect().bottom + 60,
                screenX / 5,
                screenY * 2 / 13,
                context.getString(R.string.how_to_play));
        howToPlayDifferenceGame = new Button(difference_game_button.getRect().left + difference_game_button.getRect().width() / 2 - screenX / 10,
                difference_game_button.getRect().bottom + 60,
                screenX / 5,
                screenY * 2 / 13,
                context.getString(R.string.how_to_play));

        background_button = new Button(0,
                0,
                screenX,
                screenY,
                "");
        login_background_button = new Button(screenX / 2 - (screenX / 4),
                screenY / 7,
                screenX / 2,
                screenY * 2 / 3,
                "");

        cantPlayMatchGame_button = new Button(screenX / 10,
                (screenY / 3) + ((screenY / 3) + padding) / 4,
                (screenX / 3) + padding,
                ((screenY / 3) + padding) / 2,
                context.getString(R.string.no_more_images));
        cantPlayDifferenceGame_button = new Button(screenX - (screenX / 10) - (screenX / 3),
                (screenY / 3) + ((screenY / 3) + padding) / 4,
                (screenX / 3) + padding,
                ((screenY / 3) + padding) / 2,
                context.getString(R.string.no_more_images));

        seeMatchGameScore = new Button(screenX * 3 / 4 + screenX / 8 - screenX / 10,
                screenY / 2 - screenY * 2 / 13 - 20,
                screenX / 5,
                screenY * 2 / 13,
                context.getString(R.string.match_game_score));
        seeDifferenceGameScore = new Button(screenX * 3 / 4 + screenX / 8 - screenX / 10,
                screenY / 2 + 20,
                screenX / 5,
                screenY * 2 / 13,
                context.getString(R.string.difference_game_score));

        language_button = new Button(screenX - padding - screenX / 8,
                padding + screenX / 13 + padding,
                screenX / 8,
                screenX / 13,
                context.getString(R.string.language_text));

        ca_language_button = new Button(screenX / 2 - screenX / 4 + padding,
                screenY / 7 + padding,
                screenX / 4 - 2 * padding,
                screenY * 2 / 6 - 2 * padding,
                context.getString(R.string.ca));
        en_language_button = new Button(screenX / 2 + padding,
                screenY / 7 + padding,
                screenX / 4 - 2 * padding,
                screenY * 2 / 6 - 2 * padding,
                context.getString(R.string.en));
        es_language_button = new Button(screenX / 2 - screenX / 4 + padding,
                screenY / 7 + screenY * 2 / 6 + padding,
                screenX / 4 - 2 * padding,
                screenY * 2 / 6 - 2 * padding,
                context.getString(R.string.es));
        zh_language_button = new Button(screenX / 2 + padding,
                screenY / 7 + screenY * 2 / 6 + padding,
                screenX / 4 - 2 * padding,
                screenY * 2 / 6 - 2 * padding,
                context.getString(R.string.zh));
    }

    @Override
    public void run() {
        while(menu_ing) {
            long startFrameTime = System.currentTimeMillis();
            long lapseTime = 600;
            int textTime = 3000;

            draw();

            isOnline = isOnline();

            if (downImages.contentEquals("1") && down || downMatchGameFix) {
                gameSelected = false;
                if (downMatchGameFix) {
                    File f = new File(context.getFilesDir(), matchGameFixInfoFilename);

                    try {
                        BufferedReader br = new BufferedReader(new FileReader(f));
                        String row;
                        String[] columns;

                        String num = br.readLine();
                        int numWords = Integer.valueOf(num);

                        if (numWords > 0) {

                            float minX = screenX / 4;
                            float top = (screenY * 6 / 7) + 40;
                            float bottom = screenY - 10;
                            int padding = 2;

                            RectF downBarOutline = new RectF(minX - padding, top - padding, (screenX * 3 / 4) + padding, bottom + padding);
                            RectF downBar = new RectF(minX, top, screenX * 3 / 4, bottom);

                            float width = ((screenX * 3 / 4) - minX - (numWords - 1) * 2) / numWords;

                            int[] downloadedFiles = new int[numWords];

                            for (int i = 0; i < downloadedFiles.length; i++) {
                                downloadedFiles[i] = -2;
                            }
                            int i = 0;
                            GetImagesMatchGame getImagesMatchGame;

                            while ((row = br.readLine()) != null) {
                                columns = row.split(";");

                                getImagesMatchGame = new GetImagesMatchGame(context, downloadedFiles, i);
                                downloaded = getImagesMatchGame.doInForeground(columns[0], "0");
                                i++;

                                RectF r = new RectF(minX, top, minX + width, bottom);

                                String downText = context.getString(R.string.downloading_fix_text);

                                int textHeight = NORMAL;
                                paint.setTextSize(textHeight);
                                canvas = ourHolder.lockCanvas();
                                float textWidth = paint.measureText(downText, 0, downText.length());
                                paint.setColor(Color.BLACK);
                                canvas.drawText(downText,
                                        screenX / 2 - textWidth / 2,
                                        screenY * 2 / 3 + 30 + textHeight,
                                        paint);

                                paint.setColor(Color.BLACK);
                                canvas.drawRect(downBarOutline, paint);

                                paint.setColor(Color.argb(255, 130, 215, 220));
                                canvas.drawRect(downBar, paint);

                                //noinspection ForLoopReplaceableByForEach
                                for (int j = 0; j < downloadedFiles.length; j++) {
                                    if (downloadedFiles[j] != -2) {
                                        switch (downloadedFiles[j]) {
                                            case -1:
                                                paint.setColor(Color.RED);
                                                break;
                                            case 0:
                                                paint.setColor(Color.YELLOW);
                                                break;
                                            case 1:
                                                paint.setColor(Color.argb(255, 30, 155, 40));
                                                break;
                                        }
                                        canvas.drawRect(r, paint);

                                        r.left = r.left + width + padding;
                                        r.right = r.right + width + padding;
                                    }
                                }

                                ourHolder.unlockCanvasAndPost(canvas);
                            }
                        }

                        br.close();
                    } catch (Exception e) {
                        Log.e("Error","Failed downloading fix images of match game");
                    }
                    downMatchGameFix = false;
                }

                if (down) {
                    File f = new File(context.getFilesDir(), matchGameInfoFilename);
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(f));
                        String row;
                        String[] columns;

                        String num = br.readLine();
                        int numWords = Integer.valueOf(num);

                        if ((numWords > 0) && down) {

                            float minX = screenX / 4;
                            float top = (screenY * 6 / 7) + 40;
                            float bottom = screenY - 10;
                            int padding = 2;

                            RectF downBarOutline = new RectF(minX - padding, top - padding, (screenX * 3 / 4) + padding, bottom + padding);
                            RectF downBar = new RectF(minX, top, screenX * 3 / 4, bottom);

                            float width = ((screenX * 3 / 4) - minX - (numWords - 1) * 2) / numWords;

                            int[] downloadedFiles = new int[numWords];

                            for (int i = 0; i < downloadedFiles.length; i++) {
                                downloadedFiles[i] = -2;
                            }
                            int i = 0;
                            GetImagesMatchGame getImagesMatchGame;
                            while ((row = br.readLine()) != null) {
                                columns = row.split(";");

                                getImagesMatchGame = new GetImagesMatchGame(context, downloadedFiles, i);
                                downloaded = getImagesMatchGame.doInForeground(columns[0], "0");
                                i++;

                                RectF r = new RectF(minX, top, minX + width, bottom);

                                String downText = context.getString(R.string.downloading_text);

                                int textHeight = NORMAL;
                                paint.setTextSize(textHeight);
                                canvas = ourHolder.lockCanvas();
                                float textWidth = paint.measureText(downText, 0, downText.length());
                                paint.setColor(Color.BLACK);
                                canvas.drawText(downText,
                                        screenX / 2 - textWidth / 2,
                                        screenY * 2 / 3 + 60 + textHeight,
                                        paint);

                                paint.setColor(Color.BLACK);
                                canvas.drawRect(downBarOutline, paint);

                                paint.setColor(Color.argb(255, 130, 215, 220));
                                canvas.drawRect(downBar, paint);

                                //noinspection ForLoopReplaceableByForEach
                                for (int j = 0; j < downloadedFiles.length; j++) {
                                    if (downloadedFiles[j] != -2) {
                                        switch (downloadedFiles[j]) {
                                            case -1:
                                                paint.setColor(Color.RED);
                                                break;
                                            case 0:
                                                paint.setColor(Color.YELLOW);
                                                break;
                                            case 1:
                                                paint.setColor(Color.argb(255, 30, 155, 40));
                                                break;
                                        }
                                        canvas.drawRect(r, paint);

                                        r.left = r.left + width + padding;
                                        r.right = r.right + width + padding;
                                    }
                                }

                                ourHolder.unlockCanvasAndPost(canvas);
                            }
                        }

                        br.close();

                    } catch (Exception e) {
                        Log.e("Error:", "Failed downloading image");
                    }
                    down = false;
                }
            }

            if (downImages.contentEquals("3") && down || downDifferenceGameFix) {
                gameSelected = false;
                if (downDifferenceGameFix) {
                    File f = new File(context.getFilesDir(), differenceGameFixInfoFilename);

                    try {
                        BufferedReader br = new BufferedReader(new FileReader(f));
                        String row;
                        String[] columns;

                        String num = br.readLine();
                        int numWords = Integer.valueOf(num);

                        if (numWords > 0) {

                            float minX = screenX / 4;
                            float top = (screenY * 6 / 7) + 40;
                            float bottom = screenY - 10;
                            int padding = 2;

                            RectF downBarOutline = new RectF(minX - padding, top - padding, (screenX * 3 / 4) + padding, bottom + padding);
                            RectF downBar = new RectF(minX, top, screenX * 3 / 4, bottom);

                            float width = ((screenX * 3 / 4) - minX - (numWords - 1) * 2) / numWords;

                            int[] downloadedFiles = new int[numWords];

                            for (int i = 0; i < downloadedFiles.length; i++) {
                                downloadedFiles[i] = -2;
                            }
                            int i = 0;
                            GetImagesDifferenceGame getImagesDifferenceGame;

                            while ((row = br.readLine()) != null) {
                                columns = row.split(";");

                                getImagesDifferenceGame = new GetImagesDifferenceGame(context, downloadedFiles, i);
                                downloaded = getImagesDifferenceGame.doInForeground(columns[0], "0");
                                i++;

                                RectF r = new RectF(minX, top, minX + width, bottom);

                                String downText = context.getString(R.string.downloading_fix_text);

                                int textHeight = NORMAL;
                                paint.setTextSize(textHeight);
                                canvas = ourHolder.lockCanvas();
                                float textWidth = paint.measureText(downText, 0, downText.length());
                                paint.setColor(Color.BLACK);
                                canvas.drawText(downText,
                                        screenX / 2 - textWidth / 2,
                                        screenY * 2 / 3 + 30 + textHeight,
                                        paint);

                                paint.setColor(Color.BLACK);
                                canvas.drawRect(downBarOutline, paint);

                                paint.setColor(Color.argb(255, 130, 215, 220));
                                canvas.drawRect(downBar, paint);

                                //noinspection ForLoopReplaceableByForEach
                                for (int j = 0; j < downloadedFiles.length; j++) {
                                    if (downloadedFiles[j] != -2) {
                                        switch (downloadedFiles[j]) {
                                            case -1:
                                                paint.setColor(Color.RED);
                                                break;
                                            case 0:
                                                paint.setColor(Color.YELLOW);
                                                break;
                                            case 1:
                                                paint.setColor(Color.argb(255, 30, 155, 40));
                                                break;
                                        }
                                        canvas.drawRect(r, paint);

                                        r.left = r.left + width + padding;
                                        r.right = r.right + width + padding;
                                    }
                                }

                                ourHolder.unlockCanvasAndPost(canvas);
                            }
                        }

                        br.close();
                    } catch (Exception e) {
                        Log.e("Error","Failed downloading fix images of match game");
                    }
                    downDifferenceGameFix = false;
                }

                if (down) {
                    File f = new File(context.getFilesDir(), differenceGameInfoFilename);
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(f));
                        String row;
                        String[] columns;

                        String num = br.readLine();
                        int numWords = Integer.valueOf(num);

                        if ((numWords > 0) && down) {

                            float minX = screenX / 4;
                            float top = (screenY * 6 / 7) + 40;
                            float bottom = screenY - 10;
                            int padding = 2;

                            RectF downBarOutline = new RectF(minX - padding, top - padding, (screenX * 3 / 4) + padding, bottom + padding);
                            RectF downBar = new RectF(minX, top, screenX * 3 / 4, bottom);

                            float width = ((screenX * 3 / 4) - minX - (numWords - 1) * 2) / numWords;

                            int[] downloadedFiles = new int[numWords];

                            for (int i = 0; i < downloadedFiles.length; i++) {
                                downloadedFiles[i] = -2;
                            }
                            int i = 0;
                            GetImagesDifferenceGame getImagesDifferenceGame;

                            while ((row = br.readLine()) != null) {
                                columns = row.split(";");

                                getImagesDifferenceGame = new GetImagesDifferenceGame(context, downloadedFiles, i);
                                downloaded = getImagesDifferenceGame.doInForeground(columns[0], "0");
                                i++;

                                RectF r = new RectF(minX, top, minX + width, bottom);

                                String downText = context.getString(R.string.downloading_text);

                                int textHeight = NORMAL;
                                paint.setTextSize(textHeight);
                                canvas = ourHolder.lockCanvas();
                                float textWidth = paint.measureText(downText, 0, downText.length());
                                paint.setColor(Color.BLACK);
                                canvas.drawText(downText,
                                        screenX / 2 - textWidth / 2,
                                        screenY * 2 / 3 + 60 + textHeight,
                                        paint);

                                paint.setColor(Color.BLACK);
                                canvas.drawRect(downBarOutline, paint);

                                paint.setColor(Color.argb(255, 130, 215, 220));
                                canvas.drawRect(downBar, paint);

                                //noinspection ForLoopReplaceableByForEach
                                for (int j = 0; j < downloadedFiles.length; j++) {
                                    if (downloadedFiles[j] != -2) {
                                        switch (downloadedFiles[j]) {
                                            case -1:
                                                paint.setColor(Color.RED);
                                                break;
                                            case 0:
                                                paint.setColor(Color.YELLOW);
                                                break;
                                            case 1:
                                                paint.setColor(Color.argb(255, 30, 155, 40));
                                                break;
                                        }
                                        canvas.drawRect(r, paint);

                                        r.left = r.left + width + padding;
                                        r.right = r.right + width + padding;
                                    }
                                }

                                ourHolder.unlockCanvasAndPost(canvas);
                            }
                        }

                        br.close();

                    } catch (Exception e) {
                        Log.e("Error:", "Failed downloading image");
                    }
                    down = false;
                }
            }

            if (!downImages.contentEquals("-2")) {
                if (downloaded.contentEquals("-1")) {
                    downImages = "-2";
                    downloaded = "-2";
                    needDownload = false;
                }
                if (downImages.contentEquals("1") || downImages.contentEquals("3")) {
                    if ((System.currentTimeMillis() - lastTextTimeForDown) > textTime && !downloaded.contentEquals("-2")) {
                        downImages = "-2";
                        downloaded = "-2";
                        needDownload = false;
                    }
                } else {
                    if ((System.currentTimeMillis() - lastTextTimeForDown) > textTime) {
                        downImages = "-2";
                    }
                }
            }

            if (selectMinigame) {
                if (startFrameTime - lastLapseTime > lapseTime) {
                    match_game_frame_count++;
                    difference_game_frame_count++;
                    if (match_game_frame_count > 6 /*MATCH_GAME_TOTAL_FRAMES*/) {
                        match_game_frame_count = 0;
                    }
                    if (difference_game_frame_count > 5 /*DIFFERENCE_GAME_TOTAL_FRAMES*/) {
                        difference_game_frame_count = 0;
                    }
                    lastLapseTime = System.currentTimeMillis();
                }
            }

            if (log_sign_text_status != DISABLED) {
                if ((startFrameTime - lastTextTimeForUser) > textTime) {
                    log_sign_text_status = DISABLED;
                }
            }
        }
    }

    public void draw() {
        if(ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            float textWidth;
            float textHeight;

            canvas.drawColor(Color.argb(255, 130, 215, 220));

            if (!isOnline) {
                textHeight = BIG;
                String text = context.getString(R.string.no_internet);
                paint.setTextSize(textHeight);
                textWidth = paint.measureText(text, 0, text.length());

                paint.setColor(Color.BLACK);
                canvas.drawText(text,
                        screenX / 2 - textWidth / 2,
                        screenY * 2 / 9 + textHeight,
                        paint);
            }

            if (selectMinigame) {
                if (witchExplanation == NO_GAME) {
                    if (!return_button.isPressed()) {
                        paint.setColor(Color.argb(255, 170, 170, 170));
                    } else {
                        paint.setColor(Color.argb(255, 45, 40, 190));
                    }

                    canvas.drawRect(return_button.getRect(), paint);

                    if (!match_game_button.isPressed()) {
                        paint.setColor(Color.argb(255, 255, 255, 255));
                    } else {
                        paint.setColor(Color.argb(255, 45, 40, 190));
                    }

                    canvas.drawRect(match_game_button.getRect(), paint);
                    canvas.drawBitmap(match_game[match_game_frame_count],
                            match_game_button.getRect().left + (match_game_button.getRect().width() - match_game[match_game_frame_count].getWidth()) / 2,
                            match_game_button.getRect().top + (match_game_button.getRect().height() - match_game[match_game_frame_count].getHeight()) / 2,
                            null);

                    if (cantPlayMathGame) {
                        paint.setColor(Color.argb(255, 255, 255, 255));
                        canvas.drawRect(cantPlayMatchGame_button.getRect(), paint);
                        paint.setColor(Color.BLACK);
                        textHeight = (cantPlayMatchGame_button.getRect().height() / 2) - 6 /*for the padding*/;
                        String[] split = cantPlayMatchGame_button.getText().split(";");
                        textWidth = paint.measureText(split[0], 0, split[0].length());
                        canvas.drawText(split[0],
                                cantPlayMatchGame_button.getRect().left + cantPlayMatchGame_button.getRect().width() / 2 - textWidth / 2,
                                cantPlayMatchGame_button.getRect().top + textHeight + 2,
                                paint);
                        textWidth = paint.measureText(split[1], 0, split[1].length());
                        canvas.drawText(split[1],
                                cantPlayMatchGame_button.getRect().left + cantPlayMatchGame_button.getRect().width() / 2 - textWidth / 2,
                                cantPlayMatchGame_button.getRect().bottom - textHeight - 2,
                                paint);
                    }

                    if (!difference_game_button.isPressed()) {
                        paint.setColor(Color.argb(255, 255, 255, 255));
                    } else {
                        paint.setColor(Color.argb(255, 45, 40, 190));
                    }

                    canvas.drawRect(difference_game_button.getRect(), paint);
                    canvas.drawBitmap(difference_game[difference_game_frame_count],
                            difference_game_button.getRect().left + (difference_game_button.getRect().width() - difference_game[difference_game_frame_count].getWidth()) / 2,
                            difference_game_button.getRect().top + (difference_game_button.getRect().height() - difference_game[difference_game_frame_count].getHeight()) / 2,
                            null);

                    if (cantPlayDifferenceGame) {
                        paint.setColor(Color.argb(255, 255, 255, 255));
                        canvas.drawRect(cantPlayDifferenceGame_button.getRect(), paint);
                        paint.setColor(Color.BLACK);
                        textHeight = (cantPlayDifferenceGame_button.getRect().height() / 2) - 6 /*for the padding*/;
                        String[] split = cantPlayDifferenceGame_button.getText().split(";");
                        textWidth = paint.measureText(split[0], 0, split[0].length());
                        canvas.drawText(split[0],
                                cantPlayDifferenceGame_button.getRect().left + cantPlayDifferenceGame_button.getRect().width() / 2 - textWidth / 2,
                                cantPlayDifferenceGame_button.getRect().top + textHeight + 2,
                                paint);
                        textWidth = paint.measureText(split[1], 0, split[1].length());
                        canvas.drawText(split[1],
                                cantPlayDifferenceGame_button.getRect().left + cantPlayDifferenceGame_button.getRect().width() / 2 - textWidth / 2,
                                cantPlayDifferenceGame_button.getRect().bottom - textHeight - 2,
                                paint);
                    }

                    textHeight = HUGE;
                    paint.setTextSize(textHeight);
                    paint.setColor(Color.BLACK);
                    String text = context.getString(R.string.minigame_select);
                    textWidth = paint.measureText(text, 0, text.length());
                    canvas.drawText(text,
                            screenX / 2 - textWidth / 2,
                            10 + textHeight,
                            paint);

                    textHeight = NORMAL;
                    paint.setTextSize(textHeight);
                    textWidth = paint.measureText(return_button.getText(), 0, return_button.getText().length());
                    canvas.drawText(return_button.getText(),
                            return_button.getRect().left + return_button.getRect().width() / 2 - textWidth / 2,
                            return_button.getRect().top + return_button.getRect().height() / 2 + textHeight / 2,
                            paint);

                    textHeight = BIG;
                    paint.setTextSize(textHeight);
                    textWidth = paint.measureText(match_game_button.getText(), 0, match_game_button.getText().length());
                    canvas.drawText(match_game_button.getText(),
                            match_game_button.getRect().left + match_game_button.getRect().width() / 2 - textWidth / 2,
                            match_game_button.getRect().top - textHeight / 2,
                            paint);

                    textWidth = paint.measureText(difference_game_button.getText(), 0, difference_game_button.getText().length());
                    canvas.drawText(difference_game_button.getText(),
                            difference_game_button.getRect().left + difference_game_button.getRect().width() / 2 - textWidth / 2,
                            difference_game_button.getRect().top - textHeight / 2,
                            paint);

                    if (logged) {
                        textHeight = NORMAL;
                        paint.setTextSize(textHeight);
                        text = context.getString(R.string.highscore) + ": " + Integer.toString(maxScores[0]);
                        textWidth = paint.measureText(text, 0, text.length());
                        canvas.drawText(text,
                                match_game_button.getRect().left + match_game_button.getRect().width() / 2 - textWidth / 2,
                                match_game_button.getRect().bottom + textHeight,
                                paint);
                        text = context.getString(R.string.highscore) + ": " + Integer.toString(maxScores[1]);
                        textWidth = paint.measureText(text, 0, text.length());
                        canvas.drawText(text,
                                difference_game_button.getRect().left + difference_game_button.getRect().width() / 2 - textWidth / 2,
                                difference_game_button.getRect().bottom + textHeight,
                                paint);
                    }

                    canvas.drawRect(howToPlayMatchGame.getRect(), paint);

                    if (!howToPlayMatchGame.isPressed()) {
                        paint.setColor(Color.argb(255, 25, 210, 190));
                    } else {
                        paint.setColor(Color.argb(255, 45, 40, 190));
                    }

                    canvas.drawRect(howToPlayMatchGame.getRect(), paint);

                    canvas.drawRect(howToPlayDifferenceGame.getRect(), paint);

                    if (!howToPlayDifferenceGame.isPressed()) {
                        paint.setColor(Color.argb(255, 205, 205, 35));
                    } else {
                        paint.setColor(Color.argb(255, 45, 40, 190));
                    }

                    canvas.drawRect(howToPlayDifferenceGame.getRect(), paint);

                    textHeight = NORMAL;
                    paint.setTextSize(textHeight);
                    paint.setColor(Color.BLACK);
                    textWidth = paint.measureText(howToPlayMatchGame.getText(), 0, howToPlayMatchGame.getText().length());
                    canvas.drawText(howToPlayMatchGame.getText(),
                            howToPlayMatchGame.getRect().left + howToPlayMatchGame.getRect().width() / 2 - textWidth / 2,
                            howToPlayMatchGame.getRect().top + howToPlayMatchGame.getRect().height() / 2 + textHeight / 2,
                            paint);

                    textWidth = paint.measureText(howToPlayDifferenceGame.getText(), 0, howToPlayDifferenceGame.getText().length());
                    canvas.drawText(howToPlayDifferenceGame.getText(),
                            howToPlayDifferenceGame.getRect().left + howToPlayDifferenceGame.getRect().width() / 2 - textWidth / 2,
                            howToPlayDifferenceGame.getRect().top + howToPlayDifferenceGame.getRect().height() / 2 + textHeight / 2,
                            paint);
                }

                if (witchExplanation == MATCH_GAME) {
                    canvas.drawBitmap(howToMatchGame, screenX / 20, screenY / 20, null);

                    if (!return_button.isPressed()) {
                        paint.setColor(Color.argb(255, 170, 170, 170));
                    } else {
                        paint.setColor(Color.argb(255, 45, 40, 190));
                    }

                    canvas.drawRect(return_button.getRect(), paint);

                    textHeight = NORMAL;
                    paint.setTextSize(textHeight);
                    paint.setColor(Color.BLACK);
                    textWidth = paint.measureText(return_button.getText(), 0, return_button.getText().length());
                    canvas.drawText(return_button.getText(),
                            return_button.getRect().left + return_button.getRect().width() / 2 - textWidth / 2,
                            return_button.getRect().top + return_button.getRect().height() / 2 + textHeight / 2,
                            paint);
                }

                if (witchExplanation == DIFFERENCE_GAME) {
                    canvas.drawBitmap(howToDifferenceGame, screenX / 20, screenY / 20, null);

                    if (!return_button.isPressed()) {
                        paint.setColor(Color.argb(255, 170, 170, 170));
                    } else {
                        paint.setColor(Color.argb(255, 45, 40, 190));
                    }

                    canvas.drawRect(return_button.getRect(), paint);

                    textHeight = NORMAL;
                    paint.setTextSize(textHeight);
                    paint.setColor(Color.BLACK);
                    textWidth = paint.measureText(return_button.getText(), 0, return_button.getText().length());
                    canvas.drawText(return_button.getText(),
                            return_button.getRect().left + return_button.getRect().width() / 2 - textWidth / 2,
                            return_button.getRect().top + return_button.getRect().height() / 2 + textHeight / 2,
                            paint);
                }

                if (gameSelected) {

                    String text = context.getString(R.string.initializing_game);

                    textHeight = BIG;
                    paint.setTextSize(textHeight);
                    textWidth = paint.measureText(text, 0, text.length());
                    paint.setColor(Color.BLACK);
                    canvas.drawText(text,
                            screenX / 2 - textWidth / 2,
                            screenY * 2 / 8 + textHeight,
                            paint);
                }

            } else {
                if (!seeRanking) {
                    if (!play_button.isPressed()) {
                        paint.setColor(Color.argb(255, 130, 210, 55));
                    } else {
                        paint.setColor(Color.argb(255, 45, 40, 190));
                    }

                    canvas.drawRect(play_button.getRect(), paint);

                    if (!exit_button.isPressed()) {
                        paint.setColor(Color.argb(255, 220, 45, 45));
                    } else {
                        paint.setColor(Color.argb(255, 45, 40, 190));
                    }
                    canvas.drawRect(exit_button.getRect(), paint);

                    if (!status_button.isPressed()) {
                        paint.setColor(Color.argb(255, 130, 210, 55));
                    } else {
                        paint.setColor(Color.argb(255, 45, 40, 190));
                    }
                    canvas.drawRect(status_button.getRect(), paint);

                    if (!language_button.isPressed()) {
                        paint.setColor(Color.argb(255, 150, 100, 50));
                    } else {
                        paint.setColor(Color.argb(255, 45, 40, 190));
                    }
                    canvas.drawRect(language_button.getRect(), paint);

                    textHeight = BIG;

                    paint.setColor(Color.argb(255, 0, 0, 0));
                    paint.setTextSize(textHeight);

                    textWidth = paint.measureText(play_button.getText(), 0, play_button.getText().length());
                    canvas.drawText(play_button.getText(),
                            play_button.getRect().left + play_button.getRect().width() / 2 - textWidth / 2,
                            play_button.getRect().top + play_button.getRect().height() / 2 + textHeight / 2,
                            paint);

                    textWidth = paint.measureText(exit_button.getText(), 0, exit_button.getText().length());
                    canvas.drawText(exit_button.getText(),
                            exit_button.getRect().left + exit_button.getRect().width() / 2 - textWidth / 2,
                            exit_button.getRect().top + exit_button.getRect().height() / 2 + textHeight / 2,
                            paint);

                    if (logged) {
                        status_button.setText(context.getString(R.string.session));
                    } else {
                        status_button.setText(context.getString(R.string.login));
                    }

                    textHeight = NORMAL;
                    paint.setTextSize(textHeight);
                    textWidth = paint.measureText(status_button.getText(), 0, status_button.getText().length());
                    canvas.drawText(status_button.getText(),
                            status_button.getRect().left + status_button.getRect().width() / 2 - textWidth / 2,
                            status_button.getRect().top + status_button.getRect().height() / 2 + textHeight / 2,
                            paint);

                    paint.setColor(Color.WHITE);
                    textWidth = paint.measureText(language_button.getText(), 0, language_button.getText().length());
                    canvas.drawText(language_button.getText(),
                            language_button.getRect().left + language_button.getRect().width() / 2 - textWidth / 2,
                            language_button.getRect().top + language_button.getRect().height() / 2 + textHeight / 2,
                            paint);

                    if (change_language) {
                        paint.setColor(Color.argb(255, 150, 100, 50));
                        canvas.drawRect(login_background_button.getRect(), paint);

                        if (!ca_language_button.isPressed()) {
                            paint.setColor(Color.WHITE);
                        } else {
                            paint.setColor(Color.argb(255, 45, 40, 190));
                        }
                        canvas.drawRect(ca_language_button.getRect(), paint);

                        if (!en_language_button.isPressed()) {
                            paint.setColor(Color.WHITE);
                        } else {
                            paint.setColor(Color.argb(255, 45, 40, 190));
                        }
                        canvas.drawRect(en_language_button.getRect(), paint);

                        if (!es_language_button.isPressed()) {
                            paint.setColor(Color.WHITE);
                        } else {
                            paint.setColor(Color.argb(255, 45, 40, 190));
                        }
                        canvas.drawRect(es_language_button.getRect(), paint);

                        if (!zh_language_button.isPressed()) {
                            paint.setColor(Color.WHITE);
                        } else {
                            paint.setColor(Color.argb(255, 45, 40, 190));
                        }
                        canvas.drawRect(zh_language_button.getRect(), paint);

                        paint.setColor(Color.BLACK);
                        textHeight = BIG;
                        paint.setTextSize(textHeight);

                        textWidth = paint.measureText(ca_language_button.getText(), 0, ca_language_button.getText().length());
                        canvas.drawText(ca_language_button.getText(),
                                ca_language_button.getRect().left + ca_language_button.getRect().width() / 2 - textWidth / 2,
                                ca_language_button.getRect().top + ca_language_button.getRect().height() / 2 + textHeight / 2,
                                paint);

                        textWidth = paint.measureText(en_language_button.getText(), 0, en_language_button.getText().length());
                        canvas.drawText(en_language_button.getText(),
                                en_language_button.getRect().left + en_language_button.getRect().width() / 2 - textWidth / 2,
                                en_language_button.getRect().top + en_language_button.getRect().height() / 2 + textHeight / 2,
                                paint);

                        textWidth = paint.measureText(es_language_button.getText(), 0, es_language_button.getText().length());
                        canvas.drawText(es_language_button.getText(),
                                es_language_button.getRect().left + es_language_button.getRect().width() / 2 - textWidth / 2,
                                es_language_button.getRect().top + es_language_button.getRect().height() / 2 + textHeight / 2,
                                paint);

                        textWidth = paint.measureText(zh_language_button.getText(), 0, zh_language_button.getText().length());
                        canvas.drawText(zh_language_button.getText(),
                                zh_language_button.getRect().left + zh_language_button.getRect().width() / 2 - textWidth / 2,
                                zh_language_button.getRect().top + zh_language_button.getRect().height() / 2 + textHeight / 2,
                                paint);
                    }

                    if (login_status == LOGIN) {
                        if (new_user) {
                            paint.setColor(Color.argb(255, 215, 215, 215));
                            connect_button.setText(context.getString(R.string.signin));
                        } else {
                            paint.setColor(Color.argb(255, 215, 245, 210));
                            connect_button.setText(context.getString(R.string.login));
                        }
                        canvas.drawRect(login_background_button.getRect(), paint);

                        if (!login_button.isPressed()) {
                            paint.setColor(Color.argb(255, 215, 245, 210));
                        } else {
                            paint.setColor(Color.argb(255, 45, 40, 190));
                        }

                        canvas.drawRect(login_button.getRect(), paint);

                        if (!signin_button.isPressed()) {
                            paint.setColor(Color.argb(255, 215, 215, 215));
                        } else {
                            paint.setColor(Color.argb(255, 45, 40, 190));
                        }

                        canvas.drawRect(signin_button.getRect(), paint);

                        if (!connect_button.isPressed()) {
                            paint.setColor(Color.argb(255, 70, 210, 35));
                        } else {
                            paint.setColor(Color.argb(255, 45, 40, 190));
                        }

                        canvas.drawRect(connect_button.getRect(), paint);

                        RectF r = new RectF();

                        r.left = user_textBox.getRect().left - 2;
                        r.top = user_textBox.getRect().top - 2;
                        r.right = user_textBox.getRect().right + 2;
                        r.bottom = user_textBox.getRect().bottom + 2;
                        paint.setColor(Color.BLACK);
                        canvas.drawRect(r, paint);

                        paint.setColor(Color.WHITE);
                        canvas.drawRect(user_textBox.getRect(), paint);

                        r.left = pass_textBox.getRect().left - 2;
                        r.top = pass_textBox.getRect().top - 2;
                        r.right = pass_textBox.getRect().right + 2;
                        r.bottom = pass_textBox.getRect().bottom + 2;
                        paint.setColor(Color.BLACK);
                        canvas.drawRect(r, paint);

                        paint.setColor(Color.WHITE);
                        canvas.drawRect(pass_textBox.getRect(), paint);

                        paint.setColor(Color.BLACK);
                        textHeight = BIG;
                        paint.setTextSize(textHeight);
                        textWidth = paint.measureText(login_button.getText(), 0, login_button.getText().length());
                        canvas.drawText(login_button.getText(),
                                login_button.getRect().left + login_button.getRect().width() / 2 - textWidth / 2,
                                login_button.getRect().top + login_button.getRect().height() / 2 + textHeight / 2,
                                paint);

                        textWidth = paint.measureText(signin_button.getText(), 0, signin_button.getText().length());
                        canvas.drawText(signin_button.getText(),
                                signin_button.getRect().left + signin_button.getRect().width() / 2 - textWidth / 2,
                                signin_button.getRect().top + signin_button.getRect().height() / 2 + textHeight / 2,
                                paint);

                        textHeight = NORMAL;
                        paint.setTextSize(textHeight);
                        textWidth = paint.measureText(connect_button.getText(), 0, connect_button.getText().length());
                        canvas.drawText(connect_button.getText(),
                                connect_button.getRect().left + connect_button.getRect().width() / 2 - textWidth / 2,
                                connect_button.getRect().top + connect_button.getRect().height() / 2 + textHeight / 2,
                                paint);

                        int padding = 5;

                        textHeight = NORMAL;
                        paint.setTextSize(textHeight);
                        String text = context.getString(R.string.username_text) + ": ";
                        textWidth = paint.measureText(text, 0, text.length());
                        canvas.drawText(text,
                                user_textBox.getRect().left - textWidth - padding,
                                user_textBox.getRect().top + user_textBox.getRect().height() / 2 + textHeight / 2,
                                paint);
                        text = context.getString(R.string.password_text) + ": ";
                        textWidth = paint.measureText(text, 0, text.length());
                        canvas.drawText(text,
                                pass_textBox.getRect().left - textWidth - padding,
                                pass_textBox.getRect().top + pass_textBox.getRect().height() / 2 + textHeight / 2,
                                paint);

                        canvas.drawText(user_textBox.getText(),
                                user_textBox.getRect().left + 5 /*padding*/,
                                user_textBox.getRect().top + user_textBox.getRect().height() / 2 + textHeight / 2,
                                paint);

                        String passStars = "";
                        for (int i = 0; i < pass_textBox.getText().length(); i++) {
                            if (passStars.length() < 30 /*MAX_STARS_LENGTH*/) {
                                passStars = passStars + "*";
                            }
                        }
                        canvas.drawText(passStars,
                                pass_textBox.getRect().left + 5 /*padding*/,
                                pass_textBox.getRect().top + pass_textBox.getRect().height() / 2 + textHeight / 2,
                                paint);
                    }

                    if (login_status == LOGGED && logged) {
                        RectF r = new RectF(screenX / 2 - (screenX / 4),
                                screenY / 7,
                                screenX * 3 / 4,
                                screenY / 7 + screenY * 2 / 3);

                        paint.setColor(Color.argb(255, 230, 230, 230));
                        canvas.drawRect(r, paint);

                        connect_button.setText(context.getString(R.string.logout));

                        if(!connect_button.isPressed()) {
                            paint.setColor(Color.argb(255, 160, 50, 50));
                        } else {
                            paint.setColor(Color.argb(255, 45, 40, 190));
                        }
                        canvas.drawRect(connect_button.getRect(), paint);

                        if(!ranking_button.isPressed()) {
                            paint.setColor(Color.argb(255, 130, 220, 215));
                        } else {
                            paint.setColor(Color.argb(255, 45, 40, 190));
                        }
                        canvas.drawRect(ranking_button.getRect(), paint);

                        paint.setColor(Color.BLACK);

                        textHeight = BIG;
                        paint.setTextSize(textHeight);
                        textWidth = paint.measureText(connect_button.getText(), 0, connect_button.getText().length());
                        canvas.drawText(connect_button.getText(),
                                connect_button.getRect().left + connect_button.getRect().width() / 2 - textWidth / 2,
                                connect_button.getRect().top + connect_button.getRect().height() / 2 + textHeight / 2,
                                paint);

                        textWidth = paint.measureText(ranking_button.getText(), 0, ranking_button.getText().length());
                        canvas.drawText(ranking_button.getText(),
                                ranking_button.getRect().left + ranking_button.getRect().width() / 2 - textWidth / 2,
                                ranking_button.getRect().top + ranking_button.getRect().height() / 2 + textHeight / 2,
                                paint);

                        textHeight = BIG;
                        paint.setTextSize(textHeight);

                        String text = context.getString(R.string.username_text) + ":";
                        float maxTextWidth = paint.measureText("DIFFERENCE GAME:", 0, "DIFFERENCE GAME:".length());
                        textWidth = paint.measureText(text, 0, text.length());
                        canvas.drawText(text,
                                screenX / 2 - (screenX / 4) + maxTextWidth / 2 - textWidth / 2 + 10 /*padding*/,
                                screenY / 7 + textHeight + screenY / 12,
                                paint);

                        textWidth = paint.measureText(username, 0, username.length());
                        while ((screenX / 2 - (screenX / 4) + maxTextWidth + 30 + textWidth) > r.right) {
                            textHeight = textHeight - 2;
                            if (textHeight <= 2) {
                                textHeight = 2;
                                paint.setTextSize(textHeight);
                                break;
                            }
                            paint.setTextSize(textHeight);
                            textWidth = paint.measureText(username, 0, username.length());
                        }
                        textHeight = BIG;
                        canvas.drawText(username,
                                screenX / 2 - (screenX / 4) + maxTextWidth + 30 /*padding*/,
                                screenY / 7 + textHeight  - (textHeight / 2 - paint.getTextSize() / 2) + screenY / 12,
                                paint);


                        paint.setTextSize(textHeight);
                        text = context.getString(R.string.match_game) + ":";
                        textWidth = paint.measureText(text, 0, text.length());
                        canvas.drawText(text,
                                screenX / 2 - (screenX / 4) + maxTextWidth / 2 - textWidth / 2 + 10 /*padding*/,
                                screenY / 7 + textHeight * 3 + 5 + screenY / 12,
                                paint);

                        textWidth = paint.measureText(String.valueOf(maxScores[0]), 0, String.valueOf(maxScores[0]).length());
                        while ((screenX / 2 - (screenX / 4) + maxTextWidth + 30 + textWidth) > r.right) {
                            textHeight = textHeight - 2;
                            if (textHeight <= 2) {
                                textHeight = 2;
                                paint.setTextSize(textHeight);
                                break;
                            }
                            paint.setTextSize(textHeight);
                            textWidth = paint.measureText(String.valueOf(maxScores[0]), 0, String.valueOf(maxScores[0]).length());
                        }
                        textHeight = BIG;
                        canvas.drawText(String.valueOf(maxScores[0]),
                                screenX / 2 - (screenX / 4) + maxTextWidth + 30 /*padding*/,
                                screenY / 7 + textHeight * 3 + 5 - (textHeight / 2 - paint.getTextSize() / 2) + screenY / 12,
                                paint);

                        paint.setTextSize(textHeight);
                        text = context.getString(R.string.difference_game) + ":";
                        textWidth = paint.measureText(text, 0, text.length());
                        canvas.drawText(text,
                                screenX / 2 - (screenX / 4) + maxTextWidth / 2 - textWidth / 2 + 10 /*padding*/,
                                screenY / 7 + textHeight * 4 + 5 + screenY / 12,
                                paint);

                        textWidth = paint.measureText(String.valueOf(maxScores[1]), 0, String.valueOf(maxScores[1]).length());
                        while ((screenX / 2 - (screenX / 4) + maxTextWidth + 30 + textWidth) > r.right) {
                            textHeight = textHeight - 2;
                            if (textHeight <= 2) {
                                textHeight = 2;
                                paint.setTextSize(textHeight);
                                break;
                            }
                            paint.setTextSize(textHeight);
                            textWidth = paint.measureText(String.valueOf(maxScores[1]), 0, String.valueOf(maxScores[1]).length());
                        }
                        textHeight = BIG;
                        canvas.drawText(String.valueOf(maxScores[1]),
                                screenX / 2 - (screenX / 4) + maxTextWidth + 30 /*padding*/,
                                screenY / 7 + textHeight * 4 + 5 - (textHeight / 2 - paint.getTextSize() / 2) + screenY / 12,
                                paint);
                    }
                } else {
                    canvas.drawColor(Color.argb(255, 130, 215, 220));

                    paint.setColor(Color.WHITE);
                    canvas.drawRect(rankingWindow, paint);

                    String[] row;

                    paint.setColor(Color.BLACK);
                    textHeight = BIG;
                    paint.setTextSize(textHeight);
                    String text = context.getString(R.string.users_text);
                    textWidth = paint.measureText(text, 0, text.length());

                    int padding = 30;

                    canvas.drawText(text,
                            rankingWindow.left + padding + rankingWindow.width() / 4 - textWidth / 2,
                            rankingWindow.top + 2 + textHeight,
                            paint);

                    if (!matchGameOrDifferenceGame) {
                        for (int i = 0; i < rankingScoresMatchGame.length; i++) {
                            row = rankingScoresMatchGame[i].split(",");

                            textWidth = paint.measureText(row[0], 0, row[0].length());
                            while ((screenX / 2 - (screenX / 4) + rankingWindow.width() / 4 - textWidth / 2 + padding + textWidth) > screenX / 2) {
                                textHeight = textHeight - 2;
                                if (textHeight <= 2) {
                                    textHeight = 2;
                                    paint.setTextSize(textHeight);
                                    break;
                                }
                                paint.setTextSize(textHeight);
                                textWidth = paint.measureText(row[0], 0, row[0].length()); // the text width changes too because we changed the text height
                            }
                            canvas.drawText(row[0],
                                    rankingWindow.left + padding + rankingWindow.width() / 4 - textWidth / 2,
                                    rankingWindow.top + 2 + textHeight + (i + 1)*(textHeight + 2) - (textHeight / 2 - paint.getTextSize() / 2),
                                    paint);

                            textHeight = BIG;
                            paint.setTextSize(textHeight);
                        }
                        text = context.getString(R.string.match_game);
                        textWidth = paint.measureText(text, 0, text.length());
                        canvas.drawText(text,
                                rankingWindow.right - padding - rankingWindow.width() / 4 - textWidth / 2,
                                rankingWindow.top + 2 + textHeight,
                                paint);

                        for (int i = 0; i < rankingScoresMatchGame.length; i++) {
                            row = rankingScoresMatchGame[i].split(",");

                            textWidth = paint.measureText(row[1], 0, row[1].length());
                            while ((screenX / 2 - (screenX / 4) + rankingWindow.width() / 4 - textWidth / 2 + padding + textWidth) > screenX / 2) {
                                textHeight = textHeight - 2;
                                if (textHeight <= 2) {
                                    textHeight = 2;
                                    paint.setTextSize(textHeight);
                                    break;
                                }
                                paint.setTextSize(textHeight);
                                textWidth = paint.measureText(row[1], 0, row[1].length());
                            }
                            textHeight = BIG;

                            canvas.drawText(row[1],
                                    rankingWindow.right - padding - rankingWindow.width() / 4 - textWidth / 2,
                                    rankingWindow.top + 2 + textHeight + (i + 1) * (textHeight + 2) - (textHeight / 2 - paint.getTextSize() / 2),
                                    paint);
                            paint.setTextSize(textHeight);
                        }
                    } else {
                        textHeight = BIG;
                        paint.setTextSize(textHeight);

                        for (int i = 0; i < rankingScoresDifferenceGame.length; i++) {
                            row = rankingScoresDifferenceGame[i].split(",");

                            textWidth = paint.measureText(row[0], 0, row[0].length());
                            while ((screenX / 2 - (screenX / 4) + rankingWindow.width() / 4 - textWidth / 2 + padding + textWidth) > screenX / 2) {
                                textHeight = textHeight - 2;
                                if (textHeight <= 2) {
                                    textHeight = 2;
                                    paint.setTextSize(textHeight);
                                    break;
                                }
                                paint.setTextSize(textHeight);
                                textWidth = paint.measureText(row[0], 0, row[0].length());
                            }
                            canvas.drawText(row[0],
                                    rankingWindow.left + padding + rankingWindow.width() / 4 - textWidth / 2,
                                    rankingWindow.top + 2 + textHeight + (i + 1)*(textHeight + 2) - (textHeight / 2 - paint.getTextSize() / 2),
                                    paint);

                            textHeight = BIG;
                            paint.setTextSize(textHeight);
                        }
/*
                        textHeight = NORMAL;
                        paint.setTextSize(textHeight);*/
                        text = context.getString(R.string.difference_game);
                        textWidth = paint.measureText(text, 0, text.length());
                        canvas.drawText(text,
                                rankingWindow.right - padding - rankingWindow.width() / 4 - textWidth / 2,
                                rankingWindow.top + 2 + textHeight,
                                paint);

                        for (int i = 0; i < rankingScoresDifferenceGame.length; i++) {
                            row = rankingScoresDifferenceGame[i].split(",");
                            textWidth = paint.measureText(row[1], 0, row[1].length());
                            while ((screenX / 2 - (screenX / 4) + rankingWindow.width() / 4 - textWidth / 2 + padding + textWidth) > screenX / 2) {
                                textHeight = textHeight - 2;
                                if (textHeight <= 2) {
                                    textHeight = 2;
                                    paint.setTextSize(textHeight);
                                    break;
                                }
                                paint.setTextSize(textHeight);
                                textWidth = paint.measureText(row[1], 0, row[1].length());
                            }
                            textHeight = BIG;

                            canvas.drawText(row[1],
                                    rankingWindow.right - padding - rankingWindow.width() / 4 - textWidth / 2,
                                    rankingWindow.top + 2 + textHeight + (i + 1) * (textHeight + 2) - (textHeight / 2 - paint.getTextSize() / 2),
                                    paint);
                            paint.setTextSize(textHeight);
                        }
                    }

                    if(!return_button.isPressed()) {
                        paint.setColor(Color.argb(255, 170, 170, 170));
                    } else {
                        paint.setColor(Color.argb(255, 45, 40, 190));
                    }

                    canvas.drawRect(return_button.getRect(), paint);

                    paint.setColor(Color.BLACK);
                    textHeight = NORMAL;
                    paint.setTextSize(textHeight);
                    textWidth = paint.measureText(return_button.getText(), 0, return_button.getText().length());
                    canvas.drawText(return_button.getText(),
                            return_button.getRect().left + return_button.getRect().width() / 2 - textWidth / 2,
                            return_button.getRect().top + return_button.getRect().height() / 2 + textHeight / 2,
                            paint);

                    if(!seeDifferenceGameScore.isPressed()) {
                        paint.setColor(Color.argb(255, 205, 205, 35));
                    } else {
                        paint.setColor(Color.argb(255, 45, 40, 190));
                    }

                    canvas.drawRect(seeDifferenceGameScore.getRect(), paint);

                    paint.setColor(Color.BLACK);
                    textHeight = NORMAL;
                    paint.setTextSize(textHeight);
                    textWidth = paint.measureText(seeDifferenceGameScore.getText(), 0, seeDifferenceGameScore.getText().length());
                    canvas.drawText(seeDifferenceGameScore.getText(),
                            seeDifferenceGameScore.getRect().left + seeDifferenceGameScore.getRect().width() / 2 - textWidth / 2,
                            seeDifferenceGameScore.getRect().top + seeDifferenceGameScore.getRect().height() / 2 + textHeight / 2,
                            paint);

                    if(!seeMatchGameScore.isPressed()) {
                        paint.setColor(Color.argb(255, 25, 210, 190));
                    } else {
                        paint.setColor(Color.argb(255, 45, 40, 190));
                    }

                    canvas.drawRect(seeMatchGameScore.getRect(), paint);

                    paint.setColor(Color.BLACK);
                    textWidth = paint.measureText(seeMatchGameScore.getText(), 0, seeMatchGameScore.getText().length());
                    canvas.drawText(seeMatchGameScore.getText(),
                            seeMatchGameScore.getRect().left + seeMatchGameScore.getRect().width() / 2 - textWidth / 2,
                            seeMatchGameScore.getRect().top + seeMatchGameScore.getRect().height() / 2 + textHeight / 2,
                            paint);

                    paint.setColor(Color.BLACK);
                    textHeight = HUGE;
                    paint.setTextSize(textHeight);
                    text = context.getString(R.string.top_10);
                    textWidth = paint.measureText(text, 0, text.length());
                    canvas.drawText(text,
                            screenX / 2 - textWidth / 2,
                            textHeight + 25 /*padding*/,
                            paint);
                }
            }

            if (log_sign_text_status != DISABLED && !selectMinigame && !seeRanking) {
                String log_sign_text = "";
                switch (log_sign_text_status) {
                    case LOGGED_SUCCESS:
                        log_sign_text = context.getString(R.string.logged_success);
                        break;
                    case LOGGED_FAIL:
                        log_sign_text = context.getString(R.string.logged_fail);
                        break;
                    case SIGNIN_SUCCESS:
                        log_sign_text = context.getString(R.string.signin_success);
                        break;
                    case SIGNIN_FAIL:
                        log_sign_text = context.getString(R.string.signin_fail);
                        break;
                }

                paint.setColor(Color.BLACK);
                textHeight = BIG;
                paint.setTextSize(textHeight);
                textWidth = paint.measureText(log_sign_text, 0, log_sign_text.length());
                canvas.drawText(log_sign_text,
                        screenX / 2 - (screenX / 4) + screenX / 2 / 2 - textWidth / 2,
                        screenY / 7 + textHeight + 5 /*padding*/,
                        paint);
            }

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch(motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (selectMinigame) {
                    if (witchExplanation == NO_GAME) {
                        if (return_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            return_button.inverseIsPressed();
                        }

                        if (!needDownload) {
                            if (match_game_button.getRect().contains(motionEvent.getX(), motionEvent.getY()) && !cantPlayMathGame) {
                                match_game_button.inverseIsPressed();
                            }

                            if (difference_game_button.getRect().contains(motionEvent.getX(), motionEvent.getY()) && !cantPlayDifferenceGame) {
                                difference_game_button.inverseIsPressed();
                            }

                            if (howToPlayDifferenceGame.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                howToPlayDifferenceGame.inverseIsPressed();
                            }

                            if (howToPlayMatchGame.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                howToPlayMatchGame.inverseIsPressed();
                            }
                        }
                    }

                    if (witchExplanation == MATCH_GAME || witchExplanation == DIFFERENCE_GAME) {
                        if (return_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            return_button.inverseIsPressed();
                        }
                    }
                } else {
                    if (login_status == NOT_PRESSED) {
                        if (play_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            play_button.inverseIsPressed();
                            if (logged) {
                                readScores(username);
                            }
                        }

                        if (exit_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            exit_button.inverseIsPressed();
                        }
                    }

                    if (!seeRanking) {
                        if (status_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            status_button.inverseIsPressed();
                            if (logged) {
                                readScores(username);
                            }
                        }

                        if (login_status == LOGIN) {
                            if (login_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                login_button.inverseIsPressed();
                            }

                            if (signin_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                signin_button.inverseIsPressed();
                            }

                            if (connect_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                connect_button.inverseIsPressed();
                            }
                        }

                        if (login_status == LOGGED) {
                            if (ranking_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                ranking_button.inverseIsPressed();
                            }

                            if (connect_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                connect_button.inverseIsPressed();
                            }
                        }
                    } else {
                        if (return_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            return_button.inverseIsPressed();
                        }

                        if (seeDifferenceGameScore.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            seeDifferenceGameScore.inverseIsPressed();
                        }

                        if (seeMatchGameScore.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            seeMatchGameScore.inverseIsPressed();
                        }
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
                if (selectMinigame) {
                    if (witchExplanation == NO_GAME) {
                        if (!return_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            if (return_button.isPressed()) {
                                return_button.inverseIsPressed();
                            }
                        } else {
                            selectMinigame = false;
                            if (return_button.isPressed()) {
                                return_button.inverseIsPressed();
                            }
                        }

                        if (!needDownload) {
                            if (!howToPlayDifferenceGame.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if (howToPlayDifferenceGame.isPressed()) {
                                    howToPlayDifferenceGame.inverseIsPressed();
                                }
                            } else {
                                witchExplanation = DIFFERENCE_GAME;
                                if (howToPlayDifferenceGame.isPressed()) {
                                    howToPlayDifferenceGame.inverseIsPressed();
                                }
                            }

                            if (!howToPlayMatchGame.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if (howToPlayMatchGame.isPressed()) {
                                    howToPlayMatchGame.inverseIsPressed();
                                }
                            } else {
                                witchExplanation = MATCH_GAME;
                                if (howToPlayMatchGame.isPressed()) {
                                    howToPlayMatchGame.inverseIsPressed();
                                }
                            }

                            if (!match_game_button.getRect().contains(motionEvent.getX(), motionEvent.getY()) && !cantPlayMathGame) {
                                if (match_game_button.isPressed()) {
                                    match_game_button.inverseIsPressed();
                                }
                            } else {
                                if (match_game_button.isPressed()) {
                                    match_game_button.inverseIsPressed();
                                }

                                if (isOnline) {
                                    selectMinigame = true;

                                    gameSelected = true;

                                    try {
                                        numImagesMatchGameInBBDD = new CheckNumGame_Images().execute("1").get();
                                    } catch (Exception e) {
                                        Log.e("Error", "Failed checking num of images.");
                                    }

                                    if (numImagesMatchGameInBBDD == 0) {
                                        cantPlayMathGame = true;
                                        break;
                                    }

                                    File ffix = new File(context.getFilesDir(), matchGameFixInfoFilename);
                                    int fFixNumImgs = 0;

                                    if (ffix.exists()) {
                                        String row;
                                        String[] columns;

                                        try {
                                            BufferedReader br = new BufferedReader(new FileReader(ffix));
                                            File auxImage;
                                            ArrayList<String> imageNames = new ArrayList<>();

                                            if (br.readLine() != null) {
                                                while ((row = br.readLine()) != null) {
                                                    columns = row.split(";");
                                                    // this function returns 0 if need erase the image and 1 if still need the image
                                                    int correct = new CheckMatchGameImages().execute(columns[0], "1").get();
                                                    if (correct != -1) {
                                                        if (correct == 1) {
                                                            imageNames.add(row);
                                                        } else {
                                                            if (correct != 2) {
                                                                auxImage = new File(context.getFilesDir(), columns[0]);

                                                                if (auxImage.exists()) {
                                                                    System.out.println("Deleting file: " + auxImage.getAbsolutePath());
                                                                    auxImage.delete();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            br.close();

                                            if (ffix.exists()) {
                                                ffix.delete();
                                                ffix.createNewFile();
                                            }

                                            BufferedWriter bw = new BufferedWriter(new FileWriter(ffix));

                                            System.out.println("Check fix imageNames size: " + imageNames.size());
                                            if (imageNames.size() > 0) {
                                                bw.write(String.valueOf(imageNames.size()));
                                                bw.write(System.getProperty("line.separator"));

                                                for (int i = 0; i < imageNames.size(); i++) {
                                                    bw.write(imageNames.get(i));
                                                    bw.write(System.getProperty("line.separator"));
                                                }

                                                bw.close();
                                            } else {
                                                needDownloadFix = true;
                                            }

                                        } catch (Exception e) {
                                            Log.e("Error", "Failed checking old fix images.");
                                        }

                                        try {
                                            BufferedReader br = new BufferedReader(new FileReader(ffix));
                                            String next;

                                            fFixNumImgs = Integer.valueOf(br.readLine());

                                            System.out.println("num of fix images: " + fFixNumImgs);

                                            if (fFixNumImgs > 0) {
                                                String list = "'" + br.readLine().split(";")[0] + "'";
                                                while ((next = br.readLine()) != null) {
                                                    list += ",'" + next.split(";")[0] + "'";
                                                }

                                                needDownloadFix = new CheckNewMatchGameFixImages().execute(list).get();
                                            } else {
                                                needDownloadFix = true;
                                            }

                                        } catch (Exception e) {
                                            Log.e("Error", "Failed checking fix images of match game.");
                                        }
                                    } else {
                                        needDownloadFix = true;
                                    }

                                    System.out.println("Check ffix need download:" + needDownload + ", " + needDownloadFix);

                                    File f = new File(context.getFilesDir(), matchGameInfoFilename);
                                    if (f.exists()) {
                                        String row;
                                        String[] columns;

                                        try {
                                            BufferedReader br = new BufferedReader(new FileReader(f));
                                            File auxImage;
                                            ArrayList<String> imageNames = new ArrayList<>();

                                            if (br.readLine() != null) {
                                                while ((row = br.readLine()) != null) {
                                                    columns = row.split(";");
                                                    // this function returns 0 if need erase the image and 1 if still need the image
                                                    int correct = new CheckMatchGameImages().execute(columns[0], "0").get();
                                                    if (correct != -1) {
                                                        if (correct == 1) {
                                                            imageNames.add(row);
                                                        } else {
                                                            if (correct != 2) {
                                                                auxImage = new File(context.getFilesDir(), columns[0]);

                                                                if (auxImage.exists()) {
                                                                    System.out.println("Deleting file: " + auxImage.getAbsolutePath());
                                                                    auxImage.delete();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            br.close();

                                            if (f.exists()) {
                                                f.delete();
                                                f.createNewFile();
                                            }

                                            BufferedWriter bw = new BufferedWriter(new FileWriter(f));

                                            System.out.println("Check imageNames size: " + imageNames.size());
                                            if (imageNames.size() > 0) {
                                                bw.write(String.valueOf(imageNames.size()));
                                                bw.write(System.getProperty("line.separator"));

                                                for (int i = 0; i < imageNames.size(); i++) {
                                                    bw.write(imageNames.get(i));
                                                    bw.write(System.getProperty("line.separator"));
                                                }

                                                bw.close();
                                            } else {
                                                needDownload = true;
                                            }

                                        } catch (Exception e) {
                                            Log.e("Error", "Failed checking old images.");
                                        }

                                        if (!needDownload) {
                                            try {
                                                System.out.println("Checking images...");
                                                BufferedReader br = new BufferedReader(new FileReader(f));

                                                int fNumImgs = Integer.valueOf(br.readLine());

                                                if ((fFixNumImgs + fNumImgs) < Math.min(12, numImagesMatchGameInBBDD)) {
                                                    needDownload = true;
                                                    break;
                                                }

                                                while ((row = br.readLine()) != null) {
                                                    columns = row.split(";");
                                                    f = new File(context.getFilesDir(), columns[0]);
                                                    if (!f.exists()) {
                                                        needDownload = true;
                                                        break;
                                                    }
                                                }
                                            } catch (Exception e) {
                                                Log.e("Error:", "Failed checking images.");
                                            }
                                        }
                                    } else {
                                        needDownload = true;
                                        lastTextTimeForDown = System.currentTimeMillis();
                                    }

                                    System.out.println("Check f need download:" + needDownload + ", " + needDownloadFix);

                                    if (!needDownload && !needDownloadFix) {
                                        try {
                                            new UpdatePlayTimes().execute(username, "0");
                                        } catch (Exception e) {
                                            Log.e("Error:", "Failed updating play times.");
                                        }
                                        Intent intent = new Intent(context, MatchGameActivity.class);
                                        Bundle b = new Bundle();
                                        b.putString("BDFilename", matchGameInfoFilename);
                                        b.putString("BDFixFilename", matchGameFixInfoFilename);
                                        b.putString("username", username);
                                        b.putInt("num_games", NUM_OF_MINIGAMES);
                                        intent.putExtras(b);
                                        gameSelected = false;
                                        context.startActivity(intent);
                                        if (logged) {
                                            readScores(username);
                                        }
                                    } else {
                                        if (needDownloadFix) {
                                            System.out.println("Downloading match game fix images");
                                            ffix = new File(context.getFilesDir(), matchGameFixInfoFilename);
                                            try {
                                                String list;
                                                if (ffix.exists()) {
                                                    BufferedReader br = new BufferedReader(new FileReader(ffix));

                                                    if (Integer.valueOf(br.readLine()) > 0) {
                                                        String next;
                                                        list = "'" + br.readLine().split(";")[0] + "'";
                                                        while ((next = br.readLine()) != null) {
                                                            list += ",'" + next.split(";")[0] + "'";
                                                        }
                                                    } else {
                                                        list = "''";
                                                    }
                                                } else {
                                                    list = "''";
                                                }

                                                downMatchGameFix = new GetMatchGameFixInfo(context).execute(matchGameFixInfoFilename, list).get();
                                            } catch (Exception e) {
                                                Log.e("Error:", "Failed downloading txt of fix match game images.");
                                            }
                                        }

                                        if (needDownload) {
                                            System.out.println("Downloading match game images");
                                            f = new File(context.getFilesDir(), matchGameInfoFilename);
                                            System.out.println(f.getAbsolutePath());
                                            try {
                                                String list;
                                                int numFImgs = 0;
                                                if (f.exists()) {
                                                    BufferedReader br = new BufferedReader(new FileReader(f));
                                                    String readLine = br.readLine();
                                                    if (readLine == null) {
                                                        numFImgs = 0;
                                                    } else {
                                                        numFImgs = Integer.valueOf(readLine);
                                                    }
                                                    if (numFImgs > 0) {
                                                        String next;
                                                        list = "'" + br.readLine().split(";")[0] + "'";
                                                        while ((next = br.readLine()) != null) {
                                                            list += ",'" + next.split(";")[0] + "'";
                                                        }
                                                    } else {
                                                        System.out.println("list void");
                                                        list = "''";
                                                    }
                                                } else {
                                                    list = "''";
                                                }

                                                int limit = 50 - (numFImgs);
                                                System.out.println("list: " + list + "; limit: " + limit);
                                                downImages = new GetMatchGameInfo(context).execute(matchGameInfoFilename, list, String.valueOf(limit)).get();
                                            } catch (Exception e) {
                                                Log.e("Error:", "Failed downloading txt of match game images.");
                                            }
                                        }
                                    }

                                    if (downImages.contentEquals("1")) {
                                        down = true;
                                    }
                                }
                            }


                            if (!difference_game_button.getRect().contains(motionEvent.getX(), motionEvent.getY()) && !cantPlayDifferenceGame) {
                                if (difference_game_button.isPressed()) {
                                    difference_game_button.inverseIsPressed();
                                }
                            } else {
                                if (difference_game_button.isPressed()) {
                                    difference_game_button.inverseIsPressed();
                                }

                                if (isOnline) {
                                    selectMinigame = true;
                                    gameSelected = true;

                                    try {
                                        numClusterDifferenceGameBBDD = new CheckNumGame_Images().execute("0").get();
                                    } catch (Exception e) {
                                        Log.e("Error", "Failed checking num of images.");
                                    }

                                    if (numClusterDifferenceGameBBDD == 0) {
                                        cantPlayDifferenceGame = true;
                                        break;
                                    }

                                    File ffix = new File(context.getFilesDir(), differenceGameFixInfoFilename);
                                    int fFixNumImgs = 0;

                                    if (ffix.exists()) {
                                        String row;
                                        String[] columns;

                                        try {
                                            BufferedReader br = new BufferedReader(new FileReader(ffix));
                                            File auxImage;
                                            ArrayList<String> imageNames = new ArrayList<>();

                                            if (!br.readLine().contains("0")) {
                                                while ((row = br.readLine()) != null) {/*
                                                    if (row.contains("0")) {
                                                        break;
                                                    }*/
                                                    columns = row.split(";");
                                                    // this function returns 0 if need erase the image and 1 if still need the image
                                                    int correct = new CheckDifferenceGameImages().execute(columns[0], "1").get();
                                                    if (correct != -1) {
                                                        if (correct == 1) {
                                                            imageNames.add(row);
                                                        } else {
                                                            if (correct != 2) {
                                                                auxImage = new File(context.getFilesDir(), columns[0]);

                                                                if (auxImage.exists()) {
                                                                    System.out.println("Deleting file: " + auxImage.getAbsolutePath());
                                                                    auxImage.delete();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                if (ffix.exists()) {
                                                    ffix.delete();
                                                    ffix.createNewFile();
                                                }

                                                BufferedWriter bw = new BufferedWriter(new FileWriter(ffix));

                                                System.out.println("Check fix imageNames size: " + imageNames.size());
                                                if (imageNames.size() > 0) {
                                                    bw.write(String.valueOf(imageNames.size()));
                                                    bw.write(System.getProperty("line.separator"));

                                                    for (int i = 0; i < imageNames.size(); i++) {
                                                        bw.write(imageNames.get(i));
                                                        bw.write(System.getProperty("line.separator"));
                                                    }

                                                    bw.close();
                                                } else {
                                                    needDownloadFix = true;
                                                }
                                            }
                                            br.close();


                                        } catch (Exception e) {
                                            Log.e("Error", "Failed checking old fix images.");
                                        }

                                        if (ffix.exists()) {
                                            try {
                                                BufferedReader br = new BufferedReader(new FileReader(ffix));
                                                String next;

                                                fFixNumImgs = Integer.valueOf(br.readLine());

                                                System.out.println("num of fix images: " + fFixNumImgs);

                                                if (fFixNumImgs > 0) {
                                                    String list = "'" + br.readLine().split(";")[0] + "'";
                                                    while ((next = br.readLine()) != null) {
                                                        list += ",'" + next.split(";")[0] + "'";
                                                    }

                                                    needDownloadFix = new CheckNewDifferenceGameFixClusters().execute(list).get();
                                                } else {
                                                    needDownloadFix = true;
                                                }

                                            } catch (Exception e) {
                                                Log.e("Error", "Failed checking fix images of difference game.");
                                            }
                                        } else {
                                            needDownloadFix = true;
                                        }
                                    } else {
                                        needDownloadFix = true;
                                    }

                                    System.out.println("Check ffix need download:" + needDownload + ", " + needDownloadFix);

                                    File f = new File(context.getFilesDir(), differenceGameInfoFilename);
                                    if (f.exists()) {
                                        String row;
                                        String[] columns;

                                        try {
                                            BufferedReader br = new BufferedReader(new FileReader(f));
                                            File auxImage;
                                            ArrayList<String> imageNames = new ArrayList<>();

                                            if (br.readLine() != null) {
                                                while ((row = br.readLine()) != null) {
                                                    columns = row.split(";");
                                                    // this function returns 0 if need erase the image and 1 if still need the image
                                                    int correct = new CheckDifferenceGameImages().execute(columns[0], "0").get();
                                                    if (correct != -1) {
                                                        if (correct == 1) {
                                                            imageNames.add(row);
                                                        } else {
                                                            if (correct != 2) {
                                                                auxImage = new File(context.getFilesDir(), columns[0]);

                                                                if (auxImage.exists()) {
                                                                    System.out.println("Deleting file: " + auxImage.getAbsolutePath());
                                                                    auxImage.delete();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            br.close();

                                            if (f.exists()) {
                                                f.delete();
                                                f.createNewFile();
                                            }

                                            BufferedWriter bw = new BufferedWriter(new FileWriter(f));

                                            System.out.println("Check imageNames size: " + imageNames.size());
                                            if (imageNames.size() > 0) {
                                                bw.write(String.valueOf(imageNames.size()));
                                                bw.write(System.getProperty("line.separator"));

                                                for (int i = 0; i < imageNames.size(); i++) {
                                                    bw.write(imageNames.get(i));
                                                    bw.write(System.getProperty("line.separator"));
                                                }

                                                bw.close();
                                            } else {
                                                needDownload = true;
                                            }

                                        } catch (Exception e) {
                                            Log.e("Error", "Failed checking old images.");
                                        }

                                        if (!needDownload) {
                                            try {
                                                System.out.println("Checking images...");
                                                BufferedReader br = new BufferedReader(new FileReader(f));

                                                int fNumImgs = Integer.valueOf(br.readLine());

                                                if ((fFixNumImgs + fNumImgs) < Math.min(12, numClusterDifferenceGameBBDD)) {
                                                    needDownload = true;
                                                    break;
                                                }

                                                while ((row = br.readLine()) != null) {
                                                    columns = row.split(";");
                                                    f = new File(context.getFilesDir(), columns[0]);
                                                    if (!f.exists()) {
                                                        needDownload = true;
                                                        break;
                                                    }
                                                }
                                            } catch (Exception e) {
                                                Log.e("Error:", "Failed checking images.");
                                            }
                                        }
                                    } else {
                                        needDownload = true;
                                        lastTextTimeForDown = System.currentTimeMillis();
                                    }

                                    System.out.println("Check f need download:" + needDownload + ", " + needDownloadFix);

                                    if (!needDownload && !needDownloadFix) {
                                        try {
                                            new UpdatePlayTimes().execute(username, "1");
                                        } catch (Exception e) {
                                            Log.e("Error:", "Failed updating play times.");
                                        }
                                        Intent intent = new Intent(context, DifferenceGameActivity.class);
                                        Bundle b = new Bundle();
                                        b.putString("BDFilename", differenceGameInfoFilename);
                                        b.putString("BDFixFilename", differenceGameFixInfoFilename);
                                        b.putString("username", username);
                                        b.putInt("num_games", NUM_OF_MINIGAMES);
                                        intent.putExtras(b);
                                        gameSelected = false;
                                        context.startActivity(intent);
                                        if (logged) {
                                            readScores(username);
                                        }
                                    } else {
                                        if (needDownloadFix) {
                                            System.out.println("Downloading difference game fix images");
                                            ffix = new File(context.getFilesDir(), differenceGameFixInfoFilename);
                                            String list = "''";
                                            try {
                                                if (ffix.exists()) {
                                                    BufferedReader br = new BufferedReader(new FileReader(ffix));

                                                    if (Integer.valueOf(br.readLine()) > 0) {
                                                        String next;
                                                        list = "'" + br.readLine().split(";")[0] + "'";
                                                        while ((next = br.readLine()) != null) {
                                                            list += ",'" + next.split(";")[0] + "'";
                                                        }
                                                    } else {
                                                        list = "''";
                                                    }
                                                }
                                            } catch (Exception e) {
                                                Log.e("Error:", "Failed reading the difference game fix txt.");
                                            }

                                            try {

                                                downDifferenceGameFix = new GetDifferenceGameFixInfo(context).execute(differenceGameFixInfoFilename, list).get();
                                            } catch (Exception e) {
                                                Log.e("Error:", "Failed downloading txt of fix difference game images.");
                                            }
                                        }

                                        if (needDownload) {
                                            System.out.println("Downloading difference game images");
                                            f = new File(context.getFilesDir(), differenceGameInfoFilename);
                                            String list = "''";
                                            int numImgs = 0;
                                            try {
                                                if (f.exists()) {
                                                    BufferedReader br = new BufferedReader(new FileReader(f));
                                                    numImgs = Integer.valueOf(br.readLine());
                                                    if (numImgs > 0) {
                                                        String next;
                                                        list = "'" + br.readLine().split(";")[0] + "'";
                                                        while ((next = br.readLine()) != null) {
                                                            list += ",'" + next.split(";")[0] + "'";
                                                        }
                                                    } else {
                                                        list = "''";
                                                    }
                                                }
                                            } catch (Exception e) {
                                                Log.e("Error:", "Failed reading the difference game txt.");
                                            }

                                            try {
                                                int limit = 50;
                                                if (ffix.exists()) {
                                                    BufferedReader br = new BufferedReader(new FileReader(ffix));
                                                    String num;
                                                    if ((num = br.readLine()) != null) {
                                                        limit = 50 - (numImgs + Integer.valueOf(num));
                                                    } else {
                                                        limit = 50 - numImgs;
                                                    }
                                                }

                                                System.out.println("the limit is: " + limit);

                                                if (limit > 0) {
                                                    downImages = new GetDifferenceGameInfo(context).execute(differenceGameInfoFilename, list, String.valueOf(limit)).get();
                                                } else {
                                                    downImages = "3";
                                                }
                                            } catch (Exception e) {
                                                Log.e("Error:", "Failed downloading txt of difference game images.");
                                            }
                                        }
                                    }

                                    if (downImages.contentEquals("3")) {
                                        down = true;
                                    }
                                }
                            }
                        }
                    }

                    if (witchExplanation == MATCH_GAME || witchExplanation == DIFFERENCE_GAME) {
                        if (!return_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            if (return_button.isPressed()) {
                                return_button.inverseIsPressed();
                            }
                        } else {
                            witchExplanation = NO_GAME;
                            if (return_button.isPressed()) {
                                return_button.inverseIsPressed();
                            }
                        }
                    }
                } else {
                    if (!seeRanking) {
                        if (login_status == NOT_PRESSED) {
                            if (!play_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if (play_button.isPressed()) {
                                    play_button.inverseIsPressed();
                                }
                            } else {
                                selectMinigame = true;
                                log_sign_text_status = DISABLED;
                                lastLapseTime = System.currentTimeMillis();
                                if (logged) {
                                    readScores(username);
                                }
                                if (play_button.isPressed()) {
                                    play_button.inverseIsPressed();
                                }
                            }

                            if (!exit_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if (exit_button.isPressed()) {
                                    exit_button.inverseIsPressed();
                                }
                            } else {
                                ((Activity) context).finish();
                            }
                        }

                        if (background_button.getRect().contains(motionEvent.getX(), motionEvent.getY()) && !login_button.getRect().contains(motionEvent.getX(), motionEvent.getY()) && !signin_button.getRect().contains(motionEvent.getX(), motionEvent.getY()) && !user_textBox.getRect().contains(motionEvent.getX(), motionEvent.getY()) && !pass_textBox.getRect().contains(motionEvent.getX(), motionEvent.getY()) && !connect_button.getRect().contains(motionEvent.getX(), motionEvent.getY()) && !login_background_button.getRect().contains(motionEvent.getX(), motionEvent.getY()) && !language_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            if (logged) {
                                if (login_status == LOGGED) {
                                    login_status = NOT_PRESSED;
                                }
                            } else {
                                if (login_status == LOGIN) {
                                    login_status = NOT_PRESSED;
                                }
                            }

                            if (change_language) {
                                change_language = false;
                            }
                        }

                        if (!status_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            if (status_button.isPressed()) {
                                status_button.inverseIsPressed();
                            }
                        } else {
                            if (logged) {
                                if (login_status == LOGGED) {
                                    login_status = NOT_PRESSED;
                                } else {
                                    login_status = LOGGED;
                                }
                            } else {
                                if (login_status == LOGIN) {
                                    login_status = NOT_PRESSED;
                                } else {
                                    login_status = LOGIN;
                                }
                            }
                            if (status_button.isPressed()) {
                                status_button.inverseIsPressed();
                            }

                            change_language = false;
                        }

                        if (login_status == LOGIN) {
                            if(!login_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if(login_button.isPressed()) {
                                    login_button.inverseIsPressed();
                                }
                            } else {
                                new_user = false;
                                if(login_button.isPressed()) {
                                    login_button.inverseIsPressed();
                                }
                            }

                            if(!signin_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if(signin_button.isPressed()) {
                                    signin_button.inverseIsPressed();
                                }
                            } else {
                                new_user = true;
                                if(signin_button.isPressed()) {
                                    signin_button.inverseIsPressed();
                                }
                            }

                            if(user_textBox.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                                userTextInput();

                            }

                            if(pass_textBox.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                                passTextInput();
                            }

                            if(!connect_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if(connect_button.isPressed()) {
                                    connect_button.inverseIsPressed();
                                }
                            } else {
                                if (user_textBox.getText().length() > 0 && pass_textBox.getText().length() > 0) {
                                    if (new_user) {
                                        try {
                                            logged = new InsertUser().execute(user_textBox.getText(), pass_textBox.getText()).get();
                                            if (logged) {
                                                log_sign_text_status = SIGNIN_SUCCESS;
                                                user_textBox.resetText();
                                                pass_textBox.resetText();
                                                new_user = false;
                                            } else {
                                                log_sign_text_status = SIGNIN_FAIL;
                                            }
                                            lastTextTimeForUser = System.currentTimeMillis();
                                        } catch (Exception e) {
                                            Log.e("Error:","Failed Sign in");
                                        }
                                    } else {
                                        try {
                                            logged = new Login().execute(user_textBox.getText(), pass_textBox.getText()).get();
                                            if (logged) {
                                                login_status = NOT_PRESSED;
                                                log_sign_text_status = LOGGED_SUCCESS;
                                                username = user_textBox.getText();
                                                user_textBox.resetText();
                                                pass_textBox.resetText();
                                            } else {
                                                log_sign_text_status = LOGGED_FAIL;
                                            }
                                            lastTextTimeForUser = System.currentTimeMillis();
                                        } catch (Exception e) {
                                            Log.e("Error:","Failed login");
                                        }
                                    }
                                }
                                if(connect_button.isPressed()) {
                                    connect_button.inverseIsPressed();
                                }
                            }
                        }

                        if (login_status == LOGGED) {
                            if(!ranking_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if(ranking_button.isPressed()) {
                                    ranking_button.inverseIsPressed();
                                }
                            } else {
                                seeRanking = true;
                                matchGameOrDifferenceGame = false;
                                login_status = NOT_PRESSED;
                                String[] scores = null;
                                try {
                                    scores = new GetRanking().execute("").get();
                                } catch (Exception e) {
                                    Log.e("Error:","Failed getting ranking");
                                }

                                if (scores != null) {
                                    rankingScoresMatchGame = scores[0].split("<br>");
                                    rankingScoresDifferenceGame = scores[1].split("<br>");
                                } else {
                                    rankingScoresMatchGame = new String[10];
                                    for (int i = 0; i < 10; i++) {
                                        rankingScoresMatchGame[i] = "ERROR,999999";
                                    }

                                    rankingScoresDifferenceGame = new String[10];
                                    for (int i = 0; i < 10; i++) {
                                        rankingScoresDifferenceGame[i] = "ERROR,999999";
                                    }
                                }
                                if(ranking_button.isPressed()) {
                                    ranking_button.inverseIsPressed();
                                }
                            }

                            if(!connect_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if(connect_button.isPressed()) {
                                    connect_button.inverseIsPressed();
                                }
                            } else {
                                logged = false;
                                username = "";
                                login_status = NOT_PRESSED;
                                if(connect_button.isPressed()) {
                                    connect_button.inverseIsPressed();
                                }
                            }
                        }


                        if (!language_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            if (language_button.isPressed()) {
                                language_button.inverseIsPressed();
                            }
                        } else {
                            change_language = true;
                            login_status = NOT_PRESSED;
                        }

                        if (change_language) {
                            if (!ca_language_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if (ca_language_button.isPressed()) {
                                    ca_language_button.inverseIsPressed();
                                }
                            } else {
                                changeLocale(new Locale("ca"));
                            }

                            if (!en_language_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if (en_language_button.isPressed()) {
                                    en_language_button.inverseIsPressed();
                                }
                            } else {
                                changeLocale(new Locale("en"));
                            }

                            if (!es_language_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if (es_language_button.isPressed()) {
                                    es_language_button.inverseIsPressed();
                                }
                            } else {
                                changeLocale(new Locale("es"));
                            }

                            if (!zh_language_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if (zh_language_button.isPressed()) {
                                    zh_language_button.inverseIsPressed();
                                }
                            } else {
                                changeLocale(new Locale("zh"));
                            }
                        }

                    } else {
                        if (!return_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            if(return_button.isPressed()) {
                                return_button.inverseIsPressed();
                            }
                        } else {
                            login_status = NOT_PRESSED;
                            seeRanking = false;
                            if(return_button.isPressed()) {
                                return_button.inverseIsPressed();
                            }
                        }

                        if (!seeDifferenceGameScore.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            if(seeDifferenceGameScore.isPressed()) {
                                seeDifferenceGameScore.inverseIsPressed();
                            }
                        } else {
                            matchGameOrDifferenceGame = true;
                            if(seeDifferenceGameScore.isPressed()) {
                                seeDifferenceGameScore.inverseIsPressed();
                            }
                        }

                        if (!seeMatchGameScore.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                            if(seeMatchGameScore.isPressed()) {
                                seeMatchGameScore.inverseIsPressed();
                            }
                        } else {
                            matchGameOrDifferenceGame = false;
                            if(seeMatchGameScore.isPressed()) {
                                seeMatchGameScore.inverseIsPressed();
                            }
                        }
                    }
                }
                break;
        }
        return true;
    }

    public void pause() {
        menu_ing = false;

        try {
            menuThread.join();
        } catch(InterruptedException e) {
            Log.e("Error:", "joining thread.");
        }
    }

    public void resume() {
        if (logged) {
            readScores(username);
        }
        menu_ing = true;
        menuThread = new Thread(this);
        menuThread.start();
        draw();
    }

    public void readScores(String name) {
        String[] scores;
        maxScores = new int[NUM_OF_MINIGAMES];
        try {
            scores = new GetRanking().execute(name).get();
            if (scores != null) {
                for (int i = 0; i < scores.length; i++) {
                    maxScores[i] = Integer.valueOf(scores[i]);
                }
            } else {
                for (int i = 0; i < NUM_OF_MINIGAMES; i++) {
                    maxScores[i] = 0;
                }
            }
        } catch (Exception e) {
            Log.e("Error:","Failed getting ranking");
        }
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        BaseInputConnection fic = new BaseInputConnection(this, false);
        outAttrs.actionLabel = null;
        outAttrs.inputType = InputType.TYPE_NULL;
        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN;
        return fic;
    }

    public void userTextInput() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if(((keyCode >= KeyEvent.KEYCODE_A) && (keyCode <= KeyEvent.KEYCODE_Z)) || ((keyCode >= KeyEvent.KEYCODE_0) && (keyCode <= KeyEvent.KEYCODE_9)) || (keyCode == 95)) {
                        userText = "" + (char) event.getUnicodeChar();
                        if (user_textBox.getText().length() < MAX_USERNAME_LENGTH) {
                            user_textBox.putText(userText);
                        }
                    }
                    if(keyCode == KeyEvent.KEYCODE_DEL) {
                        user_textBox.deleteText();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public void passTextInput() {
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if(((keyCode >= KeyEvent.KEYCODE_A) && (keyCode <= KeyEvent.KEYCODE_Z)) || ((keyCode >= KeyEvent.KEYCODE_0) && (keyCode <= KeyEvent.KEYCODE_9)) || (keyCode == 95) || (keyCode == KeyEvent.KEYCODE_MINUS) || ((keyCode >= KeyEvent.KEYCODE_NUMPAD_0) && (keyCode <= KeyEvent.KEYCODE_NUMPAD_DOT)) || (keyCode == KeyEvent.KEYCODE_PLUS) || (keyCode == KeyEvent.KEYCODE_PERIOD) || (keyCode == KeyEvent.KEYCODE_POUND) || (keyCode == KeyEvent.KEYCODE_SLASH) || (keyCode == KeyEvent.KEYCODE_SPACE) || (keyCode == KeyEvent.KEYCODE_STAR)) {
                        passText = "" + (char) event.getUnicodeChar();
                        pass_textBox.putText(passText);
                    }
                    if(keyCode == KeyEvent.KEYCODE_DEL) {
                        pass_textBox.deleteText();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            System.out.println("YOU PRESSED BACK, " + keyCode);
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void changeLocale(Locale locale)
    {
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        ((Activity) context).recreate();
    }
}
