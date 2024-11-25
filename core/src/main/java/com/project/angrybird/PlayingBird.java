package com.project.angrybird;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class PlayingBird extends Bird {

    public PlayingBird(World world, Vector2 position) {
        // Call the parent constructor with specific values for the PlayingBird
        super(world, position, "bird.png", 1); // "bird.png" is the texture path, 1 is the impact damage
        initializePhysics(world, position);
    }

    /**
     * Initializes the physics for the PlayingBird.
     *
     * @param world    Box2D world where the bird exists.
     * @param position The initial position of the bird.
     */
    private void initializePhysics(World world, Vector2 position) {
        // Set body definition type to dynamic (movable)
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);
        birdBody = world.createBody(bodyDef);

        // Set a circular shape for the bird body
        CircleShape shape = new CircleShape();
        shape.setRadius(0.5f); // Set radius to approximate the bird's size

        // Create fixture definition for the bird
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f; // Bounciness of the bird

        // Attach the fixture to the bird body and set user data
        Fixture birdFixture = birdBody.createFixture(fixtureDef);
        birdFixture.setUserData(this);

        shape.dispose(); // Clean up the shape to prevent memory leaks
    }

    @Override
    public void onImpact() {
        // Implement behavior for PlayingBird when it hits something
        System.out.println("Playing Bird hit an obstacle!");

        // Add specific behavior for the bird, such as triggering an animation, reducing speed, or playing a sound.
        birdBody.setLinearVelocity(birdBody.getLinearVelocity().scl(0.5f)); // Reduce velocity after impact

        // You could also add logic for changing the bird's appearance, such as changing the texture or playing an animation.
    }

    /**
     * Applies a launch force to the bird when fired from the slingshot.
     *
     * @param force Vector representing the direction and magnitude of the launch force.
     */
    @Override
    public void applyLaunchForce(Vector2 force) {
        birdBody.applyLinearImpulse(force, birdBody.getWorldCenter(), true);
        System.out.println("Playing Bird launched with force: " + force);
    }

    /**
     * Updates the bird's state. Can be used for animations or other behaviors during flight.
     */
    public void update() {
        // Add any update behavior here, like checking if the bird has stopped moving
        if (!isFlying()) {
            System.out.println("Playing Bird has stopped moving.");
        }
    }

    @Override
    public void dispose() {
        super.dispose(); // Call the dispose method from the parent class to ensure texture is disposed
    }

    /**
     * Determines if the bird is still in flight.
     *
     * @return true if the bird is flying, false otherwise.
     */
    public boolean isFlying() {
        return birdBody.getLinearVelocity().len() > 0.1f;
    }

    // Add more specific behavior for PlayingBird if needed, like special abilities or animations
}
