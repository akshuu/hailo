package com.hailo.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.hailo.local.data.RestaurantData;
import com.hailo.local.data.Results;
import com.hailo.local.tasks.RestaurantTask;
import com.hailo.local.util.Constants;

public class MainActivity extends Activity implements View.OnClickListener {

	private static final String KEY_SEARCH_RESULT = "SearchResult";
	private static int SEARCH_PAGES = 15;
	private static final String KEY_PAGE_TOKEN = "PageToken";
	private static final String KEY_PAGE_NUMBER = "PageNumber";
	private static final String SHARED_PREF_FILE = "lastRestaurant";
	protected static final int MSG_LOCATION = 0;
	private ListView list;
	private ArrayList<String> pageTokens;
	private RestaurantTask nearRestaurantTask = null;
	private Button next,previous;
	private int pageNumber = 0;
	private double latitude = 0d;
	private double longitude = 0d;
	private HashMap<Integer,Results> searchResults;
	private String name;
	Intent msgIntent;
	
	
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        next = (Button) findViewById(R.id.btnNext);
        previous = (Button) findViewById(R.id.btnPrev);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        
        msgIntent = new Intent(this, LocationService.class);
        Messenger messenger = new Messenger(uiHandler);
        msgIntent.putExtra("MESSENGER", messenger);
        startService(msgIntent);

