package edu.uab.cvc.huntingwords.screens.JumpScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import de.tomgrill.gdxdialogs.core.GDXDialogs;
import de.tomgrill.gdxdialogs.core.GDXDialogsSystem;
import de.tomgrill.gdxdialogs.core.dialogs.GDXProgressDialog;
import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.presenters.utils.LanguageManager;
import edu.uab.cvc.huntingwords.presenters.utils.PrimaryFont;
import edu.uab.cvc.huntingwords.presenters.utils.ResourceManager;
import edu.uab.cvc.huntingwords.screens.fragments.JumpGame;
import edu.uab.cvc.huntingwords.tasks.loaders.LoaderJumpGameAssets;
import edu.uab.cvc.huntingwords.tasks.loaders.UpdateJumpGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;
import static edu.uab.cvc.huntingwords.Utils.CURRENT_SCORE_JUMP;


public class GameOverScreen extends BaseScreen {

    private Stage stage;

    private Skin skin;

    private Texture img;

    private SpriteBatch batch;

    private JumpGame game;

    private int score;

    public GameOverScreen(final JumpGame game, Integer finalScore) {
        this.game = game;
        this.score = finalScore;
        GDXDialogs dialogs = GDXDialogsSystem.install();


        LanguageManager languages = LanguageManager.getInstance();
        batch = new SpriteBatch();

        img = new Texture("pizarraNegra.png");

        // Create a new stage, as usual.
        stage = new Stage(new FitViewport(640, 360));

        skin = PrimaryFont.getInstance().getSkin(20);

        Image gameOver = new Image(ResourceManager.getInstance().getAssetManager().get("gameover.png", Texture.class));

        gameOver.setPosition(320 - gameOver.getWidth() / 2, 320 - gameOver.getHeight());

        TextButton backToGame = new TextButton(languages.getString("Continuar"), skin);
        TextButton menu = new TextButton(languages.getString("MenuP"), skin);

        backToGame.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GDXProgressDialog progressDialog = dialogs.newDialog(GDXProgressDialog.class);

                progressDialog.setTitle(game.context.getString(R.string.title_loading_info));
                progressDialog.setMessage(game.context.getString(R.string.downloading_text));

                progressDialog.build().show();
                //start a new thread to process job
                new Thread(() ->  {
                    new UpdateJumpGame().update(game.context,game.username);
                    progressDialog.dismiss();
                    AssetManager localManager = new AssetManager(new LocalFileHandleResolver());
                    new LoaderJumpGameAssets().loadImages(localManager);
                    ResourceManager.getInstance().setLocalManager(localManager);
                    stage.addAction(
                            sequence(
                                    Actions.delay(0f),
                                    Actions.run(new Runnable() {
                                        @Override
                                        public void run() {
                                            game.setScreen(new LoadingScreen(game,0));
                                        }
                                    })
                            )
                    );
                }).start();
            }
        });

        menu.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                updatePreferencesScore(finalScore);
                game.finishActivity();
            }
        });

        menu.setSize(200, 80);
        menu.setPosition(380, 50);
        backToGame.setSize(200, 80);
        backToGame.setPosition(60, 50);

        stage.addActor(gameOver);
        stage.addActor(backToGame);
        stage.addActor(menu);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        // Dispose assets.
        skin.dispose();
        stage.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(img, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        stage.act();
        stage.draw();
    }

    private void updatePreferencesScore(Integer scoreJump) {
        SharedPreferences preferences = game.parentApp.getSharedPreferences(
                game.parentApp.getString(R.string.preferences_file), Context.MODE_PRIVATE);
        int oldScore = preferences.getInt(CURRENT_SCORE_JUMP,0);
        if (scoreJump > oldScore) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(CURRENT_SCORE_JUMP, scoreJump);
            editor.apply();
        }
    }
}
