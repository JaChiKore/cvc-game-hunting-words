package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.File;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.presenters.DifferenceGamePresenter;
import edu.uab.cvc.huntingwords.presenters.DifferenceGamePresenterImpl;
import edu.uab.cvc.huntingwords.presenters.MatchGamePresenter;
import edu.uab.cvc.huntingwords.presenters.MatchGamePresenterImpl;
import edu.uab.cvc.huntingwords.screens.Sounds;
import edu.uab.cvc.huntingwords.screens.Utils;
import edu.uab.cvc.huntingwords.screens.views.DifferenceView;
import edu.uab.cvc.huntingwords.screens.views.MatchView;

/**
 * Created by carlosb on 4/15/18.
 */

public class DifferenceGame extends Fragment implements DifferenceView {
    public static final int MAX_TIME = 30000;
    public static final int COUNT_DOWN_INTERVAL = 1000;

    @ColorInt
    int colorPrimary;
    private DifferenceGamePresenter presenter;

    @BindView(R.id.value_time)
    public TextView time;


    @Nullable
    @BindView(R.id.table_difference_layout)
    public TableLayout table;



    private Sounds sounds;

    public static DifferenceGame newInstance() {
        DifferenceGame frag = new DifferenceGame();
        return frag;
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
        presenter = new DifferenceGamePresenterImpl(this);


        return view;
    }

    private static int SIZE_FOR_ROW = 3;
    private float scaledWidth = 250f;

    @Override
    public void newRoundPlay(List<String> filepaths) {
        table.removeAllViews();
        TableRow row = new TableRow(this.getActivity());
        for (int i=0; i<filepaths.size(); i++) {
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            ImageButton imageButton = new ImageButton(this.getActivity());
            String filepath = filepaths.get(i);
            imageButton.setTag(filepath);
            File file =  new File(getActivity().getFilesDir(),filepath);
            Bitmap image = BitmapFactory.decodeFile(file.getAbsolutePath());

            float scaled = scaledWidth / image.getWidth();
            imageButton.setImageBitmap(Bitmap.createScaledBitmap(image, (int)scaledWidth, (int)(scaled * (float)image.getHeight()), false));
            View.OnClickListener callback = (button) -> {
                presenter.checkImage((String)button.getTag());

            };
            imageButton.setOnClickListener(callback);
            row.addView(imageButton);

            if (i%(SIZE_FOR_ROW-1) == 0 && i!=0) {
                table.addView(row);
                row = new TableRow(this.getActivity());
            }

        }
    }

    public void clickSame (Button button) {
        presenter.checkSame();
    }

    public void clickDifferent(Button button) {
        presenter.checkDifferent();
    }



    @Override
    public void onStart() {
        super.onStart();
        startCountdown();
        this.presenter.newGame();
        sounds = new Sounds(this.getActivity());

    }




    private void startCountdown()  {
        new CountDownTimer(MAX_TIME, COUNT_DOWN_INTERVAL) {

            public void onTick(long millisUntilFinished) {
                time.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                time.setText("done!");
            }
        }.start();


    }
}
