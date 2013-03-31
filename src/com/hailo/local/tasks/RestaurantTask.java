/**
 * 
 */
package com.hailo.local.tasks;

import java.net.URLEncoder;

import android.os.AsyncTask;
import android.util.Log;

import com.hailo.local.data.Results;
import com.hailo.local.util.Constants;
import com.hailo.local.util.Helper;

/**
 * @author akshatj
 *
 */
public class RestaurantTask extends AsyncTask<String, Void, Results> {

	@Override
	protected Results doInBackground(String... params) {
		String placesURL = "";
		
		if(params.length == 1){			// For Page token
			String pageToken = params[0];
			placesURL = String.format(Constants.NEARBY_URL_PAGETOKEN, Constants.API_KEY,pageToken);
		}else{						// for coordinates
			double lat = Double.valueOf(params[0]);
			double longitude = Double.valueOf(params[1]);
			double radius = Double.valueOf(params[2]);
			placesURL = String.format(Constants.NEARBY_URL, Constants.API_KEY,(int)radius, lat,longitude,URLEncoder.encode("food|restaurant"));
		}
    	String strJson = null;
    	try {
    		Log.d(Constants.LOG_TAG, "URL == " + placesURL);
			strJson = Helper.downloadData(placesURL);
		} catch (Exception e) {
				e.printStackTrace();
				return null;
		}
    	Results results = null;
    	if(strJson != null){
//    		Log.d(Constants.LOG_TAG, "JSON String == " + strJson);
    		results = Helper.parseWeatherJSON(strJson);
    	}
    	else
    		Log.d(Constants.LOG_TAG, "no JSON data returned from server == ");
    	
		return results;
	}
	
	@Override
	protected void onPostExecute(Results result) {
		if(isCancelled()){
			result = null;
			return ;
		}
		
		
		super.onPostExecute(result);
	}

}
