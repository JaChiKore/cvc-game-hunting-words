package edu.uab.cvc.huntingwords.screens.JumpScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

import edu.uab.cvc.huntingwords.presenters.utils.LanguageManager;
import edu.uab.cvc.huntingwords.presenters.utils.PrimaryFont;
import edu.uab.cvc.huntingwords.presenters.utils.ResourceManager;
import edu.uab.cvc.huntingwords.screens.fragments.JumpGame;


public class LoadingScreen extends BaseScreen {

    private Stage stage;

    private Skin skin;

    private Label loading;


    private AssetManager assetManager;
    private AssetManager localManager;
    private JumpGame game;
    private LanguageManager languageManager;

    public LoadingScreen(JumpGame game) {
        this.game = game;
        assetManager = ResourceManager.getInstance().getAssetManager();
        localManager = ResourceManager.getInstance().getLocalManager();

        languageManager = LanguageManager.getInstance();

        // Set up the stage and the skin. See GameOverScreen for more comments on this.
        stage = new Stage(new FitViewport(640, 360));
        skin = PrimaryFont.getInstance().getSkin(20);

        // Create some loading text using this skin file and position it on screen.
        loading = new Label(languageManager.getString("Cargando..."), skin);
        loading.setPosition(320 - loading.getWidth() / 2, 180 - loading.getHeight() / 2);
        stage.addActor(loading);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (assetManager.update() && localManager.update()) {
            game.finishedLoading();
        } else {
            int progress = (int) (assetManager.getProgress()*50+localManager.getProgress()*50);
            loading.setText(languageManager.getString("Cargando...") + progress + "%");
        }

        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
