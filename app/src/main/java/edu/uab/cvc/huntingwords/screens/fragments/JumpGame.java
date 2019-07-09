package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Activity;
import android.content.Intent;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import edu.uab.cvc.huntingwords.application.JumpGameLauncher;
import edu.uab.cvc.huntingwords.presenters.utils.LanguageManager;
import edu.uab.cvc.huntingwords.presenters.utils.PlatformResolver;
import edu.uab.cvc.huntingwords.screens.JumpScreen.BaseScreen;
import edu.uab.cvc.huntingwords.screens.JumpScreen.GameScreen;
import edu.uab.cvc.huntingwords.screens.JumpScreen.LoadingScreen;
import edu.uab.cvc.huntingwords.screens.JumpScreen.MenuPause;
import edu.uab.cvc.huntingwords.tasks.loaders.LoaderJumpGameAssets;

public class JumpGame extends Game {

    public BaseScreen gameScreen, gamePauseScreen;
    private static PlatformResolver resolver = null;

    public static PlatformResolver getResolver() {
        return resolver;
    }

    public JumpGameLauncher parentApp;

    public String username;

    public JumpGame(JumpGameLauncher j, String username) {
        parentApp = j;
        this.username = username;
    }

    @Override
    public void create() {

        Gdx.input.setCatchBackKey(true);
        new LoaderJumpGameAssets().load();
        LanguageManager.getInstance();

        setScreen(new LoadingScreen(this));
    }

    public void finishedLoading() {
        gameScreen = new GameScreen(this);
        gamePauseScreen = new MenuPause(this,gameScreen);

        setScreen(gameScreen);
    }

    public void finishActivity() {
        Intent returnIntent = new Intent();
        parentApp.setResult(Activity.RESULT_OK,returnIntent);
        parentApp.finish();
    }
}
