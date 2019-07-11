package edu.uab.cvc.huntingwords.tasks.loaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.LocalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import edu.uab.cvc.huntingwords.presenters.utils.PrimaryFont;
import edu.uab.cvc.huntingwords.presenters.utils.ResourceManager;
import timber.log.Timber;

public class LoaderJumpGameAssets {

    public void load() {
        AssetManager assetManager = new AssetManager();
        AssetManager localManager = new AssetManager(new LocalFileHandleResolver());

        assetManager.load("floor.png", Texture.class);
        assetManager.load("gameover.png", Texture.class);
        assetManager.load("win.png", Texture.class);
        assetManager.load("puntuaciones.png", Texture.class);
        assetManager.load("overfloor.png", Texture.class);
        assetManager.load("pause.png", Texture.class);
        assetManager.load("pizarraNegra.png", Texture.class);
        assetManager.load("actors/personaje.png", Texture.class);
        assetManager.load("corazon.png", Texture.class);
        assetManager.load("audio/die.ogg", Sound.class);
        assetManager.load("audio/jump.ogg", Sound.class);
        assetManager.load("audio/song.ogg", Music.class);
        assetManager.load("audio/gameover.ogg", Music.class);
        assetManager.load("audio/play.ogg", Music.class);
        assetManager.load("audio/nivel.ogg", Music.class);
        PrimaryFont.getInstance();
        ResourceManager rm = ResourceManager.getInstance();

        loadImages(localManager);
        rm.setAssetManager(assetManager);
        rm.setLocalManager(localManager);
    }

    public void loadImages(AssetManager am) {
        File f = new File(Gdx.files.getLocalStoragePath(), "jumpGameInfo.txt");
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            if (br.readLine() != null) {
                String row;
                while ((row = br.readLine()) != null) {
                    String filename = row.split(";")[0];
                    am.load(filename, Texture.class);
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
