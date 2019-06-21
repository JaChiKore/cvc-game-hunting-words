package edu.uab.cvc.huntingwords.screens;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.util.Locale;

import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.screens.fragments.Init;

import edu.uab.cvc.huntingwords.screens.fragments.Languages;
import edu.uab.cvc.huntingwords.screens.fragments.Play;

public class FragmentActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String currentLang = Locale.getDefault().getLanguage();
        Locale locale;
        Configuration config;
        switch (currentLang) {
            case Languages.CATALAN_TAG:
                locale = new Locale(Languages.CATALAN_TAG);
                currentLang = "Catalan";
                break;
            case Languages.SPANISH_TAG:
                locale = new Locale(Languages.SPANISH_TAG);
                currentLang = "Spanish";
                break;
            case Languages.ENGLISH_TAG:
                locale = new Locale(Languages.ENGLISH_TAG);
                currentLang = "English";
                break;
            case Languages.CHINESE_TAG:
                locale = new Locale(Languages.CHINESE_TAG);
                currentLang = "Chinese";
                break;
            default:
                locale = new Locale(Languages.ENGLISH_TAG);
                currentLang = "English";
                break;
        }

        Locale.setDefault(locale);
        config = new Configuration(this.getBaseContext().getResources().getConfiguration());
        config.locale = locale;
        this.getBaseContext().getResources().updateConfiguration(config,
                this.getBaseContext().getResources().getDisplayMetrics());

        Answers.getInstance().logCustom(new CustomEvent("Language")
                .putCustomAttribute("Language", currentLang));

        setContentView(R.layout.fragment_activity);
    }

    @Override
    public void onBackPressed() {
        Fragment match = getFragmentManager().findFragmentByTag("how_to_play_match");
        Fragment diff = getFragmentManager().findFragmentByTag("how_to_play_difference");
        Fragment jump = getFragmentManager().findFragmentByTag("how_to_play_jump");
        if ((match != null && match.isVisible()) || (diff != null && diff.isVisible()) || (jump != null && jump.isVisible())) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_switch, new Play(), "play");
            ft.commit();
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_switch, new Init(), "init");
            ft.commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}