package com.itba.edu.ar;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.itba.edu.ar.adapter.CategoryAdapter;
import com.itba.edu.ar.model.Category;
import com.itba.edu.ar.parser.CategoryParser;
import com.itba.edu.ar.utils.Utils;



public class CategoriesActivity extends Activity {

	private TextToSpeech mTts;
	private List<Category> categories;
	ProgressBar pBar;
	ListView listView;
	String filters = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categories_layout);
		listView = (ListView) findViewById(R.id.categories);
		pBar = (ProgressBar) findViewById(R.id.category_bar);

		Intent intent = getIntent();
		if(intent.hasExtra("filters")) {
			filters = intent.getStringExtra("filters");
			if(filters.contains("Masculino")) {
				setTitle(R.string.masculino);
			}
			else if(filters.contains("Femenino")) {
				setTitle(R.string.femenino);
			}
			else if(filters.contains("Infantil")) {
				setTitle(R.string.infantil);
			}
		}
		
		mTts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
			@Override
			public void onInit(int status) {
				android.content.res.Configuration conf = getResources().getConfiguration();
				mTts.setLanguage(conf.locale);
			}
		});

		if (Utils.isNetworkAvailable(CategoriesActivity.this)) {
			new MyTask().execute();
		} else {
			showToast(getString(R.string.no_network));
		}

	}
	
	class MyTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Void doInBackground(String... params) {
			
			categories = new CategoryParser().getCategories(filters);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (null != pBar && pBar.getVisibility() == View.VISIBLE) {
				pBar.setVisibility(View.GONE);
				listView.setVisibility(View.VISIBLE);
			}

			if (null == categories || categories.size() == 0) {
				showToast(getString(R.string.no_data_api));
				CategoriesActivity.this.finish();
			} else {
				setAdapterToListview();
			}

		}
	}

	public void setAdapterToListview() {
		listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
		listView.setSelector(R.drawable.listitem_background);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {
				Intent intent = new Intent(getApplicationContext(),
						SubcategoriesActivity.class);
				Category cat = (Category) adapter.getAdapter()
						.getItem(position);
				intent.putExtra("category", cat);
				startActivity(intent);
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> adapter, View arg1,
					int pos, long id) {
				mTts.speak(((Category) adapter.getAdapter().getItem(pos))
						.getName(), TextToSpeech.QUEUE_FLUSH, null);
				return true;
			}
		});
		listView.setAdapter(new CategoryAdapter(this,
				R.layout.list_item_layout, R.id.name, categories));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
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
	
	public List<Category> getCategories() {
		return categories;
	}
	

	public void showToast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
}