package com.handmadeoctopus.environment;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.handmadeoctopus.BouncingBallEngine;


public class MainScreen implements Screen {

    SpriteBatch batch, batchUi;
    Game game;
    Image img;
    OrthographicCamera camera;
    Stage stage;
    ShapeRenderer renderer;
    Zoom zoom;

    private InputHandler handler;

    public float width, height;



    public MainScreen(Game game) {
        this.game = game;

        width = BouncingBallEngine.WIDTH;
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        height = (int) width * ( h/w );

        camera = new OrthographicCamera(width, height);
        camera.update();

        zoom = new Zoom(camera);

        batch = new SpriteBatch();
        batchUi = new SpriteBatch();

        img = new Image(new Texture("badlogic.jpg"));
        img.setSize(width, width*(img.getHeight()/img.getWidth()));
        img.setPosition(width/2 - img.getWidth()/2, height /2 - img.getHeight()/2);

        renderer = new ShapeRenderer();

        stage = new Stage();

        handler = new InputHandler(camera, zoom, stage);

        Gdx.input.setInputProcessor(handler);
    }




    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        batch.setProjectionMatrix(camera.combined);
        renderer.setProjectionMatrix(batch.getProjectionMatrix());



        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.BLACK);
        renderer.rect(0, 0, width, height);
        renderer.end();





        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(Color.CYAN);
        renderer.circle(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 100, 360);
        renderer.end();

        draw();
    }

    private void draw() {
        batch.begin();
        batch.end();

        batchUi.begin();
        handler.drawUi();
        batchUi.end();

    }

    @Override
    public void resize(int width, int height) {
        float w = width;
        float h = height;
        float f = (h / w);
        this.height = this.width * f;
        camera.setToOrtho(false, this.width, this.height);
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
