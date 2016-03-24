package com.itba.edu.ar.broadcast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.itba.edu.ar.MainActivity;
import com.itba.edu.ar.OrderActivity;
import com.itba.edu.ar.R;

public class AlarmReceiver extends BroadcastReceiver {

	private static final int UPDATE_NOTIFICATION_ID = 001;
	private static final int NEW_ORDER = 101;
	private static final int UPDATED_ORDER_STATUS = 102;
	private static final int UPDATED_ORDER_POSITION = 103;
	private static HashMap<String, Order> previousOrders;
	Context context;

	public void onReceive(Context context, Intent intent) {
		this.context = context;
		// if (checkConnection(context)) {
		SharedPreferences settings = context.getSharedPreferences("login", 0);
		String username = settings.getString("user", null);
		String auth_Token = settings.getString("token", null);
		if (username != null && auth_Token != null) {
			String URL = "http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=GetAllOrders&username="
					+ username + "&authentication_token=" + auth_Token;
			new CheckForUpdates().execute(URL);
		}
		// }
	}

	private class Change {
		Order order;
		int change;

		Change(Order order, int change) {
			this.order = order;
			this.change = change;
		}
	}

	private class Order {

		String id;
		String status;
		String latitude;
		String longitude;

		Order(String id, String status, String latitude, String longitude) {
			this.id = id;
			this.status = status;
			this.latitude = latitude;
			this.longitude = longitude;
		}
	}

	private class CheckForUpdates extends AsyncTask<String, Void, String> {

		public void onPostExecute(String result) {

			String error = null;
			JSONObject jsonObject = null;

			try {
				jsonObject = new JSONObject(result);
				error = jsonObject.getString("error");
			} catch (JSONException e) {
				Log.w("onPostExecuteProcessOrder", e.getLocalizedMessage());
			}

			if (error == null) {
				sendNotification(comparateOrder(jsonObject));
			}
		}

		@Override
		protected String doInBackground(String... urls) {
			return readJSON(urls[0]);
		}

	}

