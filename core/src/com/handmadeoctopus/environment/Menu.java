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
import com.handmadeoctopus.Engine.Settings;

/* Class that contains all menu buttons,
also includes Menu entrance button from SlidingMenu
 */

public class Menu {
    Stage stage; // Stage carried out from main class.
    Array<TextButton> menuEntry; // Array which keeps all menu items at TextButtons
    Array<Actor> stageEntry; // Array from stage, you can use it to remove buttons or change them but only as an Actors.
    Skin skin; // Skin for buttons
    Label.LabelStyle labelStyle; // Style of label for text buttons
    TextButton.TextButtonStyle textButtonStyle, textButtonStyleBg; // Style for textbutton and background
    Settings settings; // Main setting object
    SlidingMenu slidingMenu; // Sliding menu - only carried to call setMenu() on or off
    TextButton blackBg;

    int numberOfItems = 0; // this variable calculates height for next button.

    float centerX, topY, spacing; // variables that help position buttons
    float z, q; // size of menu boundries.

    public Menu(Stage stage, Skin skin, Settings settings, TextButton menuButton, SlidingMenu slidingMenu) {
        this.stage = stage;
        this.skin = skin;
        this.settings = settings;
        this.slidingMenu = slidingMenu;
        init(menuButton);
    }

    public void init(TextButton menuButton) {
        // sets menu for settings class.
        settings.setMenu(this);

        // Sets topY and centreX which are % of total screen size
        topY = 0.95f * stage.getHeight();
        centerX = 0.5f * stage.getWidth();

        // Getting actors from stage and creating menuEntry which is mirrored stageEntry
        stageEntry = stage.getActors();
        menuEntry = new Array<TextButton>();

        // Sets style for buttons
        setStyle();

        // Adds black screen and "Menu" button to stage.
        stage.addActor(blackBg = newBlackScreen("BLACKS"));
        stage.addActor(menuButton);


        // Adds buttons.
        addButton("RESET");
        addSlider("BALLSQUANTITY");
        addSlider("BALLSSIZE");
        addSlider("BALLSTAIL");
        addSlider("SPRINGINESS");
        addSlider("GRAVITY");
        addSlider("FORCES");
        addSlider("SPEED");
        addSlider("UNISCALE");
        addButton("RELOAD");

        float lastY = stageEntry.get(stageEntry.size - 1).getY();
        float topYh = stageEntry.get(2).getY() + stageEntry.get(2).getHeight();
        float diff = (topYh + lastY - stage.getHeight())/2;
        for (int i = 2; i < stageEntry.size; i++) {
            stageEntry.get(i).moveBy(0, -diff);
        }

        // Fade outs whole menu and update their values.
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

    private void addButton(String name) {
        // First creates a button with position, previousHeight() checks how many buttons are before this one
        final TextButton newButton = new TextButton(Settings.SettingsEnum.valueOf(name).s, textButtonStyle);
        newButton.setHeight(stage.getHeight()*0.05f);
        newButton.setWidth(stage.getWidth()*0.075f);
        newButton.setName(name);
        newButton.setPosition(centerX - newButton.getWidth()/2, (topY - newButton.getHeight()) - previousHeight());

        // Adds listener which performs action in Settings class. Button is discovered by name string
        newButton.addListener(new InputListener() {

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                settings.set(x, newButton, null, false);
                settings.update();
                return true;
            }
        });

        // Adds button to menuEntry and stageEntry and also increases numbers of items for previousHeight()
        menuEntry.add(newButton);
        numberOfItems++;
        stageEntry.add(newButton);
    }

