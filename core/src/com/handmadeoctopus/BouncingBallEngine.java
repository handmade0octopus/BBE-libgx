package com.handmadeoctopus;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.handmadeoctopus.environment.MainScreen;


public class BouncingBallEngine extends Game {

	public final static float WIDTH = 1000;

	@Override
	public void create() {
		init();
		this.setScreen(new MainScreen(this));
	}

	private void init() {

	}


}


