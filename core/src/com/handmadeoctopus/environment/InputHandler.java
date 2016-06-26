package com.handmadeoctopus.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.handmadeoctopus.BouncingBallEngine;
import com.handmadeoctopus.entities.*;


public class InputHandler implements InputProcessor {

    OrthographicCamera camera, uiCamera; // Carried over cameras
    Zoom zoom; // Zoom object which helps us control cameras.
    SlidingMenu slidingMenu; // SlidingMenu which also calls our actions.
    Settings settings; // Main settings of the game which this class changes directly from user input.
    float x, y, x1, y1; // Variables to control input.


    public InputHandler(OrthographicCamera camera, OrthographicCamera uiCamera, Zoom zoom, Stage stage, Settings settings) {
        this.camera = camera;
        this.uiCamera = uiCamera;
        this.zoom = zoom;
        this.slidingMenu = new SlidingMenu(stage, this, settings);
        this.settings = settings;
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // x,y is position of first finger pressed, x1, y2 is position of second one. camera.unproject sets them in environment nevertheless zoom or rotation of camera.
        x = camera.unproject(new Vector3(Gdx.input.getX(0), Gdx.input.getY(0), 0)).x;
        y = camera.unproject(new Vector3(Gdx.input.getX(0), Gdx.input.getY(0), 0)).y;
        x1 = camera.unproject(new Vector3(Gdx.input.getX(1), Gdx.input.getY(1), 0)).x;
        y1 = camera.unproject(new Vector3(Gdx.input.getX(1), Gdx.input.getY(1), 0)).y;

        // Calculates ui camera to exact coordinates on the screen. Just as gui is showed.
        float z = uiCamera.viewportWidth/Gdx.graphics.getWidth();
        float q = uiCamera.viewportHeight/Gdx.graphics.getHeight();
        slidingMenu.z = z;
        slidingMenu.q = q;

        if (Gdx.input.isTouched(0)) {
            zoom.x = Gdx.input.getX(0);
            zoom.y = Gdx.input.getY(0);
            slidingMenu.onClick(z*Gdx.input.getX(0), q*(Gdx.graphics.getHeight()-Gdx.input.getY(0)));
        }
        if (Gdx.input.isTouched(1)) {
            zoom.x1 = Gdx.input.getX(1);
            zoom.y1 = Gdx.input.getY(1);
            zoom.xP = (zoom.x+zoom.x1)/2;
            zoom.yP = (zoom.y+zoom.y1)/2;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // All touch up actions are made.
        zoom.touchUpAction(pointer);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Dragged only when botch fingers are down.
        if (Gdx.input.isTouched(0) && Gdx.input.isTouched(1)) {
            zoom.dragged(Gdx.input.getX(0), Gdx.input.getY(0), Gdx.input.getX(1), Gdx.input.getY(1));
        }
        return false;
    }



    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        // When mouse middle button is scrolled.
        camera.zoom *= (10f+amount)/10f;
        zoom.checkCamera();
        camera.update();
        return false;
    }

    public void drawUi() {
        // Draws whole UI
        slidingMenu.draw();
    }

    public void update(float width, float height) {
        // Updates menu if change size
        slidingMenu.update(width, height);
        float w = width;
        float h = height;
        float f = (h / w);
        zoom.setWorldBounds(0, 0, BouncingBallEngine.WIDTH, (BouncingBallEngine.WIDTH*f));
    }

    public void updateMenu() {
        // Update menu to get real input compared to camera.
        float z = uiCamera.viewportWidth/Gdx.graphics.getWidth();
        float q = uiCamera.viewportHeight/Gdx.graphics.getHeight();
        slidingMenu.updateMenu(z, q);
    }
}
