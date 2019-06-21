package edu.uab.cvc.huntingwords.Entities;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;


public class EntityFactory {

    private AssetManager manager;


    public EntityFactory(AssetManager manager) {
        this.manager = manager;
    }


    public PlayerEntity createPlayer(World world, Vector2 position) {
        Texture playerTexture = manager.get("actors/personaje.png");
        return new PlayerEntity(world, playerTexture, position);
    }


    public FloorEntity createFloor(World world, float x, float width, float y) {
        Texture floorTexture = manager.get("floor.png");
        Texture overfloorTexture = manager.get("overfloor.png");
        return new FloorEntity(world, floorTexture, overfloorTexture, x, width, y);
    }


    public WordEntity createPalabra(World world, float x, float y, String idPalabra, String userId){

        Texture palabraTexture = manager.get(idPalabra);

        return new WordEntity(world, palabraTexture, x, y, idPalabra, userId);
    }

}
