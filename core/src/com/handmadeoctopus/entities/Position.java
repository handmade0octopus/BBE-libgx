package com.handmadeoctopus.entities;

// Class that keep position of balls
public class Position {
    float x, y, z;

    public Position(float x, float y, float z) {
        set(x, y, z);
    }

    public Position(Position position) {
        set(position);
    }

    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(Position position) {
        this.x = position.x;
        this.y = position.y;
        this.z = position.z;
    }

}
