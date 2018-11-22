package com.handmadeoctopus.Engine;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.handmadeoctopus.BouncingBallEngine;
import com.handmadeoctopus.entities.Box;
import com.handmadeoctopus.environment.Menu;
import com.handmadeoctopus.environment.Zoom;

import java.util.HashMap;


// Settings class let you store, load, save and update main settings of the game.
public class Settings {
    // Variables for main settings
    public int ballsQuantity, ballsSize, ballsTail, springiness, gravity, forces, speed, universeScale;
    public boolean gravitation, ballsForces, reset = false;
    float screenRatio = 0.5f;
    Menu menu; // To control and update after change in values
    public Zoom zoom;
    MainScreen mainScreen;
    MainEngine mainEngine;
    public Box box;
    public Age age;
    HashMap<SettingsEnum, SettingEntry> settingMap;

    static Preferences prefs; // Preferences stores, saves and loads games settings


    // Loads prefs when created.
    public Settings(MainScreen mainScreen) {
        this.mainScreen = mainScreen;
        prefs = Gdx.app.getPreferences("GAMEPREFS");
   /*     if (!prefs.getBoolean("SET")) {
            resetDefaults();
        }
        load();*/
    }

    // Sets menu on or of if necessary.
    public void setMenu(Menu menu) {
        this.menu = menu;
        settingMap = new HashMap<SettingsEnum, SettingEntry>();
        for (SettingsEnum set : SettingsEnum.values()) {
            settingMap.put(set, new SettingEntry(set, menu, this));
        }
        universeScale = getSetting(SettingsEnum.UNISCALE).getValue();
    }

    void action(SettingEntry settingEntry) {
        if(mainEngine != null) { mainEngine.action(settingEntry); }
    }

    public SettingEntry getSetting(SettingsEnum set) {
        return settingMap.get(set);
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
      /*  ballsQuantity = prefs.getInteger(SettingsEnum.BALLSQUANTITY.s);
        ballsSize = prefs.getInteger(SettingsEnum.BALLSSIZE.s);
        ballsTail = prefs.getInteger(SettingsEnum.BALLSTAIL.s);
        springiness = prefs.getInteger(SettingsEnum.SPRINGINESS.s);
        gravity = prefs.getInteger(SettingsEnum.GRAVITY.s);
        forces = prefs.getInteger(SettingsEnum.FORCES.s);
        speed = prefs.getInteger(SettingsEnum.SPEED.s);
        universeScale = prefs.getInteger(SettingsEnum.UNISCALE.s);*/
        for(SettingEntry se : settingMap.values()) {
            se.save();
        }
        update();
    }

    // Saves variables to memory.
    void save() {
   /*     prefs.putInteger(SettingsEnum.BALLSQUANTITY.s, ballsQuantity);
        prefs.putInteger(SettingsEnum.BALLSSIZE.s, ballsSize);
        prefs.putInteger(SettingsEnum.BALLSTAIL.s, ballsTail);
        prefs.putInteger(SettingsEnum.SPRINGINESS.s, springiness);
        prefs.putInteger(SettingsEnum.GRAVITY.s, gravity);
        prefs.putInteger(SettingsEnum.FORCES.s, forces);
        prefs.putInteger(SettingsEnum.SPEED.s, speed);
        prefs.putInteger(SettingsEnum.UNISCALE.s, universeScale);
        prefs.putBoolean("SET", true);
        prefs.flush();*/
        for(SettingEntry se : settingMap.values()) {
            se.save();
        }
//        age.writer.close();
    }

    // Resets all all values to defaults
    public void resetDefaults() {
      /*  ballsQuantity = 100;
        ballsSize = 10;
        ballsTail = 10;
        springiness = 100;
        gravity = 0;
        forces = 10;
        speed = 1;
        universeScale = 10;*/

        for(SettingEntry se : settingMap.values()) {
            se.resetDefault();
            se.check();
        }

        save();
        update();
    }

    // Updates values on menu
    private void update() {
        if (menu != null) { menu.updateValues(); }
    }

    // Sets menu on or off
    void setMenuVisible(boolean on) {
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

    void buttonDown(int keycode) {
        mainEngine.buttonDown(keycode);
    }


    // Enum with all variables and button names.
    public enum SettingsEnum {
        RESET("RESET", 0, 0 ,0, true, 0),
        BALLSQUANTITY("BALLS' QUANTITY", 0, 50, 500, true, 1),
        BALLSSIZE("BALLS' SIZE", 1, 10, 50, true, 2),
        BALLSTAIL("BALLS' TAIL", 0, 10, 100, true, 3),
        SPRINGINESS("SPRINGINESS %", 0, 100, 110, true, 4),
        GRAVITY("GRAVITY %", -100, 0, 100, true, 5),
        FORCES("FORCES %", -200, 10, 200, true, 6),
        SPEED("SPEED", 0, 1, 500, false, 7),
        UNISCALE("SCALE OF UNIVERSE", 1, 10, 1000, true, 8),
        RELOAD("RELOAD", 0, 0, 0, true, 9);


        public String s;
        public int min, def, max, id;
        public boolean flush;

        SettingsEnum(String s, int min, int def, int max, boolean flush, int id) {
            this.s = s;
            this.min = min;
            this.def = def;
            this.max = max;
            this.flush = flush;
            this.id = id;
        }
    }

}
