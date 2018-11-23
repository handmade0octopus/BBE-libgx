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

import java.util.HashMap;

public class MainEngine {
    Array<Ball> balls = null;
    int ballsQuantity, ballsSize, ballsTail, springiness, gravity, forces, speed, universeScale;
    boolean gravitation, ballsForces;

    private Box box;
    private Zoom zoom;
    private Settings settings;
    private Age age;
    private HashMap<Settings.SettingsEnum, Integer> settingMap;

    public static final Pixmap PIXMAP = new Pixmap(Gdx.files.internal("spaceship.png"));
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
        settingMap = new HashMap<Settings.SettingsEnum, Integer>();
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
        for (Settings.SettingsEnum set : Settings.SettingsEnum.values()) {
            settingMap.put(set, settings.getSetting(set).getValue());
        }

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



    // Draws all balls and performs actions on them BATCH
    public void drawBalls(SpriteBatch batch) {
        age.drawCurrentYear(batch);
    }

    // Draws balls with renderer.
    public void drawBalls(ShapeRenderer renderer) {
        age.drawCurrentYear(renderer);
    }

    // Updates balls after value is changed
    public void action(SettingEntry setting) {
        age.updateBalls(setting);
        loadVariables();
    }

    // Actions called on mouse/finger up/down
    public void actionDown(float x, float y) {
        age.actionDown(x, y);
    }

    public void actionUp(float x, float y) {
        age.actionUp(x, y);
    }

    public void buttonDown(int keycode) {
        age.buttonDown(keycode);
    }

    // Get settings when called
    int getSetting(Settings.SettingsEnum setting) {
        return settingMap.get(setting);
    }



}
