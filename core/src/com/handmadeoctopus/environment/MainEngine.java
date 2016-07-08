package com.handmadeoctopus.environment;

// Main engine that controls balls and other entities.

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.handmadeoctopus.entities.Ball;
import com.handmadeoctopus.entities.Box;

public class MainEngine {
    Array<Ball> balls = null;
    int ballsQuantity, ballsSize, ballsTail, springiness, gravity, forces, speed;
    boolean gravitation, ballsForces;

    private Box box;
    private Zoom zoom;
    private Settings settings;

    public static final Texture TEXTURE = new Texture("circle.png");

    // New ball input
    Ball newBall = null;

    boolean handlingBall = false; // checks if ball has been clicked

    // Constructor
    public MainEngine(Zoom zoom, Settings settings, Box box) {
        this.zoom = zoom;
        this.settings = settings;
        this.box = box;
        settings.setMainEngine(this);
        balls = new Array<Ball>();
        init();
    }

    // Initial setup
    private void init() {
        loadVariables();
        balls.setSize(ballsQuantity);
        for(int i = 0; i < ballsQuantity; i++) {
            balls.set(i, newBall());
        }
    }

    // Creates random new ball
    private Ball newBall() {
        Ball ball = new Ball(ballsSize, box);
        ball.setBallParameters(gravity, springiness, ballsTail, forces, speed, gravitation, ballsForces, box);
        return ball;
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
        if (balls != null) {
            for (int i = 0; i < balls.size; i++) {
                if (balls.size == 1) { balls.get(0).act(null); }
                for (int j = i+1; j < balls.size; j++) {
                    balls.get(i).act(balls.get(j));
                }
                if (balls.size == i+1) { balls.get(i).act(null); }
                balls.get(i).move().draw(batch);
            }
        }
        if(newBall != null) {
            newBall.grow();
            newBall.draw(batch);
        }
    }

    // Updates balls after value is changed
    public void update(Settings.SettingsEnum settingsEnum) {
        switch(settingsEnum) {
            case BALLSQUANTITY:
                int diff = settings.ballsQuantity - ballsQuantity;
                if(diff >= 0) {
                    for(int i = 0; i < diff; i++) {
                        balls.add(newBall());
                    }
                } else {
                    balls.setSize(settings.ballsQuantity);
                }
                break;
            case BALLSSIZE:
                float newSize = settings.ballsSize;
                float oldSize = ballsSize;
                float changeSize = newSize/oldSize;
                for(Ball ball : balls) {
                    ball.radius *= changeSize;
                    ball.mass = 3.14f * ball.radius * ball.radius;
                }
                break;
            case BALLSTAIL:
                for(Ball ball : balls) {
                    ball.setTail(settings.ballsTail);
                }
                break;
            case SPRINGINESS:
                for(Ball ball : balls) {
                    ball.springiness = settings.springiness/100f;
                }
                break;
            case GRAVITY:
                for(Ball ball : balls) {
                    ball.gravity = settings.gravity/100f;
                    ball.gravitation = settings.gravitation;
                }
                break;
            case FORCES:
                for(Ball ball : balls) {
                    ball.force = settings.forces/100f;
                    ball.forces = settings.ballsForces;
                }
                break;
            case SPEED:
                float oldSpeed = speed;
                float newSpeed = settings.speed;
                float speedChange = newSpeed/oldSpeed;
                for(Ball ball : balls) {
                    ball.speed = settings.speed/100f;
                    ball.speedX *= speedChange;
                    ball.speedY *= speedChange;
                }
                break;
        }
        loadVariables();
    }

    // Actions called on mouse/finger up/down
    public void actionDown(float x, float y) {
        for (Ball ball : balls) {
            if (newBall == null) {
                newBall = ball.clicked(x, y);
                if (newBall != null) {
                    balls.removeValue(newBall, true);
                    handlingBall = true;
                }
            }
        }

        if (newBall == null) {
            newBall = new Ball(1, x, y);
            newBall.startGrowing();
        }

        if (Gdx.input.isTouched(0) && Gdx.input.isTouched(1) && !handlingBall) { newBall = null; }
    }

    public void actionUp(float x, float y) {
        newBall.stopGrowing();
        newBall.setBallParameters(gravity, springiness, ballsTail, forces, speed, gravitation, ballsForces, box);
        newBall.setSpeedByPosition(x, y);
        balls.add(newBall);
        newBall = null;
        handlingBall = false;
    }
}
