package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
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
import android.widget.ImageView;
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
import edu.uab.cvc.huntingwords.screens.Sounds;
import edu.uab.cvc.huntingwords.screens.Utils;
import edu.uab.cvc.huntingwords.screens.dialogs.PlayAgainFragment;
import edu.uab.cvc.huntingwords.screens.views.MatchView;
import timber.log.Timber;

/**
 * Created by carlosb on 4/15/18.
 */

public class MatchGame  extends Fragment implements MatchView {
    public static final int MAX_TIME = 30000;
    public static final int COUNT_DOWN_INTERVAL = 1000;
    @ColorInt int colorPrimary;
    private MatchGamePresenter presenter;

    @BindView(R.id.value_time)
    public TextView time;

    @Nullable
    @BindView(R.id.value_points)
    public TextView points;

    private Sounds sounds;




    public static MatchGame newInstance() {
        MatchGame frag = new MatchGame();
        return frag;
    }



    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.match_game, container, false);
        ButterKnife.bind(this, view);

        view.setBackgroundColor( Utils.GetBackgroundColour(this.getActivity()));

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getActivity().getTheme();
        theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
         colorPrimary = typedValue.data;

        presenter = new MatchGamePresenterImpl(this);


        return view;
    }

    private static int [] idImages = {R.id.match_img_0_0, R.id.match_img_0_1, R.id.match_img_0_2, R.id.match_img_1_0, R.id.match_img_1_1, R.id.match_img_1_2
            , R.id.match_img_2_0, R.id.match_img_2_1, R.id.match_img_2_2, R.id.match_img_3_0, R.id.match_img_3_1, R.id.match_img_3_2};
    private static int [] idButtons = {R.id.match_but_0, R.id.match_but_1, R.id.match_but_2, R.id.match_but_3};

    @Override
    public void newRoundPlay(List<String> filepaths, List<String> buttons) {
        if (filepaths.size()!=idImages.length) {
            Timber.i("It doesn't have enough images");
            return;
        }
        if (buttons.size() !=idButtons.length) {
            Timber.i("It doesn't have buttons");
            return;
        }
        for (int i = 0; i<idImages.length; i++) {
            updateImageButton(idImages[i],filepaths.get(i));
        }

        for (int i=0; i < idButtons.length; i++) {
            updateInfoButton(idButtons[i],buttons.get(i));
        }
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
    }

    public void cleanButtons() {
        for (int i=0; i < idButtons.length; i++) {
            updateInfoButton(idButtons[i],"");
        }
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

    private float scaledWidth = 250f;
    private void updateImageButton(int idImage, String filepath) {
            ImageButton imageButton = (ImageButton) this.getActivity().findViewById(idImage);
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
        startCountdown();
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
/*
        PlayAgainFragment dialogFragment = new PlayAgainFragment();
        Bundle args = new Bundle();
        args.putSerializable(TABLE_RESULTS, correctValues);
        dialogFragment.setArguments(args);
        dialogFragment.show(this.getActivity().getFragmentManager(),"");
        */
    }

    @Override
    public void hideButton(int idImage) {
        this.getActivity().findViewById(idImage).setVisibility(View.INVISIBLE);
    }

    @Override
    public void messageNotEnoughImages() {
        Toast.makeText(this.getActivity(),getString(R.string.not_enough_images),Toast.LENGTH_SHORT).show();
    }


    @Override
    public void updateOK(int idImage, float currentScore) {
        playOk();
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
    public void runPlayAgainDialog() {
        FragmentManager fm = this.getFragmentManager();
        PlayAgainFragment dialog = new PlayAgainFragment();
        dialog.show(fm, "fragment_edit_name");

    }


    private void playOk () {
        sounds.soundPool.play(sounds.pass, 1, 1, 0, 0, 1);

    }

    private void playFail() {
        sounds.soundPool.play(sounds.fail, 1, 1, 0, 0, 1);
    }





    @Optional
    @OnClick({R.id.match_img_0_0, R.id.match_img_0_1, R.id.match_img_0_2, R.id.match_img_1_0, R.id.match_img_1_1, R.id.match_img_1_2, R.id.match_img_2_0, R.id.match_img_2_1, R.id.match_img_2_2, R.id.match_img_3_0, R.id.match_img_3_1, R.id.match_img_3_2})
    public void clickMatchImage(ImageButton button) {
        if (clickedImage!=-1) {
            this.getActivity().findViewById(clickedImage).setBackgroundColor(getResources().getColor(R.color.white));
        }
        clickedImage = button.getId();
        button.setBackgroundColor(colorPrimary);
        this.presenter.updateButtonsByImage((String)button.getTag());

    }
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

    private void startCountdown()  {
        new CountDownTimer(MAX_TIME, COUNT_DOWN_INTERVAL) {

            public void onTick(long millisUntilFinished) {
                time.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                presenter.finishRound();
            }
        }.start();


    }

}
