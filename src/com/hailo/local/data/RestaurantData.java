package com.hailo.local.data;

import java.io.Serializable;

import com.hailo.local.data.RestaurantData.Geometry.Location;


public class RestaurantData implements Serializable{

	private static final long serialVersionUID = 1L;
	private String vicinity;
	private String name;
	private String id;
	private String ref;
	private double rating;
	
	private Geometry geometry;
	private OpenHours opening_hours;
	
	class OpenHours implements Serializable {
		
		private static final long serialVersionUID = 1L;
		private boolean open_now ;

		public boolean isOpen_now() {
			return open_now;
		}

		public void setOpen_now(boolean open_now) {
			this.open_now = open_now;
		}
	}
	
	class Geometry implements Serializable{

		private Location location;
		private static final long serialVersionUID = 1L;

		public class Location implements Serializable{
			private static final long serialVersionUID = 1L;
			private double lat;
			private double lng;

			public double getLatitude() {
				return lat;
			}

			public void setLatitude(double latitude) {
				this.lat = latitude;
			}

			public double getLongitute() {
				return lng;
			}

			public void setLongitute(double longitute) {
				this.lng = longitute;
			}

		}

		public Location getLocation() {
			return location;
		}

		public void setLocation(Location pLocation) {
			this.location = pLocation;
		}
	}
	
	
	public String getAddress() {
		return vicinity;
	}
	
	public void setAddress(String address) {
		this.vicinity = address;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getRef() {
		return ref;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	public double getRating() {
		return rating;
	}
	
	public void setRating(double rating) {
		this.rating = rating;
	}
	
	public boolean isOpenNow() {
		return opening_hours != null ? opening_hours.isOpen_now() : false;
	}
	public void setOpenNow(boolean isOpenNow) {
		if(opening_hours != null)
			opening_hours.setOpen_now(isOpenNow);
		
	}
	
	public Location getLocation(){
		return geometry.getLocation();
	}
	
	public void setLocation(Geometry pLocation){
		this.geometry = pLocation;
	}
}
