package edu.uab.cvc.huntingwords.Entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

import edu.uab.cvc.huntingwords.presenters.utils.Constants;

public class WordEntity extends Actor {

    private Texture texture;

    private String id;

    private Fixture fixture;

    WordEntity(World world, Texture texture, float x, float y, String idWord, String userId) {
        this.texture = texture;
        id = idWord;

        BodyDef def = new BodyDef();
        def.position.set(x, y + 0.5f);
        def.type = BodyDef.BodyType.DynamicBody;
        Body body = world.createBody(def);

        PolygonShape box = new PolygonShape();
        box.setAsBox(0.5f, 0.5f);
        fixture = body.createFixture(box, 3);
        fixture.setSensor(true);
        fixture.setUserData(userId);
        box.dispose();

        setPosition((x - 0.5f) * Constants.PIXELS_IN_METER, y * Constants.PIXELS_IN_METER);
        setSize(Constants.PIXELS_IN_METER, Constants.PIXELS_IN_METER);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());
    }

    public Fixture getFixture() {
        return fixture;
    }

    public String getId() {
        return id;
    }
}
