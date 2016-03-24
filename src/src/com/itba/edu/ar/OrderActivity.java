package com.itba.edu.ar;

import java.util.List;

import com.itba.edu.ar.adapter.OrderAdapter;
import com.itba.edu.ar.model.Order;
import com.itba.edu.ar.parser.OrderParser;
import com.itba.edu.ar.utils.Utils;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class OrderActivity extends Activity{
	
	ProgressBar pDialog;
	List<Order> arrayOfList;
	ListView listView;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_list);
		pDialog = (ProgressBar) findViewById(R.id.progress);
		getActionBar().setHomeButtonEnabled(true);
		listView = (ListView) findViewById(R.id.orderlist);
		
		if (Utils.isNetworkAvailable(OrderActivity.this)) {
			SharedPreferences mypref = getSharedPreferences("login",MODE_PRIVATE);
			if (mypref.getString("token", "none").equals("none")){
				FragmentManager fragmentManager = getFragmentManager();
				MissingUserFragment fragment = new MissingUserFragment();
				fragment.show(fragmentManager, "dialog");
			}
			else{
				String jsonUrl = "http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=GetAllOrders";
				jsonUrl = jsonUrl + "&username=" + mypref.getString("user", "usuario");
				jsonUrl = jsonUrl + "&authentication_token=" + mypref.getString("token", "none");
				//Toast.makeText(getApplicationContext(), jsonUrl, Toast.LENGTH_LONG).show();
				new OrderTask().execute(jsonUrl);
			}
		} else {
			Toast.makeText(getApplicationContext(),R.string.no_network, Toast.LENGTH_LONG).show();
		}
	}
	
	class OrderTask extends AsyncTask<String, Void, Void>{

		

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			//pDialog.setMessage(getString(R.string.load_order));
			pDialog.setVisibility(View.VISIBLE);

		}

		@Override
		protected Void doInBackground(String... params) {
			arrayOfList = new OrderParser().getData(params[0]);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (null != pDialog && pDialog.getVisibility() == View.VISIBLE) {
				pDialog.setVisibility(View.GONE);
			}
			
			if (null == arrayOfList || arrayOfList.size() == 0) {
				showToast(getText(R.string.no_data_api));
				OrderActivity.this.finish();
			} else {
				setAdapterToListview();
			}

		}
	}
	
	public void setAdapterToListview() {
		OrderAdapter objAdapter = new OrderAdapter(OrderActivity.this,
				R.layout.order_item, arrayOfList);
		
		listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
		listView.setSelector(R.drawable.listitem_background);
	
		listView.setAdapter(objAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View v,
					int position, long arg3) {
				Intent intent = new Intent(OrderActivity.this, OrderViewActivity.class);
				//cambiar despues de terminar el parser
				intent.putExtra("order", ((Order) adapter.getAdapter().getItem(position)).getId());
				//Toast.makeText(getApplicationContext(), ((Order)adapter.getAdapter().getItem(position)).getId().toString(), Toast.LENGTH_LONG).show();;
				startActivity(intent);
			}
		});
	}
	public void showToast(CharSequence charSequence) {
		Toast.makeText(getApplicationContext(), charSequence, Toast.LENGTH_LONG).show();
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
}
