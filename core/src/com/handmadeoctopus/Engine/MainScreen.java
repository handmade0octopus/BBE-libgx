package com.handmadeoctopus.Engine;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;

import com.badlogic.gdx.utils.viewport.*;
import com.handmadeoctopus.BouncingBallEngine;
import com.handmadeoctopus.entities.*;
import com.handmadeoctopus.environment.Zoom;


public class MainScreen implements Screen {

    Game game; // game object which is transferred in constructor from main class

    SpriteBatch batch, batchUi; // batches for sprites and other drawings, batchUi is used to draw UI
    OrthographicCamera camera, uiCamera; // similar to batches, cameras for both environments
    ShapeRenderer renderer; // Renderer for our non-sprite objects

    Settings settings; // Main setting of the game

    Stage stage; // Main stage, for our UI

    Box box; // Rectangle where our objects will collide

    Zoom zoom; // Zoom class which helps us handle UI

    FrameBuffer buffer;

    private InputHandler handler; // Our input handler, handles all actions

    private MainEngine mainEngine; // Main engine of the game

    public float width, height; // width and height of the in game screen


    public MainScreen(Game game) {
        this.game = game;

        // We calculateAll here height from height/width ratio
        width = BouncingBallEngine.WIDTH;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        height = (int) width * ( h/w );

        // Set up cameras and update to size.
        camera = new OrthographicCamera(width, height);
        camera.update();

        uiCamera = new OrthographicCamera(width, height);
        uiCamera.update();

        // Setting zoom and box that balls bounce in
        zoom = new Zoom(camera, uiCamera);
        box = new Box(width, height);
        zoom.setWorldBounds(0, 0,  width,  height);

        // Setting up batches and renderer
        batch = new SpriteBatch();
        batchUi = new SpriteBatch();
        renderer = new ShapeRenderer();
        buffer = new FrameBuffer(Pixmap.Format.RGB565, (int) width, (int) height, false);

        // Setting up stage which is extended to uiCamera
        stage = new Stage(new ExtendViewport(width, height, uiCamera));

        // Settings, where all game characteristics are kept.
        settings = new Settings(this);
        settings.setZoom(zoom);

        // Setting up handler. And making it main handler of the game.
        handler = new InputHandler(camera, uiCamera, zoom, stage, settings);
        Gdx.input.setInputProcessor(handler);


        mainEngine = new MainEngine(zoom, settings, box);
    }



    // Called when this screen becomes the current screen for a {@link Game}.
    @Override
    public void show() {

    }

    //Called when the screen should render itself.  @param delta The time in seconds since the last render.
    @Override
    public void render(float delta) {
        // Main rendering class. First screen is cleared, then cameras are updated and combined with batches and renderer.
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);

        // Updates camera with initial values
        camera.update();
        uiCamera.update();

        // After camera is updated, projection matrix is set on batch, batchUi and (shape) renderer.
        batch.setProjectionMatrix(camera.combined);
        batchUi.setProjectionMatrix(uiCamera.combined);
        renderer.setProjectionMatrix(batch.getProjectionMatrix());

        // Draws each frame
        draw();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void draw() {
        // Method for drawing all entities and UI.
        renderer.begin(ShapeRenderer.ShapeType.Filled);

        mainEngine.drawBalls(renderer);
        box.draw(renderer);

        renderer.end();

        /* Batch renderer if we want to use texture instead of rendered cicrles.
        batch.begin();
        mainEngine.drawBalls(batch);
        batch.end();
        */


        // Draws UI.
        batchUi.begin();
        handler.drawUi();
        batchUi.end();
    }

    @Override
    public void resize(int width, int height) {
        updateEnvironment(width, height);
    }

    private void updateEnvironment(int width, int height) {
        // Called when window is resized or screen changes size.
        float w = width;
        float h = height;
        float f = (h / w);
        this.height = this.width * f; // Height calculated from screen size height/width ratio.

        // Box, cameras and handler updated.
        box.set(0, 0, 0, this.width, this.height, this.width);

        // Camera is set to default middle scren position
        camera.setToOrtho(false, this.width, this.height);
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();

        // Update handler ui positions.
        handler.update(width, height);

        // Camera is set to default middle screen position - for some reason sometimes required twice.
        uiCamera.setToOrtho(false, this.width, this.height);
        uiCamera.position.set(uiCamera.viewportWidth / 2f, uiCamera.viewportHeight / 2f, 0);
        uiCamera.update();

        handler.updateMenu();
    }


    @Override
    public void pause() {
        // Called when app pauses
        settings.save();
    }

    @Override
    public void resume() {
        // Called when app resumes.
        settings.load();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        settings.save();
    }
}
