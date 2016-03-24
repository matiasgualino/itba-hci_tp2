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

import com.itba.edu.ar.model.Attribute;
import com.itba.edu.ar.model.Category;

public class CategoryParser {

	public List<Category> getCategories(String filters) {
		List<Category> categories = new ArrayList<Category>();
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			DefaultHttpClient defaultClient = new DefaultHttpClient();
			String url = "http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetAllCategories"
					+ (filters != null ? "&filters=" + filters : "");
			url = url.replace("\"", "%22");
			url = url.replace("[", "%5B");
			url = url.replace("]", "%5D");
			url = url.replace("{", "%7B");
			url = url.replace("}", "%7D");
			url = url.replace(" ", "%20");
			HttpGet httpGetRequest = new HttpGet(url);
			HttpResponse httpResponse = defaultClient.execute(httpGetRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), "UTF-8"));

			String json = reader.readLine();
			JSONObject jsonObject = new JSONObject(json);
			Category cat = null;
			JSONArray arr = (JSONArray) jsonObject.get("categories");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject category = (JSONObject) arr.get(i);
				cat = new Category();
				cat.setId((Integer) category.get("id"));
				cat.setName((String) category.get("name"));
				JSONArray attributes = (JSONArray) category.get("attributes");
				for(int j = 0; j < attributes.length(); j++) {
					JSONObject attr = (JSONObject) attributes.get(j);
					String valuesString = "";
					JSONArray values = (JSONArray) attr.get("values");
					for(int k = 0; k < values.length(); k++) {
						valuesString += (k > 0 ? ", " : "") + (String) values.get(k);
					}
					cat.addAttribute(new Attribute((String) attr.get("name"), valuesString));	
				}
				categories.add(cat);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return categories;
	}
	
}
