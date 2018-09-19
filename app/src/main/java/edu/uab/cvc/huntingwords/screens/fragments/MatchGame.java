package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
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
import es.dmoral.toasty.Toasty;
import timber.log.Timber;

import static edu.uab.cvc.huntingwords.Utils.ANY_CORRECT;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_LEVEL_MATCH;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_MATCH;
import static edu.uab.cvc.huntingwords.Utils.EMPTY_BUTTON;

/**
 * Created by carlosb on 4/15/18.
 */

public class MatchGame  extends Fragment implements MatchView {


    @ColorInt int colorPrimary;
    private MatchGamePresenter presenter;

    @BindView(R.id.value_lives)
    public TextView lives;

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

    private static int [] idButtons = {R.id.match_but_0, R.id.match_but_1, R.id.match_but_2, R.id.match_but_3};

    public int clickedImage = -1;
    private boolean pause;


    public static MatchGame newInstance() {
        return new MatchGame();
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

        presenter = new MatchGamePresenterImpl(this, getPreferencesUsername(), this.getPreferencesLevel(), this.getDiffLevel(), this.getPreferencesScore(), this.getPreferencesDiffScore());
        ((TextView) getActivity().findViewById(R.id.value_match_score)).setText(String.valueOf(0));

        pause = false;

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void newRoundPlay(List<String> filePaths, List<String> buttons) {
        float scaledWidth = 300f;
        ((TextView)(this.getActivity().findViewById(R.id.value_match_score))).setText(String.valueOf(getPreferencesScore()));

        if (buttons.size() !=idButtons.length) {
            Timber.i("It doesn't have buttons");
            return;
        }


        table.removeAllViews();
        for (int i = 0; i< filePaths.size(); i++) {
            ImageButton imageButton = new ImageButton(this.getActivity());
            imageButton.setId(i+1);
            //first image add as selected
            if (i==0)  {
                clickedImage = i+1;
                imageButton.setBackgroundColor(colorPrimary);
            } else {
                imageButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                imageButton.setBackgroundResource(R.drawable.border);
            }
            String filepath = filePaths.get(i);
            imageButton.setTag(filepath);
            File file =  new File(getActivity().getFilesDir(),filepath);
            Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());

            float scaled = scaledWidth / image.getWidth();
            imageButton.setImageBitmap(Bitmap.createScaledBitmap(image, (int)scaledWidth, (int)(scaled * (float)image.getHeight()), false));

            View.OnClickListener callback = (button) -> selectImageButton(button);
            imageButton.setOnClickListener(callback);
            table.addView(imageButton);
        }
        for (int i=0; i < idButtons.length; i++) {
            updateInfoButton(idButtons[i],buttons.get(i));
        }


        (this.getActivity().findViewById(R.id.match_but_4)).setTag(ANY_CORRECT);
        ((Button) this.getActivity().findViewById(R.id.match_but_4)).setText(getString(R.string.none_of_these));
    }

    private void selectImageButton(View button) {
        if (pause) {
            return;
        }

        if (clickedImage!=-1) {
            if (this.getActivity().findViewById(clickedImage)!=null) {
                this.getActivity().findViewById(clickedImage).setBackgroundResource(R.drawable.border);
            }
        }
        clickedImage = button.getId();
        button.setBackgroundColor(colorPrimary);
        this.presenter.updateButtonsByImage((String)button.getTag());
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
        for (int id: idButtons) {
            updateInfoButton(id, EMPTY_BUTTON);
        }
        updateInfoButton(R.id.match_but_4,EMPTY_BUTTON);
    }

    private void showHit() {
        Toasty.success(this.getActivity(), "", Toast.LENGTH_SHORT, true).show();
    }

    private void showFail() {
        Toasty.error(this.getActivity(), "", Toast.LENGTH_SHORT, true).show();
    }




    private void updateInfoButton(int idButton, String text) {
        Button button = this.getActivity().findViewById(idButton);
        button.setTag(text);
        button.setText(text);
    }

    private void selectNextButton() {
        if (table.getChildCount()<=0) {
            return;
        }
        ImageButton button =  (ImageButton)table.getChildAt(0);
        selectImageButton(button);
    }



    @Override
    public void onStart() {
        super.onStart();
        this.presenter.newGame();
        sounds = new Sounds(this.getActivity());

    }





