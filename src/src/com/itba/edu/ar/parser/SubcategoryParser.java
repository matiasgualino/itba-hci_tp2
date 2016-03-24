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
import com.itba.edu.ar.model.Subcategory;

public class SubcategoryParser {

	public List<Subcategory> getSubcategoriesFromCategory(Category category) {
		List<Subcategory> subcategories = new ArrayList<Subcategory>();
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			DefaultHttpClient defaultClient = new DefaultHttpClient();
			HttpGet httpGetRequest = new HttpGet(
					"http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetAllSubcategories&id="
							+ category.getId());
			HttpResponse httpResponse = defaultClient.execute(httpGetRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), "UTF-8"));

			String json = reader.readLine();
			JSONObject jsonObject = new JSONObject(json);
			Subcategory subcat = null;
			JSONArray arr = (JSONArray) jsonObject.get("subcategories");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject subcategory = (JSONObject) arr.get(i);
				subcat = new Subcategory();
				subcat.setId((Integer) subcategory.get("id"));
				subcat.setName((String) subcategory.get("name"));
				JSONArray attributes = (JSONArray) subcategory.get("attributes");
				for (int j = 0; j < attributes.length(); j++) {
					JSONObject attr = (JSONObject) attributes.get(j);
					String valuesString = "";
					JSONArray values = (JSONArray) attr.get("values");
					for (int k = 0; k < values.length(); k++) {
						valuesString += (k > 0 ? ", " : "")
								+ (String) values.get(k);
					}
					subcat.addAttribute(new Attribute((String) attr.get("name"),
							valuesString));
				}
				subcategories.add(subcat);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return subcategories;
	}

}
