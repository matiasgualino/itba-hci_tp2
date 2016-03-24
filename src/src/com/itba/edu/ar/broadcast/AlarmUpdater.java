package com.itba.edu.ar.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class AlarmUpdater {

	public static final String KEY_SYNC_TIME = "notifFrequency";
	public static final String KEY_AUTOSYNC = "autosync";

	public static void UpdateAlarm(Context context) {
		int time = Integer.parseInt(PreferenceManager
				.getDefaultSharedPreferences(context).getString(KEY_SYNC_TIME,
						"1"));
		SetAlarm(context, time);
	}

	public static void SetAlarm(Context context, double time) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i,
				PendingIntent.FLAG_CANCEL_CURRENT);
		am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				System.currentTimeMillis(), (long) (1000 * 60 * time), pi); // Millisec
																			// *
																			// Second
																			// *
																			// Minute
	}

	public static void CancelAlarm(Context context) {
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}