    private void addSlider(String name) {
        // Adds slider, label is unnecessary but I left it in case I would want to do something with it.

    /*  Label label = new Label(name, labelStyle);
        label.setText(name + ": " + previousHeight);
        label.setHeight(stage.getHeight()*0.05f);
        label.setWidth(stage.getWidth()*0.075f);
        label.setPosition(centerX - label.getWidth()/2, (topY - label.getHeight()) - previousHeight - stage.getHeight()*0.01f); */

        // Firsts adds button that is background of whole slider.
        final TextButton sliderBackground = new TextButton(Settings.SettingsEnum.valueOf(name).s + ": " + settings.ballsQuantity, textButtonStyleBg);
        sliderBackground.setName(name);
        sliderBackground.setHeight(stage.getHeight()*0.05f);
        sliderBackground.setWidth(stage.getWidth()*0.5f);
        sliderBackground.setPosition(centerX - sliderBackground.getWidth()/2, topY - sliderBackground.getHeight() - previousHeight());

        // Then adds small slider itself.
        final TextButton slider = new TextButton(" ", textButtonStyle);
        slider.setName(name);
        slider.setHeight(stage.getHeight()*0.05f);
        slider.setWidth(stage.getWidth()*0.025f);
        slider.setPosition(centerX - sliderBackground.getWidth()/2, sliderBackground.getY());
        slider.addListener(new DragListener() {
            public void touchDragged (InputEvent event, float x, float y, int pointer) {
                settings.drag(z*Gdx.input.getX(), slider, sliderBackground);
            }
        });

        // Listener which checks which side from small slider is clicked, and then adds or subtracts
        sliderBackground.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                settings.set(z*Gdx.input.getX() > slider.getX() ? +10 : -10 , slider, sliderBackground, true);
                return true;
            }
        });


        // Left arrow of slider with listener
        final TextButton leftArrow = new TextButton("-", textButtonStyle);
        leftArrow.setName(name);
        leftArrow.setHeight(stage.getHeight()*0.05f);
        leftArrow.setWidth(stage.getHeight()*0.05f);
        leftArrow.setPosition(sliderBackground.getX() - leftArrow.getWidth(), sliderBackground.getY());
        leftArrow.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                settings.set(-1f, slider, sliderBackground, true);
                return true;
            }
        });

        // Right arrow of slider with listener
        final TextButton rightArrow = new TextButton("+", textButtonStyle);
        rightArrow.setName(name);
        rightArrow.setHeight(stage.getHeight()*0.05f);
        rightArrow.setWidth(stage.getHeight()*0.05f);
        rightArrow.setPosition(sliderBackground.getX() + sliderBackground.getWidth(), sliderBackground.getY());
        rightArrow.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                settings.set(1f, slider, sliderBackground, true);
                return true;
            }
        });


        // Scaling button arrows a little
        leftArrow.getLabel().setFontScale(2);
        rightArrow.getLabel().setFontScale(2);


    //  menuEntry.add(label);
        menuEntry.add(leftArrow);
        menuEntry.add(sliderBackground);
        menuEntry.add(rightArrow);
        menuEntry.add(slider);

        stageEntry.add(leftArrow);
        stageEntry.add(sliderBackground);
        stageEntry.add(rightArrow);
        stageEntry.add(slider);

        numberOfItems++;
    }

    // Black screen is background of menu
    TextButton newBlackScreen(String name) {
        TextButton.TextButtonStyle blackScreenStyle = new TextButton.TextButtonStyle();
        blackScreenStyle.up = skin.newDrawable("white", Color.BLACK);
        blackScreenStyle.font = skin.getFont("default");

        skin.add("default", blackScreenStyle);

        final TextButton newButton = new TextButton(Settings.SettingsEnum.valueOf(name).s, blackScreenStyle);
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
    boolean scrolling = false;
    float startY, movedBy = 0;

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
            if (movedBy >= 500 && newY > 0) {
                newY = 0;
            } else if (movedBy <= -500 && newY < 0) {
                newY = 0;
            } else if (end && movedBy < 10 && movedBy > - 10) {
                newY = -movedBy;
                movedBy = 0;
            }

            for (int i = 2; i < stageEntry.size-2; i++) {
                stageEntry.get(i).addAction(Actions.moveBy(0, newY, 0));
            }
            startY = y;
            movedBy += newY;
        }
    }

    // Returns height of previous buttons.
    private float previousHeight() {
        float previousHeight = 0;
        spacing = stage.getHeight()*0.075f;
        for (int i = 0; i < numberOfItems ; i++) {
            previousHeight += spacing;
        }
        return previousHeight;
    }

    // Updates menu position after resize.
    public void update() {
        float diff = 0.95f * stage.getHeight() - topY;
        for (int i = 2; i < stageEntry.size; i++) {
            stageEntry.get(i).moveBy(0, diff);
        }
        stageEntry.get(0).moveBy(0, diff);
        blackBg.setHeight(stage.getViewport().getWorldHeight());
        blackBg.setPosition(0, 0);
        topY = 0.95f * stage.getHeight();
    }

    // Updates menu values.
    public void updateValues() {
        for (int i = 2; i < menuEntry.size; i++) {
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
        }
    }

    // Reset menu values.
    public void resetValues() {
        settings.resetDefaults();
    }

    // Fades out menu if called
    public void fadeOut(float time) {
        for (int i = 2; i < menuEntry.size+2; i++) {
            stageEntry.get(i).addAction(Actions.fadeOut(time));
        }
        stageEntry.get(0).addAction(Actions.fadeOut(time));
        stageEntry.get(1).addAction(Actions.alpha(0.5f, time));
    }

    // Fades in menu if called
    public void fadeIn(float time) {
        for (int i = 0; i < menuEntry.size+2; i++) {
            stageEntry.get(i).addAction(Actions.fadeIn(time));
            if (i > 1) if (menuEntry.get(i-2).getText().toString().equals(" ")) {
                stageEntry.get(i).addAction(Actions.alpha(0.5f, time));
            }
        }
        stageEntry.get(0).addAction(Actions.alpha(0.8f, time));
    }

    // Sets menu on or off
    public void setMenu(boolean on) {
        slidingMenu.setMenu(on);
    }
}
