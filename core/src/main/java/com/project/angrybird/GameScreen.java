package com.project.angrybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import java.io.*;

public class GameScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Texture backButtonTexture;
    private Texture[] levelTextures;
    private Rectangle backButtonBounds;
    private Rectangle[] levelBounds;
    private Vector3 touchPoint;
    private int currentLevel = 0;

    public GameScreen(Main game, int level) {
        this.game = game;
        this.currentLevel = level; // You may need to declare currentLevel if it doesn't exist
        initializeGraphics();
        initializeButtons();
        loadGameProgress(); // Load progress if any
    }

    /**
     * Initializes all the graphic components such as textures.
     */
    private void initializeGraphics() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("image1.png"));
        backButtonTexture = new Texture(Gdx.files.internal("back_button.png"));

        levelTextures = new Texture[3];
        for (int i = 0; i < levelTextures.length; i++) {
            levelTextures[i] = new Texture(Gdx.files.internal("Level" + (i + 1) + ".png"));
        }

        touchPoint = new Vector3();
    }

    /**
     * Initializes buttons and calculates their bounds for touch detection.
     */
    private void initializeButtons() {
        // Initialize Back Button
        float buttonWidth = 100;
        float buttonHeight = 50;
        float buttonX = 20;
        float buttonY = Main.WORLD_HEIGHT - buttonHeight - 20;
        backButtonBounds = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);

        // Initialize Level Selection Buttons
        levelBounds = new Rectangle[3];
        for (int i = 0; i < levelTextures.length; i++) {
            float levelButtonWidth = 150;
            float levelButtonHeight = 150;
            float levelButtonX = 150 + (i * (levelButtonWidth + 50));
            float levelButtonY = Main.WORLD_HEIGHT / 2 - levelButtonHeight / 2;
            levelBounds[i] = new Rectangle(levelButtonX, levelButtonY, levelButtonWidth, levelButtonHeight);
        }
    }

    /**
     * Updates game state based on user input.
     *
     * @param delta time elapsed since the last frame.
     */
    private void update(float delta) {
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.getCamera().unproject(touchPoint);

            if (backButtonBounds.contains(touchPoint.x, touchPoint.y)) {
                saveGameProgress(); // Save game before going back
                game.setScreen(new StartScreen(game));
                return;
            }

            for (int i = 0; i < levelBounds.length; i++) {
                if (levelBounds[i].contains(touchPoint.x, touchPoint.y)) {
                    currentLevel = i + 1;
                    game.setScreen(new PlayingGameScreen(game, currentLevel, GameState.AIMING));
                    return;
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0.4f, 0.6f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.getCamera().update();
        batch.setProjectionMatrix(game.getCamera().combined);

        batch.begin();
        drawComponents();
        batch.end();
    }

    /**
     * Draws all the components of the screen.
     */
    private void drawComponents() {
        // Draw the background
        batch.draw(backgroundTexture, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        // Draw the back button
        batch.draw(backButtonTexture, backButtonBounds.x, backButtonBounds.y, backButtonBounds.width, backButtonBounds.height);

        // Draw level buttons if no level is currently active
        if (currentLevel == 0) {
            for (int i = 0; i < levelBounds.length; i++) {
                batch.draw(levelTextures[i], levelBounds[i].x, levelBounds[i].y, levelBounds[i].width, levelBounds[i].height);
            }
        }
    }

    /**
     * Saves the current game progress to a file.
     */
    private void saveGameProgress() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("gameProgress.sav"))) {
            out.writeInt(currentLevel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the game progress from a file if it exists.
     */
    private void loadGameProgress() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream("gameProgress.sav"))) {
            currentLevel = in.readInt();
        } catch (IOException e) {
            System.out.println("No saved game found. Starting fresh.");
        }
    }

    @Override
    public void resize(int width, int height) {
        game.getViewport().update(width, height, true);
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {
        saveGameProgress();
    }

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        backButtonTexture.dispose();
        for (Texture levelTexture : levelTextures) {
            levelTexture.dispose();
        }
    }
}
