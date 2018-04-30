package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
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
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.presenters.DifferenceGamePresenter;
import edu.uab.cvc.huntingwords.presenters.DifferenceGamePresenterImpl;
import edu.uab.cvc.huntingwords.screens.FragmentActivity;
import edu.uab.cvc.huntingwords.screens.Sounds;
import edu.uab.cvc.huntingwords.screens.Utils;
import edu.uab.cvc.huntingwords.screens.views.DifferenceView;
import edu.uab.cvc.huntingwords.utils.Constants;
import es.dmoral.toasty.Toasty;

import static edu.uab.cvc.huntingwords.Utils.COUNT_DOWN_INTERVAL;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_DIFF;
import static edu.uab.cvc.huntingwords.Utils.MAX_TIME;

/**
 * Created by carlosb on 4/15/18.
 */

public class DifferenceGame extends Fragment implements DifferenceView {

    @ColorInt
    int colorPrimary;
    private DifferenceGamePresenter presenter;

    @BindView(R.id.value_lives)
    public TextView lives;

    @Nullable
    @BindView(R.id.view_container_images)
    public LinearLayout table;


    @Nullable
    @BindView(R.id.value_points)
    public TextView points;


    private Sounds sounds;
    private int currentSound;
    Context context;
    FragmentActivity fragActivity;


    public static DifferenceGame newInstance() {
        DifferenceGame frag = new DifferenceGame();
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
        View view = inflater.inflate(R.layout.difference_game, container, false);
        ButterKnife.bind(this, view);

        view.setBackgroundColor(Utils.GetBackgroundColour(this.getActivity()));

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
        colorPrimary = typedValue.data;
        presenter = new DifferenceGamePresenterImpl(this,getPreferencesUsername(), this.getPreferencesLevel(), this.getPreferencesScore());


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private float scaledWidth = 300f;

    @Override
    public void newRoundPlay(List<String> filepaths) {
        ((TextView)(this.getActivity().findViewById(R.id.value_total_score))).setText(String.valueOf(getPreferencesScore()));
        table.removeAllViews();
        for (int i=0; i<filepaths.size(); i++) {
            ImageButton imageButton = new ImageButton(this.getActivity());
            imageButton.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            String filepath = filepaths.get(i);
            imageButton.setTag(filepath);
            imageButton.setBackgroundResource(R.drawable.border);
            File file =  new File(getActivity().getFilesDir(),filepath);
            Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());

            float scaled = scaledWidth / image.getWidth();
            imageButton.setImageBitmap(Bitmap.createScaledBitmap(image, (int)scaledWidth, (int)(scaled * (float)image.getHeight()), false));
            View.OnClickListener callback = (button) -> {
                presenter.checkImage((String)button.getTag());

            };
            imageButton.setOnClickListener(callback);
            table.addView(imageButton);

        }
    }


    @Override
    public void updateOK(float currentScore) {
        new Thread() {
            public void run() {
                getActivity().runOnUiThread(
                        () -> {
                            showHit();
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
                            showFail();
                            playFail();
                        });

            }
        }.start();

    }

    @Override
    public void runPlayAgainDialog(float currentScore, int level, CallbackPostDialog callback) {
        playFinish();
        if (getActivity() == null) {
            return;
        }

        Integer newTotalPoints = getPreferencesScore()+(int)currentScore;
        updatePreferencesScore(newTotalPoints);
        updatePreferencesLevel(level);
        points.setText("0");




        AlertDialog.Builder builder = new AlertDialog.Builder(DifferenceGame.this.getActivity());
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
            sounds.soundPool.stop(currentSound);
            dialog.dismiss();
            callback.execute();
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


    private void playOk () {
        currentSound = sounds.soundPool.play(sounds.pass, 1, 1, 0, 0, 1);

    }

    private void playFail() {

        currentSound = sounds.soundPool.play(sounds.fail, 1, 1, 0, 0, 1);
    }

    private void playFinish() {
        currentSound = sounds.soundPool.play(sounds.won,1,1,0,0,1);
    }



    private void showHit() {
        Toasty.success(this.getActivity(), "", Toast.LENGTH_SHORT, true).show();
    }

    private void showFail() {
        Toasty.error(this.getActivity(), "", Toast.LENGTH_SHORT, true).show();
    }



    @OnClick(R.id.dif_but_same)
    public void clickSame (Button button) {
        presenter.checkSame();
    }
    @OnClick(R.id.dif_but_more_than_one)
    public void clickDifferent(Button button) {
        presenter.checkDifferent();
    }




    @Override
    public void onStart() {
        super.onStart();
        this.presenter.newGame();
        sounds = new Sounds(this.getActivity());

    }




    private void updatePreferencesScore(Integer scoreMatch) {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(CURRENT_SCORE_DIFF,scoreMatch);
        editor.commit();
    }

    private Integer getPreferencesScore() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getInt(edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_DIFF,0);
    }
    private String getPreferencesUsername() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getString(Constants.PARAM_USERNAME,getString(R.string.anonym));
    }
    public int getPreferencesLevel() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        return preferences.getInt(edu.uab.cvc.huntingwords.Utils.CURRENT_LEVEL_DIFFERENCE,0);
    }
    public void updatePreferencesLevel(Integer level) {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                getString(R.string.preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(edu.uab.cvc.huntingwords.Utils.CURRENT_LEVEL_DIFFERENCE,level);
        editor.commit();
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
                        () -> {
                            lives.setText(String.valueOf(numLives));
                        });

            }
        }.start();
    }

}
