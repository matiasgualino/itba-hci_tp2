package com.itba.edu.ar.parser;

import java.io.BufferedReader;
import java.io.InputStreamReader;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.os.StrictMode;

import com.itba.edu.ar.model.Item;
import com.itba.edu.ar.model.Order;
import com.itba.edu.ar.model.Product;

public class OrderDetailParser {
	
	
	public Order getData(String url) {
		Order order = new Order();

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
			JSONObject orden = jsonObject.getJSONObject("order");
			if(!orden.isNull("receivedDate"))
				order.setReceivedDate(orden.getString("receivedDate"));
			if(!orden.isNull("processedDate"))
				order.setShippedDate(orden.getString("processedDate"));
			if(!orden.isNull("shippedDate"))
				order.setShippedDate(orden.getString("shippedDate"));
			if(!orden.isNull("deliveredDate"))
				order.setDeliveredDate(orden.getString("deliveredDate"));
			if(!orden.isNull("latitude"))
				order.setLatitude(orden.getInt("latitude"));
			if(!orden.isNull("longitude"))
				order.setLongitude(orden.getInt("longitude"));
			if(!orden.isNull("id"))
				order.setId(orden.getInt("id"));
			JSONArray arr = (JSONArray) orden.get("items");
			for (int i = 0; i < arr.length(); i++) {
				Item item = new Item();
			    JSONObject items = (JSONObject) arr.get(i);
			    if(!items.isNull("product")){
			    	JSONObject prod = (JSONObject) items.get("product");
			    	Product product = new Product();
			    	if(!prod.isNull("name"))
			    		product.setName(prod.getString("name"));
			    	if(!prod.isNull("imageUrl"))
			    		product.addImageUrl(prod.getString("imageUrl"));
			    	item.setProduct(product);
			    }
			    if(!items.isNull("quantity"))
			    	item.setQuantity(items.getInt("quantity"));
			    if(!items.isNull("price"))
			    	item.setPrice(items.getInt("price"));
			    
			    order.setItems(item);
			}
	

		} catch (Exception e) {
			e.printStackTrace();
			Order ob = new Order();
			ob.setShippedDate(e.toString());
			return ob;
			
		}

		return order;
	}
}
