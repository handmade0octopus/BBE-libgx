package com.handmadeoctopus.environment;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Array;
import com.handmadeoctopus.Engine.SettingEntry;
import com.handmadeoctopus.Engine.Settings;


public class MenuEntry {
    private Settings.SettingsEnum menuEntryId;
    private SettingEntry linkedSettingEntry;
    private Array<TextButton> menuEntry = new Array<TextButton>();
    private int currentValue;

    MenuEntry(SettingEntry settingEntry) {
        menuEntryId = settingEntry.getSettingId();
        linkedSettingEntry = settingEntry;
        if(menuEntryId.def == menuEntryId.min && menuEntryId.def == menuEntryId.max) {
            initButton(menuEntryId);
        } else { initSlider(menuEntryId); }
        for(TextButton button : menuEntry) {
            Menu.stage.addActor(button);
        }
        addHeight();
        updateValue();
    }

    // This is probably worst looking but best working part of the code
    private void initSlider(Settings.SettingsEnum menuEntryId) {
        // Adds slider, label is unnecessary but I left it in case I would want to do something with it.

    /*  Label label = new Label(name, labelStyle);
        label.setText(name + ": " + previousHeight);
        label.setHeight(stage.getHeight()*0.05f);
        label.setWidth(stage.getWidth()*0.075f);
        label.setPosition(centerX - label.getWidth()/2, (topY - label.getHeight()) - previousHeight - stage.getHeight()*0.01f); */

        // Firsts adds button that is background of whole slider.
        final TextButton sliderBackground = new TextButton(menuEntryId.s + ": " + linkedSettingEntry.getValue(), Menu.textButtonStyleBg);
        sliderBackground.setName(menuEntryId.s);
        sliderBackground.setHeight(Menu.stage.getHeight()*0.05f);
        sliderBackground.setWidth(Menu.stage.getWidth()*0.5f);
        sliderBackground.setPosition(Menu.centerX - sliderBackground.getWidth()/2, Menu.topY - sliderBackground.getHeight() - Menu.previousHeight);

        // Then adds small slider itself.
        final TextButton slider = new TextButton(" ", Menu.textButtonStyle);
        slider.setName(menuEntryId.s);
        slider.setHeight(Menu.stage.getHeight()*0.05f);
        slider.setWidth(Menu.stage.getWidth()*0.025f);
        slider.setPosition(Menu.centerX - sliderBackground.getWidth()/2, sliderBackground.getY());
        slider.addListener(new DragListener() {
            public void touchDragged (InputEvent event, float x, float y, int pointer) {
                linkedSettingEntry.drag(Menu.z* Gdx.input.getX(), slider, sliderBackground);
            }
        });

        // Listener which checks which side from small slider is clicked, and then adds or subtracts
        sliderBackground.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                linkedSettingEntry.set(Menu.z*Gdx.input.getX() > slider.getX() ? +10 : -10 , slider, sliderBackground, true);
                return true;
            }
        });


        // Left arrow of slider with listener
        final TextButton leftArrow = new TextButton("-", Menu.textButtonStyle);
        leftArrow.setName(menuEntryId.s);
        leftArrow.setHeight(Menu.stage.getHeight()*0.05f);
        leftArrow.setWidth(Menu.stage.getHeight()*0.05f);
        leftArrow.setPosition(sliderBackground.getX() - leftArrow.getWidth(), sliderBackground.getY());
        leftArrow.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                linkedSettingEntry.set(-1f, slider, sliderBackground, true);
                return true;
            }
        });

        // Right arrow of slider with listener
        final TextButton rightArrow = new TextButton("+", Menu.textButtonStyle);
        rightArrow.setName(menuEntryId.s);
        rightArrow.setHeight(Menu.stage.getHeight()*0.05f);
        rightArrow.setWidth(Menu.stage.getHeight()*0.05f);
        rightArrow.setPosition(sliderBackground.getX() + sliderBackground.getWidth(), sliderBackground.getY());
        rightArrow.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                linkedSettingEntry.set(1f, slider, sliderBackground, true);
                return true;
            }
        });


        // Scaling button arrows a little
        leftArrow.getLabel().setFontScale(2);
        rightArrow.getLabel().setFontScale(2);



        menuEntry.add(sliderBackground);
        menuEntry.add(slider);
        menuEntry.add(leftArrow);
        menuEntry.add(rightArrow);
        // menuEntry.add(label);
    }

    private void initButton(Settings.SettingsEnum menuEntryId) {
        // First creates a button with position, previousHeight() checks how many buttons are before this one
        final TextButton newButton = new TextButton(menuEntryId.s, Menu.textButtonStyle);
        newButton.setHeight(Menu.stage.getHeight()*0.05f);
        newButton.setWidth(Menu.stage.getWidth()*0.075f);
        newButton.setName(menuEntryId.s);
        newButton.setPosition(Menu.centerX - newButton.getWidth()/2, (Menu.topY - newButton.getHeight()) - Menu.previousHeight);

        // Adds listener which performs action in Settings class. Button is discovered by name string
        newButton.addListener(new InputListener() {

            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                linkedSettingEntry.action();
                return true;
            }
        });

        // Adds button to menuEntry and stageEntry and also increases numbers of items for previousHeight()
        menuEntry.add(newButton);
    }

    private void addHeight() {
        Menu.previousHeight += getButton(0).getHeight() + Menu.spacing;
    }

    TextButton getButton(int buttonId) {
        if(buttonId < menuEntry.size) {
            return menuEntry.get(buttonId);
        } else { return null; }
    }

    public void updateValue() {
        if( menuEntry.size > 2) {
            linkedSettingEntry.set(linkedSettingEntry.getValue(), menuEntry.get(1), menuEntry.get(0));
            currentValue = linkedSettingEntry.getValue();
        }
    }

    void updatePosition(float diff) {
        for(TextButton tb : menuEntry) {
            tb.moveBy(0, diff);
        }
    }

    void fadeOut(float time) {
        for(TextButton tb : menuEntry) {
            tb.addAction(Actions.fadeOut(time));
        }
    }
    void fadeIn(float time) {
        for(TextButton tb : menuEntry) {
            tb.addAction(Actions.fadeIn(time));
        }
    }


}
