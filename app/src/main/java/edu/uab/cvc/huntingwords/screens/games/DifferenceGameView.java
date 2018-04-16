package edu.uab.cvc.huntingwords.screens.games;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Random;

import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.tasks.GetRanking;
import edu.uab.cvc.huntingwords.tasks.difference.UpdateClusters;
import edu.uab.cvc.huntingwords.tasks.UpdatePlayTimes;
import edu.uab.cvc.huntingwords.tasks.UpdateScore;

import static edu.uab.cvc.huntingwords.screens.games.MenuView.BIG;
import static edu.uab.cvc.huntingwords.screens.games.MenuView.MEGA;
import static edu.uab.cvc.huntingwords.screens.games.MenuView.NORMAL;


@SuppressWarnings("SuspiciousNameCombination")
@SuppressLint("ViewConstructor")
public class DifferenceGameView extends SurfaceView implements Runnable {
    Context context;

    private final RectF image_box;
    private final RectF image_box_outlines;

    private Thread gameThread = null;

    private volatile boolean inApp;

    private SurfaceHolder ourHolder;

    private ArrayList<Double[]> clusters = new ArrayList<>();

    private Sounds sounds;

    private Paint paint;

    private int screenX;
    private int screenY;

    private final int starting = 0;
    private final int inGame = 1;
    private final int over = 2;
    private final int options = 3;

    private int gameState = -1;

    private final int none = 0;
    private final int fail = 1;
    private final int pass = 2;

    private Bitmap failBit;
    private Bitmap passBit;

    private int gameActions = none;

    private int gameActionsDrawCount = 3000;

    private int timer;
    private boolean lessTimer = false;
    private int countDown;
    private boolean lessCD = false;

    private final int lost = -1;

    private int playerState = -1;

    private long lastMenaceTime = System.currentTimeMillis();

    long fps;

    private Button options_button;
    private Button resume_button;
    private Button home_button;
    private Button options_background;

    private Button exit_button;
    private Button playAgain_button;

    private Button[] text_buttons;

    private int boxesWidth;
    private int boxesHeight;

    private int numRounds;
    private int meaningRounds;

    private int difficulty = 4;

    private String startDate = null;
    private String endDate = null;

    private int startScore;
    private int finalScore;

    private int numFixClusters = 4;

    private int score;
    private int[] maxScores;
    private String username;

    private boolean plusScore;
    private boolean subsScore;

    private ArrayList<String> sendData;

    private boolean stopDetect;

    private class Double {
        Bitmap image;
        String imageName;
        boolean imTheDifferent;
        boolean isValidated;
        String clusterName;

        private Double(Bitmap i, String imName, boolean d, boolean v, String c) {
            this.image = i;
            this.imageName = imName;
            this.imTheDifferent = d;
            this.isValidated = v;
            this.clusterName = c;
        }
    }

