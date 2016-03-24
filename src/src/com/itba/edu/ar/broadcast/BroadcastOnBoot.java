package com.itba.edu.ar.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class BroadcastOnBoot extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		AlarmUpdater.UpdateAlarm(context);
	}
}