    @Override
    public void updateOK(int idImage, float currentScore) {
        hideButton(idImage);
        cleanButtons();
         new Thread() {
            public void run() {
                getActivity().runOnUiThread(
                        () -> {
                            showHit();
                            playOk();
                            points.setText(String.valueOf(currentScore));
                            selectNextButton();
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
                            showFail();
                            playFail();
                        });

            }
        }.start();

    }


    @Optional
    @OnClick({ R.id.match_but_0, R.id.match_but_1, R.id.match_but_2, R.id.match_but_3, R.id.match_but_4 })
    public void clickMatchButton(Button button) {
        if (pause) {
            return;
        }
        if (clickedImage==-1) {
            return;
        }
        ImageButton image = this.getActivity().findViewById(clickedImage);
        presenter.checkSolution(clickedImage, button.getId(),(String)image.getTag(),(String)button.getTag());
    }


    @Override
    public void runPlayAgainDialog(boolean win,float currentScore, int level, CallbackPostDialog postDialog) {
        if (win) {
            playFinish();
            points.setText(String.valueOf(currentScore));
        } else {
            points.setText("0");
        }

        if (getActivity() == null) {
            return;
        }

        updatePreferencesScore((int)currentScore);
        updatePreferencesLevel(level);


        AlertDialog.Builder builder = new AlertDialog.Builder(fragActivity);
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
            sounds.soundPool.stop(currentSound);
            dialog.dismiss();
            postDialog.execute();
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, id) -> {
            sounds.soundPool.stop(currentSound);
            dialog.dismiss();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_switch, new Init());
            fragmentTransaction.commit();
        });
        builder.setTitle(getString(R.string.play_again));
        builder.setMessage(getString(R.string.score)+" "+currentScore);

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

    }



    @Override
    public void startDialog()
    {

        ProgressDialog pd = ProgressDialog.show(getActivity(),getString(R.string.title_loading_info),getString(R.string.downloading_text));
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        //start a new thread to process job
        new Thread(() ->  {
            presenter.loadMoreInfo();
            pd.dismiss();
            getActivity().runOnUiThread(() -> presenter.restartGame());
        }).start();

    }

    @Override
    public void setUpNumLives(int numLives) {
        new Thread() {
            public void run() {
                getActivity().runOnUiThread(
                        () -> lives.setText(String.valueOf(numLives)));

            }
        }.start();
    }

    @Override
    public void setPause(boolean pause) {
        this.pause = pause;
    }

    @Override
    public void updateTotalScore(float totalScore) {
        ((TextView)getActivity().findViewById(R.id.value_match_score)).setText(String.valueOf(totalScore));
    }


    @Override
    public void hideButton(int idImage) {
        View v = this.getActivity().findViewById(idImage);
        ((ViewManager)v.getParent()).removeView(v);
    }

    @Override
    public void messageNotEnoughImages() {
        new Thread() {
            public void run() {
                getActivity().runOnUiThread(
                        () -> Toast.makeText(getActivity(),getString(R.string.not_enough_images),Toast.LENGTH_LONG).show());

            }
        }.start();

    }


    private void playOk () {
        currentSound = sounds.soundPool.play(sounds.pass, 1, 1, 0, 0, 1);

    }

    private void playFail() {
        currentSound = sounds.soundPool.play(sounds.fail, 1, 1, 0, 0, 1);
    }

    private void playFinish() { currentSound = sounds.soundPool.play(sounds.won,1,1,0,0,1); }


    private void updatePreferencesScore(Integer scoreMatch) {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        int oldScore = preferences.getInt(CURRENT_SCORE_MATCH,0);
        if (scoreMatch > oldScore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(CURRENT_SCORE_MATCH, scoreMatch);
            editor.apply();
        }
    }

    private Integer getPreferencesScore() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getInt(edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_MATCH,0);
    }

    private String getPreferencesUsername() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getString(edu.uab.cvc.huntingwords.Utils.PARAM_USERNAME,getString(R.string.anonym));
    }

    public int getPreferencesLevel() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getInt(edu.uab.cvc.huntingwords.Utils.CURRENT_LEVEL_MATCH,0);
    }
    public int getDiffLevel() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getInt(edu.uab.cvc.huntingwords.Utils.CURRENT_LEVEL_DIFFERENCE,0);
    }
    public void updatePreferencesLevel(Integer level) {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        int oldLevel = preferences.getInt(CURRENT_LEVEL_MATCH,0);
        if (level > oldLevel) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(CURRENT_LEVEL_MATCH,level);
            editor.apply();
        }
    }


    public float getPreferencesDiffScore() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getInt(edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_DIFF,0);
    }
}
