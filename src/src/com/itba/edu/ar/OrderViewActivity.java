package com.itba.edu.ar;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itba.edu.ar.adapter.ProductsAdapter;
import com.itba.edu.ar.model.Item;
import com.itba.edu.ar.model.Order;
import com.itba.edu.ar.model.Product;
import com.itba.edu.ar.parser.OrderDetailParser;
import com.itba.edu.ar.utils.Utils;

public class OrderViewActivity extends FragmentActivity {
	public  String url = "http://eiffel.itba.edu.ar/hci/service3/Order.groovy?method=GetOrderById";
	Order orden;
	ProgressBar pDialog;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_view);
		pDialog = (ProgressBar) findViewById(R.id.progress);
		if (Utils.isNetworkAvailable(OrderViewActivity.this)){
			
			if(getIntent().hasExtra("order")){
				Bundle extras = getIntent().getExtras();
				final Integer id = extras.getInt("order");
				if(id != null){
					getActionBar().setTitle(getText(R.string.order)+ " Numero " + id.toString());
					SharedPreferences pref = getSharedPreferences("login", MODE_PRIVATE);
					String user = pref.getString("user", "none");
					if(!user.equals("none"))
						url = url + "&username=" + user;
					String token = pref.getString("token", "none");
					if(!token.equals("none"))
						url = url + "&authentication_token=" + token;
					url = url + "&id=" + id;

					
					new OrderDetailTask().execute(url);
				}
			}
			else
				Toast.makeText(getApplicationContext(), "No hay order", Toast.LENGTH_LONG).show();;
		}
		else{
			Toast.makeText(getApplicationContext(),R.string.no_network, Toast.LENGTH_LONG).show();
		}
	}
	class OrderDetailTask extends AsyncTask<String, Void, Void>{
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog.setVisibility(View.VISIBLE);
		}
		@Override
		protected Void doInBackground(String... params) {
			orden = new OrderDetailParser().getData(params[0]);
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (null != pDialog && pDialog.getVisibility() == View.VISIBLE) {
				pDialog.setVisibility(View.GONE);
			}
			
			if(orden == null){
				Toast.makeText(getApplicationContext(), getString(R.string.no_data_api), Toast.LENGTH_LONG).show();
			}
			else
				setAdapterToListview();
		}
	}
	public void setAdapterToListview(){
		List<Product> productos = new ArrayList<Product>();
		TextView price = (TextView) findViewById(R.id.priceVal);
		TextView quantity = (TextView) findViewById(R.id.quantityVal);
		TextView shipped = (TextView) findViewById(R.id.shippedVal);
		TextView proccesed = (TextView) findViewById(R.id.proccesedVal);
		TextView send = (TextView) findViewById(R.id.deliveredVal);
		TextView received = (TextView) findViewById(R.id.receivedVal);
		ScrollView scrollview = (ScrollView) findViewById(R.id.scroll);
		
		LatLng pos = new LatLng(
				Double.valueOf(orden.getLatitude()),
				Double.valueOf(orden.getLongitude()));

		GoogleMap map = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
		map.setMyLocationEnabled(true);
		map.setTrafficEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(true);
		map.getUiSettings().setCompassEnabled(false);
		map.addMarker(new MarkerOptions()
				.position(pos)
				.title(getString(R.string.order) + " "
						+ orden.getId() + ".")
				.snippet(orden.getLatitude() + ", " + orden.getLongitude())
				.anchor(0.5f, 0.5f));
		
		Integer acumPrice = 0;
		Integer acumQuantity = 0;
		for(Item item : orden.getItems()){
			productos.addAll(item.getProducts());
			acumPrice += item.getPrice();
			acumQuantity += item.getQuantity();
		}
		price.setText(acumPrice.toString());
		quantity.setText(acumQuantity.toString());
		
		if(orden.getShippedDate()!= null) 
			shipped.setText(getStringDate(orden.getShippedDate()));
		if(orden.getProcessedDate() != null)
			proccesed.setText(getStringDate(orden.getProcessedDate()));
		if(orden.getDeliveredDate() != null)
			send.setText(getStringDate(orden.getDeliveredDate()));
		if(orden.getReceivedDate() != null)
			received.setText(getStringDate(orden.getReceivedDate()));
		ListView listView = (ListView) findViewById(R.id.orderlist);
		ProductsAdapter objAdapter = new ProductsAdapter(OrderViewActivity.this,
				R.layout.product_item, productos);
		
		listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);
		listView.setSelector(R.drawable.listitem_background);
		
		listView.setAdapter(objAdapter);
		
		scrollview.pageScroll(View.FOCUS_UP);
		scrollview.scrollTo(0, 0);
	}
	
	private String getStringDate(String date) {
		String[] hourDate = date.split(" ");
		String[] dates = hourDate[0].split("-");
		return dates[2] + "/" + dates[1] + "/" + dates[0];
	}
	
}