    public DifferenceGameView(Context context, int x, int y, String username, int num_games) {
        super(context);

        this.context = context;

        numRounds = 5;
        meaningRounds = numRounds;

        this.username = username;

        maxScores = new int[num_games];

        score = 0;

        screenX = x;
        screenY = y;

        int padding = 5;

        image_box = new RectF(screenX / 20, screenY / 5, screenX * 19 / 20, screenY * 9 / 10);
        image_box_outlines = new RectF(screenX / 20 - padding, screenY / 5 - padding, screenX * 19 / 20 + padding, screenY * 9 / 10 + padding);

        /*
        imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        init();*/

        ourHolder = getHolder();
        paint = new Paint();
        sounds = new Sounds(context);

        padding = 25;

        int button_width = screenX / 3;
        int button_height = screenY * 2/10;
        int button_x = screenX / 2 - button_width / 2;

        options_button = new Button(screenX - screenX / 15 - padding,
                padding,
                screenX / 15,
                screenX / 15,
                context.getString(R.string.options));
        options_background = new Button(screenX / 2 - (screenX / 4),
                screenY / 7,
                screenX / 2,
                screenY * 2 / 3,
                context.getString(R.string.options));
        resume_button = new Button(button_x,
                screenY / 4,
                button_width,
                button_height,
                context.getString(R.string.resume));
        home_button = new Button(button_x,
                screenY / 4 + button_height + padding,
                button_width,
                button_height,
                context.getString(R.string.exit));
        exit_button = new Button(screenX / 2 - button_width - padding,
                screenY / 2 + 90 * 2,
                button_width,
                button_height,
                context.getString(R.string.exit));
        playAgain_button = new Button(screenX / 2 + padding,
                screenY / 2 + 90 * 2,
                button_width,
                button_height,
                context.getString(R.string.play_again));

        text_buttons = new Button[11];

        boxesWidth = screenX * 3 / 15;
        boxesHeight = screenY * 3 / 19;

        text_buttons[0] = new Button(image_box.left + 30 /*padding*/,
                image_box.top + (image_box.height() / 6) - (boxesHeight / 2), boxesWidth, boxesHeight, "");
        text_buttons[1] = new Button(image_box.left + 40 /*padding*/ + boxesWidth,
                image_box.top + (image_box.height() / 6) - (boxesHeight / 2), boxesWidth, boxesHeight, "");
        text_buttons[2] = new Button(image_box.left + 50 /*padding*/ + 2*boxesWidth,
                image_box.top + (image_box.height() / 6) - (boxesHeight / 2), boxesWidth, boxesHeight, "");
        text_buttons[3] = new Button(image_box.left + 30 /*padding*/,
                image_box.top + (2 * image_box.height() / 3) - (image_box.height() / 6) - (boxesHeight / 2), boxesWidth, boxesHeight, "");
        text_buttons[4] = new Button(image_box.left + 40 /*padding*/ + boxesWidth,
                image_box.top + (2 * image_box.height() / 3) - (image_box.height() / 6) - (boxesHeight / 2), boxesWidth, boxesHeight, "");
        text_buttons[5] = new Button(image_box.left + 50 /*padding*/ + 2*boxesWidth,
                image_box.top + (2 * image_box.height() / 3) - (image_box.height() / 6) - (boxesHeight / 2), boxesWidth, boxesHeight, "");
        text_buttons[6] = new Button(image_box.left + 30 /*padding*/,
                image_box.top + image_box.height() - (image_box.height() / 6) - (boxesHeight / 2), boxesWidth, boxesHeight, "");
        text_buttons[7] = new Button(image_box.left + 40 /*padding*/ + boxesWidth,
                image_box.top + image_box.height() - (image_box.height() / 6) - (boxesHeight / 2), boxesWidth, boxesHeight, "");
        text_buttons[8] = new Button(image_box.left + 50 /*padding*/ + 2*boxesWidth,
                image_box.top + image_box.height() - (image_box.height() / 6) - (boxesHeight / 2), boxesWidth, boxesHeight, "");

        text_buttons[9] = new Button(image_box.right - boxesWidth - 10,
                image_box.top + image_box.height() / 2 - boxesHeight - 10, boxesWidth, boxesHeight, context.getString(R.string.more_than_one_different));
        text_buttons[10] = new Button(image_box.right - boxesWidth - 10,
                image_box.top + image_box.height() / 2 + 10, boxesWidth, boxesHeight, context.getString(R.string.all_same));


        boxesWidth = screenX * 2 / 19;
        boxesHeight = screenY * 2 / 19;

        InputStream is;
        try {
            is = context.getAssets().open("sprites/images/fail.png");
            failBit = BitmapFactory.decodeStream(is);

            failBit = Bitmap.createScaledBitmap(failBit,
                    boxesWidth,
                    boxesHeight,
                    false);

            is = context.getAssets().open("sprites/images/pass.png");
            passBit = BitmapFactory.decodeStream(is);

            passBit = Bitmap.createScaledBitmap(passBit,
                    boxesWidth,
                    boxesHeight,
                    false);
        } catch (IOException e) {
            Log.e("Error:","Failed loading fail.");
        }

        //set the image box sizes for the prepareGame() function
        boxesWidth = screenX * 3 / 15;
        boxesHeight = screenY * 3 / 19;


        if (!this.username.contentEquals("")) {
            String[] scores;
            try {
                scores = new GetRanking().execute(this.username).get();
                if (scores != null) {
                    for (int i = 0; i < num_games; i++) {
                        maxScores[i] = Integer.valueOf(scores[i]);
                    }
                } else {
                    for (int i = 0; i < num_games; i++) {
                        maxScores[i] = 0;
                    }
                }
            } catch (Exception e) {
                Log.e("Error:","Failed getting ranking");
            }
        } else {
            for (int i = 0; i < num_games; i++) {
                maxScores[i] = 0;
            }
        }

        prepareGame(false);
    }


