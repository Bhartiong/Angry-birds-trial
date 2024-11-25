package com.project.angrybird;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.math.Vector2;

public class GameContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Object userDataA = contact.getFixtureA().getUserData();
        Object userDataB = contact.getFixtureB().getUserData();

        if (userDataA != null && userDataB != null) {
            handleCollision(userDataA, userDataB);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Object userDataA = contact.getFixtureA().getUserData();
        Object userDataB = contact.getFixtureB().getUserData();

        if (userDataA != null && userDataB != null) {
            System.out.println("Contact ended between " + userDataA + " and " + userDataB);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}

    private void handleCollision(Object userDataA, Object userDataB) {
        if (userDataA instanceof Bird && userDataB instanceof Obstacle) {
            Bird bird = (Bird) userDataA;
            Obstacle obstacle = (Obstacle) userDataB;
            handleBirdObstacleCollision(bird, obstacle);
        } else if (userDataB instanceof Bird && userDataA instanceof Obstacle) {
            Bird bird = (Bird) userDataB;
            Obstacle obstacle = (Obstacle) userDataA;
            handleBirdObstacleCollision(bird, obstacle);
        } else if (userDataA instanceof Bird && userDataB instanceof Pig) {
            Bird bird = (Bird) userDataA;
            Pig pig = (Pig) userDataB;
            handleBirdPigCollision(bird, pig);
        } else if (userDataB instanceof Bird && userDataA instanceof Pig) {
            Bird bird = (Bird) userDataB;
            Pig pig = (Pig) userDataA;
            handleBirdPigCollision(bird, pig);
        }
    }

    private void handleBirdObstacleCollision(Bird bird, Obstacle obstacle) {
        bird.onImpact(); // Trigger bird-specific collision behavior
        obstacle.takeDamage(bird.getImpactDamage());


    }

    private void handleBirdPigCollision(Bird bird, Pig pig) {
        bird.onImpact(); // Trigger bird-specific collision behavior
        pig.takeDamage(bird.getImpactDamage());


    }




}
