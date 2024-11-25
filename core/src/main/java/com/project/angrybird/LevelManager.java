package com.project.angrybird;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

public class LevelManager {
    private static LevelManager instance; // Singleton instance
    private World world;
    private List<Pig> pigs = new ArrayList<>();
    private List<Obstacle> obstacles = new ArrayList<>();
    private Queue<Bird> birds = new LinkedList<>();

    // Private constructor to prevent instantiation
    private LevelManager(World world) {
        this.world = world;
    }

    // Initialize method to create the singleton instance
    public static void initialize(World world) {
        if (instance == null) {
            instance = new LevelManager(world);
        }
    }

    // Get the instance of LevelManager
    public static LevelManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("LevelManager not initialized!");
        }
        return instance;
    }

    // Load level details
    public void loadLevel(int level) {
        pigs.clear();
        obstacles.clear();
        birds.clear();

        switch (level) {
            case 1:
                pigs.add(new Pig(world, new Vector2(300, 100), "pig_texture.png", 2));
                pigs.add(new Pig(world, new Vector2(400, 120), "pig_texture.png", 3));

                obstacles.add(new Obstacle(world, new Vector2(350, 100), 2));
                obstacles.add(new Obstacle(world, new Vector2(450, 120), 1));

                birds.add(new PlayingBird(world, new Vector2(100, 100)));
                birds.add(new PlayingBird(world, new Vector2(100, 100)));
                break;

            case 2:
                pigs.add(new Pig(world, new Vector2(350, 150), "pig_texture.png", 3));
                pigs.add(new Pig(world, new Vector2(450, 200), "pig_texture.png", 2));

                obstacles.add(new Obstacle(world, new Vector2(370, 150), 3));
                obstacles.add(new Obstacle(world, new Vector2(470, 180), 1));

                birds.add(new PlayingBird(world, new Vector2(100, 100)));
                birds.add(new PlayingBird(world, new Vector2(100, 100)));
                break;

            case 3:
                pigs.add(new Pig(world, new Vector2(400, 150), "pig_texture.png", 4));
                pigs.add(new Pig(world, new Vector2(500, 170), "pig_texture.png", 3));

                obstacles.add(new Obstacle(world, new Vector2(420, 150), 3));
                obstacles.add(new Obstacle(world, new Vector2(520, 180), 2));

                birds.add(new PlayingBird(world, new Vector2(100, 100)));
                birds.add(new PlayingBird(world, new Vector2(100, 100)));
                birds.add(new PlayingBird(world, new Vector2(100, 100)));
                break;

            default:
                System.out.println("Invalid level: " + level);
                break;
        }
    }

    // Getters for pigs, obstacles, and birds
    public List<Pig> getPigs() {
        return pigs;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public Queue<Bird> getBirds() {
        return birds;
    }

    // Method to get obstacles within a certain radius
    public List<Obstacle> getObstaclesInRadius(Vector2 position, float radius) {
        List<Obstacle> nearbyObstacles = new ArrayList<>();
        for (Obstacle obstacle : obstacles) {
            if (obstacle.getPosition().dst(position) <= radius) {
                nearbyObstacles.add(obstacle);
            }
        }
        return nearbyObstacles;
    }

    // Method to get pigs within a certain radius
    public List<Pig> getPigsInRadius(Vector2 position, float radius) {
        List<Pig> nearbyPigs = new ArrayList<>();
        for (Pig pig : pigs) {
            if (pig.getPosition().dst(position) <= radius) {
                nearbyPigs.add(pig);
            }
        }
        return nearbyPigs;
    }
}
