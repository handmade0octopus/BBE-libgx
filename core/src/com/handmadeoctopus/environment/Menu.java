package com.handmadeoctopus.environment;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.handmadeoctopus.Engine.SettingEntry;
import com.handmadeoctopus.Engine.Settings;

import java.util.HashMap;

/* Class that contains all menu buttons,
also includes Menu entrance button from SlidingMenu. Mostly based on LibGDX stage
 */

public class Menu {
    static Stage stage; // Stage carried out from main class.
    Array<TextButton> menuEntry; // Array which keeps all menu items at TextButtons
    Array<Actor> stageEntry; // Array from stage, you can use it to remove buttons or change them but only as an Actors.
    Skin skin; // Skin for buttons
    Label.LabelStyle labelStyle; // Style of label for text buttons
    static TextButton.TextButtonStyle textButtonStyle, textButtonStyleBg; // Style for textbutton and background
    Settings settings; // Main setting object
    SlidingMenu slidingMenu; // Sliding menu - only carried to call setMenu() on or off
    TextButton blackBg;
    HashMap<Settings.SettingsEnum, MenuEntry> menuEntries;

    int numberOfItems = 0; // this variable calculates height for next button.
    static float previousHeight = 0;

    static float centerX, topY, spacing; // variables that help position buttons
    static float z, q; // size of menu boundries.

    public Menu(Stage stage, Skin skin, Settings settings, TextButton menuButton, SlidingMenu slidingMenu) {
        this.stage = stage;
        this.skin = skin;
        this.settings = settings;
        this.slidingMenu = slidingMenu;
        init(menuButton);
    }

    private void init(TextButton menuButton) {


        // Sets topY and centreX which are % of total screen size
        topY = 0.95f * stage.getHeight();
        centerX = 0.5f * stage.getWidth();
        spacing = 0.025f*stage.getHeight();

        // Getting actors from stage and creating menuEntry which is mirrored stageEntry
        stageEntry = stage.getActors();
        menuEntry = new Array<TextButton>();

        // Sets style for buttons
        setStyle();

        // Adds black screen and "Menu" button to stage.
        stage.addActor(blackBg = newBlackScreen(" "));
        stage.addActor(menuButton);

        menuEntries = new HashMap<Settings.SettingsEnum, MenuEntry>();

        // sets menu for settings class.
        settings.setMenu(this);

        float lastY = stageEntry.get(stageEntry.size - 1).getY();
        float topYh = stageEntry.get(2).getY() + stageEntry.get(2).getHeight();
        float diff = -(topYh + lastY - stage.getHeight())/2;
        for(MenuEntry me : menuEntries.values()) {
            me.updatePosition(diff);
        }

        // Fade outs whole menu and updatePosition their values.
        fadeOut(0f);
        updateValues();
    }

    private void setStyle() {
        // Label style for buttons.
        labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        skin.add("default", labelStyle);

        // Text button styles set up
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.GRAY);
        // textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        // textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
        textButtonStyle.font = skin.getFont("default");

        skin.add("default", textButtonStyle);

        // Background style for buttons
        textButtonStyleBg = new TextButton.TextButtonStyle();
        textButtonStyleBg.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyleBg.down = skin.newDrawable("white", Color.DARK_GRAY);
        // textButtonStyleBg.checked = skin.newDrawable("white", Color.BLUE);
        // textButtonStyleBg.over = skin.newDrawable("white", Color.LIGHT_GRAY);

