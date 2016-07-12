package com.handmadeoctopus.Engine;

// Main engine that controls balls and other entities.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.MipMapGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.handmadeoctopus.entities.Ball;
import com.handmadeoctopus.entities.Box;
import com.handmadeoctopus.environment.Zoom;

public class MainEngine {
    Array<Ball> balls = null;
    int ballsQuantity, ballsSize, ballsTail, springiness, gravity, forces, speed, universeScale;
    boolean gravitation, ballsForces;

    private Box box;
    private Zoom zoom;
    private Settings settings;
    private Age age;

    public static final Pixmap PIXMAP = new Pixmap(Gdx.files.internal("circle.png"));
    public static final Texture TEXTURE = new Texture(PIXMAP, true);

    // New ball input
    Ball newBall = null;

    boolean handlingBall = false; // checks if ball has been clicked

    // Constructor
    public MainEngine(Zoom zoom, Settings settings, Box box) {
        this.zoom = zoom;
        this.settings = settings;
        this.box = box;
        settings.setMainEngine(this);
        settings.setBox(box);
        balls = new Array<Ball>();
        age = new Age(settings, this);

        TEXTURE.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.MipMapLinearNearest);
        init();
    }

    // Initial setup
    private void init() {
        loadVariables();
        age.reload();
    }


    // Load variables to this class
    private void loadVariables() {
        ballsQuantity = settings.ballsQuantity;
        ballsSize = settings.ballsSize;
        ballsTail = settings.ballsTail;
        springiness = settings.springiness;
        gravity = settings.gravity;
        forces = settings.forces;
        speed = settings.speed;
        gravitation = settings.gravitation;
        ballsForces = settings.ballsForces;
        universeScale = settings.universeScale;
    }

    //Reloads balls
    public void reload() {
        init();
    }

    // Draws all balls and performs actions on them
    public void drawBalls(ShapeRenderer renderer) {
        if (balls != null) {
            for (int i = 0; i < balls.size; i++) {
                if (balls.size == 1) { balls.get(0).act(null); }
                for (int j = i+1; j < balls.size; j++) {
                    balls.get(i).act(balls.get(j));
                }
                if (balls.size == i+1) { balls.get(i).act(null); }
                balls.get(i).draw(renderer);
            }
        }
    }

    // Draws all balls and performs actions on them BATCH
    public void drawBalls(SpriteBatch batch) {
        age.drawCurrentYear(batch);
    }

    // Updates balls after value is changed
    public void update(Settings.SettingsEnum settingsEnum) {
        age.updateBalls(settingsEnum);
        loadVariables();
    }

    // Actions called on mouse/finger up/down
    public void actionDown(float x, float y) {
        age.actionDown(x, y);
    }

    public void actionUp(float x, float y) {
        age.actionUp(x, y);
    }
}
