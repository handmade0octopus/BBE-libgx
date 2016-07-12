package com.handmadeoctopus.Engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.handmadeoctopus.BouncingBallEngine;
import com.handmadeoctopus.environment.SlidingMenu;
import com.handmadeoctopus.environment.Zoom;

// Main class handling all inputs
public class InputHandler implements InputProcessor {

    OrthographicCamera camera, uiCamera; // Carried over cameras
    Zoom zoom; // Zoom object which helps us control cameras.
    SlidingMenu slidingMenu; // SlidingMenu which also calls our actions.
    Settings settings; // Main settings of the game which this class changes directly from user input.
    float x, y, x1, y1, w, h, f; // Variables to control input.
    int lastSpeed = 1;


    public InputHandler(OrthographicCamera camera, OrthographicCamera uiCamera, Zoom zoom, Stage stage, Settings settings) {
        this.camera = camera;
        this.uiCamera = uiCamera;
        this.zoom = zoom;
        this.slidingMenu = new SlidingMenu(stage, this, settings);
        this.settings = settings;
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.SPACE && settings.speed == 0) {
            settings.speed = lastSpeed;
            if (settings.menu != null) { settings.menu.updateValues(); }
        } else if (keycode == Input.Keys.SPACE) {
            lastSpeed = Math.max(1, settings.speed);
            settings.speed = 0;
            if (settings.menu != null) { settings.menu.updateValues(); }
        }
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


        if (Gdx.input.isTouched(0) && settings.mainEngine.newBall == null) {
            zoom.x = Gdx.input.getX(0);
            zoom.y = Gdx.input.getY(0);

            // z & q translate input to screen size.
            slidingMenu.onClick(z*Gdx.input.getX(0), q*(Gdx.graphics.getHeight()-Gdx.input.getY(0)));
        }
        if (Gdx.input.isTouched(1) && settings.mainEngine.newBall == null) {
            zoom.x1 = Gdx.input.getX(1);
            zoom.y1 = Gdx.input.getY(1);
            zoom.xP = (zoom.x+zoom.x1)/2;
            zoom.yP = (zoom.y+zoom.y1)/2;
        }

        if(!slidingMenu.visible) {
            settings.mainEngine.actionDown(x, y);
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        // All touch up actions are made.
        x = camera.unproject(new Vector3(Gdx.input.getX(0), Gdx.input.getY(0), 0)).x;
        y = camera.unproject(new Vector3(Gdx.input.getX(0), Gdx.input.getY(0), 0)).y;

        if(settings.mainEngine.newBall == null){
            zoom.touchUpAction(pointer);
        } else  {
            settings.mainEngine.actionUp(x, y);
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        // Dragged only when botch fingers are down.
        x = camera.unproject(new Vector3(Gdx.input.getX(0), Gdx.input.getY(0), 0)).x;
        y = camera.unproject(new Vector3(Gdx.input.getX(0), Gdx.input.getY(0), 0)).y;
        x1 = camera.unproject(new Vector3(Gdx.input.getX(1), Gdx.input.getY(1), 0)).x;
        y1 = camera.unproject(new Vector3(Gdx.input.getX(1), Gdx.input.getY(1), 0)).y;
        if (Gdx.input.isTouched(0) && Gdx.input.isTouched(1) && settings.mainEngine.newBall == null) {
            zoom.dragged(Gdx.input.getX(0), Gdx.input.getY(0), Gdx.input.getX(1), Gdx.input.getY(1));
        } else if (settings.mainEngine.newBall != null && Gdx.input.isTouched(0)) {
            settings.mainEngine.newBall.setSpeedByPosition(x,y);
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
        zoom.scrolled(amount, settings.mainEngine.newBall == null);
        return false;
    }

    public void drawUi() {
        // Draws whole UI
        slidingMenu.draw();
    }

    public void update(float width, float height) {
        // Updates menu if change size
        slidingMenu.update(width, height);
        w = width;
        h = height;
        f = (h / w);
        settings.setUniScale(f);
    }

    public void update() {
        update(w,h);
    }

    public void updateMenu() {
        // Update menu to get real input compared to camera.
        float z = uiCamera.viewportWidth/Gdx.graphics.getWidth();
        float q = uiCamera.viewportHeight/Gdx.graphics.getHeight();
        slidingMenu.updateMenu(z, q);
    }
}
