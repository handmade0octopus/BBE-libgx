package com.handmadeoctopus.environment;


public class Settings {
    int ballsQuantity, ballsSize, ballsTail;
    float springiness, gravity, forces;
    boolean gravitation, ballsForces;

    public Settings() {

    }

    public void set(int ballsQuantity, int ballsSize, int ballsTail, float springiness, float gravity, float forces) {
        if (forces > 0 ) { ballsForces = true; }
        else { ballsForces = false; }
        if (gravity > 0) { gravitation = true; }
        else { gravitation = false; }

        this.ballsQuantity = ballsQuantity;
        this.ballsSize = ballsSize;
        this.ballsTail = ballsTail;
        this.springiness = springiness;
        this.gravity = gravity;
        this.forces = forces;

        update();
    }

    private void update() {

    }

}
