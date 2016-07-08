package com.handmadeoctopus.environment;

// Main engine that controls balls and other entities.

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.handmadeoctopus.entities.Ball;
import com.handmadeoctopus.entities.Box;

public class MainEngine {
    Array<Ball> balls = null;
    int ballsQuantity, ballsSize, ballsTail, springiness, gravity, forces, quality;
    boolean gravitation, ballsForces;

    private Box box;
    private Zoom zoom;
    private Settings settings;

    // New ball input
    Ball newBall = null;

    boolean handlingBall = false; // checks if ball has been clicked

    // Constructor
    public MainEngine(Zoom zoom, Settings settings, Box box) {
        this.zoom = zoom;
        this.settings = settings;
        this.box = box;
        settings.setMainEngine(this);
        init();
    }

    // Initial setup
    private void init() {
        loadVariables();
        balls = new Array<Ball>(ballsQuantity);
        for(int i = 0; i < ballsQuantity; i++) {
            Ball ball = new Ball(ballsSize, box);
            ball.setBallParameters(gravity, springiness, ballsTail, forces, quality, gravitation, ballsForces, box);
            balls.add(ball);
        }
    }

    // Load variables to this class
    private void loadVariables() {
        ballsQuantity = settings.ballsQuantity;
        ballsSize = settings.ballsSize;
        ballsTail = settings.ballsTail;
        springiness = settings.springiness;
        gravity = settings.gravity;
        forces = settings.forces;
        quality = settings.quality;
        gravitation = settings.gravitation;
        ballsForces = settings.ballsForces;
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

}
