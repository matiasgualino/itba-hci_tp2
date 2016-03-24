package com.itba.edu.ar;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Window;

public class SplashActivity extends Activity {

	private static final long SPLASH_SCREEN_DELAY = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Resources res = getResources();
		Locale myLocale = new Locale(prefs.getString("Language", "es"));
		DisplayMetrics dm = res.getDisplayMetrics();
		android.content.res.Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
		Locale.setDefault(myLocale);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.splash_activity);

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				if (getIntent() != null && getIntent().hasExtra("language")
						&& getIntent().getBooleanExtra("language", false)) {
					Intent settingsIntent = new Intent().setClass(getApplicationContext(),
							UserSettingActivity.class);
					startActivity(settingsIntent);
				} else {
					Intent mainIntent = new Intent(getApplicationContext(),
							MainActivity.class);
					startActivity(mainIntent);
				}
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, SPLASH_SCREEN_DELAY);
	}

}