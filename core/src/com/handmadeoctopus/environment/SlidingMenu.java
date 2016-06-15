package com.handmadeoctopus.environment;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.actions.*;

public class SlidingMenu {
        Stage stage;
        Skin skin;
        TextButton textButton;
        TextButton.TextButtonStyle textButtonStyle;
        InputHandler handler;
        Settings settings;
        Menu menu;
        int inter = 0;
        boolean visible = false, menuOn = false;


        public SlidingMenu(final Stage stage, final InputHandler handler, Settings settings) {
                this.stage = stage;
                this.handler = handler;
                this.settings = settings;
                init();
        }

        private void init() {
                // A skin can be loaded via JSON or defined programmatically, either is fine. Using a skin is optional but strongly
                // recommended solely for the convenience of getting a texture, region, etc as a drawable, tinted drawable, etc.
                skin = new Skin();
                // Generate a 1x1 white texture and store it in the skin named "white".
                Pixmap pixmap = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
                pixmap.setColor(Color.GREEN);
                pixmap.fill();

                skin.add("white", new Texture(pixmap));

                // Store the default libgdx font under the name "default".
                BitmapFont bfont = new BitmapFont();
                skin.add("default", bfont);

                // Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
                textButtonStyle = new TextButton.TextButtonStyle();
                textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
                textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
                //   textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
                textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);

                textButtonStyle.font = skin.getFont("default");

                skin.add("default", textButtonStyle);

                // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
                textButton=new TextButton("MENU",textButtonStyle);
                textButton.setWidth(100);
                textButton.setHeight(50);
                textButton.setPosition(stage.getWidth()-textButton.getWidth(), stage.getHeight()-textButton.getHeight());
                stage.addActor(textButton);
                stage.addAction(Actions.alpha(0.2f));

                menu = new Menu(stage, skin, settings);

                // Add a listener to the button. ChangeListener is fired when the button's checked state changes, eg when clicked,
                // Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will be reverted.
                // ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event won't
                // revert the checked state.
                textButton.addListener(new InputListener() {

                        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                                Gdx.input.setInputProcessor(handler);
                                menuOn = false;
                                visible = false;
                                inter = 0;
                                stage.addAction(new SequenceAction(Actions.fadeOut(0.5f), Actions.alpha(0.2f)));
                                textButton.setChecked(false);
                                return true;
                        }
                });
        }

        public void draw() {
                stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
                stage.draw();
                if (visible && !menuOn) { inter++; }
                if (inter >= 100) {
                        stage.addAction(new SequenceAction(Actions.fadeOut(0.5f), Actions.alpha(0.2f)));
                        visible = false;
                        inter = 0;
                }
        }

        public void setInputProcessor() {
                Gdx.input.setInputProcessor(stage);
        }

        public void onClick(float x, float y) {
                if(x > textButton.getX() && x < textButton.getX() + textButton.getWidth() && y > textButton.getY()
                        && y < textButton.getY() + textButton.getHeight()) {
                        if (inter == 0) {
                                stage.addAction(Actions.fadeIn(0.5f));
                                visible = true;
                        }
                        if (inter > 0) {
                                setInputProcessor();
                                menuOn = true;
                        }
                }
        }

        public void update(float width, float height) {
                float w = width;
                float h = height;
                float f = (h / w);
                float difference =  height - stage.getViewport().getWorldHeight();
                float diff = stage.getViewport().getWorldWidth() - width;
                stage.getViewport().update((int) width, (int) height, true);
                stage.getViewport().setWorldWidth(1000);
                stage.getViewport().setWorldHeight(1000*f);
                textButton.setPosition(stage.getViewport().getWorldWidth()-textButton.getWidth(), stage.getViewport().getWorldHeight()-textButton.getHeight());
                menu.update(difference + diff);
        }
}
