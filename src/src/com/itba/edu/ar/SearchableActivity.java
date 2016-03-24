package com.itba.edu.ar;

import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

import com.itba.edu.ar.adapter.ProductsAdapter;
import com.itba.edu.ar.model.Product;
import com.itba.edu.ar.parser.ProductParser;
import com.itba.edu.ar.utils.*;

public class SearchableActivity extends Activity{
	List<Product> arrayOfList;
	ListView listView;
	
	private TextToSpeech mTts;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    handleIntent(getIntent());
	    
	}
	public void onNewIntent(Intent intent){
		setIntent(intent);
		handleIntent(intent);
	}
	public void handleIntent(Intent intent){
		setContentView(R.layout.product_list_activity);
	    getActionBar().setDisplayHomeAsUpEnabled(true);
	    listView = (ListView) findViewById(R.id.listview);
		mTts = new TextToSpeech(this,
				new TextToSpeech.OnInitListener() {
					@Override
					public void onInit(int status) {
						mTts.setLanguage(new Locale("spa", "ESP"));
					}
				});
	    // Get the intent, verify the action and get the query
	    if(Utils.isNetworkAvailable(this)){

		    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
		      String query = intent.getStringExtra(SearchManager.QUERY);
		     // Toast.makeText(getApplicationContext(),query, Toast.LENGTH_LONG).show();
		      doMySearch(query);
		    }
	    }
	    else {
			Toast.makeText(getApplicationContext(),R.string.no_network, Toast.LENGTH_LONG).show();
		}
		
	}
	
	public void doMySearch(String query){
		String jsonUrl = "http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetProductsBy"
				+ "Name&name=" + query;
		new MyTask().execute(jsonUrl);
		
		
	}
	
	class MyTask extends AsyncTask<String, Void, Void>{
		ProgressDialog pDialog;
		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(SearchableActivity.this);
			pDialog.setMessage(getString(R.string.load_products));
			pDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			arrayOfList = new ProductParser().getProductList(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (null != pDialog && pDialog.isShowing()) {
				pDialog.dismiss();
			}

			if (null == arrayOfList || arrayOfList.size() == 0) {
				Toast.makeText(getApplicationContext(), getText(R.string.data_not_found), Toast.LENGTH_LONG).show();
				SearchableActivity.this.finish();
			} else {
				setAdapterToListview();
			}

		}
		
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
	
	public void setAdapterToListview() {
		ProductsAdapter objAdapter = new ProductsAdapter(SearchableActivity.this,
				R.layout.product_item, arrayOfList);
		
		//listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
		listView.setSelector(R.drawable.listitem_background);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {
				Intent intent = new Intent(SearchableActivity.this, ProductViewActivity.class);
				intent.putExtra("product", new ProductParser().getProductById(((Product) adapter.getAdapter().getItem(position)).getId()));
				startActivity(intent);
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> adapter, View arg1,
                    int pos, long id) {
            	Product prod = (Product) adapter.getAdapter().getItem(pos);
            	mTts.speak(prod.getName() + ". " + prod.getPrice() + " pesos.", TextToSpeech.QUEUE_FLUSH, null);
				return true;
            }
        }); 
		
		listView.setAdapter(objAdapter);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
}