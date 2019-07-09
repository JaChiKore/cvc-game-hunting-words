package edu.uab.cvc.huntingwords.presenters.utils;

import com.badlogic.gdx.assets.AssetManager;

public class ResourceManager {
    private static ResourceManager instance = null;
    private AssetManager assetManager;
    private AssetManager localManager;

    private ResourceManager() {}

    public static ResourceManager getInstance() {
        if (instance == null) {
            instance = new ResourceManager();
        }
        return instance;
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public AssetManager getLocalManager() {
        return localManager;
    }

    public void setAssetManager(AssetManager manager) {
        this.assetManager = manager;
    }

    public void setLocalManager(AssetManager manager) {
        this.localManager = manager;
    }
}
