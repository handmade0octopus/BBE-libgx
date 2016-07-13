package com.handmadeoctopus.Engine;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.handmadeoctopus.BouncingBallEngine;
import com.handmadeoctopus.entities.Box;
import com.handmadeoctopus.environment.Menu;
import com.handmadeoctopus.environment.Zoom;


// Settings class let you store, load, save and update main settings of the game.
public class Settings {
    // Variables for main settings
    public int ballsQuantity, ballsSize, ballsTail, springiness, gravity, forces, speed, universeScale;
    public boolean gravitation, ballsForces, reset = false;
    float screenRatio = 0.5f;
    Menu menu; // To control and update after change in values
    public Zoom zoom;
    MainEngine mainEngine;
    public Box box;
    public Age age;

    Preferences prefs; // Preferences stores, saves and loads games settings

    // Maximum and minimum values of settings
    public static final float MIN_QUANT = 0,
            MAX_QUANT = 500,
            MIN_SIZE = 1,
            MAX_SIZE = 50,
            MIN_TAIL = 0,
            MAX_TAIL = 100,
            MIN_SPRINGINESS = 0,
            MAX_SPRINGINESS = 110,
            MIN_GRAVITY = -50,
            MAX_GRAVITY = 50,
            MIN_FORCES = -200,
            MAX_FORCES = 200,
            MIN_SPEED = 0,
            MAX_SPEED = 5,
            MIN_UNI = 1,
            MAX_UNI = 1000;


    // Loads prefs when created.
    public Settings() {
        prefs = Gdx.app.getPreferences(SettingsEnum.PREFS.s);
        if (!prefs.getBoolean("SET")) {
            resetDefaults();
        }
        load();
    }

    // Sets all values if necessary.
    public void set(int ballsQuantity, int ballsSize, int ballsTail, int springiness, int gravity, int forces, int universeScale) {
        this.ballsQuantity = ballsQuantity;
        this.ballsSize = ballsSize;
        this.ballsTail = ballsTail;
        this.springiness = springiness;
        this.gravity = gravity;
        this.forces = forces;
        this.universeScale = universeScale;
        save();
        update();
    }

    // Loads all variables from memory
    void load() {
        ballsQuantity = prefs.getInteger(SettingsEnum.BALLSQUANTITY.s);
        ballsSize = prefs.getInteger(SettingsEnum.BALLSSIZE.s);
        ballsTail = prefs.getInteger(SettingsEnum.BALLSTAIL.s);
        springiness = prefs.getInteger(SettingsEnum.SPRINGINESS.s);
        gravity = prefs.getInteger(SettingsEnum.GRAVITY.s);
        forces = prefs.getInteger(SettingsEnum.FORCES.s);
        speed = prefs.getInteger(SettingsEnum.SPEED.s);
        universeScale = prefs.getInteger(SettingsEnum.UNISCALE.s);
        update();
    }

    // Saves variables to memory.
    void save() {
        prefs.putInteger(SettingsEnum.BALLSQUANTITY.s, ballsQuantity);
        prefs.putInteger(SettingsEnum.BALLSSIZE.s, ballsSize);
        prefs.putInteger(SettingsEnum.BALLSTAIL.s, ballsTail);
        prefs.putInteger(SettingsEnum.SPRINGINESS.s, springiness);
        prefs.putInteger(SettingsEnum.GRAVITY.s, gravity);
        prefs.putInteger(SettingsEnum.FORCES.s, forces);
        prefs.putInteger(SettingsEnum.SPEED.s, speed);
        prefs.putInteger(SettingsEnum.UNISCALE.s, universeScale);
        prefs.putBoolean("SET", true);
        prefs.flush();

//        age.writer.close();
    }

    // Resets all all values to defaults
    public void resetDefaults() {
        ballsQuantity = 100;
        ballsSize = 10;
        ballsTail = 10;
        springiness = 100;
        gravity = 0;
        forces = 10;
        speed = 1;
        universeScale = 10;
        save();
        update();
    }

    // Sets menu on or of if necessary.
    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    // Updates values on menu
    public void update() {
        check();

        if (menu != null) { menu.updateValues(); }
    }

    // Checks if gravitation or forces changed.
    private void check() {
        if (forces != 0 ) { ballsForces = true; }
        else { ballsForces = false; }
        if (gravity != 0) { gravitation = true; }
        else { gravitation = false; }
    }

