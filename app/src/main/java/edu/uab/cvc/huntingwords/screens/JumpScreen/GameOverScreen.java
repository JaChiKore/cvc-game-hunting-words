package edu.uab.cvc.huntingwords.screens.JumpScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

import edu.uab.cvc.huntingwords.R;
import edu.uab.cvc.huntingwords.presenters.utils.LanguageManager;
import edu.uab.cvc.huntingwords.presenters.utils.PrimaryFont;
import edu.uab.cvc.huntingwords.presenters.utils.ResourceManager;
import edu.uab.cvc.huntingwords.screens.fragments.JumpGame;

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

        LanguageManager languages = LanguageManager.getInstance();
        batch = new SpriteBatch();

        img = new Texture("pizarraNegra.png");

        // Create a new stage, as usual.
        stage = new Stage(new FitViewport(640, 360));

        skin = PrimaryFont.getInstance().getSkin(20);

        Label goBack = new Label(languages.getString(languages.getString("irMenu")), skin);
        goBack.setPosition(130,150);

        Image gameOver = new Image(ResourceManager.getInstance().getManager().get("gameover.png", Texture.class));

        gameOver.setPosition(320 - gameOver.getWidth() / 2, 320 - gameOver.getHeight());

        stage.addActor(gameOver);
        stage.addActor(goBack);


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

        if (Gdx.input.justTouched()) {
            updatePreferencesScore(score);
            game.finishActivity();
        }

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
