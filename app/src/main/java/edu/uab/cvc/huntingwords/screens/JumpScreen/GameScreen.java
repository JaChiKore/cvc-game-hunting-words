package edu.uab.cvc.huntingwords.screens.JumpScreen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.SnapshotArray;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.uab.cvc.huntingwords.Entities.EntityFactory;
import edu.uab.cvc.huntingwords.Entities.FloorEntity;
import edu.uab.cvc.huntingwords.Entities.WordEntity;
import edu.uab.cvc.huntingwords.Entities.PlayerEntity;
import edu.uab.cvc.huntingwords.presenters.utils.Constants;
import edu.uab.cvc.huntingwords.presenters.utils.LanguageManager;
import edu.uab.cvc.huntingwords.presenters.utils.PrimaryFont;
import edu.uab.cvc.huntingwords.presenters.utils.ResourceManager;
import edu.uab.cvc.huntingwords.screens.fragments.JumpGame;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.removeActor;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.repeat;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

public class GameScreen extends BaseScreen {

    private class ControlTuple {
        WordEntity control;
        String index;
        ControlTuple(WordEntity c, String i) {
            control = c;
            index = i;
        }
    }

    private Stage stage;

    private World world;

    private PlayerEntity player;

    private FloorEntity floor;

    private List<WordEntity> playingWords;

    private Sound jumpSound;

    private Sound dieSound;

    private Music level;

    private Music backgroundMusic;

    private Vector3 position;

    private BitmapFont puntos;

    private BitmapFont vida;

    private BitmapFont niveles;

    private int puntuacion;

    private int puntuacionMaxima;

    private int nivel;

    private int screenWidth, screenHeight;

    private Preferences preferences;

    private SpriteBatch batch;

    private boolean duplicado = false;

    private float ejeX = 11f, ejeY = 3f;

    private int vidas;

    private Texture img;

    private Skin skin;

    private static final int VIDA_INICIAL = 3;

    private static final int NIVEL = 1;

    private ArrayList<ControlTuple> controlWords;
    private HashMap<String,ArrayList<WordEntity>> words;
    private Iterator<ControlTuple> controlWordsIterator;
    private ControlTuple playingControlWord;

    private AssetManager manager;
    private JumpGame game;
    private LanguageManager languageManager;