        textButtonStyleBg.font = skin.getFont("default");
        skin.add("default", textButtonStyleBg);
    }

    // Black screen is background of menu
    TextButton newBlackScreen(String name) {
        TextButton.TextButtonStyle blackScreenStyle = new TextButton.TextButtonStyle();
        blackScreenStyle.up = skin.newDrawable("white", Color.BLACK);
        blackScreenStyle.font = skin.getFont("default");

        skin.add("default", blackScreenStyle);

        final TextButton newButton = new TextButton((name), blackScreenStyle);
        newButton.setHeight(stage.getHeight());
        newButton.setWidth(stage.getWidth());
        newButton.setName(name);
        newButton.setPosition(0, 0);

        // This moves whole menu up and down if necessary.
        newButton.addListener(new DragListener() {
            public void touchDragged (InputEvent event, float x, float y, int pointer) {
                setPointY(y, true);
                transformY(y, false);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                setPointY(y, false);
                transformY(y, false);
            }
        });

        return newButton;
    }

    // For setPointY and transformY class
    private boolean scrolling = false;
    private float startY, movedBy = 0;

    // Sets point for start of scrolling
    private void setPointY(float y, boolean scroll) {
        if (!scrolling) {
            startY = y;
        }
        scrolling = scroll;
    }

    // Moves stage up and down
    private void transformY(float y, boolean end) {
        if (scrolling) {
            float newY = (y - startY)/2;
            if (movedBy >= 800 && newY > 0) {
                newY = 0;
            } else if (movedBy <= -800 && newY < 0) {
                newY = 0;
            } else if (end && movedBy < 10 && movedBy > - 10) {
                newY = -movedBy;
                movedBy = 0;
            }

            for(MenuEntry me : menuEntries.values()) {
                me.updatePosition(newY);
            }
            startY = y;
            movedBy += newY;
        }
    }

    // Updates menu position after resize.
    void updatePosition() {
        float diff = 0.95f * stage.getHeight() - topY;
       /* for (int i = 2; i < stageEntry.size-2; i++) {
            stageEntry.get(i).moveBy(0, diff);
        }*/

        for(MenuEntry me : menuEntries.values()) {
            me.updatePosition(diff);
        }

        stageEntry.get(0).moveBy(0, diff);
        blackBg.setHeight(stage.getViewport().getWorldHeight());
        blackBg.setPosition(0, 0);
        topY = 0.95f * stage.getHeight();
        centerX = 0.5f * stage.getWidth();
        spacing = 0.025f*stage.getHeight();
    }

    // Updates menu values.
    public void updateValues() {
        for(MenuEntry me : menuEntries.values()) {
            me.updateValue();
        }

        /*for (int i = 2; i < menuEntry.size; i++) {
            if (menuEntry.get(i).getText().toString().equals(" ")) {
                for (int d = 2; d < menuEntry.size; d++) {
                    if (menuEntry.get(i).getName().equals(menuEntry.get(d).getName())
                            && !menuEntry.get(d).getText().toString().equals(" ")
                            && !menuEntry.get(d).getText().toString().equals("+")
                            && !menuEntry.get(d).getText().toString().equals("-"))  {
                        settings.set(settings.getVar(menuEntry.get(i)), menuEntry.get(i), menuEntry.get(d));
                    }
                }
            }
        }*/
    }

    // Reset menu values.
    public void resetValues() {
        settings.resetDefaults();
    }

    // Fades out menu if called
    void fadeOut(float time) {
        for(MenuEntry me : menuEntries.values()) {
            me.fadeOut(time);
        }
        stageEntry.get(0).addAction(Actions.fadeOut(time));
        stageEntry.get(1).addAction(Actions.alpha(0.5f, time));
    }

    // Fades in menu if called
    void fadeIn(float time) {
    /*    for (int i = 0; i < menuEntry.size+2; i++) {
            stageEntry.get(i).addAction(Actions.fadeIn(time));
            if (i > 1) if (menuEntry.get(i-2).getText().toString().equals(" ")) {
                stageEntry.get(i).addAction(Actions.alpha(0.5f, time));
            }
        }*/


        for(MenuEntry me : menuEntries.values()) {
            me.fadeIn(time);
       //     if(me.getButton(1) != null) { me.getButton(1).addAction(Actions.alpha(0.5f, time)); }
        }

        stageEntry.get(1).addAction(Actions.fadeIn(time));
        stageEntry.get(0).addAction(Actions.alpha(0.8f, time));
    }

    // Sets menu on or off
    public void setMenu(boolean on) {
        slidingMenu.setMenu(on);
    }

    public MenuEntry addMenuEntry(SettingEntry settingEntry) {
        MenuEntry newMenuEntry = new MenuEntry(settingEntry);
        menuEntries.put(settingEntry.getSettingId(), newMenuEntry);
        return newMenuEntry;
    }
}
