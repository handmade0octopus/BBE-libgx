package com.handmadeoctopus.environment;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SlidingMenu {
        Stage stage;
        Skin skin;
        final TextButton textButton;
        InputHandler handler;

        public SlidingMenu(final Stage stage, final InputHandler handler) {
                this.stage = stage;
                this.handler = handler;

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
                TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
                textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
                textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
           //   textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
                textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);

                textButtonStyle.font = skin.getFont("default");

                skin.add("default", textButtonStyle);

                // Create a button with the "default" TextButtonStyle. A 3rd parameter can be used to specify a name other than "default".
                textButton=new TextButton("PLAY",textButtonStyle);
                textButton.setPosition(200, 200);
                stage.addActor(textButton);


                // Add a listener to the button. ChangeListener is fired when the button's checked state changes, eg when clicked,
                // Button#setChecked() is called, via a key press, etc. If the event.cancel() is called, the checked state will be reverted.
                // ClickListener could have been used, but would only fire when clicked. Also, canceling a ClickListener event won't
                // revert the checked state.
                textButton.addListener(new ChangeListener(){
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                                System.out.println("Clicked! Is checked: " + textButton.isChecked());
                                Gdx.input.setInputProcessor(handler);
                                textButton.setChecked(false);
                        }
                });
        }

        public void draw() {
                stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
                stage.draw();
        }

        public void setInputProcessor() {
                Gdx.input.setInputProcessor(stage);
        /*        textButton.addListener(new ChangeListener(){
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                                System.out.println("Clicked! Is checked: " + textButton.isChecked());
                                Gdx.input.setInputProcessor(handler);
                        }
                });*/
        }

        public void onClick(float x, float y) {
                if(x > 150 && x < 150 + textButton.getWidth() && y > 150 && y < 150 + textButton.getHeight()) {
                        setInputProcessor();
                        System.out.println("CLICKED!");
                }
        }
}
