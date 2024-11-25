package com.project.angrybird;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Main extends Game {
    public static final float WORLD_WIDTH = 800;   // Width of the game world
    public static final float WORLD_HEIGHT = 480;  // Height of the game world

    private OrthographicCamera camera;  // Camera for viewing the game world
    private Viewport viewport;  // Viewport for controlling screen scaling
    private World world;  // Physics world for Box2D
    public SpriteBatch batch;  // SpriteBatch to handle rendering

    private static Main instance;  // Singleton instance for easy access to the Main game class

    @Override
    public void create() {
        // Singleton instance setup
        instance = this;

        // Set up the camera and viewport
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();

        // Create the game physics world with gravity
        world = new World(new Vector2(0, -9.81f), true);  // Gravity pointing down

        // Initialize the SpriteBatch for rendering
        batch = new SpriteBatch();

        // Initialize the LevelManager with the game world
        LevelManager.initialize(world);

        // Set the initial screen to the StartScreen
        setScreen(new StartScreen(this));
    }

    @Override
    public void render() {
        // Update the camera before rendering
        camera.update();
        // Delegate rendering to the current active screen
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport when the window is resized
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        // Dispose resources when the game is closed
        if (getScreen() != null) {
            getScreen().dispose();
        }
        if (world != null) {
            world.dispose();
        }
        if (batch != null) {
            batch.dispose();
        }
    }

    // Getter for accessing the camera
    public OrthographicCamera getCamera() {
        return camera;
    }

    // Getter for accessing the viewport
    public Viewport getViewport() {
        return viewport;
    }

    // Getter for accessing the Box2D physics world
    public World getWorld() {
        return world;
    }

    // Getter for accessing the singleton instance of Main
    public static Main getInstance() {
        return instance;
    }

    /**
     * Loads the game progress from a file if it exists and sets the initial screen accordingly.
     */
    public void loadGameProgress() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("gameProgress.sav"))) {
            int savedLevel = in.readInt();
            setScreen(new PlayingGameScreen(this, savedLevel, GameState.AIMING));
        } catch (IOException e) {
            System.out.println("No saved game found. Starting fresh.");
            setScreen(new StartScreen(this));
        }
    }

    /**
     * Saves the current game progress.
     *
     * @param currentLevel The current level to be saved.
     */
    public void saveGameProgress(int currentLevel) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("gameProgress.sav"))) {
            out.writeInt(currentLevel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles global pause functionality.
     */
    public void pauseGame() {
        if (getScreen() instanceof PlayingGameScreen) {
            ((PlayingGameScreen) getScreen()).pause();
        }
    }

    /**
     * Handles global resume functionality.
     */
    public void resumeGame() {
        if (getScreen() instanceof PlayingGameScreen) {
            ((PlayingGameScreen) getScreen()).resume();
        }
    }
}
