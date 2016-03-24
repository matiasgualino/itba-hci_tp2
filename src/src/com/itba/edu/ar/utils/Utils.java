package com.itba.edu.ar.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Utils {
	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static String getString(Context context, String string) {
		String s = string.trim().toLowerCase().replace("-", "_").replace(" ", "_");
		int resourceId = context.getResources().getIdentifier(s, "string", context.getPackageName());
		Log.d("STRING", "ME PEDISTE " + string);
		return context.getString(resourceId);
	}

}
