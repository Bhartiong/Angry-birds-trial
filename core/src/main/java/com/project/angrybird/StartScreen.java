package com.project.angrybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class StartScreen implements Screen {
    private final Main game;
    private SpriteBatch batch;
    private Texture backgroundTexture;
    private Texture playButtonTexture;
    private Rectangle playButtonBounds;
    private Vector3 touchPoint;
    private Sound buttonClickSound;
    private int currentLevel;

    public StartScreen(Main game) {
        this.game = game;

        initializeGraphics();
        initializeButtons();
        initializeSounds();
    }

    /**
     * Initializes the graphics such as textures.
     */
    private void initializeGraphics() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("image1.png")); // Replace with your background image path
        playButtonTexture = new Texture(Gdx.files.internal("play_button.png")); // Replace with your play button image path
        touchPoint = new Vector3();
    }

    /**
     * Initializes the play button and calculates its bounds.
     */
    private void initializeButtons() {
        // Set play button size and position (centered)
        float buttonWidth = 200;  // Adjust to your needs
        float buttonHeight = 100; // Adjust to your needs
        float buttonX = (Main.WORLD_WIDTH - buttonWidth) / 2;
        float buttonY = (Main.WORLD_HEIGHT - buttonHeight) / 2;

        playButtonBounds = new Rectangle(buttonX, buttonY, buttonWidth, buttonHeight);
    }

    /**
     * Initializes the sound effects for the start screen.
     */
    private void initializeSounds() {
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("button_click.mp3")); // Replace with your button click sound path
    }

    @Override
    public void render(float delta) {
        // Clear the screen with a specific color
        Gdx.gl.glClearColor(0.4f, 0.6f, 1.0f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update the camera
        game.getCamera().update();
        batch.setProjectionMatrix(game.getCamera().combined);

        // Handle input for starting the game
        handleInput();

        // Draw the background and the play button
        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        batch.draw(playButtonTexture, playButtonBounds.x, playButtonBounds.y, playButtonBounds.width, playButtonBounds.height);
        batch.end();
    }

    /**
     * Handles user input such as play button clicks.
     */
    private void handleInput() {
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.getViewport().unproject(touchPoint);

            if (playButtonBounds.contains(touchPoint.x, touchPoint.y)) {
                // Redirect to Level Selection screen instead of starting the game immediately
                game.setScreen(new GameScreen(game, currentLevel)); // Assuming GameScreen is the level selection screen
            }
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
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        playButtonTexture.dispose();
        buttonClickSound.dispose();
    }
}
