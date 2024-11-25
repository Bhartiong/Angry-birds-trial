package com.project.angrybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PauseScreen implements Screen {
    private final Main game;
    private Stage stage;
    private Texture buttonTexture;
    private Texture buttonPressedTexture;
    private BitmapFont font;
    private Texture backgroundTexture;
    private int currentLevel;

    public PauseScreen(final Main game, int currentLevel) {
        this.game = game;
        this.currentLevel = currentLevel;

        stage = new Stage(new ScreenViewport());
        buttonTexture = new Texture(Gdx.files.internal("button.png"));
        buttonPressedTexture = new Texture(Gdx.files.internal("button.png"));
        backgroundTexture = new Texture(Gdx.files.internal("image1.png"));
        font = new BitmapFont();

        initializeUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void initializeUI() {
        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        buttonStyle.down = new TextureRegionDrawable(new TextureRegion(buttonPressedTexture));
        buttonStyle.font = font;

        TextButton resumeButton = createButton("Resume", buttonStyle, () -> game.setScreen(new PlayingGameScreen(game, currentLevel, GameState.PLAYING)));
        TextButton homeButton = createButton("Home", buttonStyle, () -> {
            game.setScreen(new StartScreen(game));
            game.saveGameProgress(currentLevel);
        });
        TextButton restartButton = createButton("Restart", buttonStyle, () -> {
            game.setScreen(new PlayingGameScreen(game, currentLevel, GameState.AIMING));
            game.saveGameProgress(currentLevel);
        });

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        table.add(resumeButton).size(200, 60).pad(15);
        table.row();
        table.add(homeButton).size(200, 60).pad(15);
        table.row();
        table.add(restartButton).size(200, 60).pad(15);

        stage.addActor(table);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        Label pauseLabel = new Label("Game Paused", labelStyle);
        pauseLabel.setFontScale(2);
        pauseLabel.setPosition(Gdx.graphics.getWidth() / 2f - pauseLabel.getWidth() / 2, Gdx.graphics.getHeight() - 100);
        stage.addActor(pauseLabel);
    }

    private TextButton createButton(String text, TextButtonStyle style, Runnable action) {
        TextButton button = new TextButton(text, style);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0.8f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

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
        if (buttonTexture != null) buttonTexture.dispose();
        if (buttonPressedTexture != null) buttonPressedTexture.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (font != null) font.dispose();
        if (stage != null) stage.dispose();
    }
}
