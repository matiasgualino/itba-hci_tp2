package com.itba.edu.ar;

import java.io.InputStream;
import java.net.URL;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;

import com.itba.edu.ar.model.Product;
import com.itba.edu.ar.parser.ProductParser;
import com.itba.edu.ar.utils.Utils;

public class ProductViewActivity extends Activity implements ViewFactory {

	ImageSwitcher imageSwitcher;
	float initialX, finalX;
	private int position = 0;
	ProgressBar pBar;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.product_view);
		imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher1);
		pBar = (ProgressBar) findViewById(R.id.product_bar);

		if (getIntent().hasExtra("productId")) {
			if (Utils.isNetworkAvailable(ProductViewActivity.this)) {
				new MyTask(getIntent().getExtras().getInt("productId"))
						.execute();
			} else {
				showToast(getString(R.string.no_network));
			}
		}

	}

	private Drawable getImageDrawable(String url) {
		Drawable image;
		URL imageUrl;
		try {
			imageUrl = new URL(url);
			InputStream stream = imageUrl.openStream();
			Bitmap bitmap = BitmapFactory.decodeStream(stream);
			image = new BitmapDrawable(getResources(), bitmap);
		} catch (Exception e) {
			image = null;
		}
		return image;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.only_settings, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent order = new Intent(this, UserSettingActivity.class);
			startActivity(order);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public View makeView() {
		ImageView imageView = new ImageView(this);
		imageView.setBackgroundColor(Color.WHITE);
		imageView.setScaleType(ImageView.ScaleType.CENTER);
		imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return imageView;
	}

	class MyTask extends AsyncTask<String, Void, Void> {

		int id;
		Product product;

		public MyTask(int id) {
			this.id = id;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(String... params) {
			product = new ProductParser().getProductById(id);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (null != pBar && pBar.getVisibility() == View.VISIBLE) {
				pBar.setVisibility(View.GONE);
			}

			if (null == product) {
				showToast(getString(R.string.no_data_api));
				ProductViewActivity.this.finish();
			} else {
				loadProduct(product);
			}

		}
	}

	private void loadProduct(final Product product) {
		String toSearch = product.getSubcategory().getName();
		String title = product.getName();
		if(product.getName().contains(toSearch)) {
			title = product.getName().replace(toSearch,  "").trim();
		}
		else {
			if(toSearch.charAt(toSearch.length()-1) == 's') {
				toSearch = toSearch.substring(0, toSearch.length() - 1);
				if(product.getName().contains(toSearch)) {
					title = product.getName().replace(toSearch,  "").trim();
				}
			}	
		}
		
		getActionBar().setTitle(title);
		final TextView price = (TextView) findViewById(R.id.product_price);
		price.setText("$" + product.getPrice());
		
		final String[] attributes = { "Color", "Edad", "Estilo-Jeans",
				"Estilo-Pantalones", "Genero", "Marca", "Material-Accesorios",
				"Material-Blazers", "Material-Buzos", "Material-Calzado",
				"Material-Calzas", "Material-Camisas", "Material-Camperas",
				"Material-Cardigans", "Material-Chalecos", "Material-Jeans",
				"Material-Pantalones", "Material-Pilotos", "Material-Polleras",
				"Nuevo", "Ocasion", "Oferta", "Talle-Blazers", "Talle-Buzos",
				"Talle-Calzado", "Talle-Calzas", "Talle-Camisas",
				"Talle-Camperas", "Talle-Cardigans", "Talle-Chalecos",
				"Talle-Jeans", "Talle-Pantalones", "Talle-Pilotos",
				"Talle-Polleras" };

		TableLayout ll = (TableLayout) findViewById(R.id.info_product);
		TextView key, value;
		int cantRows = 0;
		for(int i = 0; i < attributes.length; i++) {
			String attVal = product.getAttribute(attributes[i]);
			if(attVal != null) {
				TableRow row = new TableRow(this);
		        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
		        lp.setMargins(0, 15, 0, 15);
		        row.setLayoutParams(lp);
		        key = new TextView(this);
		        //key.setText(attributes[i].contains("-") ? attributes[i].split("-")[0] : attributes[i]);
		        key.setText(Utils.getString(this, attributes[i]));
		        key.setTypeface(null, Typeface.BOLD);
		        value = new TextView(this);
		        if(attributes[i].equals("Marca") || attributes[i].contains("-")) {
		        	value.setText(attVal);	
		        } else {
		        	value.setText(Utils.getString(this, attVal));	
		        }
		        row.addView(key);
		        row.addView(value);
		        ll.addView(row,cantRows);
		        cantRows++;
			}
		}
		
		try {

			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			imageSwitcher.setFactory(this);

			imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.fade_in));
			imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.fade_out));

			imageSwitcher.setImageDrawable(getImageDrawable(product
					.getImageUrl().get(0)));

			imageSwitcher.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						initialX = (int) event.getX();
						return true;
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						finalX = (int) event.getX();
						if (finalX - initialX > 100) {
							position--;
							if (position < 0) {
								position = product.getImageUrl().size() - 1;
							}
							imageSwitcher
									.setImageDrawable(getImageDrawable(product
											.getImageUrl().get(position)));
						} else if (initialX - finalX > 100) {
							position++;
							if (position >= product.getImageUrl().size()) {
								position = 0;
							}
							imageSwitcher
									.setImageDrawable(getImageDrawable(product
											.getImageUrl().get(position)));
						}
						return true;
					}
					return false;
				}
			});
		} catch (Exception ex) {
		}
	}

	public void showToast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}

}
