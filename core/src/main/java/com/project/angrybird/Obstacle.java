package com.project.angrybird;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class Obstacle {
    private Body obstacleBody;
    private Texture obstacleTexture;
    private int hitPoints;

    public Obstacle(World world, Vector2 position, int hitPoints) {
        this.hitPoints = hitPoints;
        this.obstacleTexture = new Texture("obstacle.png");

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position);
        obstacleBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1.0f, 1.0f);
        Fixture fixture = obstacleBody.createFixture(shape, 1.0f);
        fixture.setUserData(this);
        shape.dispose();
    }

    public void takeDamage(int damage) {
        hitPoints -= damage;
        if (hitPoints <= 0) {
            obstacleBody.getWorld().destroyBody(obstacleBody);
        }
    }

    public boolean isDestroyed() {
        return hitPoints <= 0;
    }

    public Vector2 getPosition() {
        return obstacleBody.getPosition();
    }

    public Texture getTexture() {
        return obstacleTexture;
    }

    public void dispose() {
        obstacleTexture.dispose();
    }
}
