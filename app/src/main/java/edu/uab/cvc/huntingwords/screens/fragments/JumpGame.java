package edu.uab.cvc.huntingwords.screens.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;

import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.application.JumpGameLauncher;
import edu.uab.cvc.huntingwords.presenters.utils.LanguageManager;
import edu.uab.cvc.huntingwords.presenters.utils.PlatformResolver;
import edu.uab.cvc.huntingwords.presenters.utils.ResourceManager;
import edu.uab.cvc.huntingwords.screens.JumpScreen.BaseScreen;
import edu.uab.cvc.huntingwords.screens.JumpScreen.GameScreen;
import edu.uab.cvc.huntingwords.screens.JumpScreen.LoadingScreen;
import edu.uab.cvc.huntingwords.screens.JumpScreen.MenuPause;
import edu.uab.cvc.huntingwords.tasks.loaders.LoaderJumpGameAssets;
import edu.uab.cvc.huntingwords.tasks.loaders.UpdateJumpGame;

public class JumpGame extends Game {

    public BaseScreen gameScreen, gamePauseScreen;
    private static PlatformResolver resolver = null;

    public static PlatformResolver getResolver() {
        return resolver;
    }

    public JumpGameLauncher parentApp;

    public String username;

    public Context context;

    public JumpGame(JumpGameLauncher j, String username, Context context) {
        parentApp = j;
        this.username = username;
        this.context = context;
    }

    @Override
    public void create() {

        Gdx.input.setCatchBackKey(true);
        new LoaderJumpGameAssets().load();
        LanguageManager.getInstance();

        setScreen(new LoadingScreen(this,0));
    }

    public void finishedLoading(int initialScore) {
        gameScreen = new GameScreen(this,initialScore);
        gamePauseScreen = new MenuPause(this,gameScreen);

        setScreen(gameScreen);
    }

    public void loadMoreImagesAndStart(int finalScore) {
        ProgressDialog pd = ProgressDialog.show(context,context.getString(R.string.title_loading_info),context.getString(R.string.downloading_text));
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.show();
        //start a new thread to process job
        new Thread(() ->  {
            new UpdateJumpGame().update(context,username);
            pd.dismiss();
            AssetManager localManager = new AssetManager(new LocalFileHandleResolver());
            new LoaderJumpGameAssets().loadImages(localManager);
            ResourceManager.getInstance().setLocalManager(localManager);
            setScreen(new LoadingScreen(this,finalScore));
        }).start();
    }

    public void finishActivity() {
        Intent returnIntent = new Intent();
        parentApp.setResult(Activity.RESULT_OK,returnIntent);
        parentApp.finish();
    }
}
