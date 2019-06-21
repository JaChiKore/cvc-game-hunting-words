package edu.uab.cvc.huntingwords.screens.fragments;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import edu.uab.cvc.huntingwords.application.JumpGameLauncher;
import edu.uab.cvc.huntingwords.presenters.utils.LanguageManager;
import edu.uab.cvc.huntingwords.presenters.utils.PlatformResolver;
import edu.uab.cvc.huntingwords.screens.JumpScreen.BaseScreen;
import edu.uab.cvc.huntingwords.screens.JumpScreen.GameOverScreen;
import edu.uab.cvc.huntingwords.screens.JumpScreen.GameScreen;
import edu.uab.cvc.huntingwords.screens.JumpScreen.LoadingScreen;
import edu.uab.cvc.huntingwords.screens.JumpScreen.MenuPause;
import edu.uab.cvc.huntingwords.tasks.loaders.LoaderJumpGameInformation;

public class JumpGame extends Game {

    public BaseScreen gameScreen, gameOverScreen, gamePauseScreen;
    private static PlatformResolver resolver = null;

    public static PlatformResolver getResolver() {
        return resolver;
    }

    private JumpGameLauncher parentApp;

    public JumpGame(JumpGameLauncher j) {
        parentApp = j;
    }

    @Override
    public void create() {

        Gdx.input.setCatchBackKey(true);
        new LoaderJumpGameInformation().load();
        LanguageManager.getInstance();

        setScreen(new LoadingScreen(this));
    }

    public void finishedLoading() {
        gameScreen = new GameScreen(this);
        gameOverScreen = new GameOverScreen(this,gameScreen);
        gamePauseScreen = new MenuPause(this,gameScreen);

        setScreen(gameScreen);
    }

    public void finishActivity() {
        parentApp.finish();
    }
}
