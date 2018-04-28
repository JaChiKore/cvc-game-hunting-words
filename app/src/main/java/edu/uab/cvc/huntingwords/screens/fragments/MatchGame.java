package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.presenters.MatchGamePresenter;
import edu.uab.cvc.huntingwords.presenters.MatchGamePresenterImpl;
import edu.uab.cvc.huntingwords.screens.FragmentActivity;
import edu.uab.cvc.huntingwords.screens.Sounds;
import edu.uab.cvc.huntingwords.screens.Utils;
import edu.uab.cvc.huntingwords.screens.views.MatchView;
import edu.uab.cvc.huntingwords.utils.Constants;
import timber.log.Timber;

import static edu.uab.cvc.huntingwords.Utils.ANY_CORRECT;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_DIFF;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_MATCH;
import static edu.uab.cvc.huntingwords.Utils.EMPTY_BUTTON;

/**
 * Created by carlosb on 4/15/18.
 */

public class MatchGame  extends Fragment implements MatchView {


    @ColorInt int colorPrimary;
    private MatchGamePresenter presenter;

    @BindView(R.id.value_time)
    public TextView time;

    @Nullable
    @BindView(R.id.value_points)
    public TextView points;

    @Nullable
    @BindView(R.id.view_match_container_images)
    public LinearLayout table;

    private Sounds sounds;
    private int currentSound;
    Context context;
    FragmentActivity fragActivity;
    private CountDownTimer timer;


    public static MatchGame newInstance() {
        MatchGame frag = new MatchGame();
        return frag;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        context = getActivity();
        fragActivity =(FragmentActivity)context;
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.match_game, container, false);
        ButterKnife.bind(this, view);
        view.setBackgroundColor(Utils.GetBackgroundColour(this.getActivity()));

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
         colorPrimary = typedValue.data;

        presenter = new MatchGamePresenterImpl(this,getPreferencesUsername());


        return view;
    }

 //   private static int [] idImages = {R.id.match_img_0_0, R.id.match_img_0_1, R.id.match_img_0_2, R.id.match_img_1_0, R.id.match_img_1_1, R.id.match_img_1_2
 //           , R.id.match_img_2_0, R.id.match_img_2_1, R.id.match_img_2_2, R.id.match_img_3_0, R.id.match_img_3_1, R.id.match_img_3_2};
    private static int [] idButtons = {R.id.match_but_0, R.id.match_but_1, R.id.match_but_2, R.id.match_but_3};

    @Override
    public void newRoundPlay(List<String> filepaths, List<String> buttons) {
        ((TextView)(this.getActivity().findViewById(R.id.value_match_total_score))).setText(String.valueOf(getPreferencesScore()));


        if (buttons.size() !=idButtons.length) {
            Timber.i("It doesn't have buttons");
            return;
        }
        startCountdown();



        table.removeAllViews();
        for (int i=0; i<filepaths.size(); i++) {
//            ContextThemeWrapper newContext = new ContextThemeWrapper(this.getActivity(), R.style.AppTheme);
//            ImageButton imageButton = new ImageButton(newContext);
            //imageButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            ImageButton imageButton = new ImageButton(this.getActivity());
            imageButton.setId(i+1);
            imageButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            String filepath = filepaths.get(i);
            imageButton.setTag(filepath);
            File file =  new File(getActivity().getFilesDir(),filepath);
            Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());

            float scaled = scaledWidth / image.getWidth();
            imageButton.setImageBitmap(Bitmap.createScaledBitmap(image, (int)scaledWidth, (int)(scaled * (float)image.getHeight()), false));

            View.OnClickListener callback = (button) -> {
                if (clickedImage!=-1) {
                    if (this.getActivity().findViewById(clickedImage)!=null) {
                        this.getActivity().findViewById(clickedImage).setBackgroundColor(getResources().getColor(R.color.white));
                    }
                }
                clickedImage = button.getId();
                button.setBackgroundColor(colorPrimary);
                this.presenter.updateButtonsByImage((String)button.getTag());

            };
            imageButton.setOnClickListener(callback);
            table.addView(imageButton);
        }
        for (int i=0; i < idButtons.length; i++) {
            updateInfoButton(idButtons[i],buttons.get(i));
        }


        (this.getActivity().findViewById(R.id.match_but_4)).setTag(ANY_CORRECT);
        ((Button) this.getActivity().findViewById(R.id.match_but_4)).setText(getString(R.string.none_of_these));
    }

    private void updateImageButton(String filepath) {

    }

    @Override
    public void updateButtons(List<String> buttons) {
        if (buttons.size() !=idButtons.length) {
            Timber.i("It doesn't have buttons");
            return;
        }
        for (int i=0; i < idButtons.length; i++) {
            updateInfoButton(idButtons[i],buttons.get(i));
        }
        (this.getActivity().findViewById(R.id.match_but_4)).setTag(ANY_CORRECT);
        ((Button) this.getActivity().findViewById(R.id.match_but_4)).setText(getString(R.string.none_of_these));
    }

    public void cleanButtons() {
        for (int i=0; i < idButtons.length; i++) {
            updateInfoButton(idButtons[i], EMPTY_BUTTON);
        }
        updateInfoButton(R.id.match_but_4,EMPTY_BUTTON);
    }


    private void showStar() {
        ((ImageView)this.getActivity().findViewById(R.id.match_image_ok_or_fail)).setImageResource(android.R.drawable.btn_star_big_on);
    }

    private void showOffStar() {
        ((ImageView)this.getActivity().findViewById(R.id.match_image_ok_or_fail)).setImageResource(android.R.drawable.btn_star_big_off);
    }

    private void cleanStar() {
        ((ImageView)this.getActivity().findViewById(R.id.match_image_ok_or_fail)).setImageResource(0);
    }



    private void updateInfoButton(int idButton, String text) {
        Button button = (Button) this.getActivity().findViewById(idButton);
        button.setTag(text);
        button.setText(text);
    }

    private float scaledWidth = 300f;
    private void updateImageButton(int idImage, String filepath) {
            ImageButton imageButton = (ImageButton) this.getActivity().findViewById(idImage);
            getActivity().findViewById(idImage).setVisibility(View.VISIBLE);
            imageButton.setTag(filepath);
            File file =  new File(getActivity().getFilesDir(),filepath);
            Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());

            float scaled = scaledWidth / image.getWidth();
            imageButton.setImageBitmap(Bitmap.createScaledBitmap(image, (int)scaledWidth, (int)(scaled * (float)image.getHeight()), false));
    }

    @Override
    public void onStart() {
        super.onStart();
        this.presenter.newGame();
        sounds = new Sounds(this.getActivity());

    }


    public int clickedImage = -1;

    @Override
    public void cleanResult(int idImage, int idButton) {
        if ( this.getActivity() == null ||   this.getActivity().findViewById(idImage) == null) {
            return;
        }

        this.clickedImage = -1;
        //TODO remove this!!
    }

    @Override
    public void hideButton(int idImage) {
        View v = (View)this.getActivity().findViewById(idImage);
                //.setVisibility(View.INVISIBLE);
        ((ViewManager)v.getParent()).removeView(v);
    }

    @Override
    public void messageNotEnoughImages() {
        new Thread() {
            public void run() {
                getActivity().runOnUiThread(
                        () -> {
                            Toast.makeText(getActivity(),getString(R.string.not_enough_images),Toast.LENGTH_LONG).show();
                        });

            }
        }.start();

    }


    @Override
    public void updateOK(int idImage, float currentScore) {
    //    playOk();
        hideButton(idImage);
        cleanButtons();
         new Thread() {
            public void run() {
                getActivity().runOnUiThread(
                        () -> {
                            showStar();
                            playOk();
                            points.setText(String.valueOf(currentScore));
                        });

            }
        }.start();

    }

    @Override
    public void updateFail() {
        new Thread() {
            public void run() {
                getActivity().runOnUiThread(
                        () -> {
                            showOffStar();
                            playFail();
                        });

            }
        }.start();

    }

    @Override
    public void runPlayAgainDialog(float currentScore) {
        playFinish();
        if (timer!=null) {
            timer.cancel();
        }
        if (getActivity() == null) {
            return;
        }

        final Integer oldScore = getPreferencesScore();
        final Integer newTotalPoints = oldScore+(int)currentScore;
        updatePreferencesScore(newTotalPoints);
        presenter.uploadResult(oldScore,newTotalPoints);
        points.setText("0");


        AlertDialog.Builder builder = new AlertDialog.Builder(fragActivity);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sounds.soundPool.stop(currentSound);
                dialog.dismiss();
                presenter.restartGame();

            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                sounds.soundPool.stop(currentSound);
                dialog.dismiss();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_switch, new Init());
                fragmentTransaction.commit();
            }
        });
        builder.setTitle(getString(R.string.play_again));
        builder.setMessage(getString(R.string.score)+" "+currentScore);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

    }


    private void playOk () {
        currentSound = sounds.soundPool.play(sounds.pass, 1, 1, 0, 0, 1);

    }

    private void playFail() {
        currentSound = sounds.soundPool.play(sounds.fail, 1, 1, 0, 0, 1);
    }

    private void playFinish() {
        currentSound = sounds.soundPool.play(sounds.won,1,1,0,0,1);
    }




