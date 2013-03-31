package com.hailo.local;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hailo.local.data.RestaurantData;

// Custom Adapter for showing restaurant info
public class RestaurantAdapter extends BaseAdapter {

	private List<RestaurantData> restaurants ;
	private Activity mActivity;
	private LayoutInflater inflater;

	public RestaurantAdapter(Activity mainActivity,
			List<RestaurantData> restaurants) {
		mActivity = mainActivity;
		this.restaurants = restaurants;
		   inflater = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return restaurants.size();
	}

	@Override
	public Object getItem(int position) {
		if(restaurants != null)
			return restaurants.get(position);
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.places_nearby_list, null);
 
        TextView name = (TextView)vi.findViewById(R.id.restaurantname); // name
        TextView address = (TextView)vi.findViewById(R.id.address); // address
        TextView rating= (TextView)vi.findViewById(R.id.rating); // rating
        RatingBar ratingBar = (RatingBar) vi.findViewById(R.id.ratingBar1);
        ratingBar.setIsIndicator(true);
        RestaurantData restaurant = restaurants.get(position);
        // Setting all values in listview
        name.setText(restaurant.getName());
        address.setText(restaurant.getAddress());
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
        {
        	rating.setText(restaurant.getRating() > 0 ? restaurant.getRating()+"" : "-NA-");
        	ratingBar.setVisibility(View.INVISIBLE);
        }else{
        	rating.setVisibility(View.INVISIBLE);
        	ratingBar.setRating((float)restaurant.getRating());
        }
        return vi;
	}

}
