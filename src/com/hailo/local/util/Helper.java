package com.hailo.local.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.hailo.local.data.RestaurantData;
import com.hailo.local.data.Results;

public class Helper {

	   /**
     * Downloads files from URL. Use GZIP header to reduce network bandwidth
     * @param url
     * @return
     * @throws Exception
     */
    public static String downloadData(String url) throws Exception{
    	
    	final HttpClient httpClient = AndroidHttpClient.newInstance("Android");
    	HttpResponse response;	
		HttpGet getMethod;
		Log.d(Constants.LOG_TAG, "URL == : " + url);
		URI uri = null;
		try {
			uri = new URI(url);
			getMethod = new HttpGet(uri);
		
		// Add GZIP request to allow for zipped response from server
			
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)
			AndroidHttpClient.modifyRequestToAcceptGzipResponse(getMethod);

			response = httpClient.execute(getMethod);
			Log.d(Constants.LOG_TAG, "Http request status code is : " + String.valueOf(response.getStatusLine().getStatusCode()));
			
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				Log.w(Constants.LOG_TAG, "Error " + response.getStatusLine().getStatusCode()
						+ " while retrieving data from " + url); 
				return null;
			}
		
			Header[] headers = response.getAllHeaders();
			boolean isGzipEncodedStream = false;
			for(Header head: headers){
				Log.v(Constants.LOG_TAG, "Response Http header name = " + head.getName() + ", value =" + head.getValue());
				if(head.getName().equals("Content-Encoding"))
					if(head.getValue().equalsIgnoreCase("gzip"))
						isGzipEncodedStream = true;
			}
			// Parse the input and construct the JSON
			final HttpEntity entity = response.getEntity();
			String json = "";
			if (entity != null) {
				InputStream inputStream = null;
				BufferedReader reader = null;
				StringBuilder buffer = new StringBuilder();

					try {
						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO && isGzipEncodedStream){
							inputStream = AndroidHttpClient.getUngzippedContent(entity);
						}else
							inputStream = entity.getContent();
						
			             reader = new BufferedReader(new InputStreamReader(
			            		inputStream, "iso-8859-1"), 8);
			    		char[] tmp = new char[1024];
			    		int l;

			    		while ((l = reader.read(tmp)) != -1) {
			   				buffer.append(tmp, 0, l);
			   			}
			            Log.i(Constants.LOG_TAG, "JSON String == " + buffer.toString());
			            json = buffer.toString();
			        } catch (Exception e) {
			            Log.e(Constants.LOG_TAG, "Error converting result " + e.toString());
			        }finally{
			        	if(reader != null)
			        		reader.close();
			        	if(inputStream !=null)
			        		inputStream.close();
			        }
			 
			        // return JSON String
			        return json;
			}
		}
		catch(IllegalArgumentException exception) {
			Log.e(Constants.LOG_TAG, "The url " + uri + " is invalid! Can't proceed with the GET request");
			throw exception;
		}finally{
			((AndroidHttpClient)httpClient).close();																		
		}
		return null;
    }
    
	/**
	 * Parse the JSON string to weather object
	 * @param nearbyPlaces
	 * @return List of all cities weather info
	 */
    public static Results parseWeatherJSON(final String nearbyPlaces){
    	
    	Log.i(Constants.LOG_TAG,"Weather JSON String == " + nearbyPlaces);
   
    	Gson gson = new Gson();
    	
    	Results results = (Results)gson.fromJson(nearbyPlaces, Results.class);

    	/*if("OK".equalsIgnoreCase(results.getStatus())){
	    	List<RestaurantData> lPlaces = (List<RestaurantData>)results.getResults();
			for(RestaurantData place: lPlaces){
				Log.i(Constants.LOG_TAG,"Place Name = " + place.getName());
				Log.i(Constants.LOG_TAG,"Place Id = " + place.getId());
				Log.i(Constants.LOG_TAG,"Place Address = " + place.getAddress());
				Log.i(Constants.LOG_TAG,"Place Latitude = " + place.getLocation());//.getLatitude());
//				Log.i(Constants.LOG_TAG,"Place Longitude = " + place.getLocation().getLongitute());
			}
			
			
    	} */
    	return results;
    }
}
