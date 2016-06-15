package com.handmadeoctopus.environment;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;


public class Menu {
    Stage stage;
    Array<Actor> menuEntry;
    Skin skin;
    Label.LabelStyle labelStyle;
    TextButton.TextButtonStyle textButtonStyle, textButtonStyleBg;
    Settings settings;

    int numberOfItems = 0;

    float centerX, topY, spacing;
    float z, q;

    public Menu(Stage stage, Skin skin, Settings settings) {
        this.stage = stage;
        this.skin = skin;
        this.settings = settings;
        init();
    }

    public void init() {
        settings.setMenu(this);

        topY = 0.95f * stage.getHeight();
        centerX = 0.5f * stage.getWidth();
        spacing = 0.02f * stage.getHeight();

        menuEntry = stage.getActors();

        setStyle();

        addButton("RESET");
        addSlider("BALLSQUANTITYBG");
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
                settings.set(Settings.SettingsEnum.valueOf(newButton.getName()), x, y, newButton);
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

        final TextButton sliderBackground = new TextButton(Settings.SettingsEnum.valueOf(name).s + ": " + previousHeight(), textButtonStyleBg);
        sliderBackground.setHeight(stage.getHeight()*0.05f);
        sliderBackground.setWidth(stage.getWidth()*0.5f);
        sliderBackground.setPosition(centerX - sliderBackground.getWidth()/2, topY - sliderBackground.getHeight() - previousHeight());

        TextButton leftArrow = new TextButton("-", textButtonStyle);
        leftArrow.setHeight(stage.getHeight()*0.05f);
        leftArrow.setWidth(stage.getHeight()*0.05f);
        leftArrow.setPosition(sliderBackground.getX() - leftArrow.getWidth(), sliderBackground.getY());

        TextButton rightArrow = new TextButton("+", textButtonStyle);
        rightArrow.setHeight(stage.getHeight()*0.05f);
        rightArrow.setWidth(stage.getHeight()*0.05f);
        rightArrow.setPosition(sliderBackground.getX() + sliderBackground.getWidth(), sliderBackground.getY());

        final TextButton slider = new TextButton(" ", textButtonStyle);
        slider.setName(name);
        slider.setHeight(stage.getHeight()*0.05f);
        slider.setWidth(stage.getWidth()*0.025f);
        slider.setPosition(centerX - sliderBackground.getWidth()/2, sliderBackground.getY());
        slider.addListener(new DragListener() {

            public void touchDragged (InputEvent event, float x, float y, int pointer) {
                settings.drag(Settings.SettingsEnum.valueOf(slider.getName()), z*Gdx.input.getX(), y, slider, sliderBackground);
                settings.update();
            }
        });

    //  menuEntry.add(label);
        menuEntry.add(leftArrow);
        menuEntry.add(sliderBackground);
        menuEntry.add(rightArrow);
        menuEntry.add(slider);

        numberOfItems++;
    }

    private float previousHeight() {
        float previousHeight = 0;
        spacing = stage.getHeight()*0.02f;
        for (int i = 0; i < numberOfItems ; i++) {
            previousHeight += stage.getHeight()*0.05f * (i+1) + spacing;
        }
        return previousHeight;
    }



    public void update(float difference) {
        float diff = 0.95f * stage.getHeight() - topY;
        for (int i = 1; i < menuEntry.size; i++) {
            menuEntry.get(i).moveBy(0, diff);
        }
        topY = 0.95f * stage.getHeight();
    }

    private void addListener(final TextButton handledButton) {
        final String name = handledButton.getName();
        handledButton.addListener(new InputListener() {

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                settings.set(Settings.SettingsEnum.valueOf(name), x, y, handledButton);
                settings.update();
                return true;
            }
        });
    }

    public void updateValues() {

    }

    public void resetValues() {

    }


}
