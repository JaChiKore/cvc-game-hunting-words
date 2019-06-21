package edu.uab.cvc.huntingwords.tasks.loaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import edu.uab.cvc.huntingwords.presenters.utils.PrimaryFont;
import edu.uab.cvc.huntingwords.presenters.utils.ResourceManager;

public class LoaderJumpGameInformation {

    private AssetManager manager;

    public void load() {

        manager = new AssetManager();
        manager.load("floor.png", Texture.class);
        manager.load("gameover.png", Texture.class);
        manager.load("puntuaciones.png", Texture.class);
        manager.load("overfloor.png", Texture.class);
        manager.load("pause.png", Texture.class);
        manager.load("pizarraNegra.png", Texture.class);
        manager.load("actors/personaje.png", Texture.class);
        manager.load("corazon.png", Texture.class);
        manager.load("audio/die.ogg", Sound.class);
        manager.load("audio/jump.ogg", Sound.class);
        manager.load("audio/song.ogg", Music.class);
        manager.load("audio/gameover.ogg", Music.class);
        manager.load("audio/play.ogg", Music.class);
        manager.load("audio/nivel.ogg", Music.class);

        PrimaryFont.getInstance();
        ResourceManager rm = ResourceManager.getInstance();

        walk(Gdx.files.internal("files"));

        rm.setManager(manager);
    }

    private void walk(FileHandle dir) {
        FileHandle[] listFile = dir.list();
        if (listFile != null) {
            for (FileHandle f: listFile) {
                if (f.isDirectory()) {
                    walk(f);
                } else {
                    String word = String.valueOf(f);
                    manager.load(word, Texture.class);
                }
            }
        }
    }
}
