package de.manuel_joswig.socialoon.user;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import de.manuel_joswig.socialoon.util.HttpReader;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Represents a user object
 * 
 * @author		Manuel Joswig
 * @copyright	2017 Manuel Joswig
 */
public class User {
	private String id, username, password, email, latitude, longitude, biography, isVerified;
	private Drawable avatar;
	
	public User(JSONObject dataset) {
		try {
			if (dataset.has("id")) {
				String jsonId = dataset.getString("id");
				
				if (!jsonId.equals("null") && !jsonId.equals("0")) {
					id = jsonId;
				}
			}
			
        	username = dataset.getString("username");
        	password = dataset.getString("password");
        	email = dataset.getString("email");
        	latitude = dataset.getString("location_latitude");
        	longitude = dataset.getString("location_longitude");
        	biography = dataset.getString("biography");
        	isVerified = dataset.getString("is_verified");
        } catch (JSONException e) {
			Log.e("SOCIALOON_APP", "Could not parse json data!");
		}
	}
	
	public String getId() {
		return id;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public String getBiography() {
		return biography;
	}
	
	public Drawable getAvatar() throws IOException {
		// search png avatar on web server
		final String avatarUrl = "http://manuel-joswig.de/socialoon/avatar/" + id + ".png";
		
		Bitmap x;
		HttpURLConnection connection = (HttpURLConnection) new URL(avatarUrl).openConnection();
		connection.connect();
		InputStream input = connection.getInputStream();
		
		x = BitmapFactory.decodeStream(input);
		return new BitmapDrawable(x);
	}
	
	public boolean isVerified() {
		return (isVerified.equals("1"));
	}
	
	public void setLatitude(String locLat) {
		this.latitude = locLat;
	}
	
	public void setLongitude(String locLon) {
		this.longitude = locLon;
	}
	
	public void setBiography(String bio) {
		this.biography = bio;
	}
}
