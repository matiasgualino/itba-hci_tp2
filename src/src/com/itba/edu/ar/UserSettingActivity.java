package com.itba.edu.ar;

import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.widget.Toast;

public class UserSettingActivity extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PreferenceManager.getDefaultSharedPreferences(this)
				.registerOnSharedPreferenceChangeListener(this);
		addPreferencesFromResource(R.xml.settings);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("Language")) {
			Resources res = getResources();
			Locale myLocale = new Locale(sharedPreferences.getString(key, "es"));
			DisplayMetrics dm = res.getDisplayMetrics();
			android.content.res.Configuration conf = res.getConfiguration();
			if(!conf.locale.getLanguage().equals(myLocale.getLanguage())) {
				conf.locale = myLocale;
				res.updateConfiguration(conf, dm);
				Locale.setDefault(myLocale);
				Toast.makeText(this, getString(R.string.restart_app), Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(this, SplashActivity.class);
				intent.putExtra("language", true);
				startActivity(intent);
			}
		}
	}
}