    // Set value depending on which button is called.
    public void set(float x, TextButton button, TextButton bgButton, boolean relative, boolean exact) {
        switch(SettingsEnum.valueOf(button.getName())) {
            case RESET: menu.resetValues(); break;
            case BALLSQUANTITY:
                ballsQuantity = setBallsParam(x, button, bgButton, relative, exact, ballsQuantity, MIN_QUANT, MAX_QUANT);
                break;
            case BALLSSIZE:
                ballsSize = setBallsParam(x, button, bgButton, relative, exact, ballsSize, MIN_SIZE, MAX_SIZE);
                break;
            case BALLSTAIL:
                ballsTail = setBallsParam(x, button, bgButton, relative, exact, ballsTail, MIN_TAIL, MAX_TAIL);
                break;
            case SPRINGINESS:
                springiness = setBallsParam(x, button, bgButton, relative, exact, springiness, MIN_SPRINGINESS, MAX_SPRINGINESS);
                break;
            case GRAVITY:
                gravity = setBallsParam(x, button, bgButton, relative, exact, gravity, MIN_GRAVITY, MAX_GRAVITY);
                break;
            case FORCES:
                forces = setBallsParam(x, button, bgButton, relative, exact, forces, MIN_FORCES, MAX_FORCES);
                break;
            case SPEED:
                speed = setBallsParam(x, button, bgButton, relative, exact, speed, MIN_SPEED, MAX_SPEED);
                break;
            case UNISCALE:
                universeScale = setBallsParam(x, button, bgButton, relative, exact, universeScale, MIN_UNI, MAX_UNI);
                break;
            case RELOAD:
                if(mainEngine != null) { mainEngine.reload(); }
                break;
        }
        check();
        if(mainEngine != null) { mainEngine.update(SettingsEnum.valueOf(button.getName())); }
    }

    // Use when you want to increase or decrease by certain value
    public void set(float x, TextButton button, TextButton bgButton, boolean relative) {
        set(x, button, bgButton, relative, false);
    }

    // Use when you want to set certain value.
    public void set(float x, TextButton button, TextButton bgButton) {
        set(x, button, bgButton, false, true);
    }


    // Sets parameters depending on what you put into this function
    private int setBallsParam(float newValue, TextButton button, TextButton bgButton, boolean relative,
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

    // Called when input is dragged.
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

    // Sets or loads variables do not use outside class
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
            case SPEED:
                if(set) {
                    speed = (int) value;
                    return 0;
                } else { return speed; }
            case UNISCALE:
                if(set) {
                    universeScale = (int) value;
                    return 0;
                } else { return universeScale; }
            default: return 0;
        }
    }

    // Sets variable
    public void setVar(TextButton button, float value){
        var(SettingsEnum.valueOf(button.getName()), true, value);
        update();
    }

    // Gets variable
    public float getVar(TextButton button) {
        return var(SettingsEnum.valueOf(button.getName()), false, 0);
    }

    // Sets menu on or off
    void setMenu(boolean on) {
        menu.setMenu(on);
    }

    // Sets zoom class to this one
    void setZoom(Zoom zoom) {
        this.zoom = zoom;
    }

    // Resets zoom if necessary
    public void resetZoom() {
        zoom.reset();
    }

    void setMainEngine(MainEngine mainEngine) {
        this.mainEngine = mainEngine;
    }

    void setBox(Box box) {
        this.box = box;
    }

    void setAge(Age age) {
        this.age = age;
    }

    void setUniScale(float f) {
        this.screenRatio = f;
        int x = (int) (-BouncingBallEngine.WIDTH*universeScale);
        int y = (int) (-BouncingBallEngine.WIDTH*f*universeScale);
        int wi = (int) (BouncingBallEngine.WIDTH*universeScale*2 + BouncingBallEngine.WIDTH/2);
        int he = (int) (BouncingBallEngine.WIDTH*f*universeScale*2 + BouncingBallEngine.WIDTH/2);
        zoom.setWorldBounds(x, y, wi, he);
        box.set(x, y, x, wi, he, wi);
    }

    void setUniScale() {
        setUniScale(screenRatio);
    }


    // Enum with all variables and button names.
    public enum SettingsEnum {
        RESET("RESET"),
        BALLSQUANTITY("BALLS' QUANTITY"),
        BALLSSIZE("BALLS' SIZE"),
        BALLSTAIL("BALLS' TAIL"),
        SPRINGINESS("SPRINGINESS %"),
        GRAVITY("GRAVITY %"),
        FORCES("FORCES %"),
        RELOAD("RELOAD"),
        SPEED("SPEED"),
        UNISCALE("SCALE OF UNIVERSE"),
        BLACKS("  "),
        PREFS("GAMEPREFS");

        public String s;

        SettingsEnum(String s) {
            this.s = s;
        }
    }

}