    public GameScreen(final JumpGame game) {
        this.game = game;
        manager = ResourceManager.getInstance().getManager();
        languageManager = LanguageManager.getInstance();

        screenWidth = Gdx.app.getGraphics().getWidth();
        screenHeight = Gdx.app.getGraphics().getHeight();

        System.out.println("app system width,height:"+screenWidth+", "+screenHeight);
                // Create a new Scene2D stage for displaying things.
        stage = new Stage(new FitViewport(640, 360));
        position = new Vector3(stage.getCamera().position);

        System.out.println("initial camera position:" + position);

        skin = PrimaryFont.getInstance().getSkin(20);

        //puntos = new BitmapFont(Gdx.files.internal("skin/score.fnt"), Gdx.files.internal("skin/score.png"), false);
        puntos = skin.getFont("default-font");

        //niveles = new BitmapFont(Gdx.files.internal("skin/score.fnt"), Gdx.files.internal("skin/score.png"), false);
        niveles = skin.getFont("default-font");

        //vida = new BitmapFont(Gdx.files.internal("skin/vida.fnt"), Gdx.files.internal("skin/vida.png"), false);
        vida = skin.getFont("default-font");

        puntuacion = 0;
        nivel = NIVEL;
        vidas = VIDA_INICIAL;

        // Create a new Box2D world for managing things.
        world = new World(new Vector2(0, -10), true);

        world.setContactListener(new ContactListener() {
            @Override
            public void beginContact(Contact contact) {

                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                if (fixtureA.getUserData().toString().equals("floor") || fixtureB.getUserData().toString().equals("floor")) {
                    player.setJumping(false);
                }
            }

            @Override
            public void endContact(Contact contact) {}

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {}

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {}

        });

        img = new Texture("pizarraNegra.png");

        // Get the sound effect references that will play during the game.
        jumpSound = manager.get("audio/jump.ogg");
        dieSound = manager.get("audio/die.ogg");
        backgroundMusic = manager.get("audio/play.ogg");
        backgroundMusic.setVolume(0.3f);
        level = manager.get("audio/nivel.ogg");

        batch = new SpriteBatch();
        EntityFactory factory = new EntityFactory(manager);
        player = factory.createPlayer(world,  new Vector2(2.5f, 1.5f));
        floor = factory.createFloor(world, 0, 11, 1);


        controlWords = new ArrayList<>();
        words = new HashMap<>();
        randomWalk(Gdx.files.internal("files"));
        controlWordsIterator = controlWords.listIterator();
        playingControlWord = controlWordsIterator.next();
        stage.addActor(playingControlWord.control);

        stage.addActor(floor);

        playingWords = words.get(playingControlWord.index);

        for (WordEntity p : playingWords)
            stage.addActor(p);

        stage.addActor(player);

        preferences = Gdx.app.getPreferences("-_PuntuacionJugador_-");
        stage.getCamera().position.set(position);
        stage.getCamera().update();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // Everything is ready, turn the volume up.
        backgroundMusic.setLooping(true);
        backgroundMusic.play();
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Mostrar puntuacion, puntuacion maxima y las vidas
        float scale;
        switch (screenWidth) {
            case 320:
                scale = 0.5f;
                break;
            case 480:
                scale = 1f;
                break;
            case 800:
                scale = 1.5f;
                break;
            case 1280:
                scale = 2f;
                break;
            case 1794:
                scale = 2.5f;
                break;
            case 2392:
                scale = 3f;
                break;
            default:
                scale = 1.85f;
                break;
        }
        batch.begin();
        batch.draw(img, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        puntos.getData().setScale(scale);

        puntos.draw(batch, languageManager.getString("Puntuacion") + puntuacion, screenWidth/45f,screenHeight*23/25f);
        vida.getData().setScale(scale);
        vida.draw(batch, languageManager.getString("Vidas") + vidas, screenWidth/45f, screenHeight*21/25f);
        niveles.getData().setScale(scale);
        niveles.draw(batch, languageManager.getString("Nivel") + nivel, screenWidth/45f, screenHeight*19/25f);
        batch.end();

        if (player.isAlive()) {
            float speed = 0;
            if (nivel == 1) {
                speed = Constants.GAME_SPEED;
            } else if (nivel == 2) {
                speed = Constants.GAME_SPEED_LEVEL2;
            } else if (nivel == 3) {
                speed = Constants.GAME_SPEED_LEVEL3;
            }

            for (WordEntity p : playingWords)
                p.setPosition(p.getX()-speed,p.getY());
        }

        stage.act();

        for (Actor a : stage.getActors()) {
            if (a instanceof WordEntity) {
                if (a.getActions().size > 0) {
                    continue;
                }
                if (player.isJumping()) {
                    if (((player.getX() + player.getWidth() >= a.getX() && player.getX() + player.getWidth() <= a.getX() + a.getWidth()) ||
                            (player.getX() >= a.getX() && player.getX() <= a.getX() + a.getWidth()))
                            && player.getY() + player.getHeight() >= a.getY() && player.getY() + player.getHeight() <= a.getY() + a.getHeight()) {
                        if (((WordEntity) a).getFixture().getUserData().toString().contains("palabra0")) {
                            puntuacion++;
                            a.addAction(sequence(moveTo(a.getX(),a.getY()+30,0.5f),removeActor()));

                            if (puntuacion == 5) {
                                level.play();
                                nivel = 2;
                            }
                            if (puntuacion == 10) {
                                level.play();
                                nivel = 3;
                            }
                        } else {
                            vidas--;
                            dieSound.play();
                            a.addAction(sequence(repeat(3,sequence(moveTo(a.getX(),a.getY()+3),moveTo(a.getX(),a.getY()-3))),removeActor()));
                        }
                        break;
                    }
                } else {
                    if (a.getX()+a.getWidth() < player.getX() && ((WordEntity) a).getFixture().getUserData().toString().equals("palabra0")) {
                        vidas--;
                        dieSound.play();

                        a.addAction(sequence(repeat(3,sequence(moveTo(a.getX(),a.getY()+3),moveTo(a.getX(),a.getY()-3))),removeActor()));
                    }
                }
            }
        }

        if (vidas < 1) {
            backgroundMusic.stop();
            stage.addAction(
                    sequence(
                            Actions.delay(0f),
                            Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    game.setScreen(game.gameOverScreen);
                                }
                            })
                    )
            );
        }

        world.step(delta, 6, 2);

        //Si el usuario presiona el boton de atras irá al menú de pausa
        if ((Gdx.input.isKeyPressed(Input.Keys.BACK)) || (Gdx.input.isKeyPressed(Input.Keys.LEFT))) {
            backgroundMusic.stop();
            position = new Vector3(stage.getCamera().position);
            game.setScreen(game.gamePauseScreen);
        }
        // Render the screen. Remember, this is the last step!
        stage.draw();

        boolean all_out = true;
        for (WordEntity p : playingWords) {
            if (p.getX()+p.getWidth() > 0) {
                all_out = false;
                break;
            }
        }

        if (all_out) {
            SnapshotArray<Actor> actors = new SnapshotArray<>(stage.getActors());
            for (Actor actor : actors) {
                if (actor instanceof WordEntity) {
                    actor.remove();
                }
            }

            if (!controlWordsIterator.hasNext()) {
                controlWords = new ArrayList<>();
                words = new HashMap<>();
                randomWalk(Gdx.files.internal("files"));
                //shuffleAllWords();
                controlWordsIterator = controlWords.listIterator();
            }
            playingControlWord = controlWordsIterator.next();
            stage.addActor(playingControlWord.control);

            playingWords = words.get(playingControlWord.index);
            for (WordEntity p : playingWords)
                stage.addActor(p);
        }
    }

    @Override
    public void dispose() {
        preferences.putInteger("puntuacionMaxima", puntuacionMaxima);
        preferences.flush();
        stage.dispose();
        skin.dispose();
        world.dispose();
    }

    private void randomWalk(FileHandle dir) {
        EntityFactory factory = new EntityFactory(manager);
        FileHandle[] listFile = dir.list();
        ArrayList<Integer> ret = new ArrayList<>();
        for (int i=0; i<listFile.length; i++) {
            ret.add(i);
        }
        Collections.shuffle(ret);

        for (int i=0; i<ret.size(); i++) {
            if (listFile[ret.get(i)].isDirectory()) {
                randomWalk(listFile[ret.get(i)]);
            } else {
                String filename = String.valueOf(listFile[ret.get(i)].path());
                String filepath = filename.replace("\\", "/");
                String[] splitFilename = filename.split(";");
                if (splitFilename[2].equals("0") && !duplicado) { // this game x = 0 and y = 0 is in the bottom left corner. for x >= screenWidth, right. for y >= screenHeight, top.
                    controlWords.add(new ControlTuple(factory.createPalabra(world, 5f, 4.75f, filepath, "palabra"+(splitFilename[2])), splitFilename[1]));
                    duplicado = true;
                } else {
                    if (i > 0 && (splitFilename[1].equals(listFile[i-1].path().split(";")[1]))) {
                        ejeX = ejeX + 2f;
                    } else {
                        ejeX = ejeX+4f; // different cluster, put it in stage far
                    }
                    ArrayList<WordEntity> aux = words.get(splitFilename[1]);
                    if (aux == null) {
                        aux = new ArrayList<>();
                        words.put(splitFilename[1], aux);
                    }
                    aux.add(factory.createPalabra(world, ejeX, ejeY, filepath, "palabra"+(splitFilename[2])));
                    words.put(splitFilename[1], aux);
                }
            }
        }
        duplicado = false;
        ejeX = 11f;

    }
}
