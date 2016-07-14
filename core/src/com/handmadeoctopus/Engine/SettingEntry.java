package com.handmadeoctopus.Engine;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.handmadeoctopus.environment.Menu;
import com.handmadeoctopus.environment.MenuEntry;

// Entry for settings
public class SettingEntry {
    int minVal, maxVal, value;
    boolean valueBool;
    Settings.SettingsEnum settingId;
    Settings settings;
    Menu menu;
    MenuEntry menuEntry;

    public SettingEntry(Settings.SettingsEnum set, Menu menu, Settings settings) {
        this.settings = settings;
        settingId = set;
        this.menu = menu;
        minVal = settingId.min;
        maxVal = settingId.max;
        load();
        check();
        menuEntry = menu.addMenuEntry(this);
    }

    void load() {
        if (!Settings.prefs.getBoolean("SET")) {
            resetDefault();
        }
        value = Settings.prefs.getInteger(settingId.s);
    }

    void resetDefault() {
        value = settingId.def;
    }

    void save() {
        Settings.prefs.putInteger(settingId.s, value);
        Settings.prefs.putBoolean("SET", true);
        Settings.prefs.flush();
    }

    public Settings.SettingsEnum getSettingId() {
        return settingId;
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

    // Set value depending on which button is called.
    public void set(float x, TextButton button, TextButton bgButton, boolean relative, boolean exact) {
        value = setBallsParam(x, button, bgButton, relative, exact, value, minVal, maxVal);
        check();
        action();
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

        bgButton.setText(settingId.s + ": " + (int) newQuant);
        button.setX(diff);

        return (int) newQuant;
    }

    // Checks if gravitation or forces changed.
    void check() {
        if (value != 0) { valueBool = true; }
        else { valueBool = false; }
    }

    public int getValue() { return value; }

    public void setValue(int value) {
        this.value = value;
        check();
        menuEntry.updateValue();
    }

    public boolean getValueBool() { return valueBool; }

    public void action() {
        settings.action(this);
    }
}
