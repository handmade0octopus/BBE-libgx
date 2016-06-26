package com.handmadeoctopus.entities;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Box {
    float xMin, xMax, yMin, yMax, width, height;
    public float xZoomMin = 0, yZoomMin = 0, xZoomMax = 0, yZoomMax = 0;
    Stage stage;

    public Box(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void set(int x, int y, float width, float height) {
        this.width = width;
        this.height = height;
        xMin = x;
        xMax = x + width;
        yMin = y;
        yMax = y + height;



        if(xZoomMax == 0 && yZoomMax == 0 && xZoomMin == 0 && yZoomMin == 0) {
            xZoomMin = xMin;
            yZoomMax = yMin;
            xZoomMax = xMax;
            yZoomMax = yMax;
        }


    }

    public void moveZoom(float x, float y, float scale, float xP, float yP) {
  /*      xZoomMax = xZoomMax*scale - xP*(scale - 1);
        yZoomMax = yZoomMax*scale - yP*(scale - 1);
        xZoomMin = xZoomMin*scale - xP*(scale - 1);
        yZoomMin = yZoomMin*scale - yP*(scale - 1);

        this.xZoomMin += x;
        this.yZoomMin += y;
        this.xZoomMax += x;
        this.yZoomMax += y;*/

    }

    public void draw(ShapeRenderer renderer) {
        renderer.setColor(Color.BLACK);
        renderer.rect(xMin, yMin, xMax, yMax);
        renderer.setColor(Color.YELLOW);
        renderer.rect(xZoomMin, yZoomMin, 10, 10);
        renderer.rect(xZoomMax, yZoomMax, -10, -10);
    }


}