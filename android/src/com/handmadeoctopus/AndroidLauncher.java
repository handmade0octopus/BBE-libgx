package com.handmadeoctopus;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.handmadeoctopus.BouncingBallEngine;

/*
This class starts main engine for Android devices.
 */

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.numSamples = 4;
		config.useWakelock = true;
		initialize(new BouncingBallEngine(), config);
	}
}
