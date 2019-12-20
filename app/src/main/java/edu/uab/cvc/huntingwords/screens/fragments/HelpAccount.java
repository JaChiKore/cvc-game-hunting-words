package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import butterknife.ButterKnife;
import butterknife.OnClick;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.screens.Utils;

public class HelpAccount extends Fragment {
    private int image_num = 0;
    private ImageView help_image;
    private TextView help_text;
    private Button previousButton;
    private Button nextButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.help_account_fragment, container, false);
        ButterKnife.bind(this, view);

        view.setBackgroundColor(Utils.GetBackgroundColour(getActivity()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        help_image = getActivity().findViewById(R.id.helpImage);
        help_text = getActivity().findViewById(R.id.help_account_text);
        previousButton = getActivity().findViewById(R.id.previousImage);
        nextButton = getActivity().findViewById(R.id.nextImage);

        help_image.setBackground(
                ResourcesCompat.getDrawable(getResources(),R.drawable.help_account_0,null));
        help_text.setText(getString(R.string.help_account_text_0));

        previousButton.setClickable(false);
        if (Build.VERSION.SDK_INT >= 21) {
            previousButton.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.nonClicable, null));
        }
    }

    private int getImage() {
        if (image_num == 0) {
            previousButton.setClickable(false);
            if (Build.VERSION.SDK_INT >= 21) {
                previousButton.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.nonClicable, null));
            }
        } else {
            previousButton.setClickable(true);
            if (Build.VERSION.SDK_INT >= 21) {
                previousButton.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.default_button_color, null));
            }
        }

        if (image_num == 4) {
            nextButton.setClickable(false);
            if (Build.VERSION.SDK_INT >= 21) {
                nextButton.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.nonClicable, null));
            }
        } else {
            nextButton.setClickable(true);
            if (Build.VERSION.SDK_INT >= 21) {
                nextButton.setBackgroundTintList(ResourcesCompat.getColorStateList(getResources(), R.color.default_button_color, null));
            }
        }
        switch (image_num) {
            case 0:return R.drawable.help_account_0;
            case 1:return R.drawable.help_account_1;
            case 2:return R.drawable.help_account_2;
            case 3:return R.drawable.help_account_3;
            case 4:return R.drawable.help_account_4;
        }
        return 0;
    }

    private int getText() {
        switch (image_num) {
            case 0:return R.string.help_account_text_0;
            case 1:return R.string.help_account_text_1;
            case 2:return R.string.help_account_text_2;
            case 3:return R.string.help_account_text_3;
            case 4:return R.string.help_account_text_4;
        }
        return 0;
    }

    @OnClick(R.id.previousImage)
    public void previousImage() {
        if (image_num > 0) {
            image_num -= 1;
            help_image.setBackground(
                    ResourcesCompat.getDrawable(getResources(),getImage(),null));
            help_text.setText(getString(getText()));
        }
    }

    @OnClick(R.id.nextImage)
    public void nextImage() {
        if (image_num < 4) {
            image_num += 1;
            help_image.setBackground(
                    ResourcesCompat.getDrawable(getResources(),getImage(),null));
            help_text.setText(getString(getText()));
        }
    }
}
