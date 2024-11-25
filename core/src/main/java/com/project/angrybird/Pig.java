package com.project.angrybird;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Pig {
    private Texture pigTexture;
    private Body pigBody;
    private int health;
    private boolean destroyed;

    /**
     * Constructor for Pig
     *
     * @param world Box2D world instance.
     * @param position Initial position of the pig.
     * @param texturePath Path to the texture of the pig.
     * @param health Initial health of the pig.
     */
    public Pig(World world, Vector2 position, String texturePath, int health) {
        this.pigTexture = new Texture(texturePath);
        this.health = health;
        this.destroyed = false;

        // Define body and shape
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);
        this.pigBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1.0f, 1.0f); // Adjust size accordingly

        // Create fixture and attach user data for collision detection
        Fixture fixture = pigBody.createFixture(shape, 1.0f);
        fixture.setUserData(this);
        shape.dispose();
    }

    /**
     * Gets the texture of the pig.
     *
     * @return The texture of the pig.
     */
    public Texture getTexture() {
        return pigTexture;
    }

    /**
     * Gets the current position of the pig.
     *
     * @return The position of the pig.
     */
    public Vector2 getPosition() {
        return pigBody.getPosition();
    }

    /**
     * Handles the pig taking damage and checks if it should be destroyed.
     *
     * @param damage The damage dealt to the pig.
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            destroy();
        }
    }

    /**
     * Checks if the pig has been destroyed.
     *
     * @return True if the pig is destroyed, otherwise false.
     */
    public boolean isDestroyed() {
        return destroyed;
    }

    /**
     * Destroys the pig and marks it as destroyed.
     */
    private void destroy() {
        if (!destroyed) {
            destroyed = true;
            pigBody.setUserData(null); // Prevent further collision handling
            pigBody.setActive(false); // Deactivate the body to prevent further physics calculations
        }
    }

    /**
     * Disposes the resources used by the pig.
     */
    public void dispose() {
        pigTexture.dispose();
    }
}
