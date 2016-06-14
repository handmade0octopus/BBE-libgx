package com.handmadeoctopus.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.handmadeoctopus.entities.*;


public class InputHandler implements InputProcessor {

    boolean zooming = false;
    OrthographicCamera camera, uiCamera;
    Zoom zoom;
    SlidingMenu slidingMenu;
    Settings settings;
    float x, y, x1, y1;


    public InputHandler(OrthographicCamera camera, OrthographicCamera uiCamera, Zoom zoom, Stage stage, Settings settings) {
        this.camera = camera;
        this.uiCamera = camera;
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
        x = camera.unproject(new Vector3(Gdx.input.getX(0), Gdx.input.getY(0), 0)).x;
        y = camera.unproject(new Vector3(Gdx.input.getX(0), Gdx.input.getY(0), 0)).y;
        x1 = camera.unproject(new Vector3(Gdx.input.getX(1), Gdx.input.getY(1), 0)).x;
        y1 = camera.unproject(new Vector3(Gdx.input.getX(1), Gdx.input.getY(1), 0)).y;

        float z = uiCamera.viewportWidth/Gdx.graphics.getWidth();
        float q = uiCamera.viewportHeight/Gdx.graphics.getHeight();

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
        zoom.touchUpAction(pointer);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
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
        camera.zoom *= (10f+amount)/10f;
        camera.update();
        return false;
    }

    public void drawUi() {
        slidingMenu.draw();
    }

    public void update(float width, float height) {
        slidingMenu.update(width, height);
    }

}
