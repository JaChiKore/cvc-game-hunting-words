package edu.uab.cvc.huntingwords.screens.JumpScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import edu.uab.cvc.huntingwords.presenters.utils.LanguageManager;
import edu.uab.cvc.huntingwords.presenters.utils.PrimaryFont;
import edu.uab.cvc.huntingwords.presenters.utils.ResourceManager;
import edu.uab.cvc.huntingwords.screens.fragments.JumpGame;

public class MenuPause extends BaseScreen {

    private Stage stage;

    private Skin skin;

    private Texture img;

    private SpriteBatch batch;
    
    public MenuPause(final JumpGame game, BaseScreen parent) {

        LanguageManager languages = LanguageManager.getInstance();
        batch = new SpriteBatch();

        img = new Texture("pizarraNegra.png");

        stage = new Stage(new FitViewport(640, 360));

        skin = PrimaryFont.getInstance().getSkin(15);
        TextButton backToGame = new TextButton(languages.getString("Continuar"), skin);
        TextButton menu = new TextButton(languages.getString("MenuP"), skin);

        Image pause = new Image(ResourceManager.getInstance().getAssetManager().get("pause.png", Texture.class));

        backToGame.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Take me to the game screen!
                game.setScreen(parent);
            }
        });

        menu.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.finishActivity();
            }
        });

        pause.setPosition(320 - pause.getWidth() / 2, 320 - pause.getHeight());
        backToGame.setSize(200, 80);
        menu.setSize(200, 80);
        backToGame.setPosition(60, 50);
        menu.setPosition(380, 50);

        stage.addActor(backToGame);
        stage.addActor(menu);
        stage.addActor(pause);
    }

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
}
