/**
 * 
 */
package com.hailo.local.util;



/**
 * @author akshatj
 *
 */
public class Constants {
	public static final int MIN_DISTANCE_FOR_GEO_UPDATES = 1000; //Specified in meters	// 10Kms
	public static final int MIN_TIME_FOR_GEO_UPDATES = 30*60*1000; //Specified in milliseconds
	public static final String LOG_TAG = "Hailo"; 
	public static final int RADIUS = 1600;
	
	public static final String API_KEY = "AIzaSyCap0cYcsAITeA83Fq4UBd6n98WDUu7uFI";
	
	public static final String NEARBY_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?sensor=true&key=%s&radius=%d&location=%f,%f&keyword=%s";
	public static final String NEARBY_URL_PAGETOKEN = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?sensor=true&key=%s&pagetoken=%s";
	
	public static final String NEARBY_KEY_RADIUS = "radius";
	public static final String NEARBY_KEY_LOCATION = "location";
	public static final String NEARBY_KEY_KEYWORD = "keyword";
	public static final String NEARBY_KEY_TYPES = "types";
	public static final String NEARBY_TYPES_FOOD = "food";
	public static final String NEARBY_TYPES_RESTAURANT = "restaurant";
	public static final String NEARBY_KEY_PAGETOKEN = "pagetoken";
	
	public static final String NEARBY_RESP_STATUS = "status";
	public static final String NEARBY_RESP_NEXT_PAGE_TOKEN = "next_page_token";
	public static final String NEARBY_RESP_RESULTS = "results	";
	
	// Shared pref keys
	public static final String SHARED_PREF_KEY_NAME = "Name";
	public static final String SHARED_PREF_KEY_LONGITUDE = "Longitude";
	public static final String SHARED_PREF_KEY_LATITUDE = "Latitude";

	
}
