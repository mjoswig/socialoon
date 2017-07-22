package de.manuel_joswig.socialoon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import de.manuel_joswig.socialoon.map.MapActivity;

public class SplashActivity extends Activity {
	private final int SPLASH_DISPLAY_LENGTH = 1000;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this, MapActivity.class);
				intent.putExtra("last_activity", "SplashActivity");
				
				startActivity(intent);
				finish();
			}
		}, SPLASH_DISPLAY_LENGTH);
	}
}
