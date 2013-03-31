package com.hailo.local;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hailo.local.util.Constants;

public class RestaurantOnMap extends FragmentActivity {

	private GoogleMap mMap;
	double lat, lng;
	String name;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_on_map);
        
        Bundle bundle = getIntent().getExtras();
        name = bundle.getString(Constants.SHARED_PREF_KEY_NAME);
        lat = bundle.getDouble(Constants.SHARED_PREF_KEY_LATITUDE);
        lng = bundle.getDouble(Constants.SHARED_PREF_KEY_LONGITUDE);
        
        if(name == null){
        	Toast.makeText(getApplicationContext(), R.string.no_data, Toast.LENGTH_LONG).show();
        	finish();
        }
        
        mMap  = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap != null) {
        	refreshMap();
        }
        
    }

	/**
	 * @param name
	 */
	void refreshMap() {
		LatLng restPosition = new LatLng(lat, lng);
		UiSettings settings = mMap.getUiSettings();
		settings.setMyLocationButtonEnabled(true);
		mMap.setMyLocationEnabled(true);
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.addMarker(new MarkerOptions()
		.position(restPosition)
		.title(name));
//		.icon(BitmapDescriptorFactory.fromResource(R.drawable.restaurant)));
		
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restPosition, 16));
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_restaurant_on_map, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if((item.getTitle().equals(getString(R.string.menu_refresh)))){
    		refreshMap();
    	}
    	return true;
    }
}
