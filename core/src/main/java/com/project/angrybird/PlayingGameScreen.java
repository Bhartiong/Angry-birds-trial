package com.project.angrybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PlayingGameScreen implements Screen {

    private final Main game;
    private SpriteBatch batch;
    private World world;
    private OrthographicCamera camera;
    private Box2DDebugRenderer box2DDebugRenderer;
    private ShapeRenderer shapeRenderer;

    private Texture backgroundTexture, slingshotTexture;
    private Stage stage;
    private ImageButton pauseButton;

    private Queue<Bird> birdQueue = new LinkedList<>();
    private Bird currentBird;
    private Vector2 slingshotPosition = new Vector2(150, 120); // Original slingshot height
    private Vector2 birdStartPosition = new Vector2(slingshotPosition.x, slingshotPosition.y + 60);  // Increased bird start position to be above the sling
    private boolean isDragging = false;
    private Vector2 dragStart = new Vector2();

    private int currentLevel;
    private List<Pig> pigs = new ArrayList<>();
    private GameState gameState;

    public PlayingGameScreen(Main game, int level, GameState gameState) {
        this.game = game;
        this.currentLevel = level;
        this.gameState = gameState != null ? gameState : GameState.AIMING;
        initializeGraphics();
        initializePhysics();
        initializeStageAndUI();
        initializeBirds();
        initializePigs();
        createGround();  // Ground just below the slingshot height
    }

    private void initializeGraphics() {
        batch = new SpriteBatch();
        backgroundTexture = new Texture(Gdx.files.internal("image1.png"));
        slingshotTexture = new Texture(Gdx.files.internal("slingshot.png"));
        shapeRenderer = new ShapeRenderer();
    }

    private void initializePhysics() {
        world = new World(new Vector2(0, -9.81f), true);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        box2DDebugRenderer = new Box2DDebugRenderer();
        world.setContactListener(new GameContactListener());
    }

    private void createGround() {
        // Define the ground body to prevent birds and pigs from falling indefinitely
        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(Main.WORLD_WIDTH / 2, slingshotPosition.y - 35)); // Set ground slightly below the slingshot
        groundBodyDef.type = BodyDef.BodyType.StaticBody;

        Body groundBody = world.createBody(groundBodyDef);

        // Define a box shape for the ground
        PolygonShape groundBox = new PolygonShape();
        groundBox.setAsBox(Main.WORLD_WIDTH / 2, 10);  // Width and height of the ground

        FixtureDef groundFixtureDef = new FixtureDef();
        groundFixtureDef.shape = groundBox;
        groundFixtureDef.friction = 1.0f;  // High friction to prevent sliding
        groundFixtureDef.restitution = 0.0f;  // No bouncing

        groundBody.createFixture(groundFixtureDef);
        groundBox.dispose();  // Clean up the shape
    }

    private void initializeStageAndUI() {
        stage = new Stage(new ScreenViewport());
        Texture pauseTexture = new Texture(Gdx.files.internal("pause_button.jpg"));
        pauseButton = createButton(pauseTexture, Main.WORLD_WIDTH - 70, Main.WORLD_HEIGHT - 70);

        pauseButton.addListener(event -> {
            if (gameState == GameState.PLAYING || gameState == GameState.AIMING) {
                gameState = GameState.PAUSED;
                game.setScreen(new PauseScreen(game, currentLevel));
            }
            return true;
        });

        stage.addActor(pauseButton);
        Gdx.input.setInputProcessor(stage);
    }

    private ImageButton createButton(Texture texture, float x, float y) {
        ImageButton button = new ImageButton(new TextureRegionDrawable(new TextureRegion(texture)));
        button.setSize(50, 50);
        button.setPosition(x, y);
        return button;
    }

    private void initializeBirds() {
        for (int i = 0; i < 3; i++) {
            birdQueue.add(new PlayingBird(world, birdStartPosition));
        }
        loadNextBird();
    }

    private void loadNextBird() {
        if (!birdQueue.isEmpty()) {
            currentBird = birdQueue.poll();
            currentBird.setPosition(birdStartPosition.cpy());

            // Set bird to kinematic to allow controlled positioning without falling
            currentBird.getBody().setType(BodyDef.BodyType.KinematicBody);
            currentBird.getBody().setLinearVelocity(0, 0);
            currentBird.getBody().setAngularVelocity(0);
            gameState = GameState.AIMING;
        } else {
            gameState = GameState.LOST;
            game.setScreen(new LoseScreen(game));
        }
    }

    private void initializePigs() {
        pigs.add(new Pig(world, new Vector2(300, slingshotPosition.y + 20), "pig_texture.png", 2));
        pigs.add(new Pig(world, new Vector2(400, slingshotPosition.y + 20), "pig_texture.png", 3));
        pigs.add(new Pig(world, new Vector2(500, slingshotPosition.y + 20), "pig_texture.png", 1));
    }

    private void launchBird() {
        if (currentBird != null) {
            Vector2 launchVector = slingshotPosition.cpy().sub(currentBird.getPosition()).scl(3f);
            currentBird.getBody().setType(BodyDef.BodyType.DynamicBody); // Switch to dynamic to allow physics forces
            currentBird.getBody().setAwake(true); // Ensure bird is active
            currentBird.applyLaunchForce(launchVector);
            gameState = GameState.PLAYING;
        }
    }

    private void handleInput() {
        if (Gdx.input.isTouched() && gameState == GameState.AIMING) {
            Vector3 touchPoint = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            float maxDragDistance = 120f;

            Vector2 touchPoint2D = new Vector2(touchPoint.x, touchPoint.y);

            // Check if we are dragging for the first time
            if (!isDragging && touchPoint2D.dst(birdStartPosition) < 50f) { // Start dragging at bird height
                isDragging = true;
            }

            if (isDragging && currentBird != null) {
                Vector2 slingshotToPointer = new Vector2(touchPoint.x, touchPoint.y).sub(slingshotPosition);
                if (slingshotToPointer.len() > maxDragDistance) {
                    slingshotToPointer.setLength(maxDragDistance);
                }
                currentBird.setPosition(slingshotPosition.cpy().add(slingshotToPointer.x, Math.max(slingshotToPointer.y, 0)));
            }
        } else if (isDragging) {
            isDragging = false;
            launchBird();
        }
    }

    private void update(float delta) {
        if (gameState == GameState.PLAYING) {
            world.step(1 / 60f, 6, 2);
            checkGameStatus();
        }
        handleInput();
    }

    private void checkGameStatus() {
        boolean allPigsDestroyed = pigs.stream().allMatch(Pig::isDestroyed);

        if (allPigsDestroyed) {
            gameState = GameState.WON;
            game.setScreen(new WinScreen(game));
        } else if (birdQueue.isEmpty() && currentBird == null) {
            gameState = GameState.LOST;
            game.setScreen(new LoseScreen(game));
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.5f, 0.8f, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(backgroundTexture, 0, 0, Main.WORLD_WIDTH, Main.WORLD_HEIGHT);
        batch.draw(slingshotTexture, slingshotPosition.x - 25, slingshotPosition.y - 25, 50, 150);
        if (currentBird != null) {
            batch.draw(currentBird.getTexture(), currentBird.getPosition().x - 25, currentBird.getPosition().y - 25, 50, 50);
        }
        for (Pig pig : pigs) {
            if (!pig.isDestroyed()) {
                batch.draw(pig.getTexture(), pig.getPosition().x - 25, pig.getPosition().y - 25, 50, 50);
            }
        }

        batch.end();

        if (isDragging && currentBird != null) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.line(currentBird.getPosition(), slingshotPosition);
            shapeRenderer.end();
        }

        box2DDebugRenderer.render(world, camera.combined);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        backgroundTexture.dispose();
        slingshotTexture.dispose();
        stage.dispose();
        shapeRenderer.dispose();
        for (Pig pig : pigs) {
            pig.dispose();
        }
    }

    @Override
    public void pause() {
        gameState = GameState.PAUSED;
        game.setScreen(new PauseScreen(game, currentLevel));
    }

    @Override
    public void resume() {}

    @Override
    public void show() {}

    @Override
    public void hide() {}
}