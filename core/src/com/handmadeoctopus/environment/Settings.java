package com.handmadeoctopus.environment;


public class Settings {
    int ballsQuantity, ballsSize, ballsTail;
    float springiness, gravity, forces;
    boolean gravitation, ballsForces, reset = false;

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

    public void update() {
        if(reset) { reset = false; }
    }

    public void set(SettingsEnum settingsEnum, float x, float y) {
        switch(settingsEnum) {
            case RESET: reset = true; break;
        }
    }


    enum SettingsEnum {
        RESET("RESET"), BALLSQUANTITY("BALLS QUANTITY");

        String s;

        SettingsEnum(String s) {
            this.s = s;
        }
    }

}
