package edu.uab.cvc.huntingwords.Entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;


public class EntityFactory {

    private AssetManager assetManager;
    private AssetManager localManager;

    public EntityFactory(AssetManager assetManager, AssetManager localManager) {
        this.assetManager = assetManager;
        this.localManager = localManager;
    }


    public PlayerEntity createPlayer(World world, Vector2 position) {
        Texture playerTexture = assetManager.get("actors/personaje.png");
        return new PlayerEntity(world, playerTexture, position);
    }


    public FloorEntity createFloor(World world, float x, float width, float y) {
        Texture floorTexture = assetManager.get("floor.png");
        Texture overfloorTexture = assetManager.get("overfloor.png");
        return new FloorEntity(world, floorTexture, overfloorTexture, x, width, y);
    }


    public WordEntity createPalabra(World world, float x, float y, String idWord, String userId){

        Texture palabraTexture = localManager.get(idWord);

        return new WordEntity(world, palabraTexture, x, y, idWord, userId);
    }

}
