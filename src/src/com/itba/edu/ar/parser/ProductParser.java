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
import android.util.Log;

import com.itba.edu.ar.model.Attribute;
import com.itba.edu.ar.model.Category;
import com.itba.edu.ar.model.Product;
import com.itba.edu.ar.model.Subcategory;

public class ProductParser {

	public List<Product> getProductList(String url) {

		Product prod;
		List<Product> listArray;

		listArray = new ArrayList<Product>();

		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			DefaultHttpClient defaultClient = new DefaultHttpClient();
			
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

			JSONArray arr = (JSONArray) jsonObject.get("products");
			for (int i = 0; i < arr.length(); i++) {
				JSONObject product = (JSONObject) arr.get(i);
				prod = new Product();
				prod.setId((Integer) product.get("id"));
				prod.setName((String) product.get("name"));
				prod.setPrice((Integer) product.get("price"));
				JSONArray imageUrl = (JSONArray) product.get("imageUrl");
				for (int img = 0; img < imageUrl.length(); img++) {
					prod.addImageUrl((String) imageUrl.get(img));
				}
				JSONObject category = (JSONObject) product.get("category");
				Category c = new Category();
				c.setId((Integer) category.get("id"));
				c.setName((String) category.get("name"));
				prod.setCategory(c);

				JSONObject subcategory = (JSONObject) product.get("subcategory");
				Subcategory s = new Subcategory();
				s.setId((Integer) subcategory.get("id"));
				s.setName((String) subcategory.get("name"));
				prod.setSubcategory(s);

				JSONArray attributes = (JSONArray) product.get("attributes");
				for (int att = 0; att < attributes.length(); att++) {
					JSONObject attr = (JSONObject) attributes.get(att);
					String valuesString = "";
					JSONArray values = (JSONArray) attr.get("values");
					for (int j = 0; j < values.length(); j++) {
						valuesString += (j > 0 ? ", " : "")
								+ (String) values.get(j);
					}
					prod.addAttribute(new Attribute((String) attr.get("name"),
							valuesString));
				}
			
				listArray.add(prod);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return listArray;
	}

	public Product getProductById(Integer id) {
		return getProduct("http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetProductById&id=" + id);
	}
	
	public Product getProduct(String url) {

		Product prod = null;

		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
			DefaultHttpClient defaultClient = new DefaultHttpClient();
			HttpGet httpGetRequest = new HttpGet(url);
			HttpResponse httpResponse = defaultClient.execute(httpGetRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), "UTF-8"));
			String json = reader.readLine();
			JSONObject jsonObject = new JSONObject(json);
			JSONObject product = (JSONObject) jsonObject.get("product");
			prod = new Product();
			prod.setId((Integer) product.get("id"));
			prod.setName((String) product.get("name"));
			prod.setPrice((Integer) product.get("price"));
			JSONArray imageUrl = (JSONArray) product.get("imageUrl");
			for (int i = 0; i < imageUrl.length(); i++) {
				prod.addImageUrl((String) imageUrl.get(i));
			}
			JSONObject category = (JSONObject) product.get("category");
			Category c = new Category();
			c.setId((Integer) category.get("id"));
			c.setName((String) category.get("name"));
			prod.setCategory(c);

			JSONObject subcategory = (JSONObject) product.get("subcategory");
			Subcategory s = new Subcategory();
			s.setId((Integer) subcategory.get("id"));
			s.setName((String) subcategory.get("name"));
			prod.setSubcategory(s);

			JSONArray attributes = (JSONArray) product.get("attributes");
			for (int i = 0; i < attributes.length(); i++) {
				JSONObject attr = (JSONObject) attributes.get(i);
				String valuesString = "";
				JSONArray values = (JSONArray) attr.get("values");
				for (int j = 0; j < values.length(); j++) {
					valuesString += (j > 0 ? ", " : "")
							+ (String) values.get(j);
				}
				prod.addAttribute(new Attribute((String) attr.get("name"),
						valuesString));
			}

		} catch (Exception e) {
			Log.d("API ERROR", e.toString());
		}

		return prod;
	}

}