    public void prepareGame(boolean nextRound) {
        sendData = new ArrayList<>();
        this.clusters = new ArrayList<>();

        stopDetect = false;

        File fFix = new File(context.getFilesDir(), "differenceGameFix/");
        File f = new File(context.getFilesDir(), "differenceGame/");

        File[] clustersFix = fFix.listFiles();
        File[] clusters = f.listFiles();

        if (fFix.exists()) {
            if (clustersFix.length > 0) {
                if (clustersFix.length < numFixClusters) {
                    numFixClusters = clustersFix.length;
                }

                boolean[] images = new boolean[clustersFix.length];

                for (int i = 0; i < clustersFix.length; i++) {
                    images[i] = false;
                }

                Random r = new Random();
                int ran;
                int num = 0;
                while (num < numFixClusters) {
                    ran = r.nextInt(clustersFix.length);
                    if (!images[ran]) {
                        images[ran] = true;
                        num++;
                    }
                }

                System.out.println("Loading fixed clusters.");
                //preparing fix clusters
                int i = 0;
                for (File inFile : clustersFix) {
                    if (images[i]) {
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(inFile));
                            String next;
                            ArrayList<String> imageNames = new ArrayList<>();
                            while ((next = br.readLine()) != null) {
                                imageNames.add(next);
                            }

                            Double[] cluster = new Double[imageNames.size()];
                            String string;
                            String[] split;
                            Bitmap image;
                            for (int j = 0; j < imageNames.size(); j++) {
                                string = imageNames.get(j);

                                split = string.split(";");
                                File imFile = new File(context.getFilesDir(), split[0]);
                                image = BitmapFactory.decodeFile(imFile.getAbsolutePath());

                                image = Bitmap.createScaledBitmap(
                                        image,
                                        boxesWidth,
                                        boxesHeight,
                                        false);

                                cluster[j] = new Double(image, split[0], split[2].contains("1"), true, split[1]);

                            }

                            shuffleImages(cluster);

                            this.clusters.add(cluster);
                        } catch (Exception e) {
                            Log.e("Error", "Failed loading fixed clusters.");
                        }
                    }
                    i++;
                }
            }
        } else {
            numFixClusters = 0;
        }


        System.out.println("Loading non-validated clusters.");

        int numClusters = numRounds - numFixClusters;

        if (clusters.length > 0) {
            if (clusters.length < numClusters) {
                numClusters = clusters.length;
            }

            boolean[] images = new boolean[clusters.length];

            for(int i = 0; i < clusters.length; i++) {
                images[i] = false;
            }

            Random r = new Random();
            int ran;
            int num = 0;
            while(num < numClusters) {
                ran = r.nextInt(clusters.length);
                if(!images[ran]) {
                    images[ran] = true;
                    num++;
                }
            }

            //preparing non-validated clusters
            int i = 0;
            for (File inFile: clusters) {
                if (images[i]) {
                    try {
                        BufferedReader br = new BufferedReader(new FileReader(inFile));
                        String next;
                        ArrayList<String> imageNames = new ArrayList<>();
                        System.out.println("reading file:" + inFile.getAbsolutePath());
                        while((next = br.readLine()) != null) {
                            imageNames.add(next);
                        }

                        Double[] cluster = new Double[imageNames.size()];
                        String string;
                        String[] split;
                        Bitmap image;
                        for (int j = 0; j < imageNames.size(); j++) {
                            string = imageNames.get(j);

                            split = string.split(";");
                            File imFile = new File(context.getFilesDir(), split[0]);
                            System.out.println(imFile.getAbsolutePath());
                            image = BitmapFactory.decodeFile(imFile.getAbsolutePath());

                            image = Bitmap.createScaledBitmap(
                                    image,
                                    boxesWidth,
                                    boxesHeight,
                                    false);
                            System.out.println("split[0]: " + split[0]);
                            System.out.println("split[1]: " + split[1]);
                            cluster[j] = new Double(image, split[0], split[2].contains("1"), false, split[1]);
                        }

                        shuffleImages(cluster);

                        this.clusters.add(cluster);
                    } catch (Exception e) {
                        Log.e("Error","Failed loading non-validated clusters.");
                    }
                }
                i++;
            }
        }

        Collections.shuffle(this.clusters);

        if (this.clusters.size() < numRounds) {
            numRounds = this.clusters.size();
        }

        meaningRounds = numRounds;

        timer = 10 * (difficulty);
        lessTimer = false;

        plusScore = false;
        subsScore = false;

        if (!nextRound) {
            score = 0;
        }

        startScore = score;

        countDown = 3;
        lessCD = false;

        gameState = starting;
        playerState = -1;

        gameActions = none;
    }

    @Override
    public void run() {
        long menaceInterval = 1000;
        long timeThisFrame;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hhmmss");

        while(inApp) {
            long startFrameTime = System.currentTimeMillis();

            if (gameState != over) {
                update();
                endDate = sdf.format(new Date());
            }

            draw();

            timeThisFrame = System.currentTimeMillis();
            if (timeThisFrame >= 1) {
                fps = 1000 / timeThisFrame;
            }

            if (startFrameTime - lastMenaceTime >= menaceInterval) {
                if (gameState == inGame) {
                    lessTimer = true;
                }
                if (gameState == starting) {
                    lessCD = true;
                }
                lastMenaceTime = System.currentTimeMillis();
            }
        }
    }



    //here we update the game screen
    public void update() {

        if (subsScore) {
            if (score > 0) {
                score -= 5;
            }
            subsScore = false;
        }

        if (plusScore) {
            score += 100;
            plusScore = false;
        }

        if (gameState == inGame) {
            if (lessTimer && timer > 0) {
                timer--;
                lessTimer = false;
            }

            if (timer == 0 && meaningRounds > 0) {

                if (!this.username.contentEquals("anonim")) {
                    if (score > maxScores[1]) {
                        maxScores[1] = score;

                        try {
                            new UpdateScore().execute(username, String.valueOf(maxScores[0]), String.valueOf(maxScores[1]));
                        } catch (Exception e) {
                            Log.e("Error:", "Failed writing high scores");
                        }
                    }
                }

                playerState = lost;
                gameState = over;
                playAgain_button.setText(context.getString(R.string.play_again));
                finalScore = score;
            }

            if (meaningRounds == 0) {
                score += timer*10;
                if (!this.username.contentEquals("anonim")) {
                    if (score > maxScores[1]) {
                        maxScores[1] = score;


                        try {
                            new UpdateScore().execute(username, String.valueOf(maxScores[0]), String.valueOf(maxScores[1]));
                        } catch (Exception e) {
                            Log.e("Error:", "Failed writing high scores");
                        }
                    }
                }

                playerState = 1 /*won*/;
                gameState = over;
                playAgain_button.setText(context.getString(R.string.next_level));
                sounds.soundPool.play(sounds.won, 1, 1, 0, 0, 1);
                finalScore = score;
            }

            if (gameActions == none) {
                gameActionsDrawCount = 3000;
            } else {
                if (gameActionsDrawCount > 0) {
                    gameActionsDrawCount -= 50;
                }

                if (gameActionsDrawCount == 0) {
                    gameActions = none;
                    stopDetect = false;
                }
            }
        }

        if (gameState == starting) {
            if (lessCD && countDown > 0) {
                countDown--;
                lessCD = false;
            }

            if (countDown == 0) {
                gameState = inGame;
                Calendar a = Calendar.getInstance();
                a.setTime(new Date());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hhmmss");
                startDate = sdf.format(a.getTime());
            }
        }
    }



    //here we draw everything of the game screen
    public void draw() {
        Canvas canvas;
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas(); //we have to lock the canvas for draw
            float textWidth;
            float textHeight;

            if (gameState == starting) {
                canvas.drawColor(Color.WHITE);

                textHeight = MEGA;
                paint.setTextSize(textHeight);
                textWidth = paint.measureText(Integer.toString(countDown),
                        0,
                        Integer.toString(countDown).length());
                switch (countDown) {
                    case 3:
                        paint.setColor(Color.RED);
                        break;
                    case 2:
                        paint.setColor(Color.YELLOW);
                        break;
                    case 1:
                        paint.setColor(Color.GREEN);
                        break;
                    case 0:
                        paint.setColor(Color.BLACK);
                }
                canvas.drawText(Integer.toString(countDown),
                        screenX / 2 - textWidth / 2,
                        screenY / 2 + textHeight / 2,
                        paint);
            }


            //here we set the draw color (the outlines), don't affect the bitmaps
            if (gameState == inGame || gameState == options) {
                canvas.drawColor(Color.WHITE);

                //here we have the pressed effect, the button change the color if we press it
                if (!options_button.isPressed()) {
                    paint.setColor(Color.argb(255, 170, 170, 170));
                } else {
                    paint.setColor(Color.argb(255, 45, 40, 190));
                }
                canvas.drawRect(options_button.getRect(), paint); //drawing the button, parameters: rectF, paint

                paint.setColor(Color.BLACK);
                textHeight = NORMAL;
                paint.setTextSize(textHeight);
                textWidth = paint.measureText(options_button.getText(),
                        0,
                        options_button.getText().length());
                canvas.drawText(options_button.getText(),
                        options_button.getRect().left + options_button.getRect().width() / 2 - textWidth / 2,
                        options_button.getRect().bottom - options_button.getRect().height() / 2 + textHeight / 2,
                        paint);

                paint.setColor(Color.BLACK);
                canvas.drawRect(image_box_outlines, paint);

                paint.setColor(Color.WHITE);
                canvas.drawRect(image_box, paint);

                RectF r = new RectF();
                //Draw text boxes and images
                if ((numRounds - meaningRounds) < numRounds) {
                    Double[] images = clusters.get(numRounds - meaningRounds);
                    for (int i = 0; i < images.length; i++) {

                        r.left = text_buttons[i].getRect().left - 2;
                        r.top = text_buttons[i].getRect().top - 2;
                        r.right = text_buttons[i].getRect().right + 2;
                        r.bottom = text_buttons[i].getRect().bottom + 2;

                        paint.setColor(Color.BLACK);
                        canvas.drawRect(r, paint);

                        paint.setColor(Color.WHITE);
                        canvas.drawRect(text_buttons[i].getRect(), paint);

                        canvas.drawBitmap(images[i].image,
                                text_buttons[i].getRect().left,
                                text_buttons[i].getRect().top,
                                paint);
                    }

                    //button more than one different
                    r.left = text_buttons[9].getRect().left - 2;
                    r.top = text_buttons[9].getRect().top - 2;
                    r.right = text_buttons[9].getRect().right + 2;
                    r.bottom = text_buttons[9].getRect().bottom + 2;

                    paint.setColor(Color.BLACK);
                    canvas.drawRect(r, paint);

                    paint.setColor(Color.WHITE);
                    canvas.drawRect(text_buttons[9].getRect(), paint);

                    paint.setColor(Color.BLACK);

                    String[] split = text_buttons[9].getText().split(";");
                    textHeight = BIG;
                    paint.setTextSize(textHeight);
                    textWidth = paint.measureText(split[0],
                            0,
                            split[0].length());
                    canvas.drawText(split[0],
                            text_buttons[9].getRect().left + (boxesWidth / 2) - (textWidth / 2),
                            text_buttons[9].getRect().top + (boxesHeight / 2) - 2,
                            paint);
                    textWidth = paint.measureText(split[1],
                            0,
                            split[1].length());
                    canvas.drawText(split[1],
                            text_buttons[9].getRect().left + (boxesWidth / 2) - (textWidth / 2),
                            text_buttons[9].getRect().bottom - (boxesHeight / 2) + textHeight + 2,
                            paint);

                    //button all equal
                    r.left = text_buttons[10].getRect().left - 2;
                    r.top = text_buttons[10].getRect().top - 2;
                    r.right = text_buttons[10].getRect().right + 2;
                    r.bottom = text_buttons[10].getRect().bottom + 2;

                    paint.setColor(Color.BLACK);
                    canvas.drawRect(r, paint);

                    paint.setColor(Color.WHITE);
                    canvas.drawRect(text_buttons[10].getRect(), paint);

                    paint.setColor(Color.BLACK);

                    textHeight = BIG;
                    paint.setTextSize(textHeight);
                    textWidth = paint.measureText(text_buttons[10].getText(),
                            0,
                            text_buttons[10].getText().length());
                    canvas.drawText(text_buttons[10].getText(),
                            text_buttons[10].getRect().left + (boxesWidth / 2) - (textWidth / 2),
                            text_buttons[10].getRect().top + (boxesHeight / 2) + textHeight / 2,
                            paint);
                }

                //draw timer
                textHeight = BIG;
                paint.setTextSize(textHeight);
                textWidth = paint.measureText(Integer.toString(timer),
                        0,
                        Integer.toString(timer).length());
                if (timer <= 10) {
                    paint.setColor(Color.RED);
                } else {
                    paint.setColor(Color.BLACK);
                }
                canvas.drawText(Integer.toString(timer),
                        screenX / 2 - textWidth / 2,
                        5 + textHeight,
                        paint);

                if (gameActions == fail && gameActionsDrawCount > 0) {
                    canvas.drawBitmap(failBit,
                            screenX / 2 - failBit.getWidth() / 2,
                            image_box.top + failBit.getHeight() + 5,
                            paint);
                }

                if (gameActions == pass && gameActionsDrawCount > 0) {
                    canvas.drawBitmap(passBit,
                            screenX / 2 - passBit.getWidth() / 2,
                            image_box.top + passBit.getHeight() + 5,
                            paint);
                }

                // draw score
                textHeight = BIG;
                paint.setColor(Color.BLACK);
                paint.setTextSize(textHeight);
                canvas.drawText(context.getString(R.string.score) + ": " + Integer.toString(score),
                        25,
                        15 + textHeight,
                        paint);

                // draw rounds
                canvas.drawText(context.getString(R.string.round) + ": " + (numRounds - meaningRounds + 1) + " / " + numRounds,
                        25,
                        17 + textHeight * 2,
                        paint);

                // draw difficulty
                String level;
                if (difficulty < 6) {
                    level = context.getString(R.string.easy);
                } else if (difficulty < 8) {
                    level = context.getString(R.string.normal);
                } else {
                    level = context.getString(R.string.hard);
                }
                canvas.drawText(context.getString(R.string.level) + ": " + level,
                        25,
                        19 + textHeight * 3,
                        paint);
            }

            //won the game or lost the game will draw these texts
            if (gameState == over) {
                canvas.drawColor(Color.WHITE);

                String text;
                textHeight = MEGA;
                if (playerState == lost) {
                    text = context.getString(R.string.lose);
                    paint.setTextSize(textHeight);
                    paint.setColor(Color.RED);
                    textWidth = paint.measureText(text,
                            0,
                            text.length());
                    canvas.drawText(text,
                            screenX / 2 - textWidth / 2,
                            screenY / 2 - textHeight / 2,
                            paint);
                } else {
                    text = context.getString(R.string.win);
                    paint.setTextSize(textHeight);
                    paint.setColor(Color.GREEN);
                    textWidth = paint.measureText(text,
                            0,
                            text.length());
                    canvas.drawText(text,
                            screenX / 2 - textWidth / 2,
                            screenY / 2 - textHeight / 2,
                            paint);
                }

                paint.setTextSize(textHeight);
                paint.setColor(Color.BLACK);
                text = context.getString(R.string.score) + ": " + Integer.toString(score);
                textWidth = paint.measureText(text,
                        0,
                        text.length());
                canvas.drawText(text,
                        screenX / 2 - textWidth / 2,
                        screenY / 2 + textHeight + textHeight / 2,
                        paint);

                if(!exit_button.isPressed()) {
                    paint.setColor(Color.argb(255, 160, 50, 50));
                } else {
                    paint.setColor(Color.argb(255, 45, 40, 190));
                }
                canvas.drawRect(exit_button.getRect(), paint);

                if(!playAgain_button.isPressed()) {
                    paint.setColor(Color.argb(255, 70, 210, 35));
                } else {
                    paint.setColor(Color.argb(255, 45, 40, 190));
                }
                canvas.drawRect(playAgain_button.getRect(), paint);

                textHeight = NORMAL;
                paint.setTextSize(textHeight);

                paint.setColor(Color.BLACK);
                textWidth = paint.measureText(exit_button.getText(),
                        0,
                        exit_button.getText().length());
                canvas.drawText(exit_button.getText(),
                        exit_button.getRect().left + exit_button.getRect().width() / 2 - textWidth / 2,
                        exit_button.getRect().top + exit_button.getRect().height() / 2 + textHeight / 2,
                        paint);

                textWidth = paint.measureText(playAgain_button.getText(),
                        0,
                        playAgain_button.getText().length());
                canvas.drawText(playAgain_button.getText(),
                        playAgain_button.getRect().left + playAgain_button.getRect().width() / 2 - textWidth / 2,
                        playAgain_button.getRect().top + playAgain_button.getRect().height() / 2 + textHeight / 2,
                        paint);
            }

            //if we pressed the options button, we must draw the resume button and return button (go to the menu)
            if (gameState == options) {
                textHeight = NORMAL;
                paint.setTextSize(textHeight);
                paint.setColor(Color.argb(255, 200, 120, 120));
                canvas.drawRect(options_background.getRect(), paint);

                if(!resume_button.isPressed()) {
                    paint.setColor(Color.argb(255, 170, 170, 170));
                } else {
                    paint.setColor(Color.argb(255, 45, 40, 190));
                }
                canvas.drawRect(resume_button.getRect(), paint);

                if(!home_button.isPressed()) {
                    paint.setColor(Color.argb(255, 170, 170, 170));
                } else {
                    paint.setColor(Color.argb(255, 45, 40, 190));
                }
                canvas.drawRect(home_button.getRect(), paint);

                paint.setColor(Color.BLACK);
                paint.setTextSize(textHeight);

                textWidth = paint.measureText(options_background.getText(),
                                                0,
                                                options_background.getText().length());
                canvas.drawText(options_background.getText(),
                        options_background.getRect().left + options_background.getRect().width() / 2 - textWidth / 2,
                        options_background.getRect().top + 50,
                        paint);

                textWidth = paint.measureText(resume_button.getText(),
                                                0,
                                                resume_button.getText().length());
                canvas.drawText(resume_button.getText(),
                        resume_button.getRect().left + resume_button.getRect().width() / 2 - textWidth / 2,
                        resume_button.getRect().top + resume_button.getRect().height() / 2 + textHeight / 2,
                        paint);

                textWidth = paint.measureText(home_button.getText(),
                        0,
                        home_button.getText().length());
                canvas.drawText(home_button.getText(),
                        home_button.getRect().left + home_button.getRect().width() / 2 - textWidth / 2,
                        home_button.getRect().top + home_button.getRect().height() / 2 + textHeight / 2,
                        paint);
            }
            ourHolder.unlockCanvasAndPost(canvas); //finally, we unlock the canvas and show the draws
        }
    }



    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch(motionEvent.getAction()) {
            //when the player touch the screen, we can know where he pressed and respond with some effects
            case MotionEvent.ACTION_DOWN: //here the cases if the player finger is DOWN
                if (gameState == over) {
                    if (exit_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                        exit_button.inverseIsPressed();
                    }

                    if (playAgain_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                        playAgain_button.inverseIsPressed();
                    }
                }

                if (gameState == options) {
                    if (resume_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                        resume_button.inverseIsPressed();
                    }

                    if (home_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                        home_button.inverseIsPressed();
                    }
                }

                if (gameState == inGame) {
                    if (options_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                        options_button.inverseIsPressed();
                    }
                }
                break;

            case MotionEvent.ACTION_UP: //here the cases if the player finger is UP
                if (gameState == over) {
                    if (!exit_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                        if (exit_button.isPressed()) {
                            exit_button.inverseIsPressed();
                        }
                    } else {
                        if (exit_button.isPressed()) {
                            exit_button.inverseIsPressed();
                        }

                        for (int i = 0; i < sendData.size(); i++) {
                            String a = sendData.get(i);
                            String[] split = a.split(";");
                            try {
                                new UpdateClusters().execute(split[0], split[1], split[2], String.valueOf(difficulty), startDate, endDate, String.valueOf(startScore), String.valueOf(finalScore));
                            } catch (Exception e) {
                                Log.e("Error","Failed updating transcriptions");
                            }
                        }

                        ((Activity) context).finish();
                    }

                    if (!playAgain_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                        if (playAgain_button.isPressed()) {
                            playAgain_button.inverseIsPressed();
                        }
                    } else {
                        if (playAgain_button.isPressed()) {
                            playAgain_button.inverseIsPressed();
                        }
                        for (int i = 0; i < sendData.size(); i++) {
                            String a = sendData.get(i);
                            String[] split = a.split(";");
                            try {
                                new UpdateClusters().execute(split[0], split[1], split[2], String.valueOf(difficulty), startDate, endDate, String.valueOf(startScore), String.valueOf(finalScore));
                            } catch (Exception e) {
                                Log.e("Error","Failed updating transcriptions");
                            }
                        }

                        if (playAgain_button.getText().contentEquals(context.getString(R.string.next_level))) {
                            if (difficulty < 10) {
                                difficulty += 1;
                            }
                            if (numRounds < 20) {
                                numRounds += 1;
                            }
                            if (numFixClusters > 1) {
                                numFixClusters -= 1;
                            }
                            prepareGame(true);
                        } else {
                            difficulty = 4;
                            numRounds = 5;
                            numFixClusters = 4;
                            if (username.contains("")) {
                                new UpdatePlayTimes().execute("anonim", "1");
                            } else {
                                new UpdatePlayTimes().execute(username, "1");
                            }
                            prepareGame(false);
                        }
                    }
                }

                if (gameState == inGame) {
                    if (!options_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                        if (options_button.isPressed()) {
                            options_button.inverseIsPressed();
                        }
                    } else {
                        gameState = options;
                        if (options_button.isPressed()) {
                            options_button.inverseIsPressed();
                        }
                    }

                    if (!stopDetect) {
                        int thisRound = numRounds - meaningRounds;
                        for (int i = 0; i < clusters.get(thisRound).length; i++) {
                            Button t = text_buttons[i];
                            int count = 0;
                            for (int j = 0; j < clusters.get(thisRound).length; j++) {
                                if (clusters.get(thisRound)[j].imTheDifferent) {
                                    count++;
                                }
                            }
                            if (t.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if (clusters.get(thisRound)[i].isValidated) {
                                    if (clusters.get(thisRound)[i].imTheDifferent && count == 1) {
                                        plusScore = true;
                                        meaningRounds--;
                                        sounds.soundPool.play(sounds.pass, 1, 1, 0, 0, 1);
                                        gameActions = pass;
                                    } else {
                                        subsScore = true;
                                        sounds.soundPool.play(sounds.fail, 1, 1, 0, 0, 1);
                                        gameActions = fail;
                                    }
                                } else {
                                    plusScore = true;
                                    meaningRounds--;
                                    sounds.soundPool.play(sounds.pass, 1, 1, 0, 0, 1);
                                    gameActions = pass;

                                    sendData.add(clusters.get(thisRound)[i].imageName + ";" + clusters.get(thisRound)[i].clusterName + ";" + username);
                                }
                                stopDetect = true;
                                break;
                            }

                            if (text_buttons[9].getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if (clusters.get(thisRound)[i].isValidated) {
                                    if (count > 1) {
                                        plusScore = true;
                                        meaningRounds--;
                                        sounds.soundPool.play(sounds.pass, 1, 1, 0, 0, 1);
                                        gameActions = pass;
                                    } else {
                                        subsScore = true;
                                        sounds.soundPool.play(sounds.fail, 1, 1, 0, 0, 1);
                                        gameActions = fail;
                                    }
                                } else {
                                    plusScore = true;
                                    meaningRounds--;
                                    sounds.soundPool.play(sounds.pass, 1, 1, 0, 0, 1);
                                    gameActions = pass;

                                    sendData.add(clusters.get(thisRound)[i].clusterName + ";more_than_one;" + username);
                                }
                                stopDetect = true;
                                break;
                            }

                            if (text_buttons[10].getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if (clusters.get(thisRound)[i].isValidated) {
                                    if (count == 0) {
                                        plusScore = true;
                                        meaningRounds--;
                                        sounds.soundPool.play(sounds.pass, 1, 1, 0, 0, 1);
                                        gameActions = pass;
                                    } else {
                                        subsScore = true;
                                        sounds.soundPool.play(sounds.fail, 1, 1, 0, 0, 1);
                                        gameActions = fail;
                                    }
                                } else {
                                    plusScore = true;
                                    meaningRounds--;
                                    sounds.soundPool.play(sounds.pass, 1, 1, 0, 0, 1);
                                    gameActions = pass;

                                    sendData.add(clusters.get(thisRound)[i].clusterName + ";all_equal;" + username);
                                }
                                stopDetect = true;
                                break;
                            }
                        }
                    }
                }

                if (gameState == options) {
                    if (!resume_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                        if (resume_button.isPressed()) {
                            resume_button.inverseIsPressed();
                        }
                    } else {
                        gameState = inGame;
                        if (resume_button.isPressed()) {
                            resume_button.inverseIsPressed();
                        }
                    }

                    if (!home_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                        if (home_button.isPressed()) {
                            home_button.inverseIsPressed();
                        }
                    } else {
                        if (home_button.isPressed()) {
                            home_button.inverseIsPressed();
                        }
                        ((Activity) context).finish();
                    }
                }
                break;
        }
        return true;
    }

    public void pause() {
        inApp = false;

        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread.");
        }
    }

    public void resume() {
        inApp = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public static void shuffleImages(Double[] a) {
        int n = a.length;
        Random random = new Random();
        random.nextInt();
        for (int i = n - 1; i > 0; i--) {
            int change = random.nextInt(i);
            Double helper = a[i];
            a[i] = a[change];
            a[change] = helper;
        }
    }
}
