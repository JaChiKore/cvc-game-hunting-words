package edu.uab.cvc.huntingwords.screens.JumpScreen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import edu.uab.cvc.huntingwords.Entities.EntityFactory;
import edu.uab.cvc.huntingwords.Entities.FloorEntity;
import edu.uab.cvc.huntingwords.Entities.WordEntity;
import edu.uab.cvc.huntingwords.Entities.PlayerEntity;
import edu.uab.cvc.huntingwords.models.JumpClusterResult;
import edu.uab.cvc.huntingwords.presenters.utils.Constants;
import edu.uab.cvc.huntingwords.presenters.utils.LanguageManager;
import edu.uab.cvc.huntingwords.presenters.utils.PrimaryFont;
import edu.uab.cvc.huntingwords.presenters.utils.ResourceManager;
import edu.uab.cvc.huntingwords.screens.fragments.JumpGame;
import edu.uab.cvc.huntingwords.tasks.services.JumpService;
import timber.log.Timber;

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
    private ArrayList<JumpClusterResult> results;

    private ArrayList<String> addedResults;

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

    private BitmapFont playerName;

    private int puntuacion;

    private int nivel;

    private int screenWidth, screenHeight;

    private SpriteBatch batch;

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

    private AssetManager assetManager;
    private AssetManager localManager;
    private JumpGame game;
    private LanguageManager languageManager;

    private Date startedDate;
    private String startDate;
    private SimpleDateFormat sdf;
    public GameScreen(final JumpGame game) {
        results = new ArrayList<>();
        addedResults = new ArrayList<>();
        sdf = new SimpleDateFormat("yyyyMMdd HHmmss");
        startedDate = Calendar.getInstance().getTime();
        startDate = sdf.format(startedDate);
        Gdx.input.setCatchBackKey(true);
        this.game = game;
        assetManager = ResourceManager.getInstance().getAssetManager();
        localManager = ResourceManager.getInstance().getLocalManager();
        languageManager = LanguageManager.getInstance();

        screenWidth = Gdx.app.getGraphics().getWidth();
        screenHeight = Gdx.app.getGraphics().getHeight();

        System.out.println("app system width,height:"+screenWidth+", "+screenHeight);
                // Create a new Scene2D stage for displaying things.
        stage = new Stage(new FitViewport(640, 360));
        position = new Vector3(stage.getCamera().position);

        System.out.println("initial camera position:" + position);

        skin = PrimaryFont.getInstance().getSkin(20);

        puntos = skin.getFont("default-font");

        niveles = skin.getFont("default-font");

        vida = skin.getFont("default-font");

        playerName = skin.getFont("default-font");

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
        jumpSound = assetManager.get("audio/jump.ogg");
        dieSound = assetManager.get("audio/die.ogg");
        backgroundMusic = assetManager.get("audio/play.ogg");
        backgroundMusic.setVolume(0.3f);
        level = assetManager.get("audio/nivel.ogg");

        batch = new SpriteBatch();
        EntityFactory factory = new EntityFactory(assetManager,localManager);
        player = factory.createPlayer(world,  new Vector2(2.5f, 1.5f));
        floor = factory.createFloor(world, 0, 11, 1);


        controlWords = new ArrayList<>();
        words = new HashMap<>();
        getImages();
        controlWordsIterator = controlWords.listIterator();
        playingControlWord = controlWordsIterator.next();
        stage.addActor(playingControlWord.control);

        stage.addActor(floor);

        playingWords = words.get(playingControlWord.index);

        for (WordEntity p : playingWords) {
            stage.addActor(p);
        }

        stage.addActor(player);

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

        playerName.getData().setScale(scale+0.5f);
        playerName.draw(batch, languageManager.getString("Jugador") + game.username, screenWidth/45f,screenHeight*24/25f);
        puntos.getData().setScale(scale);
        puntos.draw(batch, languageManager.getString("Puntuacion") + puntuacion, screenWidth/45f,screenHeight*22/25f);
        vida.getData().setScale(scale);
        vida.draw(batch, languageManager.getString("Vidas") + vidas, screenWidth/45f, screenHeight*21/25f);
        niveles.getData().setScale(scale);
        niveles.draw(batch, languageManager.getString("Nivel") + nivel, screenWidth/45f, screenHeight*20/25f);
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
                        if (!((WordEntity) a).getFixture().getUserData().toString().contains("golden")) {
                            puntuacion++;
                            a.addAction(sequence(moveTo(a.getX(),a.getY()+30,0.5f),removeActor()));
                            a.setColor(Color.GREEN);
                            if (!addedResults.contains(((WordEntity) a).getId())) {
                                results.add(JumpClusterResult.newImageEqual(playingControlWord.index,((WordEntity) a).getId()));
                                addedResults.add(((WordEntity) a).getId());
                            }
                        } else {
                            if (((WordEntity) a).getFixture().getUserData().toString().contains("0")) {
                                puntuacion++;
                                a.addAction(sequence(moveTo(a.getX(),a.getY()+30,0.5f),removeActor()));
                                a.setColor(Color.GREEN);
                            } else {
                                vidas--;
                                dieSound.play();
                                a.addAction(sequence(repeat(3,sequence(moveTo(a.getX(),a.getY()+3),moveTo(a.getX(),a.getY()-3))),removeActor()));
                                a.setColor(Color.RED);
                            }
                        }

                        if (puntuacion == 5) {
                            level.play();
                            nivel = 2;
                        }
                        if (puntuacion == 10) {
                            level.play();
                            nivel = 3;
                        }
                        break;
                    }
                } else {
                    if (a.getX() + a.getWidth() < player.getX() - 10) {
                        if (!((WordEntity) a).getFixture().getUserData().toString().contains("golden")) {
                            if (!addedResults.contains(((WordEntity) a).getId())) {
                                results.add(JumpClusterResult.newImageDifferent(playingControlWord.index, ((WordEntity) a).getId()));
                                addedResults.add(((WordEntity) a).getId());
                            }
                        } else {
                            if (((WordEntity) a).getFixture().getUserData().toString().contains("0")) {
                                vidas--;
                                dieSound.play();

                                a.addAction(sequence(repeat(3, sequence(moveTo(a.getX(), a.getY() + 3), moveTo(a.getX(), a.getY() - 3))), removeActor()));
                                a.setColor(Color.RED);
                            }
                        }
                    }
                }
            }
        }

        if (vidas < 1) {
            Date stoppedDate = Calendar.getInstance().getTime();
            long diffInMs = stoppedDate.getTime() - startedDate.getTime();
            long diffInSec = TimeUnit.MILLISECONDS.toSeconds(diffInMs);
            new Thread (() -> new JumpService(game.username,true).run(results,String.valueOf(nivel),startDate,sdf.format(stoppedDate),diffInSec,0f,puntuacion)).start();
            backgroundMusic.stop();
            stage.addAction(
                    sequence(
                            Actions.delay(0f),
                            Actions.run(new Runnable() {
                                @Override
                                public void run() {
                                    game.setScreen(new GameOverScreen(game,puntuacion));
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
        for (Actor a : stage.getActors()) {
            if (a instanceof WordEntity && !((WordEntity) a).getFixture().getUserData().toString().equals("controlWord")) {
                if (a.getX()+a.getWidth() > 0) {
                    all_out = false;
                    break;
                }
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
                getImages();
                //shuffleAllWords();
                controlWordsIterator = controlWords.listIterator();
            }
            playingControlWord = controlWordsIterator.next();
            stage.addActor(playingControlWord.control);

            playingWords = words.get(playingControlWord.index);
            for (WordEntity p : playingWords) {
                stage.addActor(p);
            }
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        world.dispose();
    }

    private void getImages() {
        EntityFactory factory = new EntityFactory(assetManager,localManager);
        File f = new File(Gdx.files.getLocalStoragePath(), "jumpGameInfo.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            if (br.readLine() != null) {
                String row;
                int i = 0;
                String last_read_id = "-1";
                while ((row = br.readLine()) != null) {
                    String[] split = row.split(";");
                    String filename = split[0];
                    String id_jump_cluster = split[1];
                    boolean reference_image = split[2].equals("1");
                    boolean golden = split[4].equals("1");
                    String equal = split[3];

                    if (last_read_id.equals("-1")) {
                        last_read_id = id_jump_cluster;
                    } else {
                        if (!last_read_id.equals(id_jump_cluster)) {
                            last_read_id = id_jump_cluster;
                            i = 0;
                            ejeX = 11f;
                        } else {
                            i += 1;
                        }
                    }

                    if (reference_image) { // this game x = 0 and y = 0 is in the bottom left corner. for x >= screenWidth, right. for y >= screenHeight, top.
                        controlWords.add(new ControlTuple(factory.createPalabra(world, 5f, 4.75f, filename, "controlWord"), id_jump_cluster));
                    } else {
                        if (i > 0) {
                            ejeX = ejeX+2f;
                        } else {
                            ejeX = ejeX+4f; // different cluster, put it in stage far
                        }
                        ArrayList<WordEntity> aux = words.get(id_jump_cluster);
                        if (aux == null) {
                            aux = new ArrayList<>();
                            words.put(id_jump_cluster, aux);
                        }
                        aux.add(factory.createPalabra(world, ejeX, ejeY, filename, (golden ? "golden" : "")+equal));
                        words.put(id_jump_cluster, aux);
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    /*

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
                    controlWords.add(new ControlTuple(factory.createPalabra(world, 5f, 4.75f, filepath, "controlWord"), splitFilename[1]));
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
     */
}
