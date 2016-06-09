package com.handmadeoctopus.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.handmadeoctopus.BouncingBallEngine;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.samples = 4;
		config.height = 800;
		config.width = 800;
		new LwjglApplication(new BouncingBallEngine(), config);
	}
}
