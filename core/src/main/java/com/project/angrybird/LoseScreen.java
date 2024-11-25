package com.project.angrybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class LoseScreen implements Screen {
    private final Main game;
    private Texture loseTexture;
    private Texture retryButtonTexture;
    private Texture mainMenuButtonTexture;
    private Rectangle retryButtonBounds;
    private Rectangle mainMenuButtonBounds;
    private Vector3 touchPoint;

    public LoseScreen(Main game) {
        this.game = game;
        initializeGraphics();
        initializeButtons();
    }

    /**
     * Initializes all graphics for the lose screen.
     */
    private void initializeGraphics() {
        loseTexture = new Texture("lose_screen.png");
        retryButtonTexture = new Texture("retry_button.png");
        mainMenuButtonTexture = new Texture("button.png");

        touchPoint = new Vector3();
    }

    /**
     * Initializes button bounds and positions.
     */
    private void initializeButtons() {
        // Set up Retry Button
        float buttonWidth = 150;
        float buttonHeight = 60;
        float retryButtonX = (Main.WORLD_WIDTH / 2f) - buttonWidth - 20;
        float buttonY = Main.WORLD_HEIGHT - buttonHeight - 20;
        retryButtonBounds = new Rectangle(retryButtonX, buttonY, buttonWidth, buttonHeight);

        // Set up Main Menu Button
        float mainMenuButtonX = (Main.WORLD_WIDTH / 2f) + 20;
        mainMenuButtonBounds = new Rectangle(mainMenuButtonX, buttonY, buttonWidth, buttonHeight);
    }

    /**
     * Updates the screen state based on user input.
     *
     * @param delta Time elapsed since the last frame.
     */
    private void update(float delta) {
        if (Gdx.input.justTouched()) {
            touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            game.getCamera().unproject(touchPoint);

            if (retryButtonBounds.contains(touchPoint.x, touchPoint.y)) {
                // Load the current level from saved game progress and retry
                int currentLevel = 1; // Default to level 1
                game.setScreen(new PlayingGameScreen(game, currentLevel, GameState.AIMING));
                return;
            }

            if (mainMenuButtonBounds.contains(touchPoint.x, touchPoint.y)) {
                game.setScreen(new StartScreen(game));
                return;
            }
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        // Clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render the textures
        game.batch.begin();
        game.batch.draw(loseTexture, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        game.batch.draw(retryButtonTexture, retryButtonBounds.x, retryButtonBounds.y, retryButtonBounds.width, retryButtonBounds.height);
        game.batch.draw(mainMenuButtonTexture, mainMenuButtonBounds.x, mainMenuButtonBounds.y, mainMenuButtonBounds.width, mainMenuButtonBounds.height);
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {}

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
        loseTexture.dispose();
        retryButtonTexture.dispose();
        mainMenuButtonTexture.dispose();
    }
}
