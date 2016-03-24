package com.itba.edu.ar.parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.StrictMode;

import com.itba.edu.ar.model.Order;

public class OrderParser {
	Order ord;
	List<Order> listArray;

	public List<Order> getData(String url) {

		listArray = new ArrayList<Order>();

		try {
			
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			
			DefaultHttpClient defaultClient = new DefaultHttpClient();
			HttpGet httpGetRequest = new HttpGet(url);
			HttpResponse httpResponse = defaultClient.execute(httpGetRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), "UTF-8"));

			String json = reader.readLine();
			JSONObject jsonObject = new JSONObject(json);
			
			JSONArray arr = (JSONArray) jsonObject.get("orders");
			for (int i = 0; i < arr.length(); i++) {
			    JSONObject order = (JSONObject) arr.get(i);
			    JSONObject address;
			    ord = new Order();
			    if(!order.isNull("id"))
			    	ord.setId( (Integer)order.get("id"));
			    if(!order.isNull("status"))
			    	ord.setStatus( (String)order.get("status"));
			    if(!order.isNull("address")){
			    	address = (JSONObject) order.getJSONObject("address");
			    	if(!address.isNull("name"))
			    		ord.setAddres((String) address.get("name"));
			    }
			    
			    if(!order.isNull("deliveredDate"))
			    	ord.setDeliveredDate((String) order.get("deliveredDate"));
			    if(!order.isNull("processedDate"))
			    	ord.setProcessedDate((String) order.get("processedDate"));
			    if(!order.isNull("shippedDate"))
			    	ord.setShippedDate((String) order.get("shippedDate"));
			    if(!order.isNull("receivedDate"))
			    	ord.setReceivedDate((String) order.get("receivedDate"));
			    if(!order.isNull("latitude"))
			    	ord.setLatitude((Integer) order.get("latitude"));
			    if(!order.isNull("longitude"))
			    	ord.setLongitude((Integer)order.get("longitude"));
			    
			    listArray.add(ord);
			}
	

		} catch (Exception e) {
			e.printStackTrace();
			Order ob = new Order();
			ob.setAddres(e.toString());
			listArray.add(ob);
		}

		return listArray;
	}
}
