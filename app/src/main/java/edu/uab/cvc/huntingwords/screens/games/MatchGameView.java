package edu.uab.cvc.huntingwords.screens.games;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.Random;

import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.tasks.GetRanking;
import edu.uab.cvc.huntingwords.tasks.UpdatePlayTimes;
import edu.uab.cvc.huntingwords.tasks.UpdateScore;
import edu.uab.cvc.huntingwords.tasks.match.UpdateTranscriptions;

import static edu.uab.cvc.huntingwords.screens.games.MenuView.BIG;
import static edu.uab.cvc.huntingwords.screens.games.MenuView.MEGA;
import static edu.uab.cvc.huntingwords.screens.games.MenuView.NORMAL;

@SuppressLint("ViewConstructor")
public class MatchGameView extends SurfaceView implements Runnable {
    Context context;

    private final RectF image_box;
    private final RectF image_box_outlines;
    private final RectF text_box;
    private final RectF text_box_outlines;

    private final int MAX_ALPHA = 255;

    private Thread gameThread = null;

    private volatile boolean inApp;

    private SurfaceHolder ourHolder;

    private Bitmaps im_trans;

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

    private int gameActions = none;

    private int gameActionsDrawCount = 3000;

    private int timer;
    private boolean lessTimer = false;
    private int countDown;
    private boolean lessCD = false;

    private final int lost = -1;
    private final int won = 1;

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
    private int numTextButtons;

    private int boxesWidth;
    private int boxesHeight;

    private int numWords;
    private int meaningWords;

    private boolean isSelectedIm = false;
    private int selectedImId;
    private boolean changeText = false;

    private String filename;
    private String fixFilename;
    private int score;
    private int[] maxScores;
    private String username;

    private boolean plusScore;
    private boolean subsScore;

    private int level = 1;

    private ArrayList<String> sendData;

    private String startDate = null;
    private String endDate = null;

    private int startScore;
    private int finalScore;

