package com.handmadeoctopus;

import com.badlogic.gdx.Game;
import com.handmadeoctopus.Engine.MainScreen;

/*
Main class which starts MainScreen class.
 */

public class BouncingBallEngine extends Game {


	MainScreen mainScreen; // main screen, object to be used in future if need to create different MainScreen.

	public final static float WIDTH = 1000; // Standard width main screen size. Not very important, height is calculated inside MainScreen from width/height ratio.

	@Override
	public void create() {
		init();
		this.setScreen(mainScreen = new MainScreen(this)); // sets screen to what we want.
	}

	private void init() {
		// Put here whatever you need to do before starting main screen.
	}


}


