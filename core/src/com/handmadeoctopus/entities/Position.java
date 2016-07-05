package com.handmadeoctopus.entities;

// Class that keep position of balls
public class Position {
    public float x, y;

    public Position(float x, float y) {
        set(x, y);
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void set(Position position) {
        this.x = position.x;
        this.y = position.y;
    }

}
