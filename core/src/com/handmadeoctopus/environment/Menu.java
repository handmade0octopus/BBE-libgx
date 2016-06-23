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


public class Menu {
    Stage stage;
    Array<TextButton> menuEntry;
    Array<Actor> stageEntry;
    Skin skin;
    Label.LabelStyle labelStyle;
    TextButton.TextButtonStyle textButtonStyle, textButtonStyleBg;
    Settings settings;
    SlidingMenu slidingMenu;


    int numberOfItems = 0;

    float centerX, topY, spacing;
    float z, q;

    public Menu(Stage stage, Skin skin, Settings settings, TextButton menuButton, SlidingMenu slidingMenu) {
        this.stage = stage;
        this.skin = skin;
        this.settings = settings;
        this.slidingMenu = slidingMenu;
        init(menuButton);
    }

    public void init(TextButton menuButton) {
        settings.setMenu(this);

        topY = 0.95f * stage.getHeight();
        centerX = 0.5f * stage.getWidth();

        stageEntry = stage.getActors();
        menuEntry = new Array<TextButton>();

        setStyle();
        stage.addActor(newBlackScreen("BLACKS"));

        stage.addActor(menuButton);

        addButton("RESET");
        addSlider("BALLSQUANTITY");
        addSlider("BALLSSIZE");
        addSlider("BALLSTAIL");
        addSlider("SPRINGINESS");
        addSlider("GRAVITY");
        addSlider("FORCES");
        addButton("RELOAD");



        for (TextButton button : menuEntry) {
            stageEntry.add(button);
        }


        fadeOut(0f);
        updateValues();
    }

    private void setStyle() {
        labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default");
        skin.add("default", labelStyle);

        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white", Color.GRAY);
        // textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
        // textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
        textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);

        textButtonStyle.font = skin.getFont("default");

        skin.add("default", textButtonStyle);


        textButtonStyleBg = new TextButton.TextButtonStyle();
        textButtonStyleBg.up = skin.newDrawable("white", Color.DARK_GRAY);
        textButtonStyleBg.down = skin.newDrawable("white", Color.DARK_GRAY);
        // textButtonStyleBg.checked = skin.newDrawable("white", Color.BLUE);
        // textButtonStyleBg.over = skin.newDrawable("white", Color.LIGHT_GRAY);

        textButtonStyleBg.font = skin.getFont("default");
        skin.add("default", textButtonStyleBg);
    }

    private void addButton(String name) {
        final TextButton newButton = new TextButton(Settings.SettingsEnum.valueOf(name).s, textButtonStyle);
        newButton.setHeight(stage.getHeight()*0.05f);
        newButton.setWidth(stage.getWidth()*0.075f);
        newButton.setName(name);
        newButton.setPosition(centerX - newButton.getWidth()/2, (topY - newButton.getHeight()) - previousHeight());

        newButton.addListener(new InputListener() {

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                settings.set(x, newButton, null, false);
                settings.update();
                return true;
            }
        });


        menuEntry.add(newButton);
        numberOfItems++;
    }

    private void addSlider(String name) {


    /*  Label label = new Label(name, labelStyle);
        label.setText(name + ": " + previousHeight);
        label.setHeight(stage.getHeight()*0.05f);
        label.setWidth(stage.getWidth()*0.075f);
        label.setPosition(centerX - label.getWidth()/2, (topY - label.getHeight()) - previousHeight - stage.getHeight()*0.01f); */

        final TextButton sliderBackground = new TextButton(Settings.SettingsEnum.valueOf(name).s + ": " + settings.ballsQuantity, textButtonStyleBg);
        sliderBackground.setName(name);
        sliderBackground.setHeight(stage.getHeight()*0.05f);
        sliderBackground.setWidth(stage.getWidth()*0.5f);
        sliderBackground.setPosition(centerX - sliderBackground.getWidth()/2, topY - sliderBackground.getHeight() - previousHeight());


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

        sliderBackground.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                settings.set(z*Gdx.input.getX() > slider.getX() ? +10 : -10 , slider, sliderBackground, true);
                return true;
            }
        });



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

        leftArrow.getLabel().setFontScale(2);
        rightArrow.getLabel().setFontScale(2);


    //  menuEntry.add(label);
        menuEntry.add(leftArrow);
        menuEntry.add(sliderBackground);
        menuEntry.add(rightArrow);
        menuEntry.add(slider);

        numberOfItems++;
    }

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

    boolean scrolling = false;
    float startY, movedBy = 0;

    private void setPointY(float y, boolean scroll) {
        if (!scrolling) {
            startY = y;
        }
        scrolling = scroll;
    }

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

            for (int i = 2; i < stageEntry.size; i++) {
                stageEntry.get(i).addAction(Actions.moveBy(0, newY, 0));
            }
            startY = y;
            movedBy += newY;
        }
    }

    private float previousHeight() {
        float previousHeight = 0;
        spacing = stage.getHeight()*0.075f;
        for (int i = 0; i < numberOfItems ; i++) {
            previousHeight += spacing;
        }
        return previousHeight;
    }

    public void update() {
        float diff = 0.95f * stage.getHeight() - topY;
        for (int i = 2; i < stageEntry.size; i++) {
            stageEntry.get(i).moveBy(0, diff);
        }
        topY = 0.95f * stage.getHeight();
    }

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

    public void resetValues() {
        settings.resetDefaults();
    }

    public void fadeOut(float time) {
        for (int i = 2; i < stageEntry.size; i++) {
            stageEntry.get(i).addAction(Actions.fadeOut(time));
        }
        stageEntry.get(0).addAction(Actions.fadeOut(time));
        stageEntry.get(1).addAction(Actions.alpha(0.5f, time));
    }

    public void fadeIn(float time) {
        for (int i = 0; i < stageEntry.size; i++) {
            stageEntry.get(i).addAction(Actions.fadeIn(time));
            if (i > 1) if (menuEntry.get(i-2).getText().toString().equals(" ")) {
                stageEntry.get(i).addAction(Actions.alpha(0.5f, time));
            }
        }
        stageEntry.get(0).addAction(Actions.alpha(0.8f, time));
    }

    public void setMenu(boolean on) {
        slidingMenu.setMenu(on);
    }
}