	public static String readJSON(String URL) {
		StringBuilder stringBuilder = new StringBuilder();
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(URL);
		try {
			HttpResponse response = httpClient.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream inputStream = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(inputStream));
				String line;
				while ((line = reader.readLine()) != null) {
					stringBuilder.append(line);
				}
				inputStream.close();
			} else {
				Log.w("JSON", "Failed to download file");
			}
		} catch (Exception e) {
			Log.w("readJSONFeed", e.getLocalizedMessage());
		}
		return stringBuilder.toString();
	}

	private boolean checkConnection(Context context) {
		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		if (i == null)
			return false;
		if (!i.isConnected())
			return false;
		return true;
	}

	private void sendNotification(ArrayList<Change> changes) {
		String msg = "";
		if (changes == null || changes.isEmpty()) {
			return;
		}
		for (Change change : changes) {
			int changeid = change.change;
			switch (changeid) {
			case UPDATED_ORDER_STATUS:

				int status = Integer.valueOf(change.order.status);
				String str = "";
				switch (status) {
				case 1:
					str = context
							.getString(R.string.notification_status_created);
					break;
				case 2:
					str = context
							.getString(R.string.notification_status_confirmed);
					break;
				case 3:
					str = context
							.getString(R.string.notification_status_transport);
					break;
				case 4:
					str = context
							.getString(R.string.notification_status_delivered);
					break;
				}

				msg = msg + context.getString(R.string.notification_order)
						+ " " + change.order.id + " " + str + ". ";
				break;
			case UPDATED_ORDER_POSITION:
				msg = msg
						+ context.getString(R.string.notification_order)
						+ " "
						+ change.order.id
						+ " "
						+ context
								.getString(R.string.notification_order_updatedlocation)
						+ ". ";
				break;

			case NEW_ORDER:
				msg = msg + context.getString(R.string.notification_order)
						+ " " + change.order.id + ": "
						+ context.getString(R.string.notification_order_new)
						+ ". ";

			}
		}

		sendNotification(context,
				context.getString(R.string.app_name), msg,
				changes.size());

	}

	private ArrayList<Change> comparateOrder(JSONObject current) {
		HashMap<String, Order> currentOrdersMap = new HashMap<String, Order>();
		ArrayList<Change> changes = new ArrayList<Change>();
		try {
			JSONArray corders = current.getJSONArray("orders");
			for (int i = 0; i < corders.length(); i++) {
				currentOrdersMap
						.put(corders.getJSONObject(i).getString("id"),
								new Order(corders.getJSONObject(i).getString(
										"id"), corders.getJSONObject(i)
										.getString("status"),
										corders.getJSONObject(i).getString(
												"latitude"), corders
												.getJSONObject(i).getString(
														"longitude")));
			}
			
			SharedPreferences sp = context.getSharedPreferences("previousOrders", context.MODE_PRIVATE);
			String json = sp.getString("value", null);
			if (json != null) {
				previousOrders = new HashMap<String, Order>();
				JSONArray porders = new JSONArray(json);
				for (int i = 0; i < porders.length(); i++) {
					previousOrders
							.put(porders.getJSONObject(i).getString("id"),
									new Order(porders.getJSONObject(i).getString(
											"id"), porders.getJSONObject(i)
											.getString("status"),
											porders.getJSONObject(i).getString(
													"latitude"), porders
													.getJSONObject(i).getString(
															"longitude")));
				}	
			}
		} catch (JSONException e) {
			System.out.println(current);
			e.printStackTrace();
		}

		JSONArray jsonArray = new JSONArray();
		Set<String> set = currentOrdersMap.keySet();
		Order currOrder;
		Order prevOrder;
		
		if (previousOrders == null) {
			for (String curr : set) {
				currOrder = currentOrdersMap.get(curr);
				try {
					JSONObject json = new JSONObject();
					json.put("id", currOrder.id);
					json.put("status", currOrder.status);
					json.put("latitude", currOrder.latitude);
					json.put("longitude", currOrder.longitude);
					jsonArray.put(json);	
				} catch (Exception ex) {
					
				}
			}
			saveOrders(jsonArray);
			return changes;
		}
		
		for (String curr : set) {
			currOrder = currentOrdersMap.get(curr);
			try {
				JSONObject json = new JSONObject();
				json.put("id", currOrder.id);
				json.put("status", currOrder.status);
				json.put("latitude", currOrder.latitude);
				json.put("longitude", currOrder.longitude);
				jsonArray.put(json);	
			} catch (Exception ex) {
				
			}
			
			if (previousOrders.containsKey(curr)) {
				prevOrder = previousOrders.get(curr);
				if (!currOrder.status.equals(prevOrder.status)) {
					changes.add(new Change(currOrder, UPDATED_ORDER_STATUS));
				} else if (!prevOrder.latitude.equals(currOrder.latitude)
						|| !prevOrder.longitude.equals(currOrder.longitude)) {
					changes.add(new Change(currOrder, UPDATED_ORDER_POSITION));
				}
			} else {
				changes.add(new Change(currOrder, NEW_ORDER));
			}

		}

		saveOrders(jsonArray);
		return changes;
	}

	private void saveOrders(JSONArray jsonArray){
		SharedPreferences saveData = context.getSharedPreferences(
				"previousOrders", context.MODE_PRIVATE);
		SharedPreferences.Editor editor = saveData
				.edit();
		editor.putString("value", jsonArray.toString());
		editor.commit();
	}
	
	NotificationCompat.BigTextStyle createBigTextStyle(Context context, String bigText){
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(bigText);
        bigTextStyle.setBigContentTitle(context.getString(R.string.app_name));
        return bigTextStyle;
    }
	
	private void sendNotification(Context context, String title, String msg,
			int number) {
		NotificationCompat.Builder notif = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle(title).setContentText(msg).setTicker(msg);
		notif.setStyle(createBigTextStyle(context, msg));
		if (number != 0)
			notif.setNumber(number);

		Intent resultIntent = new Intent(context, OrderActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notif.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(number, notif.build());

	}
}