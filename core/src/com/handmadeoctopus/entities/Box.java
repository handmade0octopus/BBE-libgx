package com.handmadeoctopus.entities;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;

// Box class contains boundries for our world

public class Box {
    // Size of box.
    float xMin, xMax, yMin, yMax, width, height;

    // Sets box by width and height
    public Box(float width, float height) {
        this.width = width;
        this.height = height;
    }

    // Sets box size, called when change size
    public void set(int x, int y, float width, float height) {
        this.width = width;
        this.height = height;
        xMin = x;
        xMax = x + width;
        yMin = y;
        yMax = y + height;
    }

    // Draws box on screen
    public void draw(ShapeRenderer renderer) {
        renderer.setColor(Color.BLACK);
        renderer.rect(xMin, yMin, xMax, yMax);
    }


}