        if(savedInstanceState == null){
           
        	pageTokens = new ArrayList<String>(SEARCH_PAGES);
        	pageNumber = 0;
        	searchResults = new HashMap<Integer, Results>();		// Cache results
        	
        }else{
        	pageTokens = savedInstanceState.getStringArrayList(KEY_PAGE_TOKEN);
        	pageNumber = savedInstanceState.getInt(KEY_PAGE_NUMBER);
        	searchResults = (HashMap<Integer, Results>) savedInstanceState.getSerializable(KEY_SEARCH_RESULT);
        }
        
  	}
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }
    
		@Override
		public void onClick(View v) {
			String token = null;
			Log.i(Constants.LOG_TAG, "Before query pagenumber == " + pageNumber);
			if(v.getId() == R.id.btnNext){
				try{
					token = pageTokens.get(pageNumber);
				}catch(Exception ex){
					token = null;
				}
				if(token !=null){
					pageNumber++;
					if(pageNumber > SEARCH_PAGES){
						// Just resize the array
						ArrayList<String> pageTokensNew = new ArrayList<String>(2*SEARCH_PAGES);
						pageTokensNew.addAll(pageTokens);
						pageTokens = null;
						pageTokens = pageTokensNew;
						SEARCH_PAGES = 2 * SEARCH_PAGES;
					}
				}
				Log.i(Constants.LOG_TAG, "After query pagenumber == " + pageNumber);

				Log.i(Constants.LOG_TAG, "Before query token == " + token);
				if(token == null){
					Toast.makeText(getApplicationContext(), R.string.no_more_results, Toast.LENGTH_LONG).show();
					return;
				}
				// Execute the next call
				// Create async task to fetch weather info.
		    	Results results = null;
		    	nearRestaurantTask = new RestaurantTask();
		    	try {
		    		nearRestaurantTask.execute(token);
		    		
		    		results = nearRestaurantTask.get();
				} catch (InterruptedException e) {
					e.printStackTrace();
					results = null;
				} catch (ExecutionException e) {
					e.printStackTrace();
					results = null;
				}finally{
					nearRestaurantTask.cancel(false);
					nearRestaurantTask = null;
				}
		    	
		       	if(results != null)
		       		updateListView(results);

			}
			if(v.getId() == R.id.btnPrev){
				
				if(pageNumber == 0) {
					pageNumber = 0;
					Toast.makeText(getApplicationContext(), R.string.first_page, Toast.LENGTH_LONG).show();
					return;
				}
				pageNumber--;
				if(searchResults.containsKey(pageNumber)){
					updateListView(searchResults.get(pageNumber));
				}
				Log.i(Constants.LOG_TAG, "After query pagenumber == " + pageNumber);
				return;
			}
		}
    	
    
    /**
     * Gets the current weather data based on users location.
     * @param lat
     * @param longitude
     */
    public void getRestaurantsData(Double[] coords){
    
    	nearRestaurantTask = new RestaurantTask();
    	int radius = Constants.RADIUS;
     	
    	ConnectivityManager comMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    	NetworkInfo mobileNetwork = comMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    	NetworkInfo wifiNetwork = comMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	if(!(mobileNetwork.isConnected() || wifiNetwork.isConnected())){
    		Toast.makeText(getApplicationContext(), "No Data connection available. Please turn on data services",Toast.LENGTH_SHORT).show();
    		next.setEnabled(false);
    		previous.setEnabled(false);
    	}else{
    		next.setEnabled(true);
    		previous.setEnabled(true);
    	
    	
    	String params[] = {coords[0].toString(),coords[1].toString(),radius+""};
  
    	// Create async task to fetch weather info.
    	Results results = null;
    	try {
    		nearRestaurantTask.execute(params);
    		
			results = nearRestaurantTask.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}finally{
			nearRestaurantTask.cancel(false);
			nearRestaurantTask = null;
		} 
		
    		updateListView(results);
    	}
    }

	/**
	 * @param results
	 */
	void updateListView(Results results) {
		List<RestaurantData> restaurants = null;
		if(results != null){
	    	if("OK".equalsIgnoreCase(results.getStatus())){
	    		restaurants = results.getResults();
				searchResults.put(pageNumber, results);
				if(results.getNext_page_token() != null){
					Log.i(Constants.LOG_TAG, "Saving token == " + results.getNext_page_token());
					pageTokens.add(results.getNext_page_token());
				}
	    	}else{
	    		showError(results.getStatus());
	    		return;
	    	}
		}else{
    		showError("");
    		return;
		}
    	
    	if(restaurants != null){
        	/*
             * Updating parsed JSON data into ListView
             */
        		List<RestaurantData> restaurantsAdapter = new ArrayList<RestaurantData>(restaurants);
           
        		list=(ListView)findViewById(R.id.list);
        		final ListAdapter adapter = new RestaurantAdapter(this, restaurantsAdapter);
        		
        		list.setAdapter(adapter);
                // Launching new screen on Selecting Single ListItem
        		list.setOnItemClickListener(new OnItemClickListener() {
         
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                            int position, long id) {
                        // Starting new intent
                        
                        RestaurantData restaurant = (RestaurantData)adapter.getItem(position);
                        name = restaurant.getName();
                        latitude = restaurant.getLocation().getLatitude();
                        longitude = restaurant.getLocation().getLongitute();
                        
                        showOnMap(name,latitude,longitude);
                    }

					
                });
        	}
	}

	/**
	 * @param restaurant
	 */
	void showOnMap(String  name, double lat, double lng) {
		Intent in = new Intent(getApplicationContext(), RestaurantOnMap.class);
        in.putExtra(Constants.SHARED_PREF_KEY_LATITUDE, lat);
        in.putExtra(Constants.SHARED_PREF_KEY_LONGITUDE, lng);
        in.putExtra(Constants.SHARED_PREF_KEY_NAME, name);
       
        startActivity(in);
	}
	// Error handling
    private void showError(String status) {
    	String errMsg = "";
    		if("ZERO_RESULTS".equals(status)){
    			errMsg = getString(R.string.zero_results);
    		}else if("OVER_QUERY_LIMIT".equals(status)){
    			errMsg = getString(R.string.quota_exceeded);
    		}else if("REQUEST_DENIED".equals(status)){
    			errMsg = getString(R.string.request_denied);
    		}else if("INVALID_REQUEST".equals(status)){
    			errMsg = getString(R.string.invalid_request);
    		}else
    			errMsg = getString(R.string.unknown_error);
    		Toast.makeText(getApplicationContext(), errMsg, Toast.LENGTH_LONG).show();
    		
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if((item.getTitle().equals(getString(R.string.menu_refresh)))){
    		refresh();
    	}else if((item.getTitle().equals(getString(R.string.menu_last_searched)))){
    		SharedPreferences prefs = getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
    		String resName = prefs.getString(Constants.SHARED_PREF_KEY_NAME, null);
    		String sLat = prefs.getString(Constants.SHARED_PREF_KEY_LATITUDE, "0");
    		String sLong = prefs.getString(Constants.SHARED_PREF_KEY_LONGITUDE, "0");
    		double lat = Double.valueOf(sLat);
    		double lng = Double.valueOf(sLong);
    		Log.i(Constants.LOG_TAG, "Previous saved Lat = " + lat + ", longitude = " + lng);

    		Log.i(Constants.LOG_TAG, "Read from shared pref" + resName);
    		name = resName;
    		latitude = lat;
    		longitude = lng;
    		showOnMap(resName,lat,lng);
    	}
    	return true;
    }

    private void refresh() {
       	
    	// Clear old values
    	pageTokens = new ArrayList<String>(SEARCH_PAGES);
    	pageNumber = 0;
    	searchResults = new HashMap<Integer, Results>();		// Cache results
    	// Get last known location
    	LocationManager locMgr = (LocationManager) getSystemService(LOCATION_SERVICE);
    	Location location = null;
    	if(locMgr.isProviderEnabled(LocationManager.GPS_PROVIDER)){
    		 location = locMgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	}else if(locMgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
    		 location = locMgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	}
    	if(location != null){
    		double lat = location.getLatitude();
    		double lng = location.getLongitude();
			Double[] coords = new Double[] { lat, lng };

			getRestaurantsData(coords);
    	}else{
    		Toast.makeText(getApplicationContext(), "No location update received. Waiting for update...", Toast.LENGTH_LONG).show();
    		return;
    	}
	}

	@Override
    protected void onPause() {
    	super.onPause();
    	SharedPreferences prefs = getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);
    	Editor editor = prefs.edit();
    	
    	editor.putString(Constants.SHARED_PREF_KEY_LATITUDE, latitude+"");
    	editor.putString(Constants.SHARED_PREF_KEY_LONGITUDE, longitude + "");
    	editor.putString(Constants.SHARED_PREF_KEY_NAME, name);
    	Log.i(Constants.LOG_TAG, "Writing to shared pref" + name + ", lat = " + latitude);
    	editor.commit();
    	if(nearRestaurantTask != null)
    		nearRestaurantTask.cancel(true);
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	outState.putStringArrayList(KEY_PAGE_TOKEN,pageTokens);
    	outState.putInt(KEY_PAGE_NUMBER, pageNumber);
    	outState.putSerializable(KEY_SEARCH_RESULT, searchResults);
    	super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	if(savedInstanceState != null){
    		pageTokens = savedInstanceState.getStringArrayList(KEY_PAGE_TOKEN);
    		pageNumber = savedInstanceState.getInt(KEY_PAGE_NUMBER);
    		searchResults = (HashMap<Integer, Results>) savedInstanceState.getSerializable(KEY_SEARCH_RESULT);
    	}
    	super.onRestoreInstanceState(savedInstanceState);
    }
    
    @Override
    protected void onDestroy() {
    	stopService(msgIntent); 
    	super.onDestroy();
    }
    
    Handler uiHandler = new Handler(){
        public void handleMessage(final Message msg) {
    		switch(msg.what){
    		case MSG_LOCATION:
    			Log.v(Constants.LOG_TAG, "got the location from sevice..." + msg.getData());
    			final Bundle bundle = msg.getData();
    				
    			runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						if(bundle != null){
							Double coords[] = new Double[]{bundle.getDouble("latitude"),
							                             bundle.getDouble("longitude")
							};
							getRestaurantsData(coords);
						}
						
					}
				});
    			break;
    			
    		default:
    				break;
    		}
        }
    };
}
