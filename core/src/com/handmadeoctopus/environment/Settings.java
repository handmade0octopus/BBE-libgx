package com.handmadeoctopus.environment;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class Settings {
    int ballsQuantity, ballsSize, ballsTail, springiness, gravity, forces;
    boolean gravitation, ballsForces, reset = false;
    Menu menu;
    Zoom zoom;

    Preferences prefs;
    
    static float MAX_QUANT = 500, MAX_SIZE = 250, MAX_TAIL = 100, MAX_SPRINGINESS = 200, MAX_GRAVITY = 200, MAX_FORCES = 200;


    public Settings() {
        prefs = Gdx.app.getPreferences(SettingsEnum.PREFS.s);
        load();
    }

    public void set(int ballsQuantity, int ballsSize, int ballsTail, int springiness, int gravity, int forces) {
        this.ballsQuantity = ballsQuantity;
        this.ballsSize = ballsSize;
        this.ballsTail = ballsTail;
        this.springiness = springiness;
        this.gravity = gravity;
        this.forces = forces;
        save();
        update();
    }

    void load() {
        ballsQuantity = prefs.getInteger(SettingsEnum.BALLSQUANTITY.s);
        ballsSize = prefs.getInteger(SettingsEnum.BALLSSIZE.s);
        ballsTail = prefs.getInteger(SettingsEnum.BALLSTAIL.s);
        springiness = prefs.getInteger(SettingsEnum.SPRINGINESS.s);
        gravity = prefs.getInteger(SettingsEnum.GRAVITY.s);
        forces = prefs.getInteger(SettingsEnum.FORCES.s);
        update();
    }

    void save() {
        prefs.putInteger(SettingsEnum.BALLSQUANTITY.s, ballsQuantity);
        prefs.putInteger(SettingsEnum.BALLSSIZE.s, ballsSize);
        prefs.putInteger(SettingsEnum.BALLSTAIL.s, ballsTail);
        prefs.putInteger(SettingsEnum.SPRINGINESS.s, springiness);
        prefs.putInteger(SettingsEnum.GRAVITY.s, gravity);
        prefs.putInteger(SettingsEnum.FORCES.s, forces);
        prefs.flush();
    }

    public void resetDefaults() {
        ballsQuantity = 100;
        ballsSize = 50;
        ballsTail = 10;
        springiness = 100;
        gravity = 0;
        forces = 100;
        save();
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public void update() {
        if (forces > 0 ) { ballsForces = true; }
        else { ballsForces = false; }
        if (gravity > 0) { gravitation = true; }
        else { gravitation = false; }

        if (menu != null) { menu.updateValues(); }
    }

    public void set(float x, TextButton button, TextButton bgButton, boolean relative, boolean exact) {
        switch(SettingsEnum.valueOf(button.getName())) {
            case RESET: menu.resetValues(); break;
            case BALLSQUANTITY:
                ballsQuantity = setBallsParam(x, button, bgButton, relative, exact, ballsQuantity, 0, MAX_QUANT);
                break;
            case BALLSSIZE:
                ballsSize = setBallsParam(x, button, bgButton, relative, exact, ballsSize, 1, MAX_SIZE);
                break;
            case BALLSTAIL:
                ballsTail = setBallsParam(x, button, bgButton, relative, exact, ballsTail, 0, MAX_TAIL);
                break;
            case SPRINGINESS:
                springiness = setBallsParam(x, button, bgButton, relative, exact, springiness, 0, MAX_SPRINGINESS);
                break;
            case GRAVITY:
                gravity = setBallsParam(x, button, bgButton, relative, exact, gravity, 0, MAX_GRAVITY);
                break;
            case FORCES:
                forces = setBallsParam(x, button, bgButton, relative, exact, forces, 0, MAX_FORCES);
                break;
            case RELOAD: break;
        }
    }

    public void set(float x, TextButton button, TextButton bgButton, boolean relative) {
        set(x, button, bgButton, relative, false);
    }

    public void set(float x, TextButton button, TextButton bgButton) {
        set(x, button, bgButton, false, true);
    }


    int setBallsParam(float newValue, TextButton button, TextButton bgButton, boolean relative,
                        boolean exact, float var, float min, float max) {
        float newQuant = 0;
        float diff = newValue + bgButton.getX();


        if (relative) {
            newQuant = var + newValue;
            diff = button.getX() + newValue*(bgButton.getWidth()-button.getWidth())/(max-min);

        } else if (exact) {
            newQuant = var;
            diff =  bgButton.getX() + ((bgButton.getWidth() - button.getWidth()) * (newValue-min)) / (max-min) ;
        } else {
            newQuant = (max-min) * (newValue) / (bgButton.getWidth() - button.getWidth()) + min;
        }

        if (diff < bgButton.getX()) {
            diff = bgButton.getX();
        } else if (diff > bgButton.getX() + bgButton.getWidth() - button.getWidth()) {
            diff = bgButton.getX() + bgButton.getWidth() - button.getWidth();
        }

        if (newQuant < min) {
            newQuant = min;
        } else if (newQuant > max) {
            newQuant = max;
        }

        bgButton.setText(Settings.SettingsEnum.valueOf(bgButton.getName()).s + ": " + (int) newQuant);
        button.setX(diff);

        return (int) newQuant;
    }


    public void drag(float x, TextButton button, TextButton bgButton) {
        float xMin = bgButton.getX() + button.getWidth()/2;
        float xMax = bgButton.getX() + bgButton.getWidth() - button.getWidth()/2;
        if(x >= xMin && x <= xMax) {
            set(x - button.getWidth()/2 - (bgButton.getX()), button, bgButton, false);
        } else if (x < xMin) {
            set(0, button, bgButton, false);
        } else if ( x > xMax) {
            set(bgButton.getWidth()- button.getWidth(), button, bgButton, false);
        }
    }

    private float var(SettingsEnum settingsEnum, boolean set, float value) {
        switch(settingsEnum) {
            case BALLSQUANTITY:
                if(set) {
                    ballsQuantity = (int) value;
                    return 0;
                } else { return ballsQuantity; }
            case BALLSSIZE:
                if(set) {
                    ballsSize = (int) value;
                    return 0;
                } else { return ballsSize; }
            case BALLSTAIL:
                if(set) {
                    ballsTail = (int) value;
                    return 0;
                } else { return ballsTail; }
            case SPRINGINESS:
                if(set) {
                    springiness = (int) value;
                    return 0;
                } else {  return springiness; }
            case GRAVITY:
                if(set) {
                    gravity = (int) value;
                    return 0;
                } else { return gravity; }
            case FORCES:
                if(set) {
                    forces = (int) value;
                    return 0;
                } else { return forces; }
            default: return 0;
        }
    }

    private void setVar(TextButton button, float value){
        var(SettingsEnum.valueOf(button.getName()), true, value);
        update();
    }

    float getVar(TextButton button) {
        return var(SettingsEnum.valueOf(button.getName()), false, 0);

      /*  switch(SettingsEnum.valueOf(button.getName())) {
            case BALLSQUANTITY: return ballsQuantity;
            case BALLSSIZE: return ballsSize;
            case BALLSTAIL: return ballsTail;
            case SPRINGINESS: return springiness;
            case GRAVITY: return gravity;
            case FORCES: return forces;
            default: return 0;
        } */
    }

    void setMenu(boolean on) {
        menu.setMenu(on);
    }

    public void setZoom(Zoom zoom) {
        this.zoom = zoom;
    }

    public void resetZoom() {
        zoom.reset();
    }

    enum SettingsEnum {
        RESET("RESET"),
        BALLSQUANTITY("BALLS' QUANTITY"),
        BALLSSIZE("BALLS' SIZE"),
        BALLSTAIL("BALLS' TAIL"),
        SPRINGINESS("SPRINGINESS"),
        GRAVITY("GRAVITY"),
        FORCES("FORCES"),
        RELOAD("RELOAD"),
        BLACKS("  "),
        PREFS("GAMEPREFS");

        String s;

        SettingsEnum(String s) {
            this.s = s;
        }
    }

}
