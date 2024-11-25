package com.project.angrybird;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import java.io.Serializable;

public abstract class Bird implements Serializable {
    protected transient Texture birdTexture;
    protected transient Body birdBody;
    protected int impactDamage;

    public Bird(World world, Vector2 position, String texturePath, int damage) {
        birdTexture = new Texture(texturePath);
        this.impactDamage = damage;

        // Create the body in the Box2D world
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);
        birdBody = world.createBody(bodyDef);

        // Create a circular shape for the bird
        CircleShape shape = new CircleShape();
        shape.setRadius(0.5f); // Adjust radius to fit your bird size

        // Create fixture definition for the bird
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f; // Adjust mass
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.0f; // Remove bounciness

        Fixture birdFixture = birdBody.createFixture(fixtureDef);
        birdFixture.setUserData(this); // Attach the Bird instance as user data for collision handling

        // Initially, set bird to inactive so it doesn't fall
        birdBody.setActive(false);

        shape.dispose(); // Clean up the shape
    }

    public Texture getTexture() {
        return birdTexture;
    }

    public Vector2 getPosition() {
        return birdBody.getPosition();
    }

    public void applyLaunchForce(Vector2 force) {
        birdBody.setActive(true); // Activate the bird before applying force
        birdBody.applyLinearImpulse(force, birdBody.getWorldCenter(), true);
    }

    public int getImpactDamage() {
        return impactDamage;
    }

    public void setPosition(Vector2 position) {
        birdBody.setTransform(position, birdBody.getAngle());
    }

    // New method to get the bird's body
    public Body getBody() {
        return birdBody;
    }

    public abstract void onImpact(); // Define bird-specific behaviors on collision

    public void dispose() {
        birdTexture.dispose();
    }

    // Method to determine if bird is still in flight
    public boolean isFlying() {
        return !birdBody.getLinearVelocity().isZero();
    }

    // Method to prepare the bird for launch by making it dynamic
    public void prepareForLaunch() {
        birdBody.setActive(true); // Activate physics on the bird before launch
    }

    // Example launch method that handles different bird types (could be overridden)
    public void launch(Vector2 launchForce) {
        prepareForLaunch();  // Set bird to dynamic before launch
        applyLaunchForce(launchForce);
    }

    // Method to handle collisions - this could be extended in child classes
    public void handleCollision(Object collidedObject) {
        onImpact(); // Call the abstract method to define bird-specific impact behavior

        // Logic based on what the bird has collided with (blocks, pigs, other objects)
        if (collidedObject instanceof Obstacle) {
            Obstacle block = (Obstacle) collidedObject;
            block.takeDamage(impactDamage);
        } else if (collidedObject instanceof Pig) {
            Pig pig = (Pig) collidedObject;
            pig.takeDamage(impactDamage);
        }
    }
}
