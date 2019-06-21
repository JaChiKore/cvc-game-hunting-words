package edu.uab.cvc.huntingwords.presenters.utils;

import com.badlogic.gdx.assets.AssetManager;

public class ResourceManager {
    private static ResourceManager instance = null;
    private AssetManager manager;

    private ResourceManager() {}

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    public AssetManager getManager() {
        return manager;
    }

    public void setManager(AssetManager manager) {
        this.manager = manager;
    }
}
