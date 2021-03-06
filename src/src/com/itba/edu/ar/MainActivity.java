package com.itba.edu.ar;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.itba.edu.ar.adapter.NavDrawerListAdapter;
import com.itba.edu.ar.model.NavDrawerItem;
import com.itba.edu.ar.parser.ProductParser;

public class MainActivity extends Activity implements
		SearchView.OnQueryTextListener {
	private static MenuItem searchItem;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private static ActionBarDrawerToggle mDrawerToggle;
	private static SearchView mSearchView;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;
	SharedPreferences saveData;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_main);
		saveData = getSharedPreferences("login", MODE_PRIVATE);

		// mTitle = mDrawerTitle = getTitle();
		navMenuTitles = getResources().getStringArray(R.array.menu_options);

		navMenuIcons = getResources()
				.obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		for (int i = 0; i < navMenuTitles.length; i++) {
			if (navMenuTitles[i].equals("Notificaciones")) {
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[i],
						navMenuIcons.getResourceId(i, -1), true, "1"));
			} else {
				navDrawerItems.add(new NavDrawerItem(navMenuTitles[i],
						navMenuIcons.getResourceId(i, -1)));
			}
		}

		// Recycle the typed array
		navMenuIcons.recycle();

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				navDrawerItems);
		mDrawerList.setAdapter(adapter);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		mDrawerList.setSelector(R.drawable.listitem_background);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.drawer_icon, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// Con esto le digo que agarre la primera opcion (nuestro index)
			selectItem(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		searchItem = menu.findItem(R.id.action_search);
		mSearchView = (SearchView) searchItem.getActionView();
		setupSearchView(searchItem);
		return super.onCreateOptionsMenu(menu);
	}

	public void setupSearchView(MenuItem searchItem) {

		if (isAlwaysExpanded()) {
			mSearchView.setIconifiedByDefault(false);
		} else {
			searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
					| MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
		}

		mSearchView.setSubmitButtonEnabled(true);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		if (searchManager != null) {
			mSearchView.setSearchableInfo(searchManager
					.getSearchableInfo(getComponentName()));
		}
		mSearchView.setOnQueryTextListener(this);
	}

	protected boolean isAlwaysExpanded() {
		return false;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			Toast.makeText(getApplicationContext(), "Buscaste: " + query,
					Toast.LENGTH_LONG).show();
			// doSearch(query);
		}
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_search).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action buttons
		switch (item.getItemId()) {
		case R.id.action_search:
			return true;
		case R.id.action_settings:
			Intent order = new Intent(this, UserSettingActivity.class);
			startActivity(order);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onQueryTextChange(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		Fragment fragment = new BGFragment();
		Bundle args = new Bundle();
		args.putInt(BGFragment.ITEM_NUMBER, position);

		args.putString("user", getSharedPreferences("login", MODE_PRIVATE)
				.getString("user", "none user"));
		args.putString("user", getSharedPreferences("login", MODE_PRIVATE)
				.getString("user", "none user"));

		fragment.setArguments(args);

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.frame_container, fragment).commit();

		// update selected item and title, then close the drawer
		mDrawerList.setItemChecked(position, true);
		setTitle(navMenuTitles[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/**
	 * Fragment that appears in the "content_frame"
	 */
	public static class BGFragment extends Fragment {
		public static final String ITEM_NUMBER = "item_number";
		SharedPreferences saveData;

		public BGFragment() {
			// Empty constructor required for fragment subclasses
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			setHasOptionsMenu(true);
			View rootView = null;
			int i = getArguments().getInt(ITEM_NUMBER);
			String user = getArguments().getString("user");
			getActivity().setTitle(R.string.app_name);
			rootView = inflater.inflate(R.layout.fragment_main, container,
					false);

			Button btnLogin = (Button) rootView.findViewById(R.id.btnLogin);
			TextView welcome = (TextView) rootView.findViewById(R.id.welcome);

			if (user != null) {
				if (!user.equals("none user")) {

					String change_user = (String) getText(R.string.change_user);
					btnLogin.setText(change_user);
					welcome.setText(getText(R.string.welcome) + " : " + user);
				} else {
					btnLogin.setText(R.string.login);
				}
			}
			btnLogin.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(
							getActivity().getApplicationContext(),
							LoginActivity.class);
					startActivity(i);
				}
			});

			switch (i) {
			case 0:
				// Vacio, ya lo cargue por defecto al principio
				break;
			case 1:
				getActivity().setTitle(R.string.categories);
				Intent intent = new Intent(getActivity()
						.getApplicationContext(), CategoriesActivity.class);
				startActivity(intent);
				break;
			case 2:
				getActivity().setTitle(R.string.masculino);
				Intent intentMale = new Intent(getActivity()
						.getApplicationContext(), CategoriesActivity.class);
				intentMale.putExtra("filters",
						"[{\"id\":1,\"value\":\"Masculino\"}]");
				startActivity(intentMale);
				break;
			case 3:
				getActivity().setTitle(R.string.femenino);
				Intent intentFemale = new Intent(getActivity()
						.getApplicationContext(), CategoriesActivity.class);
				intentFemale.putExtra("filters",
						"[{\"id\":1,\"value\":\"Femenino\"}]");
				startActivity(intentFemale);
				break;
			case 4:
				getActivity().setTitle(R.string.infantil);
				Intent intentChild = new Intent(getActivity()
						.getApplicationContext(), CategoriesActivity.class);
				intentChild.putExtra("filters",
						"[{\"id\":2,\"value\":\"Infantil\"}]");
				startActivity(intentChild);
				break;
			case 5:
				Intent settings = new Intent(getActivity(), OrderActivity.class);
				startActivity(settings);
				break;
			case 6:
				getActivity().setTitle(R.string.sales);
				Intent intentSale = new Intent(getActivity()
						.getApplicationContext(), ProductListActivity.class);
				intentSale
						.putExtra(
								"url",
								"http://eiffel.itba.edu.ar/hci/service3/Catalog.groovy?method=GetAllProducts&filters=[{\"id\":5,\"value\":\"Oferta\"}]&pageSize=100");
				startActivity(intentSale);
				break;
			case 7:
				IntentIntegrator scanIntegrator = new IntentIntegrator(
						getActivity());
				scanIntegrator.initiateScan();
				break;
			case 8:
				Intent order = new Intent(getActivity(),
						UserSettingActivity.class);
				startActivity(order);
				break;
			case 9:
				Intent login = new Intent(getActivity(), LoginActivity.class);
				startActivity(login);
				break;
			default:
				Intent intentView = new Intent(getActivity(),
						ProductViewActivity.class);
				startActivity(intentView);
				break;
			}
			return rootView;
		}

		public void onActivityResult(int requestCode, int resultCode,
				Intent intent) {
			IntentResult scanningResult = IntentIntegrator.parseActivityResult(
					requestCode, resultCode, intent);

			if (scanningResult != null) {
				Intent productIntent = new Intent(getActivity(),
						ProductViewActivity.class);
				String productId = getProductId(scanningResult.getContents());
				if (productId == null) {
					return;
				} else {
					productIntent.putExtra("productId",
							Integer.parseInt(productId));
					startActivity(productIntent);
				}
			} else {
				Toast toast = Toast.makeText(getActivity(),
						getString(R.string.no_qr), Toast.LENGTH_SHORT);
				toast.show();
				Intent intentView = new Intent(getActivity(),
						MainActivity.class);
				startActivity(intentView);
			}
		}

		private String getProductId(String content) {
			if (content.contains("product/id/")) {
				String[] params = content.split("product/id/");
				String id = params[1];
				return id;
			}
			return null;
		}
	}
}