/*
    @Optional
    @OnClick({R.id.match_img_0_0, R.id.match_img_0_1, R.id.match_img_0_2, R.id.match_img_1_0, R.id.match_img_1_1, R.id.match_img_1_2, R.id.match_img_2_0, R.id.match_img_2_1, R.id.match_img_2_2, R.id.match_img_3_0, R.id.match_img_3_1, R.id.match_img_3_2})
    public void clickMatchImage(ImageButton button) {
        if (clickedImage!=-1) {
            if (this.getActivity().findViewById(clickedImage)!=null) {
                this.getActivity().findViewById(clickedImage).setBackgroundColor(getResources().getColor(R.color.white));
            }
        }
        clickedImage = button.getId();
        button.setBackgroundColor(colorPrimary);
        this.presenter.updateButtonsByImage((String)button.getTag());

    }
    */
    @Optional
    @OnClick({ R.id.match_but_0, R.id.match_but_1, R.id.match_but_2, R.id.match_but_3, R.id.match_but_4 })
    public void clickMatchButton(Button button) {
        if (clickedImage==-1) {
            return;
        }
        this.getActivity().findViewById(clickedImage).setBackgroundColor(getResources().getColor(R.color.white));
        ImageButton image = (ImageButton)this.getActivity().findViewById(clickedImage);
        presenter.checkSolution(clickedImage, button.getId(),(String)image.getTag(),(String)button.getTag());
        //TODO clean when it eliminate two
    }


    public void startCountdown()  {

        timer = new CountDownTimer(edu.uab.cvc.huntingwords.Utils.MAX_TIME, edu.uab.cvc.huntingwords.Utils.COUNT_DOWN_INTERVAL) {

            public void onTick(long millisUntilFinished) {
                time.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                presenter.finishRound();
            }
        };
        timer.start();
    }

    private void updatePreferencesScore(Integer scoreMatch) {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CURRENT_SCORE_MATCH,scoreMatch);
        editor.commit();
    }

    private Integer getPreferencesScore() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getInt(edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_MATCH,0);
    }

    private String getPreferencesUsername() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getString(Constants.PARAM_USERNAME,getString(R.string.anonym));
    }
}
