package com.handmadeoctopus.environment;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.handmadeoctopus.BouncingBallEngine;

public class SlidingMenu {
        Stage stage; // Main stage of our menu
        Skin skin; // Skin for our buttons.
        TextButton menuButton; // main menu button
        TextButton.TextButtonStyle textButtonStyle;
        InputHandler handler; // Input handler where all input is handled, passed from main class.
        Settings settings; // All settings of the game, passed from main class.
        Menu menu; // Main menu of

        int inter = 0; // Variable for fading window.
        boolean visible = false, menuOn = false; //  variables to controll menu visibility
        float z, q; // additional variable of screen size


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
                Pixmap pixmap = new Pixmap(1000, 1000, Pixmap.Format.RGBA8888);
                pixmap.setColor(Color.WHITE);
                pixmap.fill();

                skin.add("white", new Texture(pixmap));

                // Store the default libgdx font under the name "default".
                BitmapFont bfont = new BitmapFont();
                bfont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
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
                menuButton =new TextButton("MENU",textButtonStyle);
                menuButton.setWidth(stage.getViewport().getWorldWidth()*0.10f);
                menuButton.setHeight(stage.getViewport().getWorldWidth()*0.05f);
                menuButton.setPosition(stage.getWidth()- menuButton.getWidth(), stage.getHeight()- menuButton.getHeight());


                // Setting up menu
                menu = new Menu(stage, skin, settings, menuButton, this);

                // Adding listener to "Menu" button which hides menu if clicked.

                menuButton.addListener(new InputListener() {

                        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                                setMenu(false);
                                return true;
                        }
                });
        }

        public void draw() {
                // Make actions and draws stage.
                stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
                stage.draw();

                // If menu is clicked, adds 'inter' timer on. If inter is >= 100, menu fades out.
                if (visible && !menuOn) { inter++; }
                if (inter >= 100) {
                        menu.fadeOut(0.5f);
                        visible = false;
                        inter = 0;
                }
        }

        // Sets menu on or off.
        public void setMenu(boolean on) {
                if (on) {
                        Gdx.input.setInputProcessor(stage);
                        menuOn = true;
                        inter = 0;
                } else {
                        Gdx.input.setInputProcessor(handler);
                        menuOn = false;
                        visible = false;
                        inter = 0;
                        menu.fadeOut(0.5f);
                        menuButton.setChecked(false);
                }
        }

        // This happen when "Menu" button is clicked.
        public void onClick(float x, float y) {
                // Checks if input is inside borders.
                if(x > menuButton.getX() && x < menuButton.getX() + menuButton.getWidth() && y > menuButton.getY()
                        && y < menuButton.getY() + menuButton.getHeight()) {
                        if (inter == 0) {
                                menu.fadeIn(0.5f);
                                setMenu(true);
                                visible = true;
                        }
                        if (inter > 0) {
                                setMenu(true);
                        }
                }
                menu.z = z;
                menu.q = q;
        }

        // Called when resize happens
        public void update(float width, float height) {
                float w = width;
                float h = height;
                float f = (h / w);

                stage.getViewport().update((int) width, (int) height, true);
                stage.getViewport().setWorldWidth(BouncingBallEngine.WIDTH);
                stage.getViewport().setWorldHeight(BouncingBallEngine.WIDTH*f);
                menuButton.setPosition(stage.getViewport().getWorldWidth() - menuButton.getWidth(),
                        stage.getViewport().getWorldHeight() - menuButton.getHeight());
                menu.update();
        }

        // Update z & q variable for Menu.
        public void updateMenu(float z, float q) {
                menu.z = z;
                menu.q = q;
        }
}
