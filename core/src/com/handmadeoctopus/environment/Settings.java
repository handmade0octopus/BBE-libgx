package com.handmadeoctopus.environment;


import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class Settings {
    int ballsQuantity, ballsSize, ballsTail;
    float springiness, gravity, forces;
    boolean gravitation, ballsForces, reset = false;
    Menu menu;
    
    static float MAX_QUANT = 1000;

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

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public void update() {
        if(reset) {
            menu.resetValues();
            reset = false;
        }
        menu.updateValues();
    }

    public void set(SettingsEnum settingsEnum, float x, float y, TextButton button) {
        switch(settingsEnum) {
            case RESET: reset = true; break;
            case BALLSQUANTITYBG: setBallsQuantity(setSliderBg(x, y, button, ballsQuantity)); break;
        }
    }

    private float setSliderBg(float x, float y, TextButton button, float var) {
        float value = var;
        float diff;

        if (button.getText() == "-") {
            diff = 1;
        } else if (button.getText() == "+") {
            diff = -1;
        } else if(button.getText() == " ") {
            diff = 0;
        } else { diff = button.getX() - x; }

        value -= diff;
        return value;
    }

    void setBallsQuantity(float newQuant) {
        if(newQuant >= 0 && newQuant <= MAX_QUANT) { ballsQuantity = (int) newQuant; }
    }

    public void drag(SettingsEnum settingsEnum, float x, float y, TextButton slider, TextButton bgSlider) {
        if(x > bgSlider.getX() + slider.getWidth()/2 && x < bgSlider.getX() + bgSlider.getWidth() - slider.getWidth()/2) { 
            slider.setX(x-slider.getWidth()/2);
        }
    }


    enum SettingsEnum {
        RESET("RESET"), BALLSQUANTITYBG("BALLS QUANTITY");

        String s;

        SettingsEnum(String s) {
            this.s = s;
        }
    }

}