    public MatchGameView(Context context, int x, int y, String BDFilename, String BDFixFilename, String username, int num_games) {
        super(context);

        this.context = context;

        numTextButtons = 5;

        this.numWords = 12;

        this.filename = BDFilename;
        this.fixFilename = BDFixFilename;
        this.username = username;

        maxScores = new int[num_games];

        score = 0;

        screenX = x;
        screenY = y;

        int padding = 5;

        image_box = new RectF(screenX / 20,
                screenY / 5,
                screenX * 9 / 20,
                screenY * 9 / 10);
        image_box_outlines = new RectF(screenX / 20 - padding,
                screenY / 5 - padding,
                screenX * 9 / 20 + padding,
                screenY * 9 / 10 + padding);
        text_box = new RectF(screenX * 11 / 20,
                screenY / 5,
                screenX * 19 / 20,
                screenY * 9 / 10);
        text_box_outlines = new RectF(screenX * 11 / 20 - padding,
                screenY / 5 - padding,
                screenX * 19 / 20 + padding,
                screenY * 9 / 10 + padding);

        boxesWidth = screenX * 2 / 11;
        boxesHeight = screenY * 2 / 19;

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

        text_buttons = new Button[numTextButtons];

        text_buttons[0] = new Button(text_box.left + 10 /*padding*/,
                text_box.top + boxesHeight*2, boxesWidth, boxesHeight, "?");
        text_buttons[1] = new Button(text_box.right - 10 - boxesWidth,
                text_box.top + boxesHeight*2, boxesWidth, boxesHeight, "?");
        text_buttons[2] = new Button(text_box.left + 10 /*padding*/,
                text_box.top + boxesHeight * 3 + 10, boxesWidth, boxesHeight, "?");
        text_buttons[3] = new Button(text_box.right - 10 - boxesWidth,
                text_box.top + boxesHeight * 3 + 10, boxesWidth, boxesHeight, "?");
        text_buttons[4] = new Button(text_box.left + text_box.width()/2 - boxesWidth/2,
                text_box.top + boxesHeight * 4 + 30, boxesWidth, boxesHeight, "?");


        if (!this.username.contentEquals("anonim")) {
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

        prepareGame();
    }

    public void prepareGame() {
        int paddingX = 5;
        int paddingY = 5;

        sendData = new ArrayList<>();


        timer = 60;
        lessTimer = false;

        changeText = false;

        plusScore = false;
        subsScore = false;

        if (playerState == won) {
            level += 1;
        } else {
            score = 0;
            level = 1;
        }

        startScore = score;

        countDown = 3;
        lessCD = false;

        gameState = starting;
        playerState = -1;

        gameActions = none;

        for (int i = 0; i < numTextButtons; i++) {
            text_buttons[i].setText("?");
        }

        im_trans = new Bitmaps(context, boxesWidth, boxesHeight, numWords, filename, fixFilename, level);

        meaningWords = im_trans.getMaxWords();
        numWords = meaningWords;

        prepareImageBoxes(im_trans.getImage_transcription(), boxesWidth, boxesHeight, image_box, paddingX, paddingY, numWords);

        shuffleImages(im_trans.getImage_transcription());
    }
    @Override
    public void run() {
        long menaceInterval = 1000;
        long timeThisFrame;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hhmmss");

        while(inApp) {
            long startFrameTime = System.currentTimeMillis();

            if (gameState != over) {
                endDate = sdf.format(new Date());
                update();
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

            if (timer == 0 && meaningWords > 0) {
                if (!this.username.contentEquals("anonim")) {
                    if (score > maxScores[0]) {
                        maxScores[0] = score;

                        try {
                            new UpdateScore().execute(username, String.valueOf(maxScores[0]), String.valueOf(maxScores[1])).get();
                        } catch (Exception e) {
                            Log.e("Error:", "Failed writing high scores");
                        }
                    }
                }

                playerState = lost;
                gameState = over;
                finalScore = score;
            }

            if (meaningWords == 0) {
                String time1 = endDate.split(" ")[1].substring(2, 4);
                String time2 = startDate.split(" ")[1].substring(2, 4);
                int end = Integer.valueOf(endDate.split(" ")[1].substring(4, 6));
                int start = Integer.valueOf(startDate.split(" ")[1].substring(4, 6));
                if (time1.contentEquals(time2)) {
                    timer = 60 - (end - start);
                } else {
                    timer = start - end;
                }
                score += timer*10;
                if (!this.username.contentEquals("anonim")) {
                    if (score > maxScores[0]) {
                        maxScores[0] = score;

                        try {
                            new UpdateScore().execute(username, String.valueOf(maxScores[0]), String.valueOf(maxScores[1])).get();
                        } catch (Exception e) {
                            Log.e("Error:", "Failed writing high scores");
                        }
                    }
                }

                playerState = won;
                gameState = over;
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
                }
            }

            if (changeText) {
                if (isSelectedIm) {
                    Random r = new Random();
                    int a = r.nextInt(numTextButtons - 1);
                    int j = 0;
                    text_buttons[a].setText(im_trans.getImage_transcription().get(selectedImId).transcription);
                    for (int i = 0; i < numTextButtons - 1; i++) {
                        if (i != a) {
                            text_buttons[i].setText(im_trans.getImage_transcription().get(selectedImId).otherTrans[j]);
                            j++;
                        }
                    }
                    text_buttons[numTextButtons - 1].setText(context.getString(R.string.none_of_these));
                } else {
                    for (int i = 0; i < numTextButtons; i++) {
                        text_buttons[i].setText("?");
                    }
                }
                changeText = false;
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
                canvas.drawRect(text_box_outlines, paint);

                paint.setColor(Color.WHITE);
                canvas.drawRect(image_box, paint);
                canvas.drawRect(text_box, paint);

                //Draw images

                ArrayList<Bitmaps.Validation> images = im_trans.getImage_transcription();
                for (int i = 0; i < numWords; i++) {
                    Bitmaps.Validation im = images.get(i);
                    if (im.getStatus()) {
                        paint.setAlpha(im.alpha);
                        canvas.drawBitmap(im.image,
                                im.rect.left,
                                im.rect.top,
                                paint);
                    }
                }

                RectF r = new RectF();

                //Draw text boxes
                for (int i = 0; i < numTextButtons; i++) {
                    if (i < numTextButtons - 1) {
                        textHeight = BIG;
                    } else {
                        textHeight = NORMAL;
                    }
                    paint.setTextSize(textHeight);
                    r.left = text_buttons[i].getRect().left - 2;
                    r.top = text_buttons[i].getRect().top - 2;
                    r.right = text_buttons[i].getRect().right + 2;
                    r.bottom = text_buttons[i].getRect().bottom + 2;

                    paint.setColor(Color.BLACK);
                    canvas.drawRect(r, paint);

                    paint.setColor(Color.WHITE);
                    canvas.drawRect(text_buttons[i].getRect(), paint);

                    paint.setColor(Color.BLACK);
                    textWidth = paint.measureText(text_buttons[i].getText(),
                            0,
                            text_buttons[i].getText().length());
                    canvas.drawText(text_buttons[i].getText(),
                            text_buttons[i].getRect().left + text_buttons[i].getRect().width() / 2 - textWidth / 2,
                            text_buttons[i].getRect().top + text_buttons[i].getRect().height() / 2 + textHeight / 2,
                            paint);
                }

                paint.setAlpha(255);

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
                    paint.setAlpha(gameActionsDrawCount * MAX_ALPHA / 3000);
                    canvas.drawBitmap(im_trans.getFail(),
                            screenX / 2 - im_trans.getFail().getWidth() / 2,
                            5 + textHeight * 2,
                            paint);
                }

                if (gameActions == pass && gameActionsDrawCount > 0) {
                    paint.setAlpha(gameActionsDrawCount * MAX_ALPHA / 3000);
                    canvas.drawBitmap(im_trans.getPass(),
                            screenX / 2 - im_trans.getPass().getWidth() / 2,
                            5 + textHeight * 2,
                            paint);
                }

                paint.setAlpha(MAX_ALPHA);

                // draw score
                paint.setColor(Color.BLACK);
                paint.setTextSize(BIG);
                canvas.drawText(context.getString(R.string.score) + ": " + Integer.toString(score),
                        25,
                        25 + 40,
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

                    textHeight = NORMAL;
                    paint.setTextSize(textHeight);
                    paint.setColor(Color.BLACK);
                    text = context.getString(R.string.starts_zero);
                    textWidth = paint.measureText(text, 0, text.length());
                    canvas.drawText(text,
                            playAgain_button.getRect().left + playAgain_button.getRect().width() / 2 - textWidth / 2,
                            playAgain_button.getRect().bottom + 30,
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

                    textHeight = NORMAL;
                    paint.setTextSize(textHeight);
                    paint.setColor(Color.BLACK);
                    text = context.getString(R.string.starts_x_points);
                    textWidth = paint.measureText(text, 0, text.length());
                    canvas.drawText(text,
                            playAgain_button.getRect().left + playAgain_button.getRect().width() / 2 - textWidth / 2,
                            playAgain_button.getRect().bottom + 30,
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
        int REDUCED_ALPHA = 150;

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
                        for (int i = 0; i < sendData.size(); i++) {
                            String a = sendData.get(i);
                            String[] split = a.split(";");
                            try {
                                new UpdateTranscriptions().execute(split[0], split[1], split[2], String.valueOf(level), startDate, endDate, Integer.toString(startScore), Integer.toString(finalScore));
                            } catch (Exception e) {
                                Log.e("Error","Failed updating transcriptions");
                            }
                        }

                        ((Activity) context).finish();
                        if (exit_button.isPressed()) {
                            exit_button.inverseIsPressed();
                        }
                    }

                    if (!playAgain_button.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                        if (playAgain_button.isPressed()) {
                            playAgain_button.inverseIsPressed();
                        }
                    } else {
                        if (playAgain_button.isPressed()) {
                            playAgain_button.inverseIsPressed();
                        }
                        new UpdatePlayTimes().execute(username, "0");
                        for (int i = 0; i < sendData.size(); i++) {
                            String a = sendData.get(i);
                            String[] split = a.split(";");
                            try {
                                new UpdateTranscriptions().execute(split[0], split[1], split[2], String.valueOf(level), startDate, endDate, String.valueOf(startScore), String.valueOf(finalScore));
                            } catch (Exception e) {
                                Log.e("Error","Failed updating transcriptions");
                            }
                        }

                        prepareGame();
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

                    ArrayList<Bitmaps.Validation> images = im_trans.getImage_transcription();

                    for (int i = 0; i < numWords; i++) {
                        Bitmaps.Validation im = images.get(i);
                        if (im.getStatus()) {
                            if (im.rect.contains(motionEvent.getX(), motionEvent.getY())) {
                                if (isSelectedIm) {
                                    if (selectedImId != i) {
                                        images.get(selectedImId).setAlpha(MAX_ALPHA);
                                    }
                                }

                                if (im.getAlpha() == MAX_ALPHA) {
                                    im.setAlpha(REDUCED_ALPHA);
                                    selectedImId = i;
                                    isSelectedIm = true;
                                } else {
                                    im.setAlpha(MAX_ALPHA);
                                    isSelectedIm = false;
                                }
                                changeText = true;
                            }
                        }
                    }

                    for (int i = 0; i < numTextButtons; i++) {
                        if (isSelectedIm) {
                            Button t = text_buttons[i];
                            Bitmaps.Validation im = images.get(selectedImId);
                            if (t.getRect().contains(motionEvent.getX(), motionEvent.getY())) {
                                if (im.validated) {
                                    if (im.correct) {
                                        if (im.transcription.contentEquals(t.getText())) {
                                            im.setInactive();
                                            plusScore = true;
                                            meaningWords--;
                                            isSelectedIm = false;
                                            sounds.soundPool.play(sounds.pass, 1, 1, 0, 0, 1);
                                            gameActions = pass;
                                            changeText = true;
                                        } else {
                                            subsScore = true;
                                            sounds.soundPool.play(sounds.fail, 1, 1, 0, 0, 1);
                                            gameActions = fail;
                                        }
                                    } else {
                                        if (i == 4) {
                                            im.setInactive();
                                            plusScore = true;
                                            meaningWords--;
                                            isSelectedIm = false;
                                            sounds.soundPool.play(sounds.pass, 1, 1, 0, 0, 1);
                                            gameActions = pass;
                                            changeText = true;
                                        } else {
                                            subsScore = true;
                                            sounds.soundPool.play(sounds.fail, 1, 1, 0, 0, 1);
                                            gameActions = fail;
                                        }
                                    }
                                } else {
                                    im.setInactive();
                                    plusScore = true;
                                    meaningWords--;
                                    isSelectedIm = false;
                                    sounds.soundPool.play(sounds.pass, 1, 1, 0, 0, 1);
                                    gameActions = pass;
                                    changeText = true;

                                    if (t.getText().contains(context.getString(R.string.none_of_these))) {
                                        sendData.add(im.imName + ";None;" + username);
                                    } else {
                                        sendData.add(im.imName + ";" + t.getText() + ";" + username);
                                    }
                                    System.out.println(im.imName + ";" + t.getText() + ";" + username);
                                }
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
    
    
    public RectF getRect(RectF oldRect, float imageWidth, float imageHeight, RectF box, int paddingX, int paddingY, int level) {

        RectF newRect;
        float x, y;

        if (oldRect == null) {
            x = paddingX + box.left;
            y = paddingY + box.top;
            newRect = new RectF(x, y, x + imageWidth, y + imageHeight);
        } else {
            x = oldRect.right;
            y = oldRect.top;

            if(x + paddingX + imageWidth > box.right) {
                if (paddingY + box.top + (imageHeight + paddingY) * level <= box.bottom) {
                    x = paddingX + box.left;
                    y = paddingY + box.top + (imageHeight + paddingY) * level;

                    newRect = new RectF(x, y, x + imageWidth, y + imageHeight);
                } else {
                    newRect = null;
                }
            } else {
                x += paddingX;

                newRect = new RectF(x, y, x + imageWidth, y + imageHeight);
            }
        }

        return newRect;
    }

    public void prepareImageBoxes(ArrayList<Bitmaps.Validation> im_trans, float imageWidth, float imageHeight, RectF imBox, int paddingX, int paddingY, int numWords) {

        int level = 0;
        RectF rBefore, rNow;

        rBefore = getRect(null, imageWidth, imageHeight, imBox, paddingX, paddingY, level);

        im_trans.get(0).rect = rBefore;

        for (int i = 1; i < numWords; i++) {
            if(rBefore.right + paddingX + imageWidth > imBox.right) {
                level++;
            }

            rNow = getRect(rBefore, imageWidth, imageHeight, imBox, paddingX, paddingY, level);
            if(rNow == null) {
                System.out.println("YA NO CABEN MÁS IMÁGENES. MÁXIMO DE IMÁGENES: " + i);
                break;
            }

            im_trans.get(i).rect = rNow;
            rBefore = rNow;
        }
    }

    //TODO ANALYSE ERROR HERE
    public static void shuffleImages(ArrayList<Bitmaps.Validation> a) {
        int n = a.size();
        Random random = new Random();
        random.nextInt();
        for (int i = n - 1; i > 0; i--) {
            int change = random.nextInt(i);
            RectF helper = a.get(i).rect;
            a.get(i).rect = a.get(change).rect;
            a.get(change).rect = helper;
        }
    }
}
