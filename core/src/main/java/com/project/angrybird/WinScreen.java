package com.project.angrybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class WinScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Texture winTexture;
    private BitmapFont font;
    private TextButton playAgainButton, mainMenuButton;
    private Sound buttonClickSound;

    public WinScreen(Main game) {
        this.game = game;

        // Load win screen texture and sound effects
        winTexture = new Texture("win_screen.png");
        buttonClickSound = Gdx.audio.newSound(Gdx.files.internal("button_click.mp3")); // Add button click sound path

        // Initialize stage and input processing
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create a default font
        font = new BitmapFont(); // Uses default LibGDX font

        // Create button style
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = new TextureRegionDrawable(new Texture("button.png"));
        buttonStyle.down = new TextureRegionDrawable(new Texture("button_pressed.png"));

        // Create buttons
        playAgainButton = new TextButton("Play Again", buttonStyle);
        mainMenuButton = new TextButton("Main Menu", buttonStyle);

        // Position buttons
        playAgainButton.setSize(200, 60);
        playAgainButton.setPosition((Main.WORLD_WIDTH - playAgainButton.getWidth()) / 2, Main.WORLD_HEIGHT / 2 + 20);

        mainMenuButton.setSize(200, 60);
        mainMenuButton.setPosition((Main.WORLD_WIDTH - mainMenuButton.getWidth()) / 2, Main.WORLD_HEIGHT / 2 - 80);

        // Add button listeners
        playAgainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(); // Play sound
                game.setScreen(new PlayingGameScreen(game, 1, GameState.AIMING)); // Start at level 1, can be made dynamic
            }
        });

        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonClickSound.play(); // Play sound
                game.setScreen(new StartScreen(game));
            }
        });

        // Add buttons to stage
        stage.addActor(playAgainButton);
        stage.addActor(mainMenuButton);
    }

    @Override
    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Draw win screen
        stage.getBatch().begin();
        stage.getBatch().draw(winTexture, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        stage.getBatch().end();

        // Draw buttons
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        winTexture.dispose();
        stage.dispose();
        font.dispose();
        buttonClickSound.dispose();
    }